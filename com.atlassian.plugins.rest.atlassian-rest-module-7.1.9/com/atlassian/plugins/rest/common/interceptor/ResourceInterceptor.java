/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.rest.common.interceptor;

import com.atlassian.plugins.rest.common.interceptor.MethodInvocation;
import java.lang.reflect.InvocationTargetException;

public interface ResourceInterceptor {
    public void intercept(MethodInvocation var1) throws IllegalAccessException, InvocationTargetException;
}

