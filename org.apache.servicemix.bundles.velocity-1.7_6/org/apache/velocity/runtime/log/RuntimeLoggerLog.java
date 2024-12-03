/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.runtime.log;

import org.apache.velocity.runtime.RuntimeLogger;
import org.apache.velocity.runtime.log.Log;
import org.apache.velocity.runtime.log.LogChute;

public class RuntimeLoggerLog
extends Log {
    private RuntimeLogger rlog;

    public RuntimeLoggerLog(RuntimeLogger rlog) {
        if (rlog == null) {
            throw new NullPointerException("RuntimeLogger cannot be null!");
        }
        this.rlog = rlog;
    }

    protected void setLogChute(LogChute newLogChute) {
        throw new UnsupportedOperationException("RuntimeLoggerLog does not support this method.");
    }

    protected LogChute getLogChute() {
        throw new UnsupportedOperationException("RuntimeLoggerLog does not support this method.");
    }

    protected void setShowStackTraces(boolean showStacks) {
        throw new UnsupportedOperationException("RuntimeLoggerLog does not support this method.");
    }

    public boolean getShowStackTraces() {
        throw new UnsupportedOperationException("RuntimeLoggerLog does not support this method.");
    }

    public boolean isTraceEnabled() {
        return true;
    }

    public void trace(Object message) {
        this.debug(message);
    }

    public void trace(Object message, Throwable t) {
        this.debug(message, t);
    }

    public boolean isDebugEnabled() {
        return true;
    }

    public void debug(Object message) {
        this.rlog.debug(message);
    }

    public void debug(Object message, Throwable t) {
        this.rlog.debug(message);
        this.rlog.debug(t);
    }

    public boolean isInfoEnabled() {
        return true;
    }

    public void info(Object message) {
        this.rlog.info(message);
    }

    public void info(Object message, Throwable t) {
        this.rlog.info(message);
        this.rlog.info(t);
    }

    public boolean isWarnEnabled() {
        return true;
    }

    public void warn(Object message) {
        this.rlog.warn(message);
    }

    public void warn(Object message, Throwable t) {
        this.rlog.warn(message);
        this.rlog.warn(t);
    }

    public boolean isErrorEnabled() {
        return true;
    }

    public void error(Object message) {
        this.rlog.error(message);
    }

    public void error(Object message, Throwable t) {
        this.rlog.error(message);
        this.rlog.error(t);
    }
}

