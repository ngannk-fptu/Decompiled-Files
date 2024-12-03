/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.aop.interceptor;

import java.io.Serializable;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public abstract class AbstractTraceInterceptor
implements MethodInterceptor,
Serializable {
    @Nullable
    protected transient Log defaultLogger = LogFactory.getLog(this.getClass());
    private boolean hideProxyClassNames = false;
    private boolean logExceptionStackTrace = true;

    public void setUseDynamicLogger(boolean useDynamicLogger) {
        this.defaultLogger = useDynamicLogger ? null : LogFactory.getLog(this.getClass());
    }

    public void setLoggerName(String loggerName) {
        this.defaultLogger = LogFactory.getLog((String)loggerName);
    }

    public void setHideProxyClassNames(boolean hideProxyClassNames) {
        this.hideProxyClassNames = hideProxyClassNames;
    }

    public void setLogExceptionStackTrace(boolean logExceptionStackTrace) {
        this.logExceptionStackTrace = logExceptionStackTrace;
    }

    @Override
    @Nullable
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Log logger = this.getLoggerForInvocation(invocation);
        if (this.isInterceptorEnabled(invocation, logger)) {
            return this.invokeUnderTrace(invocation, logger);
        }
        return invocation.proceed();
    }

    protected Log getLoggerForInvocation(MethodInvocation invocation) {
        if (this.defaultLogger != null) {
            return this.defaultLogger;
        }
        Object target = invocation.getThis();
        Assert.state((target != null ? 1 : 0) != 0, (String)"Target must not be null");
        return LogFactory.getLog(this.getClassForLogging(target));
    }

    protected Class<?> getClassForLogging(Object target) {
        return this.hideProxyClassNames ? AopUtils.getTargetClass(target) : target.getClass();
    }

    protected boolean isInterceptorEnabled(MethodInvocation invocation, Log logger) {
        return this.isLogEnabled(logger);
    }

    protected boolean isLogEnabled(Log logger) {
        return logger.isTraceEnabled();
    }

    protected void writeToLog(Log logger, String message) {
        this.writeToLog(logger, message, null);
    }

    protected void writeToLog(Log logger, String message, @Nullable Throwable ex) {
        if (ex != null && this.logExceptionStackTrace) {
            logger.trace((Object)message, ex);
        } else {
            logger.trace((Object)message);
        }
    }

    @Nullable
    protected abstract Object invokeUnderTrace(MethodInvocation var1, Log var2) throws Throwable;
}

