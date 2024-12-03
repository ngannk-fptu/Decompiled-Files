/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.event.events.PluginEnabledEvent
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.upm.license.internal.event;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.event.events.PluginEnabledEvent;
import com.atlassian.upm.api.license.event.PluginLicenseCheckEvent;
import com.atlassian.upm.license.internal.PluginLicenseEventPublisher;
import com.atlassian.upm.license.internal.PluginLicenseRepository;
import com.atlassian.upm.license.internal.event.PluginLicenseEventPublisherRegistry;
import com.atlassian.upm.lifecycle.UpmProductDataStartupComponent;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class PluginLicenseEventPublisherPublishCheckEvent
implements DisposableBean,
InitializingBean,
UpmProductDataStartupComponent {
    private final PluginLicenseEventPublisherRegistry publisherRegistry;
    private final PluginLicenseRepository repository;
    private final EventPublisher eventPublisher;
    private final AtomicBoolean systemReady;

    public PluginLicenseEventPublisherPublishCheckEvent(PluginLicenseEventPublisherRegistry publisherRegistry, PluginLicenseRepository repository, EventPublisher eventPublisher) {
        this.publisherRegistry = Objects.requireNonNull(publisherRegistry, "publisherRegistry");
        this.repository = Objects.requireNonNull(repository, "repository");
        this.eventPublisher = Objects.requireNonNull(eventPublisher, "eventPublisher");
        this.systemReady = new AtomicBoolean(false);
    }

    public void afterPropertiesSet() {
        this.eventPublisher.register((Object)this);
    }

    public void destroy() {
        this.eventPublisher.unregister((Object)this);
    }

    @Override
    public void onStartupWithProductData() {
        this.systemReady.set(true);
    }

    @EventListener
    public void onPluginEnabled(PluginEnabledEvent event) {
        if (this.systemReady.get()) {
            this.firePluginLicenseCheckEvent(event);
        }
    }

    private void firePluginLicenseCheckEvent(PluginEnabledEvent event) {
        String pluginKey = event.getPlugin().getKey();
        for (PluginLicenseEventPublisher publisher : this.publisherRegistry.getPublisher(pluginKey)) {
            publisher.publish(new PluginLicenseCheckEvent(pluginKey, this.repository.getPluginLicense(pluginKey)));
        }
    }
}

