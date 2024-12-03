/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.events.cluster.ClusterEventWrapper
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventListenerRegistrar
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.sal.api.lifecycle.LifecycleAware
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Component
 */
package com.atlassian.mywork.host.service;

import com.atlassian.confluence.event.events.cluster.ClusterEventWrapper;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventListenerRegistrar;
import com.atlassian.mywork.host.event.ClientRegistrationEvent;
import com.atlassian.mywork.host.service.LocalRegistrationService;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import javax.annotation.ParametersAreNonnullByDefault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@ParametersAreNonnullByDefault
@ExportAsService(value={LifecycleAware.class})
@Component
public class ClientRegistrationEventListener
implements LifecycleAware {
    private static final Logger log = LoggerFactory.getLogger(ClientRegistrationEventListener.class);
    private final EventListenerRegistrar eventListenerRegistrar;
    private final LocalRegistrationService registrationService;

    public ClientRegistrationEventListener(EventListenerRegistrar eventListenerRegistrar, LocalRegistrationService registrationService) {
        this.eventListenerRegistrar = eventListenerRegistrar;
        this.registrationService = registrationService;
    }

    @EventListener
    public void onClientRegistrationEvent(ClusterEventWrapper clusterEventWrapper) {
        if (clusterEventWrapper.getEvent() instanceof ClientRegistrationEvent) {
            log.debug("Received and unwrapping clustered ClientRegistrationEvent");
            this.onClientRegistrationEvent((ClientRegistrationEvent)clusterEventWrapper.getEvent());
        } else {
            log.debug("Received uninteresting cluster event, ignoring {}", (Object)clusterEventWrapper.getEvent());
        }
    }

    @EventListener
    public void onClientRegistrationEvent(ClientRegistrationEvent event) {
        log.debug("Received ClientRegistrationEvent, propagating {} registrations to RegistrationService", (Object)event.getRegistrations().size());
        this.registrationService.register(event.getRegistrations());
    }

    public void onStart() {
        this.eventListenerRegistrar.register((Object)this);
    }

    public void onStop() {
        this.eventListenerRegistrar.unregister((Object)this);
    }
}

