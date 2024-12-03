/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.xwork2.ActionInvocation
 *  com.opensymphony.xwork2.interceptor.Interceptor
 *  org.apache.struts2.ServletActionContext
 */
package com.atlassian.confluence.security.interceptors;

import com.atlassian.confluence.util.SecurityHeadersHelper;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;
import org.apache.struts2.ServletActionContext;

public class SecurityHeadersInterceptor
implements Interceptor {
    public void destroy() {
    }

    public void init() {
    }

    public String intercept(ActionInvocation actionInvocation) throws Exception {
        SecurityHeadersHelper.intercept(ServletActionContext.getResponse());
        return actionInvocation.invoke();
    }
}

