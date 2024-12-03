/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.aopalliance.intercept.MethodInvocation
 *  org.springframework.util.Assert
 */
package org.springframework.security.authorization.method;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.util.Assert;

public class MethodInvocationResult {
    private final MethodInvocation methodInvocation;
    private final Object result;

    public MethodInvocationResult(MethodInvocation methodInvocation, Object result) {
        Assert.notNull((Object)methodInvocation, (String)"methodInvocation cannot be null");
        this.methodInvocation = methodInvocation;
        this.result = result;
    }

    public MethodInvocation getMethodInvocation() {
        return this.methodInvocation;
    }

    public Object getResult() {
        return this.result;
    }
}

