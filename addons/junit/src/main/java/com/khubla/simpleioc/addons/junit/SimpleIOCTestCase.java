package com.khubla.simpleioc.addons.junit;

import org.junit.*;

import com.khubla.simpleioc.*;
import com.khubla.simpleioc.impl.*;

/**
 * @author tome
 */
public class SimpleIOCTestCase {
	/**
	 * bean registry profile
	 */
	private static Profile profile = null;

	/**
	 * get bean registry profile
	 */
	private Profile getProfile() {
		if (null == profile) {
			final IOCBeanRegistry iocBeanRegistry = new DefaultIOCBeanRegistry();
			iocBeanRegistry.load();
			profile = iocBeanRegistry.getProfile(IOCBeanRegistry.DEFAULT_PROFILE);
		}
		return profile;
	}

	@Before
	public void processJSR330Injections() {
		final InjectUtil injectUtil = new InjectUtil(getProfile());
		injectUtil.performJSR330Injection(this);
	}
}
