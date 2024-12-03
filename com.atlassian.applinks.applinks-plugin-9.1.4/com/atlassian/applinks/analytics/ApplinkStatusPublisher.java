/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ReadOnlyApplicationLinkService
 *  com.atlassian.event.api.EventPublisher
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.applinks.analytics;

import com.atlassian.applinks.analytics.ApplinkStatusEventBuilderFactory;
import com.atlassian.applinks.api.ReadOnlyApplicationLinkService;
import com.atlassian.event.api.EventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class ApplinkStatusPublisher {
    private static final Logger log = LoggerFactory.getLogger(ApplinkStatusPublisher.class);
    private final ReadOnlyApplicationLinkService readOnlyApplicationLinkService;
    private final EventPublisher eventPublisher;
    private final ApplinkStatusEventBuilderFactory applinkStatusEventBuilderFactory;

    @Autowired
    public ApplinkStatusPublisher(ReadOnlyApplicationLinkService readOnlyApplicationLinkService, EventPublisher eventPublisher, ApplinkStatusEventBuilderFactory applinkStatusEventBuilderFactory) {
        this.readOnlyApplicationLinkService = readOnlyApplicationLinkService;
        this.eventPublisher = eventPublisher;
        this.applinkStatusEventBuilderFactory = applinkStatusEventBuilderFactory;
    }

    public void publishApplinkStatus() {
        ApplinkStatusEventBuilderFactory.Builder builder = this.applinkStatusEventBuilderFactory.createBuilder();
        this.readOnlyApplicationLinkService.getApplicationLinks().forEach(builder::addApplink);
        this.eventPublisher.publish((Object)builder.buildMainEvent());
        for (ApplinkStatusEventBuilderFactory.ApplinkStatusApplinkEvent event : builder.buildApplinkEvents()) {
            this.eventPublisher.publish((Object)event);
        }
    }
}

