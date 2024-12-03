/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.event.config.EventThreadPoolConfiguration
 *  com.atlassian.event.config.ListenerHandlersConfiguration
 *  com.atlassian.event.internal.AsynchronousAbleEventDispatcher
 *  com.atlassian.event.internal.DirectEventExecutorFactory
 *  com.atlassian.event.internal.EventPublisherImpl
 *  com.atlassian.event.internal.EventThreadPoolConfigurationImpl
 *  com.atlassian.event.spi.EventDispatcher
 *  com.atlassian.event.spi.EventExecutorFactory
 *  com.atlassian.plugin.event.NotificationException
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.scope.ScopeManager
 *  com.atlassian.plugin.util.Assertions
 */
package com.atlassian.plugin.event.impl;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.event.config.EventThreadPoolConfiguration;
import com.atlassian.event.config.ListenerHandlersConfiguration;
import com.atlassian.event.internal.AsynchronousAbleEventDispatcher;
import com.atlassian.event.internal.DirectEventExecutorFactory;
import com.atlassian.event.internal.EventPublisherImpl;
import com.atlassian.event.internal.EventThreadPoolConfigurationImpl;
import com.atlassian.event.spi.EventDispatcher;
import com.atlassian.event.spi.EventExecutorFactory;
import com.atlassian.plugin.event.NotificationException;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.event.impl.AnnotationListenerMethodSelector;
import com.atlassian.plugin.event.impl.ListenerMethodSelector;
import com.atlassian.plugin.event.impl.MethodNameListenerMethodSelector;
import com.atlassian.plugin.event.impl.MethodSelectorListenerHandler;
import com.atlassian.plugin.scope.ScopeManager;
import com.atlassian.plugin.util.Assertions;
import java.util.ArrayList;

public class DefaultPluginEventManager
implements PluginEventManager {
    private final EventPublisher eventPublisher;

    public DefaultPluginEventManager() {
        this(DefaultPluginEventManager.defaultMethodSelectors());
    }

    public DefaultPluginEventManager(ListenerMethodSelector ... selectors) {
        ListenerHandlersConfiguration configuration = () -> {
            ArrayList<MethodSelectorListenerHandler> handlers = new ArrayList<MethodSelectorListenerHandler>(selectors.length);
            for (ListenerMethodSelector selector : selectors) {
                handlers.add(new MethodSelectorListenerHandler(selector));
            }
            return handlers;
        };
        DirectEventExecutorFactory executorFactory = new DirectEventExecutorFactory((EventThreadPoolConfiguration)new EventThreadPoolConfigurationImpl());
        AsynchronousAbleEventDispatcher eventDispatcher = new AsynchronousAbleEventDispatcher((EventExecutorFactory)executorFactory);
        this.eventPublisher = new EventPublisherImpl((EventDispatcher)eventDispatcher, configuration);
    }

    @Deprecated
    public DefaultPluginEventManager(ScopeManager scopeManager) {
        this(DefaultPluginEventManager.defaultMethodSelectors());
    }

    @Deprecated
    public DefaultPluginEventManager(ScopeManager scopeManager, ListenerMethodSelector ... selectors) {
        this(selectors);
    }

    public DefaultPluginEventManager(EventPublisher eventPublisher) {
        this.eventPublisher = (EventPublisher)Assertions.notNull((String)"eventPublisher", (Object)eventPublisher);
    }

    public void register(Object listener) {
        this.eventPublisher.register(Assertions.notNull((String)"listener", (Object)listener));
    }

    public void unregister(Object listener) {
        this.eventPublisher.unregister(Assertions.notNull((String)"listener", (Object)listener));
    }

    public void broadcast(Object event) {
        Assertions.notNull((String)"event", (Object)event);
        try {
            this.eventPublisher.publish(event);
        }
        catch (RuntimeException e) {
            throw new NotificationException((Throwable)e);
        }
    }

    public EventPublisher getEventPublisher() {
        return this.eventPublisher;
    }

    static ListenerMethodSelector[] defaultMethodSelectors() {
        MethodNameListenerMethodSelector methodNames = new MethodNameListenerMethodSelector();
        AnnotationListenerMethodSelector pluginEvent = new AnnotationListenerMethodSelector();
        AnnotationListenerMethodSelector eventListener = new AnnotationListenerMethodSelector(EventListener.class);
        return new ListenerMethodSelector[]{methodNames, pluginEvent, eventListener};
    }
}

