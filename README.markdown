XML AutoBean Framework
=============


Introduction
-------------------

AutoBean is a framework for configuring autowire beans using XML.

Example
-------------------

	final AutowireBeanRegistry dynabeanRegistry = new DefaultAutowireBeanRegistry();
	dynabeanRegistry.load(inputStream);
	/*
	 * populate the beans we need
	 */
	jcrPersistenceContext = (JcrPersistenceContext) dynabeanRegistry.getBean("jcrpersistencecontext");

