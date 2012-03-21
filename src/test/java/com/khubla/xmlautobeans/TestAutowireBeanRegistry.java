package com.khubla.xmlautobeans;

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

import junit.framework.Assert;

import org.junit.Test;

import com.khubla.xmlautobeans.impl.DefaultAutowireBeanRegistry;

/**
 * 
 * @author tome
 * 
 */
public class TestAutowireBeanRegistry {

	@Test
	public void test1() {
		try {
			final AutowireBeanRegistry autobeanRegistry = new DefaultAutowireBeanRegistry();
			autobeanRegistry.load();
			Assert.assertNotNull(autobeanRegistry);
			Assert.assertNotNull(autobeanRegistry.getBean("jcrsessionfactory"));
			File f = (File) autobeanRegistry.getBean("omfactory");
			Assert.assertNotNull(f);
			Assert.assertTrue(f.getPath().compareTo("target/repository") == 0);
		} catch (final Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
}
