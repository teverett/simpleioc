package com.khubla.simpleioc.classlibrary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;

import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;

/**
 * @author tome
 */
public class AnnotationScanner {
   @SuppressWarnings("unchecked")
   public static List<Class<?>> getAnnotatedClasses(final Hashtable<String, ClassNode> classes, Class<?> annotation) throws Exception {
      try {
         /*
          * the ret
          */
         final ArrayList<Class<?>> ret = new ArrayList<Class<?>>();
         /*
          * walk classnodes
          */
         final Collection<ClassNode> collection = classes.values();
         for (final ClassNode classNode : collection) {
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
                   * instantiate the literal
                   */
                  if (annotationClassName.compareTo(annotation.getName()) == 0) {
                     ret.add(Class.forName(classNode.name.replaceAll("/", ".")));
                  }
               }
            }
         }
         return ret;
      } catch (final Exception e) {
         throw new Exception("Exception in getAnnotatedClasses", e);
      }
   }
}
