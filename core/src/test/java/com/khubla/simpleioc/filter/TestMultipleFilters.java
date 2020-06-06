package com.khubla.simpleioc.filter;

import org.junit.*;

import com.khubla.simpleioc.*;
import com.khubla.simpleioc.impl.*;

/**
 * @author tome
 */
public class TestMultipleFilters {
	/**
	 * in this case jsr330 is the first filter, so we are wrapping the object with the example proxy after processing jsr330 injections
	 */
	@Test
	public void injectBeforeProxy() {
		try {
			final IOCBeanRegistry autobeanRegistry = new DefaultIOCBeanRegistry();
			autobeanRegistry.load();
			final ExampleBean1 exampleBean1 = (ExampleBean1) autobeanRegistry.getBean("exampleBean1");
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
	 * in this case the example proxy is run before jsr330, so jsr330 filter is injecting into a proxy.
	 */
	@Test()
	public void proxyBeforeInject() {
		try {
			final IOCBeanRegistry autobeanRegistry = new DefaultIOCBeanRegistry();
			autobeanRegistry.load();
			final ExampleBean1 exampleBean1 = (ExampleBean1) autobeanRegistry.getBean("exampleBean1");
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
