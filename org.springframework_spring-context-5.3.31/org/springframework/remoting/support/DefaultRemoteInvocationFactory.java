/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.aopalliance.intercept.MethodInvocation
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

