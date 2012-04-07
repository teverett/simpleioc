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
import org.testng.Assert;
import org.testng.annotations.Test;

import com.khubla.simpleioc.IOCBeanRegistry;
import com.khubla.simpleioc.impl.DefaultIOCBeanRegistry;

/**
 * 
 * @author tome
 * 
 */
public class TestMultipleFilters {
	/**
	 * in this case jsr330 is the first filter, so we are wrapping the object
	 * with the example proxy after processing jsr330 injections
	 */
	@Test
	public void test1() {
		try {
			final IOCBeanRegistry autobeanRegistry = new DefaultIOCBeanRegistry();
			autobeanRegistry.load("/multiplefilters1.xml");
			final ExampleBean1 exampleBean1 = (ExampleBean1) autobeanRegistry.getBean("regBean");
			Assert.assertNotNull(exampleBean1);
			final ExampleBean2 bean2 = exampleBean1.getExampleBean2();
			Assert.assertNotNull(bean2);
			Assert.assertTrue(bean2.getDrink().compareTo("beer") == 0);
		} catch (final Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	/**
	 * in this case the example proxy is run before jsr330, so jsr330 filter is
	 * injecting into a proxy.
	 */
	@Test(enabled = false)
	public void test2() {
		try {
			final IOCBeanRegistry autobeanRegistry = new DefaultIOCBeanRegistry();
			autobeanRegistry.load("/multiplefilters2.xml");
			final ExampleBean1 exampleBean1 = (ExampleBean1) autobeanRegistry.getBean("regBean");
			Assert.assertNotNull(exampleBean1);
			final ExampleBean2 bean2 = exampleBean1.getExampleBean2();
			Assert.assertNotNull(bean2);
			Assert.assertTrue(bean2.getDrink().compareTo("beer") == 0);
		} catch (final Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
}
