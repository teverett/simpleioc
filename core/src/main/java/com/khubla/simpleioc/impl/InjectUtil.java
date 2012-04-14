package com.khubla.simpleioc.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.beanutils.BeanUtils;

import com.khubla.simpleioc.IOCBeanRegistry;
import com.khubla.simpleioc.exception.IOCException;

/**
 * @author tome
 */
public class InjectUtil {
   /**
    * IOC
    */
   private final IOCBeanRegistry iocBeanRegistry;

   /**
    * ctor
    */
   public InjectUtil(IOCBeanRegistry iocBeanRegistry) {
      this.iocBeanRegistry = iocBeanRegistry;
   }

   /**
    * inject
    */
   public Object performJSR330Injection(final Object object) throws IOCException {
      try {
         Object ret = object;
         /*
          * get the members
          */
         final Field[] fields = object.getClass().getDeclaredFields();
         if ((null != fields) && (fields.length > 0)) {
            /*
             * walk
             */
            for (int i = 0; i < fields.length; i++) {
               /*
                * get @Inject annotation on field
                */
               final Annotation annotation = fields[i].getAnnotation(Inject.class);
               if (null != annotation) {
                  ret = processInject(iocBeanRegistry, ret, fields[i], (Inject) annotation);
               }
            }
         }
         return ret;
      } catch (final Exception e) {
         throw new IOCException("Exception in performJSR330Injection", e);
      }
   }

   private Object processInject(final IOCBeanRegistry iocBeanRegistry, Object object, Field field, Inject inject) throws IOCException {
      try {
         /*
          * the default name of the bean we want is the field name
          */
         String name = field.getName();
         /*
          * check if there's an @Named
          */
         final Annotation namedAnnotation = field.getAnnotation(Named.class);
         if (null != namedAnnotation) {
            name = ((Named) namedAnnotation).value();
         }
         /*
          * get the bean
          */
         final Object injectBean = iocBeanRegistry.getBean(name);
         if (null != injectBean) {
            /*
             * set value
             */
            BeanUtils.setProperty(object, field.getName(), injectBean);
            /*
             * done
             */
            return object;
         } else {
            throw new IOCException("Unable to find bean with name '" + name + "' for injection in to field '" + field.getName() + "' of class type '"
                  + object.getClass().getName() + "'");
         }
      } catch (final Exception e) {
         throw new IOCException("Exception in processInject", e);
      }
   }
}
