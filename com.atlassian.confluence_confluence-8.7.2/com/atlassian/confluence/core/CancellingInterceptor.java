/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.xwork2.ActionInvocation
 *  com.opensymphony.xwork2.interceptor.Interceptor
 */
package com.atlassian.confluence.core;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;

public class CancellingInterceptor
implements Interceptor {
    public String intercept(ActionInvocation invocation) throws Exception {
        ConfluenceActionSupport confluenceAction;
        if (invocation.getAction() instanceof ConfluenceActionSupport && (confluenceAction = (ConfluenceActionSupport)invocation.getAction()).isCanceled()) {
            return confluenceAction.getCancelResult();
        }
        return invocation.invoke();
    }

    public void destroy() {
    }

    public void init() {
    }
}

