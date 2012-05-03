package com.khubla.simpleioc;

/**
 * Copyright 2012 Tom Everett Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
import com.khubla.simpleioc.exception.IOCException;
import com.khubla.simpleioc.impl.Profile;

/**
 * @author tome
 */
public interface IOCBeanRegistry {
   /**
    * default profile
    */
   public final static String DEFAULT_PROFILE = "default";

   /**
    * get a bean
    */
   Object getBean(String name) throws IOCException;

   /**
    * get a bean
    */
   <T> T getBean(String name, Class<T> clazz) throws IOCException;

   /**
    * get a bean
    */
   <T> T getBean(String name, Class<T> clazz, String profile) throws IOCException;

   /**
    * get a bean, a specify a profile
    */
   Object getBean(String name, String profile) throws IOCException;

   /**
    * load bean definitions
    */
   void load() throws IOCException;

   /**
    * get profile
    */
   Profile getProfile(String name) throws IOCException;
}
