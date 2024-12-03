/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth2.client.api.storage.config.ClientConfigStorageService
 *  io.atlassian.util.concurrent.LazyReference
 *  org.osgi.framework.BundleContext
 *  org.osgi.util.tracker.ServiceTracker
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.applinks.core.oauth2;

import com.atlassian.oauth2.client.api.storage.config.ClientConfigStorageService;
import io.atlassian.util.concurrent.LazyReference;
import java.util.Optional;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ClientConfigStorageServiceFactory
implements DisposableBean {
    private final LazyReference<ServiceTracker> trackerReference;

    @Autowired
    public ClientConfigStorageServiceFactory(final BundleContext bundleContext) {
        this.trackerReference = new LazyReference<ServiceTracker>(){

            protected ServiceTracker create() {
                ServiceTracker tracker = new ServiceTracker(bundleContext, ClientConfigStorageService.class, null);
                tracker.open();
                return tracker;
            }
        };
    }

    Optional<ClientConfigStorageService> get() {
        return Optional.ofNullable((ClientConfigStorageService)((ServiceTracker)this.trackerReference.get()).getService());
    }

    public void destroy() throws Exception {
        if (this.trackerReference.isInitialized()) {
            ((ServiceTracker)this.trackerReference.get()).close();
        }
    }
}

