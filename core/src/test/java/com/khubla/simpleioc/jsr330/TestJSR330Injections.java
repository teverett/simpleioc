package com.khubla.simpleioc.jsr330;

import org.junit.*;

import com.khubla.simpleioc.*;
import com.khubla.simpleioc.impl.*;

/**
 * @author tome
 */
public class TestJSR330Injections {
	/**
	 * a test of the JSR 330 inject
	 */
	@Test
	public void test1() {
		try {
			final IOCBeanRegistry autobeanRegistry = new DefaultIOCBeanRegistry();
			autobeanRegistry.load();
			Assert.assertNotNull(autobeanRegistry);
			Assert.assertNotNull(autobeanRegistry.getBean("sampleInjectObject"));
			final SampleInjectObject sio = (SampleInjectObject) autobeanRegistry.getBean("sampleInjectObject");
			Assert.assertNotNull(sio);
			Assert.assertNotNull(sio.getSampleInjectedObject());
			Assert.assertNotNull(sio.getInterface2());
		} catch (final Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
}
