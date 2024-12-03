/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.opensymphony.xwork2.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoggingInterceptor
extends AbstractInterceptor {
    private static final Logger LOG = LogManager.getLogger(LoggingInterceptor.class);
    private static final String FINISH_MESSAGE = "Finishing execution stack for action ";
    private static final String START_MESSAGE = "Starting execution stack for action ";

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        this.logMessage(invocation, START_MESSAGE);
        String result = invocation.invoke();
        this.logMessage(invocation, FINISH_MESSAGE);
        return result;
    }

    private void logMessage(ActionInvocation invocation, String baseMessage) {
        if (LOG.isInfoEnabled()) {
            StringBuilder message = new StringBuilder(baseMessage);
            String namespace = invocation.getProxy().getNamespace();
            if (namespace != null && namespace.trim().length() > 0) {
                message.append(namespace).append("/");
            }
            message.append(invocation.getProxy().getActionName());
            LOG.info(message.toString());
        }
    }
}

