/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.remoting.support;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.remoting.support.RemoteInvocation;
import org.springframework.remoting.support.RemoteInvocationFactory;

public class DefaultRemoteInvocationFactory
implements RemoteInvocationFactory {
    @Override
    public RemoteInvocation createRemoteInvocation(MethodInvocation methodInvocation) {
        return new RemoteInvocation(methodInvocation);
    }
}

