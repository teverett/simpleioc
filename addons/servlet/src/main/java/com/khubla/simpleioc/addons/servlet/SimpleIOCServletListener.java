package com.khubla.simpleioc.addons.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.khubla.simpleioc.classlibrary.ClassLibrary;

/**
 * @author tome
 */
public class SimpleIOCServletListener implements ServletContextListener {
   /**
    * good to know
    */
   private final static String LIBS = "WEB-INF/classes/";

   public void contextDestroyed(ServletContextEvent servletContextEvent) {
      // TODO Auto-generated method stub
   }

   public void contextInitialized(ServletContextEvent servletContextEvent) {
      /*
       * find the libs dir
       */
      String path = servletContextEvent.getServletContext().getRealPath(LIBS);
      /*
       * set the path
       */
      ClassLibrary.setWarPath(path);
   }
}
