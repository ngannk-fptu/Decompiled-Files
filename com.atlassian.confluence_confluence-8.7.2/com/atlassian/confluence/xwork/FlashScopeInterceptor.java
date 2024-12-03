/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.xwork2.ActionInvocation
 *  com.opensymphony.xwork2.interceptor.Interceptor
 */
package com.atlassian.confluence.xwork;

import com.atlassian.confluence.xwork.FlashScope;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;

public class FlashScopeInterceptor
implements Interceptor {
    public void destroy() {
    }

    public void init() {
    }

    public String intercept(ActionInvocation actionInvocation) throws Exception {
        FlashScope.retrieve();
        return actionInvocation.invoke();
    }
}

