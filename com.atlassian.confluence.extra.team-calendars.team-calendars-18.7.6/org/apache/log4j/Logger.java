/*
 * Decompiled with CFR 0.152.
 */
package org.apache.log4j;

import org.apache.log4j.Category;
import org.apache.log4j.Log4jLoggerFactory;
import org.apache.log4j.spi.LoggerFactory;

public class Logger
extends Category {
    private static final String LOGGER_FQCN = Logger.class.getName();

    protected Logger(String name) {
        super(name);
    }

    public static Logger getLogger(String name) {
        return Log4jLoggerFactory.getLogger(name);
    }

    public static Logger getLogger(String name, LoggerFactory loggerFactory) {
        return Log4jLoggerFactory.getLogger(name, loggerFactory);
    }

    public static Logger getLogger(Class clazz) {
        return Logger.getLogger(clazz.getName());
    }

    public static Logger getRootLogger() {
        return Log4jLoggerFactory.getLogger("ROOT");
    }

    public boolean isTraceEnabled() {
        return this.slf4jLogger.isTraceEnabled();
    }

    public void trace(Object message) {
        this.differentiatedLog(null, LOGGER_FQCN, 0, message, null);
    }

    public void trace(Object message, Throwable t) {
        this.differentiatedLog(null, LOGGER_FQCN, 0, message, null);
    }
}

