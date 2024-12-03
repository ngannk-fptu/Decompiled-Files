/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.event.config.ListenerHandlersConfiguration
 *  com.atlassian.event.internal.AnnotatedMethodsListenerHandler
 *  com.atlassian.event.internal.EventPublisherImpl
 *  com.atlassian.event.legacy.LegacyListenerHandler
 *  com.atlassian.event.spi.EventDispatcher
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.BeansException
 *  org.springframework.context.ApplicationContext
 *  org.springframework.context.ApplicationContextAware
 *  org.springframework.context.ApplicationEvent
 *  org.springframework.context.ApplicationListener
 */
package com.atlassian.confluence.event;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.event.config.ListenerHandlersConfiguration;
import com.atlassian.event.internal.AnnotatedMethodsListenerHandler;
import com.atlassian.event.internal.EventPublisherImpl;
import com.atlassian.event.legacy.LegacyListenerHandler;
import com.atlassian.event.spi.EventDispatcher;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

public class ConfluenceEventPublisherManager
implements EventPublisher,
ApplicationListener,
ApplicationContextAware {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceEventPublisherManager.class);
    private EventPublisher eventPublisher;
    private ApplicationContext applicationContext;

    public ConfluenceEventPublisherManager(EventDispatcher eventDispatcher, ListenerHandlersConfiguration listenerHandlersConfiguration) {
        this.eventPublisher = new EventPublisherImpl(eventDispatcher, listenerHandlersConfiguration);
    }

    public ConfluenceEventPublisherManager(EventDispatcher eventDispatcher) {
        this(eventDispatcher, () -> List.of(new LegacyListenerHandler(), new AnnotatedMethodsListenerHandler()));
    }

    public void publish(Object o) {
        if (o instanceof ApplicationEvent) {
            this.applicationContext.publishEvent((ApplicationEvent)o);
        } else {
            this.eventPublisher.publish(o);
        }
    }

    public void register(Object o) {
        this.eventPublisher.register(o);
    }

    public void unregister(Object o) {
        this.eventPublisher.unregister(o);
    }

    public void unregisterAll() {
        this.eventPublisher.unregisterAll();
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void onApplicationEvent(ApplicationEvent event) {
        if (log.isDebugEnabled()) {
            log.debug("ApplicationEvent received: " + event);
        }
        this.eventPublisher.publish((Object)event);
    }
}

