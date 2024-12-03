/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.remoting.support;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.remoting.support.RemoteInvocation;

public interface RemoteInvocationFactory {
    public RemoteInvocation createRemoteInvocation(MethodInvocation var1);
}

