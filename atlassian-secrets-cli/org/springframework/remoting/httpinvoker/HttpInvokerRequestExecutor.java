/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.remoting.httpinvoker;

import org.springframework.remoting.httpinvoker.HttpInvokerClientConfiguration;
import org.springframework.remoting.support.RemoteInvocation;
import org.springframework.remoting.support.RemoteInvocationResult;

@FunctionalInterface
public interface HttpInvokerRequestExecutor {
    public RemoteInvocationResult executeRequest(HttpInvokerClientConfiguration var1, RemoteInvocation var2) throws Exception;
}

