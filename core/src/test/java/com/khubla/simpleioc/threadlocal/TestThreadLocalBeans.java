package com.khubla.simpleioc.threadlocal;

import org.junit.*;

import com.khubla.simpleioc.*;
import com.khubla.simpleioc.impl.*;

/**
 * @author tome
 */
public class TestThreadLocalBeans {
	/**
	 * a very basic test
	 */
	@Test
	public void test1() {
		try {
			final IOCBeanRegistry autobeanRegistry = new DefaultIOCBeanRegistry();
			autobeanRegistry.load();
			Assert.assertNotNull(autobeanRegistry);
			Object o = autobeanRegistry.getBean("sampleInjectObject");
			Assert.assertNotNull(o);
			o = autobeanRegistry.getBean("sampleInjectObject");
			Assert.assertNotNull(o);
		} catch (final Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
}
