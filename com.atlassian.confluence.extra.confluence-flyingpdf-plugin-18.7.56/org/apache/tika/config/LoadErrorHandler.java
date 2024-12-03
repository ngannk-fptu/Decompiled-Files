/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.config;

import java.util.logging.Level;
import java.util.logging.Logger;

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
            Logger.getLogger(classname).log(Level.WARNING, "Unable to load " + classname, throwable);
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

