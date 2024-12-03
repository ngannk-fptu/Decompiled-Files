/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.interceptor.MethodInvocation
 *  com.atlassian.plugins.rest.common.interceptor.ResourceInterceptor
 */
package com.atlassian.applinks.core.rest.context;

import com.atlassian.applinks.core.rest.context.CurrentContext;
import com.atlassian.plugins.rest.common.interceptor.MethodInvocation;
import com.atlassian.plugins.rest.common.interceptor.ResourceInterceptor;
import java.lang.reflect.InvocationTargetException;

public class ContextInterceptor
implements ResourceInterceptor {
    public void intercept(MethodInvocation invocation) throws IllegalAccessException, InvocationTargetException {
        try {
            CurrentContext.setContext(invocation.getHttpContext());
            invocation.invoke();
        }
        finally {
            CurrentContext.setContext(null);
        }
    }
}

