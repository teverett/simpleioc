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
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.beanutils.BeanUtils;

import com.khubla.simpleioc.IOCBeanRegistry;
import com.khubla.simpleioc.exception.IOCException;
import com.khubla.simpleioc.filter.IOCInstantiationFilter;
import com.khubla.simpleioc.xml.Bean;

/**
 * 
 * @author tome
 * 
 *         A very simple implementation of a filter for @Inject
 * 
 */
public class JSR330Filter implements IOCInstantiationFilter {

	public Object filter(final IOCBeanRegistry iocBeanRegistry, final Object object, final Bean bean) throws IOCException {
		try {
			Object ret = object;
			/*
			 * get the members
			 */
			final Field[] fields = object.getClass().getDeclaredFields();
			if ((null != fields) && (fields.length > 0)) {
				/*
				 * walk
				 */
				for (int i = 0; i < fields.length; i++) {
					/*
					 * get @Inject annotation on field
					 */
					final Annotation annotation = fields[i].getAnnotation(Inject.class);
					if (null != annotation) {
						ret = processInject(iocBeanRegistry, ret, fields[i], (Inject) annotation);
					}
				}
			}
			return ret;
		} catch (final Exception e) {
			throw new IOCException("Exception in filter", e);
		}
	}

	private Object processInject(final IOCBeanRegistry iocBeanRegistry, Object object, Field field, Inject inject) throws IOCException {
		try {
			/*
			 * the default name of the bean we want is the field name
			 */
			String name = field.getName();
			/*
			 * check if there's an @Named
			 */
			final Annotation namedAnnotation = field.getAnnotation(Named.class);
			if (null != namedAnnotation) {
				name = ((Named) namedAnnotation).value();
			}
			/*
			 * get the bean
			 */
			final Object injectBean = iocBeanRegistry.getBean(name);
			if (null != injectBean) {
				/*
				 * set value
				 */
				BeanUtils.setProperty(object, field.getName(), injectBean);
				/*
				 * done
				 */
				return object;
			} else {
				throw new IOCException("Unable to find bean with name '" + name + "' for injection in to field '" + field.getName() + "' of class type '"
						+ object.getClass().getName() + "'");
			}
		} catch (final Exception e) {
			throw new IOCException("Exception in processInject", e);
		}
	}
}
