/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.apache.commons.logging.impl.NoOpLog
 */
package org.apache.commons.configuration2.io;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.impl.NoOpLog;

public class ConfigurationLogger {
    private final Log log;

    public ConfigurationLogger(String loggerName) {
        this(ConfigurationLogger.createLoggerForName(loggerName));
    }

    public ConfigurationLogger(Class<?> logCls) {
        this(ConfigurationLogger.createLoggerForClass(logCls));
    }

    protected ConfigurationLogger() {
        this((Log)null);
    }

    ConfigurationLogger(Log wrapped) {
        this.log = wrapped;
    }

    public static ConfigurationLogger newDummyLogger() {
        return new ConfigurationLogger((Log)new NoOpLog());
    }

    public boolean isDebugEnabled() {
        return this.getLog().isDebugEnabled();
    }

    public void debug(String msg) {
        this.getLog().debug((Object)msg);
    }

    public boolean isInfoEnabled() {
        return this.getLog().isInfoEnabled();
    }

    public void info(String msg) {
        this.getLog().info((Object)msg);
    }

    public void warn(String msg) {
        this.getLog().warn((Object)msg);
    }

    public void warn(String msg, Throwable ex) {
        this.getLog().warn((Object)msg, ex);
    }

    public void error(String msg) {
        this.getLog().error((Object)msg);
    }

    public void error(String msg, Throwable ex) {
        this.getLog().error((Object)msg, ex);
    }

    Log getLog() {
        return this.log;
    }

    private static Log createLoggerForName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Logger name must not be null!");
        }
        return LogFactory.getLog((String)name);
    }

    private static Log createLoggerForClass(Class<?> cls) {
        if (cls == null) {
            throw new IllegalArgumentException("Logger class must not be null!");
        }
        return LogFactory.getLog(cls);
    }
}

