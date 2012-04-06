package com.khubla.simpleioc.impl;

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
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.beanutils.ConstructorUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.khubla.simpleioc.IOCBeanRegistry;
import com.khubla.simpleioc.annotation.RegistryBean;
import com.khubla.simpleioc.exception.IOCException;
import com.khubla.simpleioc.filter.IOCInstantiationFilter;
import com.khubla.simpleioc.xml.Argument;
import com.khubla.simpleioc.xml.Bean;
import com.khubla.simpleioc.xml.Beans;
import com.khubla.simpleioc.xml.Filter;
import com.khubla.simpleioc.xml.IOCBeanRegistryXMLMarshaller;
import com.khubla.simpleioc.xml.Include;
import com.khubla.simpleioc.xml.Package;

/**
 * 
 * @author tome
 * 
 */
public class DefaultIOCBeanRegistry implements IOCBeanRegistry {
	/**
	 * log
	 */
	private final Log log = LogFactory.getLog(DefaultIOCBeanRegistry.class);
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

	/**
	 * filters
	 */
	private final List<IOCInstantiationFilter> beanInstantiationFilters = new ArrayList<IOCInstantiationFilter>();

	public Object getBean(String name) throws IOCException {
		try {
			if (null != name) {
				Object ret = beanCache.get(name);
				if (null != ret) {
					log.info("returning cached bean '" + name);
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
			throw new IOCException("Exception in get", e);
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T getBean(String name, Class<T> clazz) throws IOCException {
		final Object o = this.getBean(name);
		if (null != o) {
			if (o.getClass() == clazz) {
				return (T) o;
			}
		}
		return null;
	}

	/**
	 * get ctor arguments for bean
	 */
	private Object[] getBeanConstructorArguments(List<Argument> allArguments) throws IOCException {
		try {
			Object[] arguments = null;
			if (null != allArguments) {
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
							throw new IOCException("Unable to find argument named '" + arg.getValue());
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
			 * done
			 */
			return arguments;
		} catch (final Exception e) {
			throw new IOCException("Exception in getBeanConstructorArguments", e);
		}
	}

	/**
	 * instantiate bean. This is 2-recursive. "instantiateBean" calls "getBean"
	 * on referenced beans, which in turn calls "instantiateBean". This allows
	 * us to create beans which are declared in the XML before the beans they
	 * reference.
	 */
	private Object instantiateBean(Bean bean) throws IOCException {
		try {
			log.info("instanting bean '" + bean.getName() + "' of type '" + bean.getClazz() + "'");
			/*
			 * collect the arguments
			 */
			Object[] arguments = null;
			if (null != bean.getArguments()) {
				arguments = getBeanConstructorArguments(bean.getArguments().getArgument());
			}
			/*
			 * get the class
			 */
			final Class<?> clazz = Class.forName(bean.getClazz().trim());
			/*
			 * create
			 */
			Object o = ConstructorUtils.invokeConstructor(clazz, arguments);
			/*
			 * cache?
			 */
			if (bean.isCache()) {
				beanCache.put(bean.getName(), o);
			}
			/*
			 * filter
			 */
			o = processInstantiationFilters(o, bean);
			/*
			 * done
			 */
			return o;
		} catch (final Exception e) {
			throw new IOCException("Exception in instantiateBean for bean '" + bean.getName() + "' of type '" + bean.getClazz() + "'", e);
		}

	}

	/**
	 * default loader
	 */
	public void load() throws IOCException {
		load(DEFAULT_BEAN_FILE);
	}

	public void load(InputStream inputStream) throws IOCException {
		try {
			/*
			 * read the xml
			 */
			final Beans beans = IOCBeanRegistryXMLMarshaller.unmarshall(inputStream);
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
				 * add bean definitions for classes annotated with @RegistryBean
				 */
				final List<Package> lst4 = beans.getPackage();
				if ((null != lst4) && (lst4.size() > 0)) {
					for (int i = 0; i < lst4.size(); i++) {
						final Package pkg = lst4.get(i);
						scanPackage(pkg.getName());
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
				/*
				 * filters
				 */
				final List<Filter> lst3 = beans.getFilter();
				if ((null != lst3) && (lst3.size() > 0)) {
					for (int i = 0; i < lst3.size(); i++) {
						final Filter filter = lst3.get(i);
						/*
						 * get the class
						 */
						final Class<?> clazz = Class.forName(filter.getClazz().trim());
						/*
						 * create
						 */
						final IOCInstantiationFilter iocInstantiationFilter = (IOCInstantiationFilter) ConstructorUtils.invokeConstructor(clazz, null);
						/*
						 * add
						 */
						beanInstantiationFilters.add(iocInstantiationFilter);
					}
				}

			}

			/*
			 * autocreate
			 */
			preInstantiateBeans();
		} catch (final Exception e) {
			throw new IOCException("Exception in load", e);
		}
	}

	/**
	 * load from resource
	 */
	public void load(String resourceName) throws IOCException {
		try {
			log.info("Loading autobeans from " + resourceName);
			final InputStream inputStream = DefaultIOCBeanRegistry.class.getResourceAsStream(resourceName);
			if (null != inputStream) {
				load(inputStream);
			} else {
				throw new Exception("Unable to find '" + resourceName + "'");
			}
		} catch (final Exception e) {
			throw new IOCException("Exception in load", e);
		}
	}

	/**
	 * create and cache all the autocreate beans
	 */
	private void preInstantiateBeans() throws IOCException {
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
			throw new IOCException("Exception in preInstantiateBeans", e);
		}
	}

	/**
	 * process instantiation filters
	 */
	private Object processInstantiationFilters(Object object, Bean bean) throws IOCException {
		try {
			Object ret = object;
			final Iterator<IOCInstantiationFilter> iter = beanInstantiationFilters.iterator();
			while (iter.hasNext()) {
				final IOCInstantiationFilter filter = iter.next();
				/*
				 * process each filter in a try-catch, in order to trap and make
				 * helpful exceptions
				 */
				try {
					ret = filter.filter(this, ret, bean);
				} catch (final Exception e) {
					throw new Exception("Exception in filter of type '" + filter.getClass().getName() + "'", e);
				}
			}
			/*
			 * done
			 */
			return ret;
		} catch (final Exception e) {
			throw new IOCException("Exception in processInstantiationFilters for bean '" + bean.getName() + "' of type '" + bean.getClazz() + "'", e);
		}
	}

	/**
	 * scan the package for annotated registry objects. this adds bean
	 * definitions for each bean found.
	 */
	private void scanPackage(String pkg) throws IOCException {
		try {
			if (null != pkg) {
				/*
				 * get all classes
				 */
				final Class<?>[] classes = PackageUtil.getClasses(pkg);
				/*
				 * walk classes
				 */
				if (null != classes) {
					for (int i = 0; i < classes.length; i++) {
						/*
						 * class
						 */
						final Class<?> clazz = classes[i];
						/*
						 * marked with the annotation?
						 */
						final RegistryBean ro = clazz.getAnnotation(RegistryBean.class);
						if (null != ro) {
							/*
							 * add it
							 */
							final Bean bean = new Bean();
							bean.setClazz(clazz.getName());
							bean.setName(ro.name());
							bean.setAutocreate(ro.autocreate());
							bean.setCache(ro.cached());
							beanDefinitions.put(bean.getName(), bean);
						}
					}
				}
			}
		} catch (final Exception e) {
			throw new IOCException("Exception in scanPackage", e);
		}
	}
}
