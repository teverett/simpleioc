package com.khubla.xmlautobeans.impl;

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
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.beanutils.ConstructorUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.khubla.xmlautobeans.AutowireBeanRegistry;
import com.khubla.xmlautobeans.exception.AutowireBeanRegistryException;
import com.khubla.xmlautobeans.xml.Argument;
import com.khubla.xmlautobeans.xml.AutowireBeanRegistryXMLMarshaller;
import com.khubla.xmlautobeans.xml.Bean;
import com.khubla.xmlautobeans.xml.Beans;
import com.khubla.xmlautobeans.xml.Include;

/**
 * 
 * @author tome
 * 
 */
public class DefaultAutowireBeanRegistry implements AutowireBeanRegistry {
	/**
	 * log
	 */
	private final Log log = LogFactory.getLog(DefaultAutowireBeanRegistry.class);
	/**
	 * default bean file
	 */
	private final static String DEFAULT_BEAN_FILE = "/autobeans.xml";

	/**
	 * beans
	 */
	private final Hashtable<String, Object> beanCache = new Hashtable<String, Object>();
	/**
	 * bean definitions
	 */
	private final Hashtable<String, Bean> beanDefinitions = new Hashtable<String, Bean>();

	public Object getBean(String name) throws AutowireBeanRegistryException {
		try {
			if (null != name) {
				Object ret = beanCache.get(name);
				if (null != ret) {
					/*
					 * was in cache
					 */
					return ret;
				} else {
					/*
					 * get bean definition
					 */
					final Bean bean = beanDefinitions.get(name);
					if (null != bean) {
						/*
						 * create it
						 */
						ret = instantiateBean(bean);
						/*
						 * done
						 */
						return ret;
					} else {
						throw new Exception("Unknown bean name '" + name + "'");
					}
				}
			} else {
				return null;
			}
		} catch (final Exception e) {
			throw new AutowireBeanRegistryException("Exception in get", e);
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T getBean(String name, Class<T> clazz) throws AutowireBeanRegistryException {
		final Object o = this.getBean(name);
		if (null != o) {
			if (o.getClass() == clazz) {
				return (T) o;
			}
		}
		return null;
	}

	/**
	 * instantiate bean. This is 2-recursive. "instantiateBean" calls "getBean"
	 * on referenced beans, which in turn calls "instantiateBean". This allows
	 * us to create beans which are declared in the XML before the beans they
	 * reference.
	 */
	private Object instantiateBean(Bean bean) throws AutowireBeanRegistryException {
		try {
			log.info("instanting bean '" + bean.getName() + "' of type '" + bean.getClazz() + "'");
			/*
			 * collect the arguments
			 */
			Object[] arguments = null;
			if (null != bean.getArguments()) {
				final List<Argument> allArguments = bean.getArguments().getArgument();
				arguments = new Object[allArguments.size()];
				for (int i = 0; i < allArguments.size(); i++) {
					/*
					 * single argument
					 */
					final Argument arg = allArguments.get(i);
					if (arg.isReference()) {
						/*
						 * resolve the argument by name
						 */
						final Object o = getBean(arg.getValue());
						if (null != o) {
							arguments[i] = o;
						} else {
							throw new AutowireBeanRegistryException("Unable to find argument named '" + arg.getValue() + "' for bean '" + bean.getName() + "'");
						}
					} else {
						/*
						 * argument is a value type
						 */
						arguments[i] = arg.getValue();
						/*
						 * convert
						 */
						if (null != arg.getValuetype()) {
							if (arg.getValuetype().compareTo("byte") == 0) {
								arguments[i] = Byte.parseByte((String) arguments[i]);
							} else if (arg.getValuetype().compareTo("int") == 0) {
								arguments[i] = Integer.parseInt((String) arguments[i]);
							} else if (arg.getValuetype().compareTo("long") == 0) {
								arguments[i] = Long.parseLong((String) arguments[i]);
							} else if (arg.getValuetype().compareTo("float") == 0) {
								arguments[i] = Float.parseFloat((String) arguments[i]);
							} else if (arg.getValuetype().compareTo("double") == 0) {
								arguments[i] = Double.parseDouble((String) arguments[i]);
							}
						}
					}
				}
			}
			/*
			 * get the class
			 */
			final Class<?> clazz = Class.forName(bean.getClazz().trim());
			/*
			 * create
			 */
			final Object o = ConstructorUtils.invokeConstructor(clazz, arguments);
			/*
			 * cache?
			 */
			if (bean.isCache()) {
				beanCache.put(bean.getName(), o);
			}
			/*
			 * done
			 */
			return o;
		} catch (final Exception e) {
			throw new AutowireBeanRegistryException("Exception in instantiateBean for bean '" + bean.getName() + "' of type '" + bean.getClazz() + "'", e);
		}

	}

	/**
	 * default loader
	 */
	public void load() throws AutowireBeanRegistryException {
		load(DEFAULT_BEAN_FILE);
	}

	public void load(InputStream inputStream) throws AutowireBeanRegistryException {
		try {
			/*
			 * read the xml
			 */
			final Beans beans = AutowireBeanRegistryXMLMarshaller.unmarshall(inputStream);
			if (null != beans) {
				/*
				 * beans
				 */
				final List<Bean> lst = beans.getBean();
				if ((null != lst) && (lst.size() > 0)) {
					for (int i = 0; i < lst.size(); i++) {
						final Bean bean = lst.get(i);
						beanDefinitions.put(bean.getName().trim(), bean);
					}
				}
				/*
				 * includes
				 */
				final List<Include> lst2 = beans.getInclude();
				if ((null != lst2) && (lst2.size() > 0)) {
					for (int i = 0; i < lst2.size(); i++) {
						final Include include = lst2.get(i);
						/*
						 * recurse
						 */
						this.load("/" + include.getPath());
					}
				}
			}
			/*
			 * autocreate
			 */
			preInstantiateBeans();
		} catch (final Exception e) {
			throw new AutowireBeanRegistryException("Exception in load", e);
		}
	}

	/**
	 * load from resource
	 */
	public void load(String resourceName) throws AutowireBeanRegistryException {
		try {
			log.info("Loading autobeans from " + resourceName);
			final InputStream inputStream = DefaultAutowireBeanRegistry.class.getResourceAsStream(resourceName);
			if (null != inputStream) {
				load(inputStream);
			} else {
				throw new Exception("Unable to find '" + resourceName + "'");
			}
		} catch (final Exception e) {
			throw new AutowireBeanRegistryException("Exception in load", e);
		}
	}

	/**
	 * create and cache all the autocreate beans
	 */
	private void preInstantiateBeans() throws AutowireBeanRegistryException {
		try {
			final Enumeration<String> enumer = beanDefinitions.keys();
			while (enumer.hasMoreElements()) {
				final String key = enumer.nextElement();
				final Bean bean = beanDefinitions.get(key);
				if (bean.isAutocreate()) {
					log.info("preinstanting bean '" + bean.getName() + "' of type '" + bean.getClazz() + "'");
					instantiateBean(bean);
				}
			}
		} catch (final Exception e) {
			throw new AutowireBeanRegistryException("Exception in preInstantiateBeans", e);
		}
	}
}
