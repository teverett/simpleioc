package com.khubla.simpleioc.impl;

/**
 * @author tome
 */
public class Bean {
   private boolean threadlocal;
   private boolean autocreate;
   private boolean cache;

   public boolean isCache() {
      return cache;
   }

   public void setCache(boolean cache) {
      this.cache = cache;
   }

   public boolean isAutocreate() {
      return autocreate;
   }

   public void setAutocreate(boolean autocreate) {
      this.autocreate = autocreate;
   }

   private String clazz;
   private String name;

   public String getClazz() {
      return clazz;
   }

   public void setClazz(String clazz) {
      this.clazz = clazz;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public boolean isThreadlocal() {
      return threadlocal;
   }

   public void setThreadlocal(boolean threadlocal) {
      this.threadlocal = threadlocal;
   }
}
