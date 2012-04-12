package com.khubla.simpleioc.addons.struts;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.beanutils.BeanUtils;

import com.khubla.simpleioc.IOCBeanRegistry;
import com.khubla.simpleioc.exception.IOCException;
import com.khubla.simpleioc.impl.DefaultIOCBeanRegistry;
import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.config.entities.ActionConfig;

/**
 * @author tome
 */
public class SimpleIOCObjectFactory extends ObjectFactory {
   /**
    * 
    */
   private static final long serialVersionUID = 1L;
   /**
    * bean registry
    */
   private static IOCBeanRegistry iocBeanRegistry = null;

   @Override
   public Object buildAction(String actionName, String namespace, ActionConfig config, Map<String, Object> extraContext) throws Exception {
      try {
         Object o = super.buildAction(actionName, namespace, config, extraContext);
         o = process(o);
         return o;
      } catch (final Exception e) {
         throw new Exception("Exception in buildAction", e);
      }
   }

   /**
    * get bean registry
    */
   private IOCBeanRegistry getBeanRegistry() {
      if (null == iocBeanRegistry) {
         iocBeanRegistry = new DefaultIOCBeanRegistry();
         iocBeanRegistry.load();
      }
      return iocBeanRegistry;
   }

   /**
    * scan for annotations and process them
    */
   private Object process(Object object) throws Exception {
      try {
         Object ret = object;
         if (null != object) {
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
                     ret = processInject(getBeanRegistry(), ret, fields[i], (Inject) annotation);
                  }
               }
            }
         }
         return ret;
      } catch (final Exception e) {
         throw new Exception("Exception in process", e);
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
