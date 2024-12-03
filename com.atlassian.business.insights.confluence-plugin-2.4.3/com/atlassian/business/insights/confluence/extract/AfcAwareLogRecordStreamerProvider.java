/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.addonengine.addons.analytics.service.EventService
 *  com.atlassian.business.insights.api.extract.LogRecordStreamer
 *  com.atlassian.business.insights.api.extract.LogRecordStreamersProvider
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.google.common.collect.ImmutableList$Builder
 *  javax.annotation.Nonnull
 */
package com.atlassian.business.insights.confluence.extract;

import com.addonengine.addons.analytics.service.EventService;
import com.atlassian.business.insights.api.extract.LogRecordStreamer;
import com.atlassian.business.insights.api.extract.LogRecordStreamersProvider;
import com.atlassian.business.insights.confluence.afc.AfcPluginTracker;
import com.atlassian.business.insights.confluence.extract.AfcEventLogRecordStreamer;
import com.atlassian.sal.api.ApplicationProperties;
import com.google.common.collect.ImmutableList;
import java.util.List;
import javax.annotation.Nonnull;

public class AfcAwareLogRecordStreamerProvider
implements LogRecordStreamersProvider {
    private final ApplicationProperties applicationProperties;
    private final LogRecordStreamersProvider delegate;
    private final AfcPluginTracker tracker;

    public AfcAwareLogRecordStreamerProvider(LogRecordStreamersProvider delegate, AfcPluginTracker tracker, ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
        this.delegate = delegate;
        this.tracker = tracker;
    }

    @Nonnull
    public List<LogRecordStreamer> getLogRecordStreamers() {
        if (this.tracker.isAfcEnabled()) {
            return new ImmutableList.Builder().addAll((Iterable)this.delegate.getLogRecordStreamers()).add((Object)new AfcEventLogRecordStreamer((EventService)this.tracker.getAfcEventService(), this.applicationProperties)).build();
        }
        return this.delegate.getLogRecordStreamers();
    }
}

