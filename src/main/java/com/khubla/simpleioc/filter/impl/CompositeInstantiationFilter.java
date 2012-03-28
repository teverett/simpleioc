package com.khubla.simpleioc.filter.impl;

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
import java.util.ArrayList;
import java.util.List;

import com.khubla.simpleioc.IOCBeanRegistry;
import com.khubla.simpleioc.exception.IOCException;
import com.khubla.simpleioc.filter.IOCInstantiationFilter;
import com.khubla.simpleioc.xml.Bean;

/**
 * 
 * @author tome
 * 
 */
public class CompositeInstantiationFilter implements IOCInstantiationFilter {

	/**
	 * filters
	 */
	private final List<IOCInstantiationFilter> filters = new ArrayList<IOCInstantiationFilter>();

	public void add(IOCInstantiationFilter iocInstantiationFilter) {
		filters.add(iocInstantiationFilter);
	}

	public Object filter(final IOCBeanRegistry iocBeanRegistry, Object object, Bean bean) throws IOCException {
		try {
			Object o = object;
			for (int i = 0; i < filters.size(); i++) {
				o = filters.get(i).filter(iocBeanRegistry, o, bean);
			}
			return o;
		} catch (final Exception e) {
			throw new IOCException("Exception in filter", e);
		}
	}
}
