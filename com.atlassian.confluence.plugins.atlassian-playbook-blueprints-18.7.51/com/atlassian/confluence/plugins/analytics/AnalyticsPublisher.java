/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ConfluenceImport
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.analytics;

import com.atlassian.confluence.plugins.analytics.CreateAnalyticsEvent;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ConfluenceImport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AnalyticsPublisher {
    private final EventPublisher eventPublisher;

    @Autowired
    public AnalyticsPublisher(@ConfluenceImport EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void publishCreatedEvent(String blueprintKey) {
        this.eventPublisher.publish((Object)new CreateAnalyticsEvent(blueprintKey));
    }
}

