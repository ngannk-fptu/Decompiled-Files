/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.LoggerFactory
 */
package org.apache.tika.config;

import org.slf4j.LoggerFactory;

public interface LoadErrorHandler {
    public static final LoadErrorHandler IGNORE = new LoadErrorHandler(){

        @Override
        public void handleLoadError(String classname, Throwable throwable) {
        }

        public String toString() {
            return "IGNORE";
        }
    };
    public static final LoadErrorHandler WARN = new LoadErrorHandler(){

        @Override
        public void handleLoadError(String classname, Throwable throwable) {
            LoggerFactory.getLogger((String)classname).warn("Unable to load {}", (Object)classname, (Object)throwable);
        }

        public String toString() {
            return "WARN";
        }
    };
    public static final LoadErrorHandler THROW = new LoadErrorHandler(){

        @Override
        public void handleLoadError(String classname, Throwable throwable) {
            throw new RuntimeException("Unable to load " + classname, throwable);
        }

        public String toString() {
            return "THROW";
        }
    };

    public void handleLoadError(String var1, Throwable var2);
}

