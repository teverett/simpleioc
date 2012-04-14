package com.khubla.simpleioc;

/**
 * Copyright 2012 Tom Everett Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
import org.testng.Assert;
import org.testng.annotations.Test;

import com.khubla.simpleioc.filter.ExampleBean1;
import com.khubla.simpleioc.impl.DefaultIOCBeanRegistry;

/**
 * @author tome
 */
public class TestIOCBeanRegistry {
   /**
    * a very basic test
    */
   @Test
   public void test1() {
      try {
         final IOCBeanRegistry autobeanRegistry = new DefaultIOCBeanRegistry();
         autobeanRegistry.load();
         Assert.assertNotNull(autobeanRegistry);
         Assert.assertNotNull(autobeanRegistry.getBean("exampleBean1"));
         final ExampleBean1 eb1 = (ExampleBean1) autobeanRegistry.getBean("exampleBean1");
         Assert.assertNotNull(eb1);
         Assert.assertTrue(eb1.getField().compareTo("hi there") == 0);
      } catch (final Exception e) {
         e.printStackTrace();
         Assert.fail();
      }
   }
}
