package com.khubla.simpleioc.impl;

import java.util.*;

import org.apache.commons.beanutils.*;
import org.apache.commons.logging.*;

import com.khubla.simpleioc.exception.*;
import com.khubla.simpleioc.filter.*;

/**
 * @author tome
 */
public class Profile {
	/**
	 * name
	 */
	private final String name;
	/**
	 * log
	 */
	private final Log log = LogFactory.getLog(Profile.class);
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
	/**
	 * the ThreadLocal cache
	 */
	private ThreadLocal<Hashtable<String, Object>> threadLocalBeanCache = null;

	/**
	 * ctor
	 */
	public Profile(String name) {
		this.name = name;
	}

	public void addBeanDefinition(Bean bean) throws IOCException {
		try {
			beanDefinitions.put(bean.getName(), bean);
		} catch (final Exception e) {
			throw new IOCException("Exception in addBeanDefinition", e);
		}
	}

	public void addFilter(IOCInstantiationFilter filter) throws IOCException {
		try {
			if (false == beanInstantiationFilters.contains(filter)) {
				beanInstantiationFilters.add(filter);
			}
		} catch (final Exception e) {
			throw new IOCException("Exception in addFilter", e);
		}
	}

	public Object getBean(String name) throws IOCException {
		try {
			if (null != name) {
				/*
				 * the ret
				 */
				Object ret = null;
				/*
				 * check which cache....
				 */
				final Bean beanDefinition = beanDefinitions.get(name);
				if (null != beanDefinition) {
					if (beanDefinition.isThreadlocal()) {
						ret = getThreadLocalBean(name);
					} else {
						ret = beanCache.get(name);
					}
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
					/*
					 * no such bean definition
					 */
					log.info("bean '" + name + "' is not registered and cannot be found");
					return null;
				}
			} else {
				/*
				 * no name supplied
				 */
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

	public Bean getBeanDefinition(String name) throws IOCException {
		try {
			return beanDefinitions.get(name);
		} catch (final Exception e) {
			throw new IOCException("Exception in getBeanDefinition", e);
		}
	}

	public String getName() {
		return name;
	}

	/**
	 * get a threadlocal bean
	 */
	private Object getThreadLocalBean(String name) throws IOCException {
		try {
			if (null != threadLocalBeanCache) {
				final Hashtable<String, Object> hash = threadLocalBeanCache.get();
				if (null != hash) {
					return hash.get(name);
				}
			}
			return null;
		} catch (final Exception e) {
			throw new IOCException("Exception in getThreadLocalBean", e);
		}
	}

	public boolean hasBeanDefinition(String name) throws IOCException {
		try {
			return beanDefinitions.containsKey(name);
		} catch (final Exception e) {
			throw new IOCException("Exception in hasBeanDefinition", e);
		}
	}

	/**
	 * instantiate bean. This is 2-recursive. "instantiateBean" calls "getBean" on referenced beans, which in turn calls "instantiateBean". This allows us to create beans which are declared in the XML
	 * before the beans they reference.
	 */
	private Object instantiateBean(Bean bean) throws IOCException {
		try {
			log.info("instanting bean '" + bean.getName() + "' of type '" + bean.getClassName() + "'");
			/*
			 * collect the arguments
			 */
			final Object[] arguments = null;
			/*
			 * get the class
			 */
			final Class<?> clazz = bean.getClazz();
			/*
			 * create
			 */
			Object o = ConstructorUtils.invokeConstructor(clazz, arguments);
			/*
			 * cache?
			 */
			if (bean.isCache()) {
				if (bean.isThreadlocal()) {
					setThreadLocalBean(bean.getName(), o);
				} else {
					beanCache.put(bean.getName(), o);
				}
			}
			/*
			 * perform jsr 330 injections
			 */
			final InjectUtil injectUtil = new InjectUtil(this);
			o = injectUtil.performJSR330Injection(o);
			/*
			 * filter
			 */
			o = processInstantiationFilters(o, bean);
			/*
			 * done
			 */
			return o;
		} catch (final Exception e) {
			throw new IOCException("Exception in instantiateBean for bean '" + bean.getName() + "' of type '" + bean.getClassName() + "'", e);
		}
	}

	/**
	 * create and cache all the autocreate beans
	 */
	public void preInstantiateBeans() throws IOCException {
		try {
			final Enumeration<String> enumer = beanDefinitions.keys();
			while (enumer.hasMoreElements()) {
				final String key = enumer.nextElement();
				final Bean bean = beanDefinitions.get(key);
				if (bean.isAutocreate()) {
					log.info("preinstanting bean '" + bean.getName() + "' of type '" + bean.getClassName() + "'");
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
				 * process each filter in a try-catch, in order to trap and make helpful exceptions
				 */
				try {
					/*
					 * drop a message
					 */
					log.info("processing filter '" + filter.getClass().getName() + "' on bean '" + bean.getName() + "' of type '" + bean.getClassName() + "'");
					/*
					 * filter
					 */
					ret = filter.filter(this, ret, object, bean);
				} catch (final Exception e) {
					throw new Exception("Exception in filter of type '" + filter.getClass().getName() + "'", e);
				}
			}
			/*
			 * done
			 */
			return ret;
		} catch (final Exception e) {
			throw new IOCException("Exception in processInstantiationFilters for bean '" + bean.getName() + "' of type '" + bean.getClassName() + "'", e);
		}
	}

	/**
	 * set a threadlocal bean
	 */
	private void setThreadLocalBean(String name, Object object) throws IOCException {
		try {
			/*
			 * create cache threadlocal if needed
			 */
			if (null == threadLocalBeanCache) {
				threadLocalBeanCache = new ThreadLocal<Hashtable<String, Object>>();
			}
			/*
			 * create hash if needed
			 */
			Hashtable<String, Object> hash = threadLocalBeanCache.get();
			if (null == hash) {
				hash = new Hashtable<String, Object>();
			}
			/*
			 * set bean
			 */
			hash.put(name, object);
			/*
			 * set the hash
			 */
			threadLocalBeanCache.set(hash);
		} catch (final Exception e) {
			throw new IOCException("Exception in setThreadLocalBean", e);
		}
	}
}
