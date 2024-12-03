/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.opensymphony.xwork2.ActionInvocation
 *  com.opensymphony.xwork2.interceptor.AbstractInterceptor
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.xwork;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.event.events.analytics.HttpRequestStats;
import com.atlassian.confluence.pages.actions.AbstractCreatePageAction;
import com.atlassian.confluence.pages.actions.AbstractEditPageAction;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRequestStatsInterceptor
extends AbstractInterceptor {
    private static final Logger log = LoggerFactory.getLogger(HttpRequestStatsInterceptor.class);

    public String intercept(ActionInvocation invocation) throws Exception {
        String result = invocation.invoke();
        this.after(invocation, result);
        return result;
    }

    @VisibleForTesting
    void after(ActionInvocation dispatcher, String result) {
        try {
            String actionName = dispatcher.getInvocationContext().getActionName();
            if ("dashboard".equals(actionName) && "success".equals(result)) {
                HttpRequestStats.setKey("confluence.dashboard.view");
            } else if ("viewpage".equals(actionName) && ("page".equals(result) || "blogpost".equals(result))) {
                HttpRequestStats.setKey("confluence." + result + ".view");
            } else if (dispatcher.getAction() instanceof AbstractEditPageAction) {
                AbstractEditPageAction action = (AbstractEditPageAction)dispatcher.getAction();
                String contentType = action.getContentType();
                HttpRequestStats.setKey("confluence." + contentType + ".edit.view");
            } else if (dispatcher.getAction() instanceof AbstractCreatePageAction) {
                AbstractCreatePageAction action = (AbstractCreatePageAction)dispatcher.getAction();
                String contentType = action.getContentType();
                HttpRequestStats.setKey("confluence." + contentType + ".create.view");
            } else if ("viewspace".equals(actionName) && ("collector".equals(result) || "browse".equals(result))) {
                HttpRequestStats.setKey("confluence.space.view");
            }
        }
        catch (RuntimeException e) {
            log.warn("Failed to generate HTTP request key", (Throwable)e);
        }
    }
}

