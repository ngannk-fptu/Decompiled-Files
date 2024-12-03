/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.aspectj.weaver.tools;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.weaver.tools.AbstractTrace;

public class CommonsTrace
extends AbstractTrace {
    private Log log;
    private String className;

    public CommonsTrace(Class clazz) {
        super(clazz);
        this.log = LogFactory.getLog((Class)clazz);
        this.className = this.tracedClass.getName();
    }

    @Override
    public void enter(String methodName, Object thiz, Object[] args) {
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)this.formatMessage(">", this.className, methodName, thiz, args));
        }
    }

    @Override
    public void enter(String methodName, Object thiz) {
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)this.formatMessage(">", this.className, methodName, thiz, null));
        }
    }

    @Override
    public void exit(String methodName, Object ret) {
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)this.formatMessage("<", this.className, methodName, ret, null));
        }
    }

    @Override
    public void exit(String methodName, Throwable th) {
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)this.formatMessage("<", this.className, methodName, th, null));
        }
    }

    @Override
    public void exit(String methodName) {
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)this.formatMessage("<", this.className, methodName, null, null));
        }
    }

    @Override
    public void event(String methodName, Object thiz, Object[] args) {
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)this.formatMessage("-", this.className, methodName, thiz, args));
        }
    }

    @Override
    public void event(String methodName) {
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)this.formatMessage("-", this.className, methodName, null, null));
        }
    }

    @Override
    public boolean isTraceEnabled() {
        return this.log.isDebugEnabled();
    }

    @Override
    public void setTraceEnabled(boolean b) {
    }

    @Override
    public void debug(String message) {
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)message);
        }
    }

    @Override
    public void info(String message) {
        if (this.log.isInfoEnabled()) {
            this.log.info((Object)message);
        }
    }

    @Override
    public void warn(String message, Throwable th) {
        if (this.log.isWarnEnabled()) {
            this.log.warn((Object)message, th);
        }
    }

    @Override
    public void error(String message, Throwable th) {
        if (this.log.isErrorEnabled()) {
            this.log.error((Object)message, th);
        }
    }

    @Override
    public void fatal(String message, Throwable th) {
        if (this.log.isFatalEnabled()) {
            this.log.fatal((Object)message, th);
        }
    }
}

