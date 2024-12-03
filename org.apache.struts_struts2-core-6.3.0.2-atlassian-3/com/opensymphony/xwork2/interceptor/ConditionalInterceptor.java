/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;

public interface ConditionalInterceptor
extends Interceptor {
    public boolean shouldIntercept(ActionInvocation var1);
}

