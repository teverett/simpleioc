package com.khubla.simpleioc.proxy;

/**
 * Copyright 2012 Tom Everett Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
import net.sf.cglib.proxy.Enhancer;

import com.khubla.simpleioc.exception.IOCException;
import com.khubla.simpleioc.filter.ExampleIterceptor;
import com.khubla.simpleioc.filter.IOCInstantiationFilter;
import com.khubla.simpleioc.impl.Bean;
import com.khubla.simpleioc.impl.Profile;

/**
 * @author tome
 */
// @RegistryFilter()
public class ExampleProxyCreatingFilter implements IOCInstantiationFilter {
   public Object filter(Profile profile, Object object, Object originalObject, Bean bean) throws IOCException {
      try {
         final Enhancer e = new Enhancer();
         e.setSuperclass(object.getClass());
         e.setCallback(new ExampleIterceptor(object));
         return e.create();
      } catch (final Exception e) {
         throw new IOCException("Exception in filter", e);
      }
   }
}
