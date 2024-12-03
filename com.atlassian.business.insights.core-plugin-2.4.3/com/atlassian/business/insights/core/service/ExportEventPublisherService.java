/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  javax.annotation.Nonnull
 */
package com.atlassian.business.insights.core.service;

import com.atlassian.business.insights.core.analytics.AnalyticEvent;
import com.atlassian.business.insights.core.plugin.CorePluginInfo;
import com.atlassian.business.insights.core.service.api.EventPublisherService;
import com.atlassian.event.api.EventPublisher;
import java.util.Objects;
import javax.annotation.Nonnull;

public class ExportEventPublisherService
implements EventPublisherService {
    private final EventPublisher eventPublisher;
    private final CorePluginInfo corePluginInfo;

    public ExportEventPublisherService(EventPublisher eventPublisher, CorePluginInfo corePluginInfo) {
        this.eventPublisher = eventPublisher;
        this.corePluginInfo = corePluginInfo;
    }

    @Override
    public void publish(@Nonnull AnalyticEvent analyticEvent) {
        Objects.requireNonNull(analyticEvent);
        this.eventPublisher.publish((Object)analyticEvent);
    }

    @Override
    public String getPluginVersion() {
        return this.corePluginInfo.getPluginVersion();
    }
}

