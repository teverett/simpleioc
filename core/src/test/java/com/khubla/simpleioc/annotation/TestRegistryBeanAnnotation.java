package com.khubla.simpleioc.annotation;

import org.junit.*;

import com.khubla.simpleioc.*;
import com.khubla.simpleioc.impl.*;

/**
 * @author tome
 */
public class TestRegistryBeanAnnotation {
	@Test
	public void test1() {
		try {
			final IOCBeanRegistry autobeanRegistry = new DefaultIOCBeanRegistry();
			autobeanRegistry.load();
			Assert.assertNotNull(autobeanRegistry);
			final ExampleAnnotatedBean exampleBean = (ExampleAnnotatedBean) autobeanRegistry.getBean("regBean");
			Assert.assertNotNull(exampleBean);
			Assert.assertTrue(exampleBean.getField().compareTo("hi there") == 0);
		} catch (final Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
}
