package com.khubla.simpleioc;

import org.junit.*;

import com.khubla.simpleioc.filter.*;
import com.khubla.simpleioc.impl.*;

/**
 * @author tome
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
			Assert.assertNotNull(autobeanRegistry.getBean("exampleBean1"));
			final ExampleBean1 eb1 = (ExampleBean1) autobeanRegistry.getBean("exampleBean1");
			Assert.assertNotNull(eb1);
			Assert.assertTrue(eb1.getField().compareTo("hi there") == 0);
		} catch (final Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
}
