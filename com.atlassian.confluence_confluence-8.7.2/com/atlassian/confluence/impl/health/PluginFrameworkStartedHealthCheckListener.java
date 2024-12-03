/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventListenerRegistrar
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 */
package com.atlassian.confluence.impl.health;

import com.atlassian.confluence.event.events.plugin.PluginFrameworkStartedEvent;
import com.atlassian.confluence.impl.health.HealthCheckRunner;
import com.atlassian.confluence.internal.health.LifecyclePhase;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventListenerRegistrar;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

public class PluginFrameworkStartedHealthCheckListener {
    private final HealthCheckRunner healthCheckRunner;
    private final EventListenerRegistrar eventListenerRegistrar;

    public PluginFrameworkStartedHealthCheckListener(HealthCheckRunner healthCheckRunner, EventListenerRegistrar eventListenerRegistrar) {
        this.healthCheckRunner = Objects.requireNonNull(healthCheckRunner);
        this.eventListenerRegistrar = eventListenerRegistrar;
    }

    @PostConstruct
    void registerListener() {
        this.eventListenerRegistrar.register((Object)this);
    }

    @PreDestroy
    void unregisterListener() {
        this.eventListenerRegistrar.unregister((Object)this);
    }

    @EventListener
    public void onPluginFrameworkStartedEvent(PluginFrameworkStartedEvent event) {
        this.healthCheckRunner.runHealthChecks(LifecyclePhase.PLUGIN_FRAMEWORK_STARTED);
    }
}

