/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.event.Event
 *  com.atlassian.event.EventListener
 *  com.atlassian.event.EventManager
 *  com.atlassian.event.legacy.SpringContextEventPublisher
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.event;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.event.events.exception.ConfluenceEventPropagatingException;
import com.atlassian.event.Event;
import com.atlassian.event.EventListener;
import com.atlassian.event.EventManager;
import com.atlassian.event.legacy.SpringContextEventPublisher;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

@Deprecated
@ParametersAreNonnullByDefault
@Transactional
public class ConfluenceEventManager
implements EventManager {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceEventManager.class);
    private final EventManager delegateEventManager;

    public ConfluenceEventManager(EventManager delegateEventManager, SpringContextEventPublisher springContextEventPublisher) {
        Objects.requireNonNull(delegateEventManager);
        Objects.requireNonNull(springContextEventPublisher);
        this.delegateEventManager = delegateEventManager;
        this.registerListener(springContextEventPublisher.getClass().getName(), (EventListener)springContextEventPublisher);
    }

    public void publishEvent(Event event) {
        try {
            this.delegateEventManager.publishEvent(event);
        }
        catch (ConfluenceEventPropagatingException e) {
            throw e;
        }
        catch (Exception e) {
            log.error("An exception was encountered while processing the event: " + event, (Throwable)e);
        }
    }

    public void registerListener(String key, EventListener eventListener) {
        this.delegateEventManager.registerListener(key, eventListener);
    }

    public void unregisterListener(String key) {
        this.delegateEventManager.unregisterListener(key);
    }
}

