package com.khubla.simpleioc.filter;

import com.khubla.simpleioc.annotation.RegistryBean;

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
/**
 * 
 * @author tome
 * 
 */
@RegistryBean(name = "regBean2")
public class ExampleBean2 {

	private String drink = "beer";

	public String getDrink() {
		return drink;
	}

	public void setDrink(String drink) {
		this.drink = drink;
	}

}