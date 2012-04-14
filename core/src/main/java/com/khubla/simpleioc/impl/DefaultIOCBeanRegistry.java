package com.khubla.simpleioc.impl;

/**
 * Copyright 2012 Tom Everett Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

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
    * beans
    */
   private final Hashtable<String, Object> beanCache = new Hashtable<String, Object>();
   /**
    * bean definitions
    */
   private final Hashtable<String, Bean> beanDefinitions = new Hashtable<String, Bean>();
   /**
    * filters
    */
   private final List<IOCInstantiationFilter> beanInstantiationFilters = new ArrayList<IOCInstantiationFilter>();
   /**
    * the ThreadLocal cache
    */
   private ThreadLocal<Hashtable<String, Object>> threadLocalBeanCache = null;

   public Object getBean(String name) throws IOCException {
      try {
         if (null != name) {
            /*
             * the ret
             */
            Object ret = null;
            /*
             * check which cache....
             */
            final Bean beanDefinition = beanDefinitions.get(name);
            if (null != beanDefinition) {
               if (beanDefinition.isThreadlocal()) {
                  ret = getThreadLocalBean(name);
               } else {
                  ret = beanCache.get(name);
               }
               if (null != ret) {
                  log.info("returning cached bean '" + name);
                  /*
                   * was in cache
                   */
                  return ret;
               } else {
                  /*
                   * get bean definition
                   */
                  final Bean bean = beanDefinitions.get(name);
                  if (null != bean) {
                     /*
                      * create it
                      */
                     ret = instantiateBean(bean);
                     /*
                      * done
                      */
                     return ret;
                  } else {
                     throw new Exception("Unknown bean name '" + name + "'");
                  }
               }
            } else {
               /*
                * no such bean definition
                */
               log.info("bean '" + name + "' is not registered and cannot be found");
               return null;
            }
         } else {
            /*
             * no name supplied
             */
            return null;
         }
      } catch (final Exception e) {
         throw new IOCException("Exception in get", e);
      }
   }

   @SuppressWarnings("unchecked")
   public <T> T getBean(String name, Class<T> clazz) throws IOCException {
      final Object o = this.getBean(name);
      if (null != o) {
         if (o.getClass() == clazz) {
            return (T) o;
         }
      }
      return null;
   }

   /**
    * get a threadlocal bean
    */
   private Object getThreadLocalBean(String name) throws IOCException {
      try {
         if (null != threadLocalBeanCache) {
            final Hashtable<String, Object> hash = threadLocalBeanCache.get();
            if (null != hash) {
               return hash.get(name);
            }
         }
         return null;
      } catch (final Exception e) {
         throw new IOCException("Exception in getThreadLocalBean", e);
      }
   }

   /**
    * instantiate bean. This is 2-recursive. "instantiateBean" calls "getBean" on referenced beans, which in turn calls "instantiateBean". This allows us to create beans which are declared in the XML
    * before the beans they reference.
    */
   private Object instantiateBean(Bean bean) throws IOCException {
      try {
         log.info("instanting bean '" + bean.getName() + "' of type '" + bean.getClazz() + "'");
         /*
          * collect the arguments
          */
         Object[] arguments = null;
         /*
          * get the class
          */
         final Class<?> clazz = Class.forName(bean.getClazz().trim());
         /*
          * create
          */
         Object o = ConstructorUtils.invokeConstructor(clazz, arguments);
         /*
          * cache?
          */
         if (bean.isCache()) {
            if (bean.isThreadlocal()) {
               setThreadLocalBean(bean.getName(), o);
            } else {
               beanCache.put(bean.getName(), o);
            }
         }
         /*
          * perform jsr 330 injections
          */
         final InjectUtil injectUtil = new InjectUtil(this);
         o = injectUtil.performJSR330Injection(o);
         /*
          * filter
          */
         o = processInstantiationFilters(o, bean);
         /*
          * done
          */
         return o;
      } catch (final Exception e) {
         throw new IOCException("Exception in instantiateBean for bean '" + bean.getName() + "' of type '" + bean.getClazz() + "'", e);
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
         preInstantiateBeans();
      } catch (final Exception e) {
         throw new IOCException("Exception in load", e);
      }
   }

   /**
    * create and cache all the autocreate beans
    */
   private void preInstantiateBeans() throws IOCException {
      try {
         final Enumeration<String> enumer = beanDefinitions.keys();
         while (enumer.hasMoreElements()) {
            final String key = enumer.nextElement();
            final Bean bean = beanDefinitions.get(key);
            if (bean.isAutocreate()) {
               log.info("preinstanting bean '" + bean.getName() + "' of type '" + bean.getClazz() + "'");
               instantiateBean(bean);
            }
         }
      } catch (final Exception e) {
         throw new IOCException("Exception in preInstantiateBeans", e);
      }
   }

   /**
    * process instantiation filters
    */
   private Object processInstantiationFilters(Object object, Bean bean) throws IOCException {
      try {
         Object ret = object;
         final Iterator<IOCInstantiationFilter> iter = beanInstantiationFilters.iterator();
         while (iter.hasNext()) {
            final IOCInstantiationFilter filter = iter.next();
            /*
             * process each filter in a try-catch, in order to trap and make helpful exceptions
             */
            try {
               /*
                * drop a message
                */
               log.info("processing filter '" + filter.getClass().getName() + "' on bean '" + bean.getName() + "' of type '" + bean.getClazz() + "'");
               /*
                * filter
                */
               ret = filter.filter(this, ret, object, bean);
            } catch (final Exception e) {
               throw new Exception("Exception in filter of type '" + filter.getClass().getName() + "'", e);
            }
         }
         /*
          * done
          */
         return ret;
      } catch (final Exception e) {
         throw new IOCException("Exception in processInstantiationFilters for bean '" + bean.getName() + "' of type '" + bean.getClazz() + "'", e);
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
                   * check if we already have a bean with that name
                   */
                  if (false == beanDefinitions.containsKey(ro.name())) {
                     /*
                      * log
                      */
                     log.info("adding bean definition '" + clazz.getName() + "' with name '" + ro.name() + "'");
                     /*
                      * add it
                      */
                     final Bean bean = new Bean();
                     bean.setClazz(clazz.getName());
                     bean.setName(ro.name());
                     bean.setAutocreate(ro.autocreate());
                     bean.setCache(ro.cached());
                     bean.setThreadlocal(ro.threadlocal());
                     beanDefinitions.put(bean.getName(), bean);
                  } else {
                     /*
                      * log a message
                      */
                     log.info("Cannot add bean of type '" + clazz.getName() + "'.  Bean with name '" + ro.name() + "' already exists and is of type '" + beanDefinitions.get(ro.name()).getClazz()
                           + "'");
                     /*
                      * explode
                      */
                     throw new IOCException("Cannot add bean of type '" + clazz.getName() + "'.  Bean with name '" + ro.name() + "' already exists and is of type '"
                           + beanDefinitions.get(ro.name()).getClazz()
                           + "'");
                  }
               }
               /*
                * filters
                */
               final RegistryFilter filter = clazz.getAnnotation(RegistryFilter.class);
               if (null != filter) {
                  /*
                   * log
                   */
                  log.info("adding filter '" + clazz.getName() + "'");
                  /*
                   * create
                   */
                  final IOCInstantiationFilter iocInstantiationFilter = (IOCInstantiationFilter) ConstructorUtils.invokeConstructor(clazz, null);
                  /*
                   * add it
                   */
                  if (false == beanInstantiationFilters.contains(iocInstantiationFilter)) {
                     beanInstantiationFilters.add(iocInstantiationFilter);
                  }
               }
            }
         }
      } catch (final Exception e) {
         throw new IOCException("Exception in scanPackage", e);
      }
   }

   /**
    * set a threadlocal bean
    */
   private void setThreadLocalBean(String name, Object object) throws IOCException {
      try {
         /*
          * create cache threadlocal if needed
          */
         if (null == threadLocalBeanCache) {
            threadLocalBeanCache = new ThreadLocal<Hashtable<String, Object>>();
         }
         /*
          * create hash if needed
          */
         Hashtable<String, Object> hash = threadLocalBeanCache.get();
         if (null == hash) {
            hash = new Hashtable<String, Object>();
         }
         /*
          * set bean
          */
         hash.put(name, object);
         /*
          * set the hash
          */
         threadLocalBeanCache.set(hash);
      } catch (final Exception e) {
         throw new IOCException("Exception in setThreadLocalBean", e);
      }
   }
}
