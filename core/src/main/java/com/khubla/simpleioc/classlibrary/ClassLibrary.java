package com.khubla.simpleioc.classlibrary;

/**
 * Copyright 2012 Tom Everett Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Hashtable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

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
   private final Hashtable<String, ClassNode> classes;
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
      Hashtable<String, ClassNode> ret = null;
      try {
         ret = discoverClasses();
      } catch (final Exception e) {
         e.printStackTrace();
      }
      classes = ret;
   }

   /**
    * find all the classes in a jar file
    */
   private Hashtable<String, ClassNode> crackJar(String jarfile) throws Exception {
      try {
         /*
          * the ret
          */
         final Hashtable<String, ClassNode> ret = new Hashtable<String, ClassNode>();
         /*
          * the jar
          */
         final FileInputStream fis = new FileInputStream(jarfile);
         final ZipInputStream zip_inputstream = new ZipInputStream(fis);
         ZipEntry current_zip_entry = null;
         while ((current_zip_entry = zip_inputstream.getNextEntry()) != null)
         {
            if (current_zip_entry.getName().endsWith(".class")) {
               if (current_zip_entry.getSize() > 0) {
                  final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                  int data = 0;
                  while ((data = zip_inputstream.read()) != -1)
                  {
                     baos.write(data);
                  }
                  baos.flush();
                  baos.close();
                  final ClassNode classNode = new ClassNode();
                  final ClassReader cr = new ClassReader(new ByteArrayInputStream(baos.toByteArray()));
                  cr.accept(classNode, 0);
                  ret.put(classNode.name.replaceAll("/", "."), classNode);
                  log.debug(classNode.name);
               }
            }
         }
         zip_inputstream.close();
         fis.close();
         return ret;
      } catch (final Throwable e) {
         throw new Exception("Exception in crackJar for jar '" + jarfile + "'", e);
      }
   }

   /**
    * find all classes
    */
   private Hashtable<String, ClassNode> discoverClasses() throws Exception {
      try {
         /*
          * collection of all classes
          */
         final Hashtable<String, ClassNode> classes = new Hashtable<String, ClassNode>();
         /*
          * scan the war
          */
         if (null != warPath) {
            scan(new File(warPath), "");
         }
         /*
          * scan all the jars on the class path
          */
         final String pathSep = System.getProperty("path.separator");
         final String list = System.getProperty("java.class.path");
         for (final String path : list.split(pathSep)) {
            final File file = new File(path);
            classes.putAll(scan(file, ""));
         }
         /*
          * done
          */
         return classes;
      } catch (final Exception e) {
         throw new Exception("Exception in discoverClasses", e);
      }
   }

   public Hashtable<String, ClassNode> getClasses() {
      return classes;
   }

   /**
    * scan a given directory
    */
   private Hashtable<String, ClassNode> scan(File file, String concatenatedName) throws Exception {
      try {
         /*
          * collection of all classes
          */
         final Hashtable<String, ClassNode> ret = new Hashtable<String, ClassNode>();
         if (file.isDirectory()) {
            final File[] files = file.listFiles();
            if (null != files) {
               for (final File f : files) {
                  if (false == f.isHidden()) {
                     if (f.isDirectory()) {
                        log.debug("scanning '" + file.getAbsolutePath() + "'");
                        ret.putAll(scan(f, concatenatedName + f.getName() + "."));
                     } else {
                        ret.putAll(scan(f, concatenatedName + f.getName()));
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
               ret.put(classNode.name.replaceAll("/", "."), classNode);
               log.debug(classNode.name);
            } else if (file.getName().endsWith(JAR)) {
               ret.putAll(crackJar(file.getAbsolutePath()));
            }
         }
         /*
          * done
          */
         return ret;
      } catch (final Exception e) {
         throw new Exception("Exception in scan", e);
      }
   }
}
