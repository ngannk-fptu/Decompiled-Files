/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.business.insights.core.analytics.path;

import com.atlassian.business.insights.core.analytics.AnalyticEvent;
import javax.annotation.Nonnull;

public class CustomExportPathAnalyticEvent
extends AnalyticEvent {
    public CustomExportPathAnalyticEvent(@Nonnull String pluginVersion) {
        super(pluginVersion);
    }
}

