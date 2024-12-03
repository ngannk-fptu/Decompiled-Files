/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.ConditionalInterceptor;

public abstract class AbstractInterceptor
implements ConditionalInterceptor {
    private boolean disabled;

    @Override
    public void init() {
    }

    @Override
    public void destroy() {
    }

    @Override
    public abstract String intercept(ActionInvocation var1) throws Exception;

    public void setDisabled(String disable) {
        this.disabled = Boolean.parseBoolean(disable);
    }

    @Override
    public boolean shouldIntercept(ActionInvocation invocation) {
        return !this.disabled;
    }
}

