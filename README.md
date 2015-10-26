Simple IOC
=============


Introduction
-------------------

SimpleIOC is a framework for configuring autowire (JSR330) beans using XML.

Configurable bean properties include

* caching (singleton)
* ThreadLocal beans
* autocreation

Defining a Bean in code:
-------------------

	@RegistryBean(name = "regBean", cached=false, autocreate=true)
	public class ExampleAnnotatedBean {
	}


Injection via JCR330
-------------------

	@Inject()
	@Named("fuddleduddle")
	private SampleInjectedInterface interface2;
	

Construction filters can be used by SimpleIOC to wrap beans in arbitrary dynamic proxies at construction time. 

A typical example is using cglib to define a dynamic proxy which can be used to implement a custom annotation such as @Transactional

Finding a bean in code
-------------------

	final IOCBeanRegistry autobeanRegistry = new DefaultIOCBeanRegistry();
	autobeanRegistry.load("beans.xml");
	/*
	 * populate the beans we need
	 */
	jcrPersistenceContext = (JcrPersistenceContext) autobeanRegistry.getBean("jcrpersistencecontext");


Defining Construction filters.  
-------------------

	public class ExampleProxyCreatingFilter implements IOCInstantiationFilter {
		public Object filter(IOCBeanRegistry iocBeanRegistry, Object object, Bean bean) throws IOCException {
			try {
				return Proxy.newProxyInstance(object.getClass().getClassLoader(), object.getClass().getInterfaces(), new DebugProxy(object));
			} catch (final Exception e) {
				throw new IOCException("Exception in filter", e);
			}
		}
	}

Addons Available  
-------------------

* simpleioc-struts.  Support for injection of JSR330 beans into Strut2 Actions
* simpleioc-testng.  Support for TestNG Testing
* simpleioc-junit.   Support for JUnit Testing

Travis Status
-------------------

<a href="https://travis-ci.org/teverett/simpleioc"><img src="https://api.travis-ci.org/teverett/simpleioc.png"></a>






