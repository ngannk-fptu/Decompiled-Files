/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.atlassian.extras.common.log;

import com.atlassian.extras.common.log.Logger;
import org.apache.log4j.Logger;

class Log4jLogger
implements Logger.Log {
    private Logger logger;

    public Log4jLogger() {
    }

    public Log4jLogger(Class clazz) {
        this.logger = Logger.getLogger((Class)clazz);
    }

    @Override
    public void setClass(Class clazz) {
        this.logger = Logger.getLogger((Class)clazz);
    }

    @Override
    public void debug(Object o) {
        this.logger.debug(o);
    }

    @Override
    public void debug(Object o, Throwable t) {
        this.logger.debug(o, t);
    }

    @Override
    public void info(Object o) {
        this.logger.info(o);
    }

    @Override
    public void info(Object o, Throwable t) {
        this.logger.info(o, t);
    }

    @Override
    public void warn(Object o) {
        this.logger.info(o);
    }

    @Override
    public void warn(Object o, Throwable t) {
        this.logger.info(o, t);
    }

    @Override
    public void error(Object o) {
        this.logger.error(o);
    }

    @Override
    public void error(Object o, Throwable t) {
        this.logger.error(o, t);
    }

    @Override
    public void fatal(Object o) {
        this.logger.fatal(o);
    }

    @Override
    public void fatal(Object o, Throwable t) {
        this.logger.fatal(o, t);
    }
}

