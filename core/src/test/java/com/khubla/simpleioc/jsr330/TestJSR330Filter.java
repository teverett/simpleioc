package com.khubla.simpleioc.jsr330;

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
public class TestJSR330Filter {
	/**
	 * a test of the JSR 330 inject
	 */
	@Test
	public void test1() {
		try {
			final IOCBeanRegistry autobeanRegistry = new DefaultIOCBeanRegistry();
			autobeanRegistry.load("/injectbeans.xml");
			Assert.assertNotNull(autobeanRegistry);
			Assert.assertNotNull(autobeanRegistry.getBean("sampleInjectObject"));
			final SampleInjectObject sio = (SampleInjectObject) autobeanRegistry.getBean("sampleInjectObject");
			Assert.assertNotNull(sio);
			Assert.assertNotNull(sio.getSampleInjectedInterface());
			Assert.assertNotNull(sio.getInterface2());
		} catch (final Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
}
