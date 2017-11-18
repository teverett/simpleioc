package com.khubla.simpleioc.classlibrary;

/**
 * Copyright 2012 Tom Everett Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;

import com.khubla.simpleioc.annotation.RegistryBean;
import com.khubla.simpleioc.annotation.RegistryFilter;
import com.khubla.simpleioc.exception.IOCException;

/**
 * @author tome
 */
public class ClassLibrary {
   /**
    * log
    */
   private final Log log = LogFactory.getLog(ClassLibrary.class);
   /**
    * some defines
    */
   private final static String CLASS = ".class";
   private final static String JAR = ".jar";
   /**
    * singleton
    */
   private static ClassLibrary instance = null;

   /**
    * get instance
    */
   public static ClassLibrary getInstance() {
      if (null == instance) {
         instance = new ClassLibrary();
      }
      return instance;
   }

   public static void setWarPath(String warPath) {
      ClassLibrary.warPath = warPath;
   }

   /**
    * the classes
    */
   private final List<Class<?>> classes;
   /**
    * the actual path to the war file classes
    */
   private static String warPath = null;

   public static String getWarPath() {
      return warPath;
   }

   /**
    * ctor
    */
   private ClassLibrary() {
      List<Class<?>> ret = null;
      try {
         ret = discoverClasses();
      } catch (final Exception e) {
         e.printStackTrace();
      }
      classes = ret;
   }

   private boolean annotated(ClassNode classNode) throws Exception {
      try {
         if (hasAnnotation(classNode, RegistryBean.class) || (hasAnnotation(classNode, RegistryFilter.class))) {
            return true;
         }
         return false;
      } catch (final Exception e) {
         throw new IOCException("Exception in annnotated", e);
      }
   }

   /**
    * find all the classes in a jar file
    */
   private List<Class<?>> crackJar(String jarfile) throws Exception {
      try {
         /*
          * the ret
          */
         final List<Class<?>> ret = new ArrayList<Class<?>>();
         /*
          * the jar
          */
         final FileInputStream fis = new FileInputStream(jarfile);
         final ZipInputStream zip_inputstream = new ZipInputStream(fis);
         try {
            ZipEntry current_zip_entry = null;
            while ((current_zip_entry = zip_inputstream.getNextEntry()) != null) {
               if (current_zip_entry.getName().endsWith(".class")) {
                  if (current_zip_entry.getSize() > 0) {
                     final ClassNode classNode = new ClassNode();
                     final ClassReader cr = new ClassReader(zip_inputstream);
                     cr.accept(classNode, 0);
                     if (annotated(classNode)) {
                        ret.add(Class.forName(classNode.name.replaceAll("/", ".")));
                        log.debug("Found " + classNode.name + " in " + jarfile);
                     }
                  }
               }
            }
         } finally {
            zip_inputstream.close();
            fis.close();
         }
         return ret;
      } catch (final Throwable e) {
         throw new IOCException("Exception in crackJar for jar '" + jarfile + "'", e);
      }
   }

   /**
    * find all classes
    */
   private List<Class<?>> discoverClasses() throws Exception {
      try {
         /*
          * collection of all classes
          */
         final List<Class<?>> classes = new ArrayList<Class<?>>();
         /*
          * scan the war
          */
         if (null != warPath) {
            classes.addAll(scan(new File(warPath), ""));
         }
         /*
          * scan all the jars on the class path
          */
         final String pathSep = System.getProperty("path.separator");
         final String list = System.getProperty("java.class.path");
         for (final String path : list.split(pathSep)) {
            final File file = new File(path);
            classes.addAll(scan(file, ""));
         }
         /*
          * done
          */
         return classes;
      } catch (final Exception e) {
         throw new IOCException("Exception in discoverClasses", e);
      }
   }

   public List<Class<?>> getClasses() {
      return classes;
   }

   @SuppressWarnings("unchecked")
   private boolean hasAnnotation(ClassNode classNode, Class<?> annotation) throws Exception {
      try {
         /*
          * walk annotations
          */
         final List<AnnotationNode> annotations = classNode.visibleAnnotations;
         if (null != annotations) {
            for (final AnnotationNode annotationNode : annotations) {
               /*
                * get the class name
                */
               final String annotationClassName = annotationNode.desc.replaceAll("/", ".").substring(1, annotationNode.desc.length() - 1);
               /*
                * check
                */
               if (annotationClassName.compareTo(annotation.getName()) == 0) {
                  return true;
               }
            }
         }
         return false;
      } catch (final Exception e) {
         throw new IOCException("Exception in hasAnnotation", e);
      }
   }

   /**
    * scan a given directory
    */
   private List<Class<?>> scan(File file, String concatenatedName) throws Exception {
      try {
         log.debug("scanning directory'" + file.getAbsolutePath() + "'");
         /*
          * collection of all classes
          */
         final List<Class<?>> ret = new ArrayList<Class<?>>();
         if (file.isDirectory()) {
            final File[] files = file.listFiles();
            if (null != files) {
               for (final File f : files) {
                  if (false == f.isHidden()) {
                     if (f.isDirectory()) {
                        ret.addAll(scan(f, concatenatedName + f.getName() + "."));
                     } else {
                        ret.addAll(scan(f, concatenatedName + f.getName()));
                     }
                  }
               }
            }
         } else {
            /*
             * its a file, could be a class file or a jar file, or neither
             */
            if (file.getName().endsWith(CLASS)) {
               final ClassNode classNode = new ClassNode();
               final ClassReader cr = new ClassReader(new FileInputStream(file.getAbsolutePath()));
               cr.accept(classNode, 0);
               if (annotated(classNode)) {
                  ret.add(Class.forName(classNode.name.replaceAll("/", ".")));
                  log.debug("adding " + classNode.name + " from " + file.getAbsolutePath());
               }
            } else if (file.getName().endsWith(JAR)) {
               ret.addAll(crackJar(file.getAbsolutePath()));
            }
         }
         /*
          * done
          */
         return ret;
      } catch (final Exception e) {
         throw new IOCException("Exception in scan", e);
      }
   }
}
