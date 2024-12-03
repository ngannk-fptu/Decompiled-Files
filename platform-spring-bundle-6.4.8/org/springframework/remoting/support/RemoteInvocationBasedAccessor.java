/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.remoting.support;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.lang.Nullable;
import org.springframework.remoting.support.DefaultRemoteInvocationFactory;
import org.springframework.remoting.support.RemoteInvocation;
import org.springframework.remoting.support.RemoteInvocationFactory;
import org.springframework.remoting.support.RemoteInvocationResult;
import org.springframework.remoting.support.UrlBasedRemoteAccessor;

public abstract class RemoteInvocationBasedAccessor
extends UrlBasedRemoteAccessor {
    private RemoteInvocationFactory remoteInvocationFactory = new DefaultRemoteInvocationFactory();

    public void setRemoteInvocationFactory(RemoteInvocationFactory remoteInvocationFactory) {
        this.remoteInvocationFactory = remoteInvocationFactory != null ? remoteInvocationFactory : new DefaultRemoteInvocationFactory();
    }

    public RemoteInvocationFactory getRemoteInvocationFactory() {
        return this.remoteInvocationFactory;
    }

    protected RemoteInvocation createRemoteInvocation(MethodInvocation methodInvocation) {
        return this.getRemoteInvocationFactory().createRemoteInvocation(methodInvocation);
    }

    @Nullable
    protected Object recreateRemoteInvocationResult(RemoteInvocationResult result) throws Throwable {
        return result.recreate();
    }
}

