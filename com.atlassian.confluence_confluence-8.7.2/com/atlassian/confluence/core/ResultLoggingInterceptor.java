/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.xwork2.ActionInvocation
 *  com.opensymphony.xwork2.interceptor.LoggingInterceptor
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.core;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.LoggingInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResultLoggingInterceptor
extends LoggingInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(LoggingInterceptor.class);

    public String intercept(ActionInvocation invocation) throws Exception {
        String result = super.intercept(invocation);
        this.after(invocation, result);
        return result;
    }

    private void after(ActionInvocation actionInvocation, String result) throws Exception {
        this.logMessage(actionInvocation, "Result was \"" + result + "\" for action ");
    }

    private void logMessage(ActionInvocation invocation, String baseMessage) {
        if (logger.isInfoEnabled()) {
            StringBuilder message = new StringBuilder(baseMessage);
            String namespace = invocation.getProxy().getNamespace();
            if (namespace != null && namespace.trim().length() > 0) {
                message.append(namespace).append("/");
            }
            message.append(invocation.getProxy().getActionName());
            logger.info(message.toString());
        }
    }
}

