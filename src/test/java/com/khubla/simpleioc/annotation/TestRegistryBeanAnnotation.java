package com.khubla.simpleioc.annotation;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.khubla.simpleioc.IOCBeanRegistry;
import com.khubla.simpleioc.impl.DefaultIOCBeanRegistry;

/**
 * 
 * @author tome
 * 
 */
public class TestRegistryBeanAnnotation {

	@Test
	public void test1() {
		try {
			final IOCBeanRegistry autobeanRegistry = new DefaultIOCBeanRegistry();
			autobeanRegistry.load("/annotationbeans.xml");
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
