package com.khubla.simpleioc.impl;

/**
 * Copyright 2012 Tom Everett Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.beanutils.ConstructorUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.khubla.simpleioc.IOCBeanRegistry;
import com.khubla.simpleioc.annotation.RegistryBean;
import com.khubla.simpleioc.annotation.RegistryFilter;
import com.khubla.simpleioc.classlibrary.ClassLibrary;
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
         Object ret = this.getBean(name, DEFAULT_PROFILE);
         if (null == ret) {
            ret = this.getBean(name, ALL_PROFILES);
         }
         return ret;
      } catch (final Exception e) {
         throw new IOCException("Exception in getBean '" + name + "'", e);
      }
   }

   public <T> T getBean(String name, Class<T> clazz) throws IOCException {
      try {
         T ret = this.getBean(name, clazz, DEFAULT_PROFILE);
         if (null == ret) {
            ret = this.getBean(name, clazz, ALL_PROFILES);
         }
         return ret;
      } catch (final Exception e) {
         throw new IOCException("Exception in getBean '" + name + "'", e);
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
         throw new IOCException("Exception in getBean '" + name + " in profile '" + profile + "'", e);
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
         throw new IOCException("Exception in getBean '" + name + " in profile '" + profile + "'", e);
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
          * message
          */
         final List<Class<?>> beanClasses = ClassLibrary.getInstance().getClasses();
         for (final Class<?> cls : beanClasses) {
            final RegistryBean registryBeanAnnotation = cls.getAnnotation(RegistryBean.class);
            if (null != registryBeanAnnotation) {
               /*
                * bean name
                */
               String beanName = registryBeanAnnotation.name();
               if (beanName.length() == 0) {
                  /*
                   * use the class name
                   */
                  beanName = cls.getSimpleName();
                  beanName = Character.toLowerCase(beanName.charAt(0)) + beanName.substring(1);
               }
               /*
                * iterate the profiles
                */
               for (final String profileName : registryBeanAnnotation.profiles()) {
                  /*
                   * check if we already have a bean with that name
                   */
                  Profile profile = profiles.get(profileName);
                  if ((null != profile) && (profile.hasBeanDefinition(beanName))) {
                     /*
                      * log a message
                      */
                     log.info("Cannot add bean of type '" + cls.getName() + "'.  Bean with name '" + beanName + "' already exists and is of type '"
                           + profile.getBeanDefinition(beanName).getClassName()
                           + "'");
                     /*
                      * explode
                      */
                     throw new IOCException("Cannot add bean of type '" + cls.getName() + "'.  Bean with name '" + beanName + "' already exists and is of type '"
                           + profile.getBeanDefinition(beanName).getClassName()
                           + "'");
                  }
                  else {
                     /*
                      * log
                      */
                     log.info("adding bean definition '" + cls.getName() + "' with name '" + beanName + "' to profile '" + profileName + "'");
                     /*
                      * add it
                      */
                     final Bean bean = new Bean();
                     bean.setClazz(cls);
                     bean.setClassName(cls.getName());
                     bean.setName(beanName);
                     bean.setProfile(profileName);
                     bean.setAutocreate(registryBeanAnnotation.autocreate());
                     bean.setCache(registryBeanAnnotation.cached());
                     bean.setThreadlocal(registryBeanAnnotation.threadlocal());
                     if (null == profile) {
                        profile = new Profile(profileName, this);
                        profiles.put(profileName, profile);
                     }
                     profile.addBeanDefinition(bean);
                  }
               }
            }
         }
         /*
          * filters
          */
         for (final Class<?> cls : beanClasses) {
            final RegistryFilter registryFilterAnnotation = cls.getAnnotation(RegistryFilter.class);
            if (null != registryFilterAnnotation) {
               /*
                * create
                */
               final IOCInstantiationFilter iocInstantiationFilter = (IOCInstantiationFilter) ConstructorUtils.invokeConstructor(cls, null);
               /*
                * iterate the profiles
                */
               for (final String profileName : registryFilterAnnotation.profiles()) {
                  /*
                   * log
                   */
                  log.info("adding filter '" + cls.getName() + "' to profile '" + profileName + "'");
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
      } catch (final Exception e) {
         throw new IOCException("Exception in scanPackage", e);
      }
   }
}
