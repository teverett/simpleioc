package com.khubla.simpleioc.impl;

/**
 * Copyright 2012 Tom Everett Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author tome
 */
public class PackageUtil {
   private final static String CLASS = ".class";

   public static Class<?>[] getClasses() throws ClassNotFoundException, IOException {
      final List<Class<?>> lst = new ArrayList<Class<?>>();
      final String pathSep = System.getProperty("path.separator");
      final String list = System.getProperty("java.class.path");
      for (final String path : list.split(pathSep)) {
         final File file = new File(path);
         if (file.isDirectory()) {
            lst.addAll(processDirectory(file, ""));
         }
      }
      return lst.toArray(new Class[lst.size()]);
   }

   private static List<Class<?>> processDirectory(File directory, String concatenatedName) throws ClassNotFoundException {
      final List<Class<?>> classes = new ArrayList<Class<?>>();
      final File[] files = directory.listFiles();
      for (final File file : files) {
         if (file.isDirectory()) {
            classes.addAll(processDirectory(file, concatenatedName + file.getName() + "."));
         } else if (file.getName().endsWith(CLASS)) {
            final String classname = concatenatedName + file.getName().substring(0, file.getName().length() - CLASS.length());
            final Class<?> clazz = Class.forName(classname);
            classes.add(clazz);
         }
      }
      return classes;
   }
}
