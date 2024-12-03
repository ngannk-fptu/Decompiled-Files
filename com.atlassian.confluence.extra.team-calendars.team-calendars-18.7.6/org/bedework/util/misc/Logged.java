/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.util.misc;

import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

public class Logged {
    protected boolean debug;
    private transient Logger log;
    private final Map<String, Logger> loggers = new HashMap<String, Logger>(5);
    public final String errorLoggerName = "errors";
    public final String auditLoggerName = "audit";
    public final String metricsLoggerName = "metrics";

    protected Logged() {
        this.debug = this.getLogger().isDebugEnabled();
    }

    protected Logger getLogger() {
        if (this.log == null) {
            this.log = Logger.getLogger(this.getClass());
        }
        return this.log;
    }

    protected Logger getLogger(String name) {
        Logger theLogger = this.loggers.get(name);
        if (theLogger != null) {
            return theLogger;
        }
        theLogger = Logger.getLogger(name + "." + this.getClass().getName());
        this.loggers.put(name, theLogger);
        return theLogger;
    }

    protected void enableErrorLogger() {
        this.getLogger("errors");
    }

    protected void enableAuditLogger() {
        this.getLogger("audit");
    }

    protected void enableMetricsLogger() {
        this.getLogger("metrics");
    }

    protected boolean isErrorLoggerEnabled() {
        return this.getLogger("errors") != null;
    }

    protected boolean isAuditLoggerEnabled() {
        return this.getLogger("audit") != null;
    }

    protected boolean isMetricsLoggerEnabled() {
        return this.getLogger("metrics") != null;
    }

    protected Logger getErrorLoggerIfEnabled() {
        return this.loggers.get("errors");
    }

    protected Logger getAuditLoggerIfEnabled() {
        return this.loggers.get("audit");
    }

    protected Logger getMetricsLoggerIfEnabled() {
        return this.loggers.get("metrics");
    }

    protected void error(Throwable t) {
        this.getLogger().error(this, t);
        Logger errorLogger = this.getErrorLoggerIfEnabled();
        if (errorLogger != null) {
            errorLogger.error(this, t);
        }
    }

    protected void error(String msg) {
        this.getLogger().error(msg);
        Logger errorLogger = this.getErrorLoggerIfEnabled();
        if (errorLogger != null) {
            errorLogger.error(msg);
        }
    }

    protected void warn(String msg) {
        this.getLogger().warn(msg);
    }

    protected void info(String msg) {
        this.getLogger().info(msg);
    }

    protected void audit(String msg) {
        if (this.isAuditLoggerEnabled()) {
            this.getLogger("audit").info(msg);
        }
    }

    protected void metrics(String msg) {
        if (this.isMetricsLoggerEnabled()) {
            this.getLogger("metrics").info(msg);
        }
    }

    protected void debug(String msg) {
        this.getLogger().debug(msg);
    }
}

