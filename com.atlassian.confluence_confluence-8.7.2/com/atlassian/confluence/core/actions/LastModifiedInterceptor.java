/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.servlet.util.LastModifiedHandler
 *  com.opensymphony.xwork2.Action
 *  com.opensymphony.xwork2.ActionInvocation
 *  com.opensymphony.xwork2.interceptor.Interceptor
 *  org.apache.struts2.ServletActionContext
 */
package com.atlassian.confluence.core.actions;

import com.atlassian.confluence.core.actions.HttpCacheValidatable;
import com.atlassian.plugin.servlet.util.LastModifiedHandler;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;
import org.apache.struts2.ServletActionContext;

public class LastModifiedInterceptor
implements Interceptor {
    public void destroy() {
    }

    public void init() {
    }

    public String intercept(ActionInvocation actionInvocation) throws Exception {
        LastModifiedHandler lastModifiedHandler;
        Action action = (Action)actionInvocation.getAction();
        if (action instanceof HttpCacheValidatable && (lastModifiedHandler = ((HttpCacheValidatable)action).getLastModifiedHandler()).checkRequest(ServletActionContext.getRequest(), ServletActionContext.getResponse())) {
            return "none";
        }
        return actionInvocation.invoke();
    }
}

