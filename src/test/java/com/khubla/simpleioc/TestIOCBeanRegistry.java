package com.khubla.simpleioc;

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

import java.io.File;
import java.io.InputStream;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.khubla.simpleioc.impl.DefaultIOCBeanRegistry;

/**
 * 
 * @author tome
 * 
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
			Assert.assertNotNull(autobeanRegistry.getBean("jcrsessionfactory"));
			final File f = (File) autobeanRegistry.getBean("omfactory");
			Assert.assertNotNull(f);
			Assert.assertTrue(f.getPath().compareTo("target/repository") == 0);
		} catch (final Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	/**
	 * a test of the include functionality
	 */
	@Test
	public void test2() {
		try {
			final IOCBeanRegistry autobeanRegistry = new DefaultIOCBeanRegistry();
			autobeanRegistry.load("/autobeans2.xml");
			Assert.assertNotNull(autobeanRegistry);
			Assert.assertNotNull(autobeanRegistry.getBean("jcrsessionfactory"));
			final File f = (File) autobeanRegistry.getBean("omfactory");
			Assert.assertNotNull(f);
			Assert.assertTrue(f.getPath().compareTo("target/repository") == 0);
		} catch (final Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	/**
	 * a test which checks types
	 */
	@Test
	public void test3() {
		try {
			final IOCBeanRegistry autobeanRegistry = new DefaultIOCBeanRegistry();
			autobeanRegistry.load();
			Assert.assertNotNull(autobeanRegistry);
			final File f = autobeanRegistry.getBean("jcrsessionfactory", File.class);
			Assert.assertNotNull(f);
			final InputStream is = autobeanRegistry.getBean("jcrsessionfactory", InputStream.class);
			Assert.assertNull(is);
		} catch (final Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
}
