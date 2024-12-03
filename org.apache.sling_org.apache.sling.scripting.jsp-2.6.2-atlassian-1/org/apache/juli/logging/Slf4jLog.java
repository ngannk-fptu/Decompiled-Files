/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.juli.logging;

import org.apache.juli.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Slf4jLog
implements Log {
    private Logger delegatee;

    Slf4jLog(String name) {
        this.delegatee = LoggerFactory.getLogger((String)name);
    }

    @Override
    public void debug(Object message, Throwable t) {
        this.delegatee.debug(message.toString(), t);
    }

    @Override
    public void debug(Object message) {
        this.delegatee.debug(message.toString());
    }

    @Override
    public void error(Object message, Throwable t) {
        this.delegatee.error(message.toString(), t);
    }

    @Override
    public void error(Object message) {
        this.delegatee.error(message.toString());
    }

    @Override
    public void fatal(Object message, Throwable t) {
        this.delegatee.error(message.toString(), t);
    }

    @Override
    public void fatal(Object message) {
        this.delegatee.error(message.toString());
    }

    @Override
    public void info(Object message, Throwable t) {
        this.delegatee.info(message.toString(), t);
    }

    @Override
    public void info(Object message) {
        this.delegatee.info(message.toString());
    }

    @Override
    public boolean isDebugEnabled() {
        return this.delegatee.isDebugEnabled();
    }

    @Override
    public boolean isErrorEnabled() {
        return this.delegatee.isErrorEnabled();
    }

    @Override
    public boolean isFatalEnabled() {
        return this.delegatee.isErrorEnabled();
    }

    @Override
    public boolean isInfoEnabled() {
        return this.delegatee.isInfoEnabled();
    }

    @Override
    public boolean isTraceEnabled() {
        return this.delegatee.isTraceEnabled();
    }

    @Override
    public boolean isWarnEnabled() {
        return this.delegatee.isWarnEnabled();
    }

    @Override
    public void trace(Object message, Throwable t) {
        this.delegatee.trace(message.toString(), t);
    }

    @Override
    public void trace(Object message) {
        this.delegatee.trace(message.toString());
    }

    @Override
    public void warn(Object message, Throwable t) {
        this.delegatee.warn(message.toString(), t);
    }

    @Override
    public void warn(Object message) {
        this.delegatee.warn(message.toString());
    }
}

