/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.logging.impl;

import java.io.Serializable;
import org.apache.commons.logging.Log;

public class NoOpLog
implements Log,
Serializable {
    public NoOpLog() {
    }

    public NoOpLog(String name) {
    }

    @Override
    public boolean isFatalEnabled() {
        return false;
    }

    @Override
    public boolean isErrorEnabled() {
        return false;
    }

    @Override
    public boolean isWarnEnabled() {
        return false;
    }

    @Override
    public boolean isInfoEnabled() {
        return false;
    }

    @Override
    public boolean isDebugEnabled() {
        return false;
    }

    @Override
    public boolean isTraceEnabled() {
        return false;
    }

    @Override
    public void fatal(Object message) {
    }

    @Override
    public void fatal(Object message, Throwable t) {
    }

    @Override
    public void error(Object message) {
    }

    @Override
    public void error(Object message, Throwable t) {
    }

    @Override
    public void warn(Object message) {
    }

    @Override
    public void warn(Object message, Throwable t) {
    }

    @Override
    public void info(Object message) {
    }

    @Override
    public void info(Object message, Throwable t) {
    }

    @Override
    public void debug(Object message) {
    }

    @Override
    public void debug(Object message, Throwable t) {
    }

    @Override
    public void trace(Object message) {
    }

    @Override
    public void trace(Object message, Throwable t) {
    }
}

