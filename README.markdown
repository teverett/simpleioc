Simple IOC
=============


Introduction
-------------------

SimpleIOC is a framework for configuring autowire (JSR330) beans using XML.

Example
-------------------

	final IOCBeanRegistry autobeanRegistry = new DefaultIOCBeanRegistry();
	autobeanRegistry.load(inputStream);
	/*
	 * populate the beans we need
	 */
	jcrPersistenceContext = (JcrPersistenceContext) autobeanRegistry.getBean("jcrpersistencecontext");

