/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.spring.container.LazyComponentReference
 *  javax.servlet.http.HttpSessionAttributeListener
 *  javax.servlet.http.HttpSessionBindingEvent
 *  org.springframework.security.core.session.SessionRegistry
 */
package com.atlassian.confluence.event.listeners;

import com.atlassian.spring.container.LazyComponentReference;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import org.springframework.security.core.session.SessionRegistry;

public final class HttpSessionRegistrarAttributeListener
implements HttpSessionAttributeListener {
    private final LazyComponentReference<SessionRegistry> registry = new LazyComponentReference("sessionRegistry");

    public void attributeAdded(HttpSessionBindingEvent event) {
        if (this.supportsAttribute(event)) {
            this.sessionRegistry().registerNewSession(event.getSession().getId(), event.getValue());
        }
    }

    public void attributeRemoved(HttpSessionBindingEvent event) {
        if (this.supportsAttribute(event)) {
            this.sessionRegistry().removeSessionInformation(event.getSession().getId());
        }
    }

    public void attributeReplaced(HttpSessionBindingEvent event) {
    }

    private boolean supportsAttribute(HttpSessionBindingEvent event) {
        return event.getName().equals("seraph_defaultauthenticator_user");
    }

    private SessionRegistry sessionRegistry() {
        return (SessionRegistry)this.registry.get();
    }
}

