/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.velocity.app.Velocity
 *  org.apache.velocity.runtime.log.Log
 */
package org.apache.velocity.tools.generic.log;

import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.log.Log;

public class LogChuteCommonsLog
implements org.apache.commons.logging.Log {
    private static Log target = null;
    private String category;

    protected static Log getVelocityLog() {
        return target;
    }

    public static void setVelocityLog(Log target) {
        LogChuteCommonsLog.target = target;
    }

    public LogChuteCommonsLog() {
        this("");
    }

    public LogChuteCommonsLog(String category) {
        this.category = category + ": ";
    }

    protected Log getTarget() {
        if (target == null) {
            return Velocity.getLog();
        }
        return target;
    }

    public void trace(Object message) {
        this.getTarget().trace((Object)(this.category + message));
    }

    public void trace(Object message, Throwable t) {
        this.getTarget().trace((Object)(this.category + message), t);
    }

    public void debug(Object message) {
        this.getTarget().debug((Object)(this.category + message));
    }

    public void debug(Object message, Throwable t) {
        this.getTarget().debug((Object)(this.category + message), t);
    }

    public void info(Object message) {
        this.getTarget().info((Object)(this.category + message));
    }

    public void info(Object message, Throwable t) {
        this.getTarget().info((Object)(this.category + message), t);
    }

    public void warn(Object message) {
        this.getTarget().warn((Object)(this.category + message));
    }

    public void warn(Object message, Throwable t) {
        this.getTarget().warn((Object)(this.category + message), t);
    }

    public void error(Object message) {
        this.getTarget().error((Object)(this.category + message));
    }

    public void error(Object message, Throwable t) {
        this.getTarget().error((Object)(this.category + message), t);
    }

    public void fatal(Object message) {
        this.getTarget().error((Object)(this.category + message));
    }

    public void fatal(Object message, Throwable t) {
        this.getTarget().error((Object)(this.category + message), t);
    }

    public boolean isTraceEnabled() {
        return this.getTarget().isTraceEnabled();
    }

    public boolean isDebugEnabled() {
        return this.getTarget().isDebugEnabled();
    }

    public boolean isInfoEnabled() {
        return this.getTarget().isInfoEnabled();
    }

    public boolean isWarnEnabled() {
        return this.getTarget().isWarnEnabled();
    }

    public boolean isErrorEnabled() {
        return this.getTarget().isErrorEnabled();
    }

    public boolean isFatalEnabled() {
        return this.isErrorEnabled();
    }
}

