/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.remoting.support;

import java.lang.reflect.InvocationTargetException;
import org.springframework.remoting.support.DefaultRemoteInvocationExecutor;
import org.springframework.remoting.support.RemoteExporter;
import org.springframework.remoting.support.RemoteInvocation;
import org.springframework.remoting.support.RemoteInvocationExecutor;
import org.springframework.remoting.support.RemoteInvocationResult;

public abstract class RemoteInvocationBasedExporter
extends RemoteExporter {
    private RemoteInvocationExecutor remoteInvocationExecutor = new DefaultRemoteInvocationExecutor();

    public void setRemoteInvocationExecutor(RemoteInvocationExecutor remoteInvocationExecutor) {
        this.remoteInvocationExecutor = remoteInvocationExecutor;
    }

    public RemoteInvocationExecutor getRemoteInvocationExecutor() {
        return this.remoteInvocationExecutor;
    }

    protected Object invoke(RemoteInvocation invocation, Object targetObject) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("Executing " + invocation);
        }
        try {
            return this.getRemoteInvocationExecutor().invoke(invocation, targetObject);
        }
        catch (NoSuchMethodException ex) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Could not find target method for " + invocation, ex);
            }
            throw ex;
        }
        catch (IllegalAccessException ex) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Could not access target method for " + invocation, ex);
            }
            throw ex;
        }
        catch (InvocationTargetException ex) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Target method failed for " + invocation, ex.getTargetException());
            }
            throw ex;
        }
    }

    protected RemoteInvocationResult invokeAndCreateResult(RemoteInvocation invocation, Object targetObject) {
        try {
            Object value = this.invoke(invocation, targetObject);
            return new RemoteInvocationResult(value);
        }
        catch (Throwable ex) {
            return new RemoteInvocationResult(ex);
        }
    }
}

