/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.remoting.support.RemoteInvocation
 *  org.springframework.remoting.support.RemoteInvocationResult
 */
package org.springframework.remoting.httpinvoker;

import org.springframework.remoting.httpinvoker.HttpInvokerClientConfiguration;
import org.springframework.remoting.support.RemoteInvocation;
import org.springframework.remoting.support.RemoteInvocationResult;

@Deprecated
@FunctionalInterface
public interface HttpInvokerRequestExecutor {
    public RemoteInvocationResult executeRequest(HttpInvokerClientConfiguration var1, RemoteInvocation var2) throws Exception;
}

