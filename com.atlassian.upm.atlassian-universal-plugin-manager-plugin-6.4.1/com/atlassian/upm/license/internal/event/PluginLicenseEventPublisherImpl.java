/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 */
package com.atlassian.upm.license.internal.event;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.upm.api.license.PluginLicenseEventRegistry;
import com.atlassian.upm.api.license.event.PluginLicenseEvent;
import com.atlassian.upm.license.internal.PluginLicenseEventPublisher;
import java.util.Objects;

public class PluginLicenseEventPublisherImpl
implements PluginLicenseEventPublisher,
PluginLicenseEventRegistry {
    private final EventPublisher publisher;
    private final String pluginKey;

    public PluginLicenseEventPublisherImpl(EventPublisher underlyingPublisher, String pluginKey) {
        this.publisher = Objects.requireNonNull(underlyingPublisher, "publisher");
        this.pluginKey = Objects.requireNonNull(pluginKey, "pluginKey");
    }

    @Override
    public void publish(PluginLicenseEvent event) {
        if (!this.pluginKey.equals(event.getPluginKey())) {
            throw new IllegalArgumentException("Attempted to publish event for another plugin. Expected: " + this.pluginKey + ", Found: " + event.getPluginKey());
        }
        this.publisher.publish((Object)event);
    }

    @Override
    public void register(Object listener) {
        this.publisher.register(listener);
    }

    @Override
    public void unregister(Object listener) {
        this.publisher.unregister(listener);
    }
}

