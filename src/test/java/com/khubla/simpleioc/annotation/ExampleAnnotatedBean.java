package com.khubla.simpleioc.annotation;

/**
 * 
 * @author tome
 * 
 */
@RegistryBean(name = "regBean")
public class ExampleAnnotatedBean {

	private String field = "hi there";

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

}
