/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.xwork2.Action
 *  com.opensymphony.xwork2.ActionInvocation
 *  com.opensymphony.xwork2.interceptor.DefaultWorkflowInterceptor
 */
package com.atlassian.confluence.core;

import com.atlassian.confluence.validation.MessageHolder;
import com.atlassian.confluence.validation.MessageHolderAware;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.DefaultWorkflowInterceptor;

public class ConfluenceWorkflowInterceptor
extends DefaultWorkflowInterceptor {
    public String intercept(ActionInvocation actionInvocation) throws Exception {
        MessageHolderAware holderAware;
        MessageHolder messageHolder;
        Action action = (Action)actionInvocation.getAction();
        if (action instanceof MessageHolderAware && (messageHolder = (holderAware = (MessageHolderAware)action).getMessageHolder()) != null && messageHolder.hasErrors()) {
            return "input";
        }
        return super.intercept(actionInvocation);
    }
}

