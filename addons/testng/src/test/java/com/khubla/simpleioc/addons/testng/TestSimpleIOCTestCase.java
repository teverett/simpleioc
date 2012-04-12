package com.khubla.simpleioc.addons.testng;

import javax.inject.Inject;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author tome
 */
public class TestSimpleIOCTestCase extends SimpleIOCTestCase {
   @Inject
   private ExampleInjectableObject exampleInjectableObject;

   public ExampleInjectableObject getExampleInjectableObject() {
      return exampleInjectableObject;
   }

   public void setExampleInjectableObject(ExampleInjectableObject exampleInjectableObject) {
      this.exampleInjectableObject = exampleInjectableObject;
   }

   @Test
   public void test1() {
      try {
         Assert.assertNotNull(exampleInjectableObject);
      } catch (final Exception e) {
         e.printStackTrace();
         Assert.fail();
      }
   }
}
