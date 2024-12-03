/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.AsynchronousPreferred
 *  javax.annotation.Nonnull
 */
package com.atlassian.business.insights.core.analytics;

import com.atlassian.event.api.AsynchronousPreferred;
import java.util.Objects;
import javax.annotation.Nonnull;

@AsynchronousPreferred
public abstract class AnalyticEvent {
    private final String pluginVersion;

    public AnalyticEvent(@Nonnull String pluginVersion) {
        this.pluginVersion = Objects.requireNonNull(pluginVersion);
    }

    public String getPluginVersion() {
        return this.pluginVersion;
    }
}

