/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.aopalliance.intercept.MethodInvocation
 */
package org.springframework.remoting.support;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.remoting.support.RemoteInvocation;

public interface RemoteInvocationFactory {
    public RemoteInvocation createRemoteInvocation(MethodInvocation var1);
}

