/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.event.events.PluginEvent
 *  javax.annotation.Nonnull
 */
package com.atlassian.ratelimiting.internal.plugin;

import com.atlassian.plugin.event.events.PluginEvent;
import javax.annotation.Nonnull;

public class PluginChecker {
    public boolean isRateLimitingPlugin(@Nonnull PluginEvent event) {
        return "com.atlassian.ratelimiting.rate-limiting-plugin".equals(event.getPlugin().getKey());
    }
}

