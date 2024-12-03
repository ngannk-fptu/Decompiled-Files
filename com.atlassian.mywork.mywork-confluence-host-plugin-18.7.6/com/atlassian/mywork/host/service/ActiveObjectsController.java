/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.event.api.AsynchronousPreferred
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.stereotype.Component
 */
package com.atlassian.mywork.host.service;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.event.api.AsynchronousPreferred;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.mywork.host.event.ActiveObjectsInitializedEvent;
import java.io.Serializable;
import java.util.concurrent.ExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
public class ActiveObjectsController
implements InitializingBean,
DisposableBean {
    private static final Logger log = LoggerFactory.getLogger(ActiveObjectsController.class);
    private final EventPublisher eventPublisher;
    private final ActiveObjects activeObjects;

    public ActiveObjectsController(EventPublisher eventPublisher, ActiveObjects activeObjects) {
        this.eventPublisher = eventPublisher;
        this.activeObjects = activeObjects;
    }

    public void monitorInitialization() {
        this.eventPublisher.publish((Object)new AsyncEvent());
    }

    @EventListener
    public void onActiveObjectsModuleEnabled(AsyncEvent event) throws ExecutionException, InterruptedException {
        log.debug("Waiting Active objects initialization...");
        this.activeObjects.moduleMetaData().awaitInitialization();
        log.debug("Active objects initialized.");
        this.eventPublisher.publish((Object)new ActiveObjectsInitializedEvent());
    }

    public boolean isInitialized() {
        return this.activeObjects.moduleMetaData().isInitialized();
    }

    public void afterPropertiesSet() {
        this.eventPublisher.register((Object)this);
    }

    public void destroy() {
        this.eventPublisher.unregister((Object)this);
    }

    @AsynchronousPreferred
    private class AsyncEvent
    implements Serializable {
        private AsyncEvent() {
        }
    }
}

