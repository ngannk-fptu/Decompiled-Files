/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.event.config.EventThreadPoolConfiguration
 *  com.atlassian.event.config.ListenerHandlersConfiguration
 *  com.atlassian.event.internal.AsynchronousAbleEventDispatcher
 *  com.atlassian.event.internal.EventExecutorFactoryImpl
 *  com.atlassian.event.internal.EventPublisherImpl
 *  com.atlassian.event.internal.EventThreadPoolConfigurationImpl
 *  com.atlassian.event.internal.ListenerHandlerConfigurationImpl
 *  com.atlassian.event.spi.EventDispatcher
 *  com.atlassian.event.spi.EventExecutorFactory
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.ServiceFactory
 *  org.osgi.framework.ServiceRegistration
 */
package com.atlassian.upm.license.internal.event;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.event.config.EventThreadPoolConfiguration;
import com.atlassian.event.config.ListenerHandlersConfiguration;
import com.atlassian.event.internal.AsynchronousAbleEventDispatcher;
import com.atlassian.event.internal.EventExecutorFactoryImpl;
import com.atlassian.event.internal.EventPublisherImpl;
import com.atlassian.event.internal.EventThreadPoolConfigurationImpl;
import com.atlassian.event.internal.ListenerHandlerConfigurationImpl;
import com.atlassian.event.spi.EventDispatcher;
import com.atlassian.event.spi.EventExecutorFactory;
import com.atlassian.upm.license.internal.PluginLicenseRepository;
import com.atlassian.upm.license.internal.event.PluginLicenseEventPublisherImpl;
import com.atlassian.upm.license.internal.event.PluginLicenseEventPublisherRegistry;
import com.atlassian.upm.license.internal.impl.PluginKeyAccessor;
import java.util.Objects;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;

public class PluginLicenseEventPublisherServiceFactory
extends PluginKeyAccessor
implements ServiceFactory {
    private final PluginLicenseEventPublisherRegistry publisherRegistry;
    private final EventDispatcher eventDispatcher;
    private final ListenerHandlersConfiguration listenerHandlersConfiguration;
    private final PluginLicenseRepository repository;

    public PluginLicenseEventPublisherServiceFactory(PluginLicenseEventPublisherRegistry publisherRegistry, PluginLicenseRepository repository) {
        this.publisherRegistry = Objects.requireNonNull(publisherRegistry, "publisherRegistry");
        this.repository = Objects.requireNonNull(repository, "repository");
        EventThreadPoolConfigurationImpl eventThreadPoolConfiguration = new EventThreadPoolConfigurationImpl();
        EventExecutorFactoryImpl eventExecutorFactory = new EventExecutorFactoryImpl((EventThreadPoolConfiguration)eventThreadPoolConfiguration);
        this.eventDispatcher = new AsynchronousAbleEventDispatcher((EventExecutorFactory)eventExecutorFactory);
        this.listenerHandlersConfiguration = new ListenerHandlerConfigurationImpl();
    }

    public Object getService(Bundle bundle, ServiceRegistration serviceRegistration) {
        EventPublisherImpl underlyingPublisher = new EventPublisherImpl(this.eventDispatcher, this.listenerHandlersConfiguration);
        PluginLicenseEventPublisherImpl publisher = new PluginLicenseEventPublisherImpl((EventPublisher)underlyingPublisher, this.getPluginKey(bundle));
        this.publisherRegistry.register(this.getPluginKey(bundle), publisher);
        return publisher;
    }

    public void ungetService(Bundle bundle, ServiceRegistration serviceRegistration, Object o) {
        this.publisherRegistry.unregister(this.getPluginKey(bundle));
    }
}

