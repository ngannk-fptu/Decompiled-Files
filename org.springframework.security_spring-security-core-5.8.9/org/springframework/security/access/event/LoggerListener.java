/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.context.ApplicationListener
 *  org.springframework.core.log.LogMessage
 */
package org.springframework.security.access.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.core.log.LogMessage;
import org.springframework.security.access.event.AbstractAuthorizationEvent;
import org.springframework.security.access.event.AuthenticationCredentialsNotFoundEvent;
import org.springframework.security.access.event.AuthorizationFailureEvent;
import org.springframework.security.access.event.AuthorizedEvent;
import org.springframework.security.access.event.PublicInvocationEvent;

@Deprecated
public class LoggerListener
implements ApplicationListener<AbstractAuthorizationEvent> {
    private static final Log logger = LogFactory.getLog(LoggerListener.class);

    public void onApplicationEvent(AbstractAuthorizationEvent event) {
        if (event instanceof AuthenticationCredentialsNotFoundEvent) {
            this.onAuthenticationCredentialsNotFoundEvent((AuthenticationCredentialsNotFoundEvent)event);
        }
        if (event instanceof AuthorizationFailureEvent) {
            this.onAuthorizationFailureEvent((AuthorizationFailureEvent)event);
        }
        if (event instanceof AuthorizedEvent) {
            this.onAuthorizedEvent((AuthorizedEvent)event);
        }
        if (event instanceof PublicInvocationEvent) {
            this.onPublicInvocationEvent((PublicInvocationEvent)event);
        }
    }

    private void onAuthenticationCredentialsNotFoundEvent(AuthenticationCredentialsNotFoundEvent authEvent) {
        logger.warn((Object)LogMessage.format((String)"Security interception failed due to: %s; secure object: %s; configuration attributes: %s", (Object)authEvent.getCredentialsNotFoundException(), (Object)authEvent.getSource(), authEvent.getConfigAttributes()));
    }

    private void onPublicInvocationEvent(PublicInvocationEvent event) {
        logger.info((Object)LogMessage.format((String)"Security interception not required for public secure object: %s", (Object)event.getSource()));
    }

    private void onAuthorizedEvent(AuthorizedEvent authEvent) {
        logger.info((Object)LogMessage.format((String)"Security authorized for authenticated principal: %s; secure object: %s; configuration attributes: %s", (Object)authEvent.getAuthentication(), (Object)authEvent.getSource(), authEvent.getConfigAttributes()));
    }

    private void onAuthorizationFailureEvent(AuthorizationFailureEvent authEvent) {
        logger.warn((Object)LogMessage.format((String)"Security authorization failed due to: %s; authenticated principal: %s; secure object: %s; configuration attributes: %s", (Object)authEvent.getAccessDeniedException(), (Object)authEvent.getAuthentication(), (Object)authEvent.getSource(), authEvent.getConfigAttributes()));
    }
}

