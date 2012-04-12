package com.khubla.simpleioc.addons.struts;

import java.util.Map;

import com.khubla.simpleioc.IOCBeanRegistry;
import com.khubla.simpleioc.impl.DefaultIOCBeanRegistry;
import com.khubla.simpleioc.impl.InjectUtil;
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
         InjectUtil injectUtil = new InjectUtil(getBeanRegistry());
         return injectUtil.performJSR330Injection(o);
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
}
