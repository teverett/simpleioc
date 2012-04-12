package com.khubla.simpleioc.proxy;

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

/**
 * 
 * @author tome
 * 
 */
public class DebugProxy implements java.lang.reflect.InvocationHandler {

	private final Object obj;

	public DebugProxy(Object obj) {
		this.obj = obj;
	}

	public Object invoke(Object proxy, Method m, Object[] args) throws Throwable {
		Object result;
		try {
			System.out.println("before method " + m.getName());
			result = m.invoke(obj, args);
		} catch (final Exception e) {
			throw new RuntimeException("unexpected invocation exception: " + e.getMessage());
		} finally {
			System.out.println("after method " + m.getName());
		}
		return result;
	}
}