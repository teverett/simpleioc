package com.khubla.simpleioc.addons.testng;

import com.khubla.simpleioc.annotation.RegistryBean;

/**
 * @author tome
 */
@RegistryBean(name = "exampleInjectableObject")
public class ExampleInjectableObject {
   private String xx;

   public String getXx() {
      return xx;
   }

   public void setXx(String xx) {
      this.xx = xx;
   }
}
