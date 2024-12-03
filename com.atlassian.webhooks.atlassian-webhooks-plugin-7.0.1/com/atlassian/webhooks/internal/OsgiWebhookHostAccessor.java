/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.lifecycle.LifecycleAware
 *  com.atlassian.webhooks.WebhookEvent
 *  com.atlassian.webhooks.WebhookEventProvider
 *  com.atlassian.webhooks.WebhookFilter
 *  com.atlassian.webhooks.WebhookPayloadProvider
 *  com.atlassian.webhooks.WebhookRequestEnricher
 *  com.atlassian.webhooks.WebhooksConfiguration
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  io.atlassian.util.concurrent.ThreadFactories
 *  io.atlassian.util.concurrent.ThreadFactories$Type
 *  javax.annotation.Nonnull
 *  javax.annotation.PreDestroy
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.ServiceReference
 *  org.osgi.util.tracker.ServiceTracker
 *  org.osgi.util.tracker.ServiceTrackerCustomizer
 */
package com.atlassian.webhooks.internal;

import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.webhooks.WebhookEvent;
import com.atlassian.webhooks.WebhookEventProvider;
import com.atlassian.webhooks.WebhookFilter;
import com.atlassian.webhooks.WebhookPayloadProvider;
import com.atlassian.webhooks.WebhookRequestEnricher;
import com.atlassian.webhooks.WebhooksConfiguration;
import com.atlassian.webhooks.internal.WebhookHostAccessor;
import com.atlassian.webhooks.internal.model.UnknownWebhookEvent;
import com.google.common.collect.ImmutableList;
import io.atlassian.util.concurrent.ThreadFactories;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.PreDestroy;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public class OsgiWebhookHostAccessor
implements WebhookHostAccessor,
LifecycleAware {
    private final Object lock = new Object();
    private final BundleContext bundleContext;
    private final ServiceTracker<WebhooksConfiguration, WebhooksConfiguration> configurationTracker;
    private final ServiceTracker<WebhookRequestEnricher, WebhookRequestEnricher> enricherTracker;
    private final SortedSet<WebhookRequestEnricher> enrichers;
    private final ServiceTracker<WebhookEventProvider, WebhookEventProvider> eventProviderTracker;
    private final SortedSet<WebhookEventProvider> eventProviders;
    private final ServiceTracker<ScheduledExecutorService, ScheduledExecutorService> executorServiceTracker;
    private final ServiceTracker<WebhookFilter, WebhookFilter> filterTracker;
    private final SortedSet<WebhookFilter> filters;
    private final ServiceTracker<WebhookPayloadProvider, WebhookPayloadProvider> payloadProviderTracker;
    private final SortedSet<WebhookPayloadProvider> payloadProviders;
    private volatile ScheduledExecutorService executorService;
    private volatile boolean stopped;

    public OsgiWebhookHostAccessor(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
        this.configurationTracker = new ServiceTracker(bundleContext, WebhooksConfiguration.class, null);
        this.configurationTracker.open();
        this.enrichers = new ConcurrentSkipListSet<WebhookRequestEnricher>(OsgiWebhookHostAccessor.getComparator(WebhookRequestEnricher::getWeight));
        this.enricherTracker = new ServiceTracker(bundleContext, WebhookRequestEnricher.class, new CachingCustomiser(this.enrichers));
        this.enricherTracker.open();
        this.eventProviders = new ConcurrentSkipListSet<WebhookEventProvider>(OsgiWebhookHostAccessor.getComparator(WebhookEventProvider::getWeight));
        this.eventProviderTracker = new ServiceTracker(bundleContext, WebhookEventProvider.class, new CachingCustomiser(this.eventProviders));
        this.eventProviderTracker.open();
        this.filters = new ConcurrentSkipListSet<WebhookFilter>(OsgiWebhookHostAccessor.getComparator(WebhookFilter::getWeight));
        this.filterTracker = new ServiceTracker(bundleContext, WebhookFilter.class, new CachingCustomiser(this.filters));
        this.filterTracker.open();
        this.payloadProviders = new ConcurrentSkipListSet<WebhookPayloadProvider>(OsgiWebhookHostAccessor.getComparator(WebhookPayloadProvider::getWeight));
        this.payloadProviderTracker = new ServiceTracker(bundleContext, WebhookPayloadProvider.class, new CachingCustomiser(this.payloadProviders));
        this.payloadProviderTracker.open();
        this.executorServiceTracker = new ServiceTracker(bundleContext, ScheduledExecutorService.class, null);
    }

    @PreDestroy
    public void destroy() {
        this.enricherTracker.close();
        this.eventProviderTracker.close();
        this.filterTracker.close();
        this.payloadProviderTracker.close();
        this.configurationTracker.close();
    }

    @Override
    @Nonnull
    public Optional<WebhooksConfiguration> getConfiguration() {
        return Optional.ofNullable(this.configurationTracker.getService());
    }

    @Override
    @Nonnull
    public Collection<WebhookRequestEnricher> getEnrichers() {
        return this.enrichers;
    }

    @Override
    @Nonnull
    public WebhookEvent getEvent(@Nonnull String id) {
        Objects.requireNonNull(id, "eventId");
        for (WebhookEventProvider provider : this.eventProviders) {
            WebhookEvent type = provider.forId(id);
            if (type == null) continue;
            return type;
        }
        return new UnknownWebhookEvent(id);
    }

    @Override
    @Nonnull
    public List<WebhookEvent> getEvents() {
        ImmutableList.Builder builder = ImmutableList.builder();
        this.eventProviders.stream().map(WebhookEventProvider::getEvents).filter(Objects::nonNull).flatMap(Collection::stream).filter(Objects::nonNull).sorted(Comparator.comparing(WebhookEvent::getId)).forEach(arg_0 -> ((ImmutableList.Builder)builder).add(arg_0));
        return builder.build();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    @Nonnull
    public ScheduledExecutorService getExecutorService() {
        if (this.executorService != null) {
            return this.executorService;
        }
        ScheduledExecutorService result = (ScheduledExecutorService)this.executorServiceTracker.getService();
        if (result != null) {
            return result;
        }
        Object object = this.lock;
        synchronized (object) {
            if (this.stopped) {
                throw new IllegalStateException("The webhooks plugin has been stopped");
            }
            if (this.executorService == null) {
                this.executorService = new ScheduledThreadPoolExecutor(1, ThreadFactories.namedThreadFactory((String)"atlassian-webhooks-scheduler", (ThreadFactories.Type)ThreadFactories.Type.DAEMON));
            }
            return this.executorService;
        }
    }

    @Override
    @Nonnull
    public Collection<WebhookFilter> getFilters() {
        return this.filters;
    }

    @Override
    @Nonnull
    public Collection<WebhookPayloadProvider> getPayloadProviders() {
        return this.payloadProviders;
    }

    public void onStart() {
        this.stopped = false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void onStop() {
        Object object = this.lock;
        synchronized (object) {
            this.stopped = true;
            if (this.executorService != null) {
                this.executorService.shutdown();
                this.executorService = null;
            }
        }
    }

    private static <T> Comparator<T> getComparator(Function<T, Integer> weightFunction) {
        return (o1, o2) -> {
            int weight = ((Integer)weightFunction.apply(o1)).compareTo((Integer)weightFunction.apply(o2));
            if (weight != 0) {
                return weight;
            }
            return o1.getClass().getCanonicalName().compareTo(o2.getClass().getCanonicalName());
        };
    }

    private class CachingCustomiser<T>
    implements ServiceTrackerCustomizer<T, T> {
        private final SortedSet<T> cache;

        private CachingCustomiser(SortedSet<T> cache) {
            this.cache = cache;
        }

        public T addingService(ServiceReference<T> serviceReference) {
            Object service = OsgiWebhookHostAccessor.this.bundleContext.getService(serviceReference);
            this.cache.add(service);
            return (T)service;
        }

        public void modifiedService(ServiceReference<T> serviceReference, T t) {
        }

        public void removedService(ServiceReference<T> serviceReference, T t) {
            this.cache.remove(t);
            OsgiWebhookHostAccessor.this.bundleContext.ungetService(serviceReference);
        }
    }
}

