/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.util;

import org.jfree.util.Log;

public class LogContext {
    private String contextPrefix;

    public LogContext(String contextPrefix) {
        this.contextPrefix = contextPrefix;
    }

    public boolean isDebugEnabled() {
        return Log.isDebugEnabled();
    }

    public boolean isInfoEnabled() {
        return Log.isInfoEnabled();
    }

    public boolean isWarningEnabled() {
        return Log.isWarningEnabled();
    }

    public boolean isErrorEnabled() {
        return Log.isErrorEnabled();
    }

    public void debug(Object message) {
        this.log(3, message);
    }

    public void debug(Object message, Exception e) {
        this.log(3, message, e);
    }

    public void info(Object message) {
        this.log(2, message);
    }

    public void info(Object message, Exception e) {
        this.log(2, message, e);
    }

    public void warn(Object message) {
        this.log(1, message);
    }

    public void warn(Object message, Exception e) {
        this.log(1, message, e);
    }

    public void error(Object message) {
        this.log(0, message);
    }

    public void error(Object message, Exception e) {
        this.log(0, message, e);
    }

    public void log(int level, Object message) {
        if (this.contextPrefix != null) {
            Log.getInstance().doLog(level, new Log.SimpleMessage(this.contextPrefix, ":", message));
        } else {
            Log.getInstance().doLog(level, message);
        }
    }

    public void log(int level, Object message, Exception e) {
        if (this.contextPrefix != null) {
            Log.getInstance().doLog(level, new Log.SimpleMessage(this.contextPrefix, ":", message), e);
        } else {
            Log.getInstance().doLog(level, message, e);
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LogContext)) {
            return false;
        }
        LogContext logContext = (LogContext)o;
        return !(this.contextPrefix != null ? !this.contextPrefix.equals(logContext.contextPrefix) : logContext.contextPrefix != null);
    }

    public int hashCode() {
        return this.contextPrefix != null ? this.contextPrefix.hashCode() : 0;
    }
}

