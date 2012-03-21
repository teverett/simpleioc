package com.khubla.xmlautobeans;

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
import java.io.InputStream;

import com.khubla.xmlautobeans.exception.AutowireBeanRegistryException;

/**
 * 
 * @author tome
 * 
 */
public interface AutowireBeanRegistry {

	/**
	 * get a bean
	 */
	Object getBean(String name) throws AutowireBeanRegistryException;

	/**
	 * load
	 */
	void load(InputStream inputStream) throws AutowireBeanRegistryException;

	/**
	 * load from default class path resource "autobeans.xml"
	 */
	void load() throws AutowireBeanRegistryException;
}
