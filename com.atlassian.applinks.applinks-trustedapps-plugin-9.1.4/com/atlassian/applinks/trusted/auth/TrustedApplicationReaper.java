/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.core.event.BeforeApplicationLinkDeletedEvent
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.security.auth.trustedapps.TrustedApplicationsConfigurationManager
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.applinks.trusted.auth;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.core.event.BeforeApplicationLinkDeletedEvent;
import com.atlassian.applinks.trusted.auth.AbstractTrustedAppsServlet;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.security.auth.trustedapps.TrustedApplicationsConfigurationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TrustedApplicationReaper
implements DisposableBean {
    private final EventPublisher eventPublisher;
    private final TrustedApplicationsConfigurationManager trustedAppsManager;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public TrustedApplicationReaper(EventPublisher eventPublisher, TrustedApplicationsConfigurationManager trustedAppsManager) {
        this.eventPublisher = eventPublisher;
        this.trustedAppsManager = trustedAppsManager;
        eventPublisher.register((Object)this);
    }

    @EventListener
    public void onApplicationLinkDeleted(BeforeApplicationLinkDeletedEvent deletedEvent) {
        ApplicationLink link = deletedEvent.getApplicationLink();
        Object value = link.getProperty(AbstractTrustedAppsServlet.TRUSTED_APPS_INCOMING_ID);
        if (value != null) {
            this.trustedAppsManager.deleteApplication(value.toString());
            this.logger.debug("Removed certificate (trusted apps Id: {}) for deleted application link {}", (Object)value.toString(), (Object)link.getId());
        }
    }

    public void destroy() throws Exception {
        this.eventPublisher.unregister((Object)this);
    }
}

