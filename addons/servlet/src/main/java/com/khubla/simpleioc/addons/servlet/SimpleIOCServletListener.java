package com.khubla.simpleioc.addons.servlet;

import javax.servlet.*;

import com.khubla.simpleioc.classlibrary.*;

/**
 * @author tome
 */
public class SimpleIOCServletListener implements ServletContextListener {
	/**
	 * good to know
	 */
	private final static String LIBS = "WEB-INF/classes/";

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		// TODO Auto-generated method stub
	}

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		/*
		 * find the libs dir
		 */
		final String path = servletContextEvent.getServletContext().getRealPath(LIBS);
		/*
		 * set the path
		 */
		ClassLibrary.setWarPath(path);
	}
}
