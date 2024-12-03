/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.events.SessionDestroyedEvent
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.spring.container.LazyComponentReference
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Supplier
 *  javax.servlet.http.HttpSessionEvent
 *  javax.servlet.http.HttpSessionListener
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.user.listeners;

import com.atlassian.confluence.event.events.security.LogoutEvent;
import com.atlassian.confluence.setup.SetupContext;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.sal.api.events.SessionDestroyedEvent;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.spring.container.LazyComponentReference;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Supplier;
import java.security.Principal;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserSessionExpiryListener
implements HttpSessionListener {
    public static final Logger log = LoggerFactory.getLogger(UserSessionExpiryListener.class);
    private final Supplier<EventPublisher> eventPublisher;

    public UserSessionExpiryListener() {
        this((Supplier<EventPublisher>)new LazyComponentReference("eventPublisher"));
    }

    @VisibleForTesting
    UserSessionExpiryListener(Supplier<EventPublisher> eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void sessionCreated(HttpSessionEvent event) {
    }

    public void sessionDestroyed(HttpSessionEvent event) {
        try {
            Principal user = (Principal)event.getSession().getAttribute("seraph_defaultauthenticator_user");
            String username = user != null ? user.getName() : null;
            this.getEventPublisher().publish((Object)SessionDestroyedEvent.builder().sessionId(event.getSession().getId()).userName(username).build());
            if (username != null) {
                this.getEventPublisher().publish((Object)new LogoutEvent(this, username, event.getSession().getId()));
            }
        }
        catch (IllegalStateException e) {
            log.error("Application server does not give us access to expired sessions. Listeners that depend on receiving LogoutEvent will not be reliable.  This web server is probably unsupported.", (Throwable)e);
        }
    }

    private EventPublisher getEventPublisher() {
        if (ContainerManager.isContainerSetup()) {
            return (EventPublisher)this.eventPublisher.get();
        }
        return (EventPublisher)SetupContext.get().getBean("setupEventPublisher", EventPublisher.class);
    }
}

