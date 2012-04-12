package com.khubla.simpleioc.filter.impl;

/**
 * Copyright 2012 Tom Everett Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
import com.khubla.simpleioc.IOCBeanRegistry;
import com.khubla.simpleioc.exception.IOCException;
import com.khubla.simpleioc.filter.IOCInstantiationFilter;
import com.khubla.simpleioc.impl.InjectUtil;
import com.khubla.simpleioc.xml.Bean;

/**
 * @author tome A very simple implementation of a filter for @Inject
 */
public class JSR330Filter implements IOCInstantiationFilter {
   public Object filter(final IOCBeanRegistry iocBeanRegistry, final Object object, final Object originalObject, final Bean bean) throws IOCException {
      try {
         InjectUtil injectUtil = new InjectUtil(iocBeanRegistry);
         return injectUtil.performJSR330Injection(originalObject);
      } catch (final Exception e) {
         throw new IOCException("Exception in filter", e);
      }
   }
}
