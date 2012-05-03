package com.khubla.simpleioc.annotation;

/**
 * Copyright 2012 Tom Everett Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.khubla.simpleioc.IOCBeanRegistry;

/**
 * @author tome
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface RegistryBean {
   /**
    * bean is automatically created by the IOC Registry?
    */
   boolean autocreate() default false;

   /**
    * bean is cached by the IOC Registry? (use this to implement singletons)
    */
   boolean cached() default false;

   /**
    * name of the bean. if not specified, the class name with the first letter lower case will be used
    */
   String name() default "";

   /**
    * profiles that this bean is a member of
    */
   String[] profiles() default { IOCBeanRegistry.DEFAULT_PROFILE };

   /**
    * bean is cached thread-local?
    */
   boolean threadlocal() default false;

   /**
    * global (exists in all profiles)
    */
   boolean global() default false;
}
