package com.khubla.simpleioc.addons.junit;

import org.junit.Before;

import com.khubla.simpleioc.IOCBeanRegistry;
import com.khubla.simpleioc.impl.DefaultIOCBeanRegistry;
import com.khubla.simpleioc.impl.InjectUtil;
import com.khubla.simpleioc.impl.Profile;

/**
 * @author tome
 */
public class SimpleIOCTestCase {
   /**
    * bean registry profile
    */
   private static Profile profile = null;

   /**
    * get bean registry profile
    */
   private Profile getProfile() {
      if (null == profile) {
         IOCBeanRegistry iocBeanRegistry = new DefaultIOCBeanRegistry();
         iocBeanRegistry.load();
         profile = iocBeanRegistry.getProfile(IOCBeanRegistry.DEFAULT_PROFILE);
      }
      return profile;
   }

   @Before
   public void processJSR330Injections() {
      final InjectUtil injectUtil = new InjectUtil(getProfile());
      injectUtil.performJSR330Injection(this);
   }
}
