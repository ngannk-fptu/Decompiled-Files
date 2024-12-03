/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.xwork2.ActionInvocation
 *  com.opensymphony.xwork2.ActionSupport
 *  com.opensymphony.xwork2.interceptor.DefaultWorkflowInterceptor
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.struts2.ServletActionContext
 */
package com.atlassian.xwork.interceptors;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.interceptor.DefaultWorkflowInterceptor;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;

public class CancelWorkflowInterceptor
extends DefaultWorkflowInterceptor {
    public static final String CANCEL = "cancel";

    public String intercept(ActionInvocation actionInvocation) throws Exception {
        if (actionInvocation.getAction() instanceof ActionSupport && StringUtils.isNotEmpty((CharSequence)ServletActionContext.getRequest().getParameter(CANCEL))) {
            return CANCEL;
        }
        return super.intercept(actionInvocation);
    }
}

