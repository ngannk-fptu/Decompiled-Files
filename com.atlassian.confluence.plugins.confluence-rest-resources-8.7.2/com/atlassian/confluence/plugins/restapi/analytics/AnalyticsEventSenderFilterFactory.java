/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.google.common.collect.ImmutableList
 *  com.sun.jersey.api.model.AbstractMethod
 *  com.sun.jersey.spi.container.ResourceFilter
 *  com.sun.jersey.spi.container.ResourceFilterFactory
 */
package com.atlassian.confluence.plugins.restapi.analytics;

import com.atlassian.confluence.plugins.restapi.analytics.AnalyticEventSenderResourceFilter;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.common.collect.ImmutableList;
import com.sun.jersey.api.model.AbstractMethod;
import com.sun.jersey.spi.container.ResourceFilter;
import com.sun.jersey.spi.container.ResourceFilterFactory;
import java.util.List;

public final class AnalyticsEventSenderFilterFactory
implements ResourceFilterFactory {
    private final EventPublisher eventPublisher;

    public AnalyticsEventSenderFilterFactory(@ComponentImport EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public List<ResourceFilter> create(AbstractMethod method) {
        return ImmutableList.builder().add((Object)new AnalyticEventSenderResourceFilter(this.eventPublisher, method)).build();
    }
}

