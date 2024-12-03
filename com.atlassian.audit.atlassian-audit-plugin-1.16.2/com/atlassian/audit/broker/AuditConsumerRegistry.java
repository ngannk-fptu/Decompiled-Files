/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditConsumer
 *  com.atlassian.event.api.EventPublisher
 *  javax.annotation.Nonnull
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.InvalidSyntaxException
 *  org.osgi.framework.ServiceReference
 *  org.osgi.util.tracker.ServiceTracker
 *  org.osgi.util.tracker.ServiceTrackerCustomizer
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.audit.broker;

import com.atlassian.audit.api.AuditConsumer;
import com.atlassian.audit.event.AuditConsumerAddedEvent;
import com.atlassian.audit.event.AuditConsumerRemovedEvent;
import com.atlassian.event.api.EventPublisher;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nonnull;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class AuditConsumerRegistry
implements ServiceTrackerCustomizer<AuditConsumer, AuditConsumer>,
InitializingBean,
DisposableBean {
    private static final Logger log = LoggerFactory.getLogger(AuditConsumerRegistry.class);
    private final Set<AuditConsumer> consumers = Collections.newSetFromMap(new ConcurrentHashMap());
    private final EventPublisher eventPublisher;
    private final BundleContext bundleContext;
    private volatile ServiceTracker<AuditConsumer, AuditConsumer> serviceTracker;

    public AuditConsumerRegistry(@Nonnull EventPublisher eventPublisher, @Nonnull BundleContext bundleContext) {
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
        this.bundleContext = Objects.requireNonNull(bundleContext);
    }

    public void registerConsumer(@Nonnull AuditConsumer consumer) {
        if (!this.consumers.add(consumer)) {
            log.debug("AuditConsumer of type {} has already been tracked", (Object)consumer.getClass().getName());
            return;
        }
        this.eventPublisher.publish((Object)new AuditConsumerAddedEvent(consumer));
    }

    public void removeConsumer(@Nonnull AuditConsumer consumer) {
        if (!this.consumers.remove(consumer)) {
            log.debug("Removed service AuditConsumer of type {} was not being tracked", (Object)consumer.getClass().getName());
        }
        this.eventPublisher.publish((Object)new AuditConsumerRemovedEvent(consumer));
    }

    public void startTrackingAndAddInitialConsumers() {
        this.serviceTracker = new ServiceTracker(this.bundleContext, AuditConsumer.class, (ServiceTrackerCustomizer)this);
        this.serviceTracker.open();
        try {
            this.bundleContext.getServiceReferences(AuditConsumer.class, null).forEach(this::addingService);
        }
        catch (InvalidSyntaxException e) {
            log.error("Failed to register AuditConsumer", (Throwable)e);
        }
    }

    public AuditConsumer addingService(ServiceReference<AuditConsumer> serviceReference) {
        AuditConsumer consumer = (AuditConsumer)this.bundleContext.getService(serviceReference);
        if (consumer == null) {
            log.debug("Failed to resolve AuditConsumer from bundle {} for reference {}", (Object)serviceReference.getBundle(), serviceReference);
            return null;
        }
        this.registerConsumer(consumer);
        return consumer;
    }

    public void modifiedService(ServiceReference<AuditConsumer> serviceReference, AuditConsumer consumer) {
    }

    public void removedService(ServiceReference<AuditConsumer> serviceReference, AuditConsumer consumerService) {
        this.bundleContext.ungetService(serviceReference);
        this.removeConsumer(consumerService);
    }

    public void afterPropertiesSet() {
        this.startTrackingAndAddInitialConsumers();
    }

    public void destroy() {
        if (this.serviceTracker != null) {
            this.serviceTracker.close();
        }
    }
}

