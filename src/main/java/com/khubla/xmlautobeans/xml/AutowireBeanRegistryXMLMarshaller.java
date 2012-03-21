package com.khubla.xmlautobeans.xml;

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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

/**
 * @author tom
 */
@SuppressWarnings("restriction")
public class AutowireBeanRegistryXMLMarshaller {
	/**
	 * marshall
	 */
	public static String marshall(Beans beans) throws Exception {
		try {
			final ByteArrayOutputStream baos = new ByteArrayOutputStream();
			marshall(beans, baos);
			return baos.toString();
		} catch (final Exception e) {
			throw new Exception("Exception in marshall", e);
		}
	}

	/**
	 * marshall
	 */
	public static void marshall(Beans beans, OutputStream outputStream) throws Exception {
		try {
			final JAXBContext jaxbContext = JAXBContext.newInstance(Beans.class);
			final Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty("jaxb.formatted.output", true);
			final SchemaFactory schemaFactory = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
			final Schema schema = schemaFactory.newSchema(AutowireBeanRegistryXMLMarshaller.class.getResource("/autobeans.xsd"));
			marshaller.setSchema(schema);
			marshaller.marshal(beans, outputStream);
		} catch (final Exception e) {
			throw new Exception("Exception in marshall", e);
		}
	}

	/**
	 * unmarshall
	 */
	public static Beans unmarshall(InputStream xml) throws Exception {
		try {
			final JAXBContext jaxbContext = JAXBContext.newInstance(Beans.class);
			final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			final SchemaFactory schemaFactory = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
			final Schema schema = schemaFactory.newSchema(AutowireBeanRegistryXMLMarshaller.class.getResource("/autobeans.xsd"));
			unmarshaller.setSchema(schema);
			return (Beans) unmarshaller.unmarshal(xml);
		} catch (final Exception e) {
			throw new Exception("Exception in unmarshall", e);
		}
	}

	/**
	 * unmarshall
	 */
	public static Beans unmarshall(String xml) throws Exception {
		try {
			return unmarshall(new ByteArrayInputStream(xml.getBytes()));
		} catch (final Exception e) {
			throw new Exception("Exception in unmarshall", e);
		}
	}
}
