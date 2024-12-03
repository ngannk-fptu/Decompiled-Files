/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.Level
 *  org.apache.logging.log4j.spi.LoggerContext
 *  org.apache.logging.log4j.util.StackLocatorUtil
 */
package org.apache.log4j;

import org.apache.log4j.Category;
import org.apache.log4j.LogManager;
import org.apache.log4j.spi.LoggerFactory;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.spi.LoggerContext;
import org.apache.logging.log4j.util.StackLocatorUtil;

public class Logger
extends Category {
    private static final String FQCN = Logger.class.getName();

    public static Logger getLogger(Class<?> clazz) {
        return LogManager.getLogger(clazz.getName(), StackLocatorUtil.getCallerClassLoader((int)2));
    }

    public static Logger getLogger(String name) {
        return LogManager.getLogger(name, StackLocatorUtil.getCallerClassLoader((int)2));
    }

    public static Logger getLogger(String name, LoggerFactory factory) {
        return LogManager.getLogger(name, factory, StackLocatorUtil.getCallerClassLoader((int)2));
    }

    public static Logger getRootLogger() {
        return LogManager.getRootLogger();
    }

    Logger(LoggerContext context, String name) {
        super(context, name);
    }

    protected Logger(String name) {
        super(name);
    }

    public boolean isTraceEnabled() {
        return this.getLogger().isTraceEnabled();
    }

    public void trace(Object message) {
        this.maybeLog(FQCN, Level.TRACE, message, null);
    }

    public void trace(Object message, Throwable t) {
        this.maybeLog(FQCN, Level.TRACE, message, t);
    }
}

