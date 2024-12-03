/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.LoggerFactory
 */
package org.apache.tika.config;

import org.apache.tika.exception.TikaConfigException;
import org.slf4j.LoggerFactory;

public interface InitializableProblemHandler {
    public static final InitializableProblemHandler IGNORE = new InitializableProblemHandler(){

        @Override
        public void handleInitializableProblem(String className, String message) {
        }

        public String toString() {
            return "IGNORE";
        }
    };
    public static final InitializableProblemHandler INFO = new InitializableProblemHandler(){

        @Override
        public void handleInitializableProblem(String classname, String message) {
            LoggerFactory.getLogger((String)classname).info(message);
        }

        public String toString() {
            return "INFO";
        }
    };
    public static final InitializableProblemHandler WARN = new InitializableProblemHandler(){

        @Override
        public void handleInitializableProblem(String classname, String message) {
            LoggerFactory.getLogger((String)classname).warn(message);
        }

        public String toString() {
            return "WARN";
        }
    };
    public static final InitializableProblemHandler THROW = new InitializableProblemHandler(){

        @Override
        public void handleInitializableProblem(String classname, String message) throws TikaConfigException {
            throw new TikaConfigException(message);
        }

        public String toString() {
            return "THROW";
        }
    };
    public static final InitializableProblemHandler DEFAULT = WARN;

    public void handleInitializableProblem(String var1, String var2) throws TikaConfigException;
}

