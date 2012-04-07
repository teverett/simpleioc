package com.khubla.simpleioc.filter;

/**
 * Copyright 2012 Tom Everett
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
import java.lang.reflect.Method;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 * 
 * @author tome
 * 
 */
public class ExampleIterceptor implements MethodInterceptor {

	private final Object realObj;

	public ExampleIterceptor(Object realObj) {
		this.realObj = realObj;
	}

	public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
		return method.invoke(realObj, objects);
	}
}
