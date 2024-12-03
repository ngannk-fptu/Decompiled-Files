/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.runtime.log;

import org.apache.velocity.runtime.log.Log;
import org.apache.velocity.runtime.log.LogChute;

public class LogDisplayWrapper
extends Log {
    private final String prefix;
    private final boolean outputMessages;
    private final Log log;

    public LogDisplayWrapper(Log log, String prefix, boolean outputMessages) {
        super(log.getLogChute());
        this.log = log;
        this.prefix = prefix;
        this.outputMessages = outputMessages;
    }

    protected LogChute getLogChute() {
        return this.log.getLogChute();
    }

    protected void log(int level, Object message) {
        this.log(this.outputMessages, level, message);
    }

    protected void log(boolean doLogging, int level, Object message) {
        if (doLogging) {
            this.getLogChute().log(level, this.prefix + String.valueOf(message));
        }
    }

    protected void log(int level, Object message, Throwable t) {
        this.log(this.outputMessages, level, message);
    }

    protected void log(boolean doLogging, int level, Object message, Throwable t) {
        if (doLogging) {
            this.getLogChute().log(level, this.prefix + String.valueOf(message), t);
        }
    }

    public void trace(boolean doLogging, Object message) {
        this.log(doLogging, -1, message);
    }

    public void trace(boolean doLogging, Object message, Throwable t) {
        this.log(doLogging, -1, message, t);
    }

    public void debug(boolean doLogging, Object message) {
        this.log(doLogging, 0, message);
    }

    public void debug(boolean doLogging, Object message, Throwable t) {
        this.log(doLogging, 0, message, t);
    }

    public void info(boolean doLogging, Object message) {
        this.log(doLogging, 1, message);
    }

    public void info(boolean doLogging, Object message, Throwable t) {
        this.log(doLogging, 1, message, t);
    }

    public void warn(boolean doLogging, Object message) {
        this.log(doLogging, 2, message);
    }

    public void warn(boolean doLogging, Object message, Throwable t) {
        this.log(doLogging, 2, message, t);
    }

    public void error(boolean doLogging, Object message) {
        this.log(doLogging, 3, message);
    }

    public void error(boolean doLogging, Object message, Throwable t) {
        this.log(doLogging, 3, message, t);
    }
}

