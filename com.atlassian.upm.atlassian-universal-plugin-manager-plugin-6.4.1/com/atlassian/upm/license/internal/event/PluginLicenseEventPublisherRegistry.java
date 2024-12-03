/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.license.internal.event;

import com.atlassian.upm.api.license.event.PluginLicenseEvent;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.license.internal.PluginLicenseEventPublisher;
import com.atlassian.upm.license.internal.PluginLicenseGlobalEvent;
import com.atlassian.upm.license.internal.PluginLicenseGlobalEventPublisher;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;

public class PluginLicenseEventPublisherRegistry {
    private final ConcurrentMap<String, PluginLicenseEventPublisher> publishers = new ConcurrentHashMap<String, PluginLicenseEventPublisher>();
    private final CopyOnWriteArraySet<PluginLicenseGlobalEventPublisher> globalPublishers = new CopyOnWriteArraySet();

    public Option<PluginLicenseEventPublisher> getPublisher(String pluginKey) {
        return Option.option(this.publishers.get(pluginKey));
    }

    public Set<PluginLicenseGlobalEventPublisher> getGlobalPublishers() {
        return Collections.unmodifiableSet(this.globalPublishers);
    }

    public void register(String pluginKey, PluginLicenseEventPublisher publisher) {
        this.publishers.put(pluginKey, publisher);
    }

    public void unregister(String pluginKey) {
        this.publishers.remove(pluginKey);
    }

    public void registerGlobal(PluginLicenseGlobalEventPublisher publisher) {
        this.globalPublishers.add(publisher);
    }

    public void unregisterGlobal(PluginLicenseEventPublisher publisher) {
        this.globalPublishers.remove(publisher);
    }

    public void publishEvent(PluginLicenseEvent event) {
        for (PluginLicenseEventPublisher pluginLicenseEventPublisher : this.getGlobalPublishers()) {
            pluginLicenseEventPublisher.publish(event);
        }
        for (PluginLicenseEventPublisher pluginLicenseEventPublisher : this.getPublisher(event.getPluginKey())) {
            pluginLicenseEventPublisher.publish(event);
        }
    }

    public void publishGlobalEvent(PluginLicenseGlobalEvent event) {
        for (PluginLicenseGlobalEventPublisher global : this.getGlobalPublishers()) {
            global.publishGlobal(event);
        }
    }
}

