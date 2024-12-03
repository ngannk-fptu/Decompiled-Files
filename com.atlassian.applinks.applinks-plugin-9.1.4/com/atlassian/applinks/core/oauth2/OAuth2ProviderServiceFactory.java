/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth2.provider.api.external.OAuth2ProviderService
 *  io.atlassian.util.concurrent.LazyReference
 *  org.osgi.framework.BundleContext
 *  org.osgi.util.tracker.ServiceTracker
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.applinks.core.oauth2;

import com.atlassian.oauth2.provider.api.external.OAuth2ProviderService;
import io.atlassian.util.concurrent.LazyReference;
import java.util.Optional;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OAuth2ProviderServiceFactory
implements DisposableBean {
    private final LazyReference<ServiceTracker> trackerReference;

    @Autowired
    public OAuth2ProviderServiceFactory(final BundleContext bundleContext) {
        this.trackerReference = new LazyReference<ServiceTracker>(){

            protected ServiceTracker create() {
                ServiceTracker tracker = new ServiceTracker(bundleContext, OAuth2ProviderService.class, null);
                tracker.open();
                return tracker;
            }
        };
    }

    Optional<OAuth2ProviderService> get() {
        return Optional.ofNullable((OAuth2ProviderService)((ServiceTracker)this.trackerReference.get()).getService());
    }

    public void destroy() throws Exception {
        if (this.trackerReference.isInitialized()) {
            ((ServiceTracker)this.trackerReference.get()).close();
        }
    }
}

