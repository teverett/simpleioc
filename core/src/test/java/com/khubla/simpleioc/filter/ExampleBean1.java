package com.khubla.simpleioc.filter;

import javax.inject.Inject;
import javax.inject.Named;

import com.khubla.simpleioc.annotation.RegistryBean;

/**
 * Copyright 2012 Tom Everett Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
/**
 * @author tome
 */
@RegistryBean()
public class ExampleBean1 {
   private String field = "hi there";
   @Inject
   @Named("exampleBean2")
   private ExampleBean2 exampleBean2;

   public ExampleBean2 getExampleBean2() {
      return exampleBean2;
   }

   public String getField() {
      return field;
   }

   public void setExampleBean2(ExampleBean2 exampleBean2) {
      this.exampleBean2 = exampleBean2;
   }

   public void setField(String field) {
      this.field = field;
   }
}