package com.khubla.simpleioc.addons.struts;

import java.util.*;

import com.khubla.simpleioc.*;
import com.khubla.simpleioc.exception.*;
import com.khubla.simpleioc.impl.*;
import com.opensymphony.xwork2.*;
import com.opensymphony.xwork2.config.entities.*;

/**
 * @author tome
 */
public class SimpleIOCObjectFactory extends ObjectFactory {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * bean registry profile
	 */
	private static Profile profile = null;

	@Override
	public Object buildAction(String actionName, String namespace, ActionConfig config, Map<String, Object> extraContext) throws Exception {
		try {
			final Object o = super.buildAction(actionName, namespace, config, extraContext);
			final InjectUtil injectUtil = new InjectUtil(getProfile());
			return injectUtil.performJSR330Injection(o);
		} catch (final Exception e) {
			throw new IOCException("Exception in buildAction", e);
		}
	}

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
}
