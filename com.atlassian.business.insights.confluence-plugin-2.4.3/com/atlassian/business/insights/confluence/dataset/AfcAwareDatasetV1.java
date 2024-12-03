/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.addonengine.addons.analytics.service.EventService
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.business.insights.api.dataset.DataChannel
 *  com.atlassian.business.insights.api.dataset.Dataset
 *  com.atlassian.business.insights.api.extract.LogRecordStreamer
 *  com.atlassian.business.insights.api.schema.Schema
 *  com.atlassian.business.insights.attribute.AttributeDefinition
 *  com.atlassian.business.insights.core.dataset.DefaultDataChannel
 *  com.atlassian.business.insights.core.schema.ColumnFileSchema
 *  com.atlassian.business.insights.core.schema.DefaultSchema
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.google.common.collect.ImmutableList$Builder
 *  javax.annotation.Nonnull
 */
package com.atlassian.business.insights.confluence.dataset;

import com.addonengine.addons.analytics.service.EventService;
import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.business.insights.api.dataset.DataChannel;
import com.atlassian.business.insights.api.dataset.Dataset;
import com.atlassian.business.insights.api.extract.LogRecordStreamer;
import com.atlassian.business.insights.api.schema.Schema;
import com.atlassian.business.insights.attribute.AttributeDefinition;
import com.atlassian.business.insights.confluence.afc.AfcPluginTracker;
import com.atlassian.business.insights.confluence.attribute.AfcEventAttributes;
import com.atlassian.business.insights.confluence.extract.AfcEventLogRecordStreamer;
import com.atlassian.business.insights.core.dataset.DefaultDataChannel;
import com.atlassian.business.insights.core.schema.ColumnFileSchema;
import com.atlassian.business.insights.core.schema.DefaultSchema;
import com.atlassian.sal.api.ApplicationProperties;
import com.google.common.collect.ImmutableList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;

public class AfcAwareDatasetV1
implements Dataset {
    @VisibleForTesting
    public static final String AFC_FILE_NAME = "analytics";
    @VisibleForTesting
    static final Schema afcSchema = new DefaultSchema("analytics", Collections.singletonList(new ColumnFileSchema("analytics", AttributeDefinition.toLinkedHashMap(AfcEventAttributes.getAttributes()))), 1);
    private final Dataset delegate;
    private final AfcPluginTracker tracker;
    private final ApplicationProperties applicationProperties;

    public AfcAwareDatasetV1(Dataset delegate, AfcPluginTracker tracker, ApplicationProperties applicationProperties) {
        this.delegate = delegate;
        this.tracker = tracker;
        this.applicationProperties = applicationProperties;
    }

    @Nonnull
    public String getDescription() {
        return this.delegate.getDescription() + ",analytics";
    }

    public int getVersion() {
        return this.delegate.getVersion();
    }

    public boolean isDeprecated() {
        return this.delegate.isDeprecated();
    }

    @Nonnull
    public List<DataChannel> getChannels() {
        if (this.tracker.isAfcEnabled()) {
            AfcEventLogRecordStreamer afcEventLogRecordStreamer = new AfcEventLogRecordStreamer((EventService)this.tracker.getAfcEventService(), this.applicationProperties);
            return new ImmutableList.Builder().addAll((Iterable)this.delegate.getChannels()).add((Object)new DefaultDataChannel(afcSchema, (LogRecordStreamer)afcEventLogRecordStreamer)).build();
        }
        return this.delegate.getChannels();
    }
}

