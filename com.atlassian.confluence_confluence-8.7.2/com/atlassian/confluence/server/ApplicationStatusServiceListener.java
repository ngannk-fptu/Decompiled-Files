/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.lifecycle.events.ApplicationStoppingEvent
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventListenerRegistrar
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.confluence.server;

import com.atlassian.config.lifecycle.events.ApplicationStoppingEvent;
import com.atlassian.confluence.server.ApplicationState;
import com.atlassian.confluence.server.MutableApplicationStatusService;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventListenerRegistrar;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class ApplicationStatusServiceListener
implements InitializingBean,
DisposableBean {
    MutableApplicationStatusService applicationStatusService;
    EventListenerRegistrar eventListenerRegistrar;

    public ApplicationStatusServiceListener(MutableApplicationStatusService applicationStatusService, EventListenerRegistrar eventListenerRegistrar) {
        this.applicationStatusService = applicationStatusService;
        this.eventListenerRegistrar = eventListenerRegistrar;
    }

    public void afterPropertiesSet() throws Exception {
        this.eventListenerRegistrar.register((Object)this);
    }

    @EventListener
    public void onApplicationStoppingEvent(ApplicationStoppingEvent e) {
        this.applicationStatusService.setState(ApplicationState.STOPPING);
    }

    public void destroy() throws Exception {
        this.eventListenerRegistrar.unregister((Object)this);
    }
}

