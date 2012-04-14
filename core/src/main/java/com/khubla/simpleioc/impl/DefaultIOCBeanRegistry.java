package com.khubla.simpleioc.impl;

/**
 * Copyright 2012 Tom Everett Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
import java.util.Enumeration;
import java.util.Hashtable;

import org.apache.commons.beanutils.ConstructorUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.khubla.simpleioc.IOCBeanRegistry;
import com.khubla.simpleioc.annotation.RegistryBean;
import com.khubla.simpleioc.annotation.RegistryFilter;
import com.khubla.simpleioc.exception.IOCException;
import com.khubla.simpleioc.filter.IOCInstantiationFilter;

/**
 * @author tome
 */
public class DefaultIOCBeanRegistry implements IOCBeanRegistry {
   /**
    * log
    */
   private final Log log = LogFactory.getLog(DefaultIOCBeanRegistry.class);
   /**
    * profiles
    */
   private final Hashtable<String, Profile> profiles = new Hashtable<String, Profile>();

   public Object getBean(String name) throws IOCException {
      try {
         return this.getBean(name, DEFAULT_PROFILE);
      } catch (final Exception e) {
         throw new IOCException("Exception in getBean", e);
      }
   }

   public <T> T getBean(String name, Class<T> clazz) throws IOCException {
      try {
         return this.getBean(name, clazz, DEFAULT_PROFILE);
      } catch (final Exception e) {
         throw new IOCException("Exception in getBean", e);
      }
   }

   public <T> T getBean(String name, Class<T> clazz, String profile) throws IOCException {
      try {
         final Profile p = profiles.get(profile);
         if (null != p) {
            return p.getBean(name, clazz);
         }
         return null;
      } catch (final Exception e) {
         throw new IOCException("Exception in getBean", e);
      }
   }

   public Object getBean(String name, String profile) throws IOCException {
      try {
         final Profile p = profiles.get(profile);
         if (null != p) {
            return p.getBean(name);
         }
         return null;
      } catch (final Exception e) {
         throw new IOCException("Exception in getBean", e);
      }
   }

   /**
    * default loader
    */
   public void load() throws IOCException {
      try {
         /*
          * perform scan
          */
         scanPackages();
         /*
          * autocreate
          */
         final Enumeration<Profile> enumer = profiles.elements();
         while (enumer.hasMoreElements()) {
            enumer.nextElement().preInstantiateBeans();
         }
      } catch (final Exception e) {
         throw new IOCException("Exception in load", e);
      }
   }

   /**
    * scan the packages for annotated registry objects. this adds bean definitions for each bean found.
    */
   private void scanPackages() throws IOCException {
      try {
         /*
          * get all classes
          */
         final Class<?>[] classes = PackageUtil.getClasses();
         /*
          * walk classes
          */
         if (null != classes) {
            /*
             * message
             */
            log.info("scanning '" + classes.length + "' classes");
            for (int i = 0; i < classes.length; i++) {
               /*
                * class
                */
               final Class<?> clazz = classes[i];
               /*
                * marked with the annotation?
                */
               final RegistryBean ro = clazz.getAnnotation(RegistryBean.class);
               if (null != ro) {
                  /*
                   * bean name
                   */
                  String beanName = ro.name();
                  if (beanName.length() == 0) {
                     /*
                      * use the class name
                      */
                     beanName = clazz.getSimpleName();
                     beanName = Character.toLowerCase(beanName.charAt(0)) + beanName.substring(1);
                  }
                  /*
                   * iterate the profiles
                   */
                  for (final String profileName : ro.profiles()) {
                     /*
                      * check if we already have a bean with that name
                      */
                     Profile profile = profiles.get(profileName);
                     if ((null != profile) && (profile.hasBeanDefinition(beanName))) {
                        /*
                         * log a message
                         */
                        log.info("Cannot add bean of type '" + clazz.getName() + "'.  Bean with name '" + beanName + "' already exists and is of type '"
                              + profile.getBeanDefinition(beanName).getClassName()
                              + "'");
                        /*
                         * explode
                         */
                        throw new IOCException("Cannot add bean of type '" + clazz.getName() + "'.  Bean with name '" + beanName + "' already exists and is of type '"
                              + profile.getBeanDefinition(beanName).getClassName()
                              + "'");
                     }
                     else {
                        /*
                         * log
                         */
                        log.info("adding bean definition '" + clazz.getName() + "' with name '" + beanName + "' to profile '" + profileName + "'");
                        /*
                         * add it
                         */
                        final Bean bean = new Bean();
                        bean.setClazz(clazz);
                        bean.setClassName(clazz.getName());
                        bean.setName(beanName);
                        bean.setProfile(profileName);
                        bean.setAutocreate(ro.autocreate());
                        bean.setCache(ro.cached());
                        bean.setThreadlocal(ro.threadlocal());
                        if (null == profile) {
                           profile = new Profile(profileName, this);
                           profiles.put(profileName, profile);
                        }
                        profile.addBeanDefinition(bean);
                     }
                  }
               }
               /*
                * filters
                */
               final RegistryFilter filter = clazz.getAnnotation(RegistryFilter.class);
               if (null != filter) {
                  /*
                   * create
                   */
                  final IOCInstantiationFilter iocInstantiationFilter = (IOCInstantiationFilter) ConstructorUtils.invokeConstructor(clazz, null);
                  /*
                   * iterate the profiles
                   */
                  for (final String profileName : filter.profiles()) {
                     /*
                      * log
                      */
                     log.info("adding filter '" + clazz.getName() + "' to profile '" + profileName + "'");
                     /*
                      * add it
                      */
                     Profile profile = profiles.get(profileName);
                     if (null == profile) {
                        profile = new Profile(profileName, this);
                        profiles.put(profileName, profile);
                     }
                     profile.addFilter(iocInstantiationFilter);
                  }
               }
            }
         }
      } catch (final Exception e) {
         throw new IOCException("Exception in scanPackage", e);
      }
   }
}
