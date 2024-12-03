/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.services.AnalyticsConfigService
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.ServiceReference
 */
package com.atlassian.confluence.plugins.analytics.jobs;

import com.atlassian.analytics.api.services.AnalyticsConfigService;
import java.util.Optional;
import java.util.function.Function;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

final class ServiceTrackingAnalyticsConfigService
implements AnalyticsConfigService {
    private final BundleContext bundleContext;

    ServiceTrackingAnalyticsConfigService(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    private ServiceProvider getServiceProvider() {
        final Optional<ServiceReference<AnalyticsConfigService>> serviceReference = this.getServiceReference();
        return new ServiceProvider(){

            @Override
            public Optional<AnalyticsConfigService> getAnalyticsConfigService() {
                return serviceReference.map(arg_0 -> ((BundleContext)ServiceTrackingAnalyticsConfigService.this.bundleContext).getService(arg_0));
            }

            @Override
            public void close() {
                serviceReference.ifPresent(arg_0 -> ((BundleContext)ServiceTrackingAnalyticsConfigService.this.bundleContext).ungetService(arg_0));
            }
        };
    }

    private Optional<ServiceReference<AnalyticsConfigService>> getServiceReference() {
        return Optional.ofNullable(this.bundleContext.getServiceReference(AnalyticsConfigService.class));
    }

    public boolean isAnalyticsEnabled() {
        return this.getOrDefault(AnalyticsConfigService::isAnalyticsEnabled, false);
    }

    public boolean canCollectAnalytics() {
        return this.getOrDefault(AnalyticsConfigService::canCollectAnalytics, false);
    }

    private <T> T getOrDefault(Function<AnalyticsConfigService, T> f, T defaultValue) {
        try (ServiceProvider serviceProvider = this.getServiceProvider();){
            T t = serviceProvider.getAnalyticsConfigService().map(f).orElse(defaultValue);
            return t;
        }
    }

    private static interface ServiceProvider
    extends AutoCloseable {
        public Optional<AnalyticsConfigService> getAnalyticsConfigService();

        @Override
        public void close();
    }
}

