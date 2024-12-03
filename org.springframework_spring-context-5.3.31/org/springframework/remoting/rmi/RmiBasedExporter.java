/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.remoting.rmi;

import java.lang.reflect.InvocationTargetException;
import java.rmi.Remote;
import org.springframework.remoting.rmi.RmiInvocationWrapper;
import org.springframework.remoting.support.RemoteInvocation;
import org.springframework.remoting.support.RemoteInvocationBasedExporter;

@Deprecated
public abstract class RmiBasedExporter
extends RemoteInvocationBasedExporter {
    protected Remote getObjectToExport() {
        if (this.getService() instanceof Remote && (this.getServiceInterface() == null || Remote.class.isAssignableFrom(this.getServiceInterface()))) {
            return (Remote)this.getService();
        }
        if (this.logger.isDebugEnabled()) {
            this.logger.debug((Object)("RMI service [" + this.getService() + "] is an RMI invoker"));
        }
        return new RmiInvocationWrapper(this.getProxyForService(), this);
    }

    @Override
    protected Object invoke(RemoteInvocation invocation, Object targetObject) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return super.invoke(invocation, targetObject);
    }
}

