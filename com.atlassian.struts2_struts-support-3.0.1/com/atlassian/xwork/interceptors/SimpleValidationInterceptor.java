/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.xwork2.Action
 *  com.opensymphony.xwork2.ActionInvocation
 *  com.opensymphony.xwork2.Validateable
 *  com.opensymphony.xwork2.interceptor.Interceptor
 *  com.opensymphony.xwork2.interceptor.ValidationAware
 */
package com.atlassian.xwork.interceptors;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.Validateable;
import com.opensymphony.xwork2.interceptor.Interceptor;
import com.opensymphony.xwork2.interceptor.ValidationAware;

public class SimpleValidationInterceptor
implements Interceptor {
    public String intercept(ActionInvocation invocation) throws Exception {
        Action action = (Action)invocation.getAction();
        if (action instanceof ValidationAware && action instanceof Validateable) {
            ((Validateable)action).validate();
            if (((ValidationAware)action).hasErrors()) {
                return "input";
            }
        }
        return invocation.invoke();
    }

    public void destroy() {
    }

    public void init() {
    }
}

