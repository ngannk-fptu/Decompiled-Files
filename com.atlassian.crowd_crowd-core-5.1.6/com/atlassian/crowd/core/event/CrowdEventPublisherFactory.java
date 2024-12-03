/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.event.config.EventThreadPoolConfiguration
 *  com.atlassian.event.config.ListenerHandlersConfiguration
 *  com.atlassian.event.internal.AnnotatedMethodsListenerHandler
 *  com.atlassian.event.internal.DirectEventExecutorFactory
 *  com.atlassian.event.internal.EventPublisherImpl
 *  com.atlassian.event.internal.EventThreadPoolConfigurationImpl
 *  com.atlassian.event.internal.ListenerHandlerConfigurationImpl
 *  com.atlassian.event.spi.EventDispatcher
 *  com.atlassian.event.spi.EventExecutorFactory
 *  com.atlassian.event.spi.ListenerHandler
 *  com.atlassian.plugin.event.PluginEventListener
 *  com.google.common.collect.Lists
 *  javax.annotation.Nonnull
 *  org.springframework.transaction.PlatformTransactionManager
 */
package com.atlassian.crowd.core.event;

import com.atlassian.crowd.core.event.MultiEventPublisher;
import com.atlassian.crowd.core.event.TransactionAwareEventDispatcher;
import com.atlassian.crowd.core.event.TransactionAwareEventPublisher;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.event.config.EventThreadPoolConfiguration;
import com.atlassian.event.config.ListenerHandlersConfiguration;
import com.atlassian.event.internal.AnnotatedMethodsListenerHandler;
import com.atlassian.event.internal.DirectEventExecutorFactory;
import com.atlassian.event.internal.EventPublisherImpl;
import com.atlassian.event.internal.EventThreadPoolConfigurationImpl;
import com.atlassian.event.internal.ListenerHandlerConfigurationImpl;
import com.atlassian.event.spi.EventDispatcher;
import com.atlassian.event.spi.EventExecutorFactory;
import com.atlassian.event.spi.ListenerHandler;
import com.atlassian.plugin.event.PluginEventListener;
import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nonnull;
import org.springframework.transaction.PlatformTransactionManager;

public class CrowdEventPublisherFactory {
    private final PlatformTransactionManager transactionManager;

    public CrowdEventPublisherFactory(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public MultiEventPublisher createEventPublisher() {
        EventThreadPoolConfigurationImpl eventThreadPoolConfiguration = new EventThreadPoolConfigurationImpl();
        DirectEventExecutorFactory eventExecutorFactory = new DirectEventExecutorFactory((EventThreadPoolConfiguration)eventThreadPoolConfiguration);
        TransactionAwareEventDispatcher eventDispatcher = new TransactionAwareEventDispatcher((EventExecutorFactory)eventExecutorFactory, this.transactionManager);
        ListenerHandlerConfigurationImpl listenerHandlersConfiguration = new ListenerHandlerConfigurationImpl(){

            @Nonnull
            public List<ListenerHandler> getListenerHandlers() {
                return Lists.newArrayList((Object[])new ListenerHandler[]{new AnnotatedMethodsListenerHandler(), new AnnotatedMethodsListenerHandler(PluginEventListener.class)});
            }
        };
        EventPublisherImpl publisher = new EventPublisherImpl((EventDispatcher)eventDispatcher, (ListenerHandlersConfiguration)listenerHandlersConfiguration);
        return new TransactionAwareEventPublisher((EventPublisher)publisher);
    }
}

