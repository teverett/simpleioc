package com.khubla.simpleioc.addons.testng;

import org.testng.annotations.BeforeClass;

import com.khubla.simpleioc.IOCBeanRegistry;
import com.khubla.simpleioc.impl.DefaultIOCBeanRegistry;
import com.khubla.simpleioc.impl.InjectUtil;

/**
 * @author tome
 */
public class SimpleIOCTestCase {
   /**
    * bean registry
    */
   private static IOCBeanRegistry iocBeanRegistry = null;

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

   @BeforeClass
   public void processJSR330Injections() {
      final InjectUtil injectUtil = new InjectUtil(getBeanRegistry());
      injectUtil.performJSR330Injection(this);
   }
}
