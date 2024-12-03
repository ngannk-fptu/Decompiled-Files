/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.aopalliance.intercept.MethodInvocation
 */
package com.atlassian.confluence.tenant;

import java.lang.reflect.Method;
import org.aopalliance.intercept.MethodInvocation;

@Deprecated(forRemoval=true)
public class VacantException
extends IllegalStateException {
    private final MethodInvocation methodInvocation;

    public VacantException(MethodInvocation methodInvocation) {
        super(String.format("Confluence is vacant, a call to tenanted [%s] is not allowed.", methodInvocation.getMethod().toGenericString()));
        this.methodInvocation = methodInvocation;
    }

    public Object[] getArguments() {
        return this.methodInvocation.getArguments();
    }

    public Method getMethod() {
        return this.methodInvocation.getMethod();
    }
}

