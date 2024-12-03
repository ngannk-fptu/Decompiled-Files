/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  javax.annotation.Nonnull
 */
package com.atlassian.business.insights.core.analytics.schema;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.business.insights.core.analytics.AnalyticEvent;
import javax.annotation.Nonnull;

@EventName(value="data-pipeline.supported.schema.versions.get.requested")
public class SupportedSchemaVersionsGetRequestedAnalyticEvent
extends AnalyticEvent {
    public SupportedSchemaVersionsGetRequestedAnalyticEvent(@Nonnull String pluginVersion) {
        super(pluginVersion);
    }
}

