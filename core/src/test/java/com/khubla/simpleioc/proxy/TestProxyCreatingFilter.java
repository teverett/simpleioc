package com.khubla.simpleioc.proxy;

import org.junit.*;

import com.khubla.simpleioc.*;
import com.khubla.simpleioc.impl.*;

/**
 * Copyright 2012 Tom Everett Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
public class TestProxyCreatingFilter {
	/**
	 * a test of a proxy creating filter
	 */
	@Test
	public void test1() {
		try {
			final IOCBeanRegistry autobeanRegistry = new DefaultIOCBeanRegistry();
			autobeanRegistry.load();
			Assert.assertNotNull(autobeanRegistry);
			final Object o = autobeanRegistry.getBean("sampleInjectObject");
			Assert.assertNotNull(o);
			// Assert.assertTrue(Proxy.isProxyClass(o.getClass()));
		} catch (final Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
}
