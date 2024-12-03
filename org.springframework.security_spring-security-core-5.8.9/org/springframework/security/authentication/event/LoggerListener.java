/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.context.ApplicationListener
 *  org.springframework.core.log.LogMessage
 *  org.springframework.util.ClassUtils
 */
package org.springframework.security.authentication.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.core.log.LogMessage;
import org.springframework.security.authentication.event.AbstractAuthenticationEvent;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.util.ClassUtils;

public class LoggerListener
implements ApplicationListener<AbstractAuthenticationEvent> {
    private static final Log logger = LogFactory.getLog(LoggerListener.class);
    private boolean logInteractiveAuthenticationSuccessEvents = true;

    public void onApplicationEvent(AbstractAuthenticationEvent event) {
        if (!this.logInteractiveAuthenticationSuccessEvents && event instanceof InteractiveAuthenticationSuccessEvent) {
            return;
        }
        logger.warn((Object)LogMessage.of(() -> this.getLogMessage(event)));
    }

    private String getLogMessage(AbstractAuthenticationEvent event) {
        StringBuilder builder = new StringBuilder();
        builder.append("Authentication event ");
        builder.append(ClassUtils.getShortName(((Object)((Object)event)).getClass()));
        builder.append(": ");
        builder.append(event.getAuthentication().getName());
        builder.append("; details: ");
        builder.append(event.getAuthentication().getDetails());
        if (event instanceof AbstractAuthenticationFailureEvent) {
            builder.append("; exception: ");
            builder.append(((AbstractAuthenticationFailureEvent)event).getException().getMessage());
        }
        return builder.toString();
    }

    public boolean isLogInteractiveAuthenticationSuccessEvents() {
        return this.logInteractiveAuthenticationSuccessEvents;
    }

    public void setLogInteractiveAuthenticationSuccessEvents(boolean logInteractiveAuthenticationSuccessEvents) {
        this.logInteractiveAuthenticationSuccessEvents = logInteractiveAuthenticationSuccessEvents;
    }
}

