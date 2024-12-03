/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import java.io.Serializable;

public interface Interceptor
extends Serializable {
    public void destroy();

    public void init();

    public String intercept(ActionInvocation var1) throws Exception;
}

