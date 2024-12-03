/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.velocity.runtime.log.Log
 */
package org.apache.velocity.tools.config;

import org.apache.velocity.runtime.log.Log;

public abstract class LogSupport {
    private static final String DEFAULT_PREFIX = "";
    private Log log;

    protected String logPrefix() {
        return DEFAULT_PREFIX;
    }

    public void setLog(Log log) {
        this.log = log;
    }

    protected Log getLog() {
        return this.log;
    }

    protected boolean isWarnEnabled() {
        return this.log != null && this.log.isWarnEnabled();
    }

    protected void warn(String msg) {
        if (this.isWarnEnabled()) {
            this.log.warn((Object)(this.logPrefix() + msg));
        }
    }

    protected boolean isDebugEnabled() {
        return this.log != null && this.log.isDebugEnabled();
    }

    protected void debug(String msg) {
        if (this.isDebugEnabled()) {
            this.log.debug((Object)(this.logPrefix() + msg));
        }
    }

    protected boolean isTraceEnabled() {
        return this.log != null && this.log.isTraceEnabled();
    }

    protected void trace(String msg) {
        if (this.isTraceEnabled()) {
            this.log.trace((Object)(this.logPrefix() + msg));
        }
    }
}

