/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.config;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.tika.exception.TikaConfigException;

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
            Logger.getLogger(classname).log(Level.INFO, message);
        }

        public String toString() {
            return "INFO";
        }
    };
    public static final InitializableProblemHandler WARN = new InitializableProblemHandler(){

        @Override
        public void handleInitializableProblem(String classname, String message) {
            Logger.getLogger(classname).log(Level.WARNING, message);
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

