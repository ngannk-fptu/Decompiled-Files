/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth2.provider.api.external.OAuth2ProviderService
 *  com.atlassian.oauth2.scopes.api.ScopeDescriptionService
 *  io.atlassian.util.concurrent.LazyReference
 *  org.osgi.framework.BundleContext
 *  org.osgi.util.tracker.ServiceTracker
 *  org.springframework.beans.factory.DisposableBean
 */
package com.atlassian.oauth.serviceprovider.internal.oauth2;

import com.atlassian.oauth2.provider.api.external.OAuth2ProviderService;
import com.atlassian.oauth2.scopes.api.ScopeDescriptionService;
import io.atlassian.util.concurrent.LazyReference;
import java.util.Optional;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.springframework.beans.factory.DisposableBean;

public class OAuth2OsgiServiceFactory
implements DisposableBean {
    private final LazyReference<ServiceTracker> oauth2ProviderServiceTracker;
    private final LazyReference<ServiceTracker> scopeDescriptionServiceTracker;

    public OAuth2OsgiServiceFactory(final BundleContext bundleContext) {
        this.oauth2ProviderServiceTracker = new LazyReference<ServiceTracker>(){

            protected ServiceTracker create() {
                ServiceTracker tracker = new ServiceTracker(bundleContext, OAuth2ProviderService.class, null);
                tracker.open();
                return tracker;
            }
        };
        this.scopeDescriptionServiceTracker = new LazyReference<ServiceTracker>(){

            protected ServiceTracker create() {
                ServiceTracker tracker = new ServiceTracker(bundleContext, ScopeDescriptionService.class, null);
                tracker.open();
                return tracker;
            }
        };
    }

    public Optional<OAuth2ProviderService> getOAuth2ProviderService() {
        return Optional.ofNullable((OAuth2ProviderService)((ServiceTracker)this.oauth2ProviderServiceTracker.get()).getService());
    }

    public Optional<ScopeDescriptionService> getScopeDescriptionService() {
        return Optional.ofNullable((ScopeDescriptionService)((ServiceTracker)this.scopeDescriptionServiceTracker.get()).getService());
    }

    public void destroy() throws Exception {
        if (this.oauth2ProviderServiceTracker.isInitialized()) {
            ((ServiceTracker)this.oauth2ProviderServiceTracker.get()).close();
        }
        if (this.scopeDescriptionServiceTracker.isInitialized()) {
            ((ServiceTracker)this.scopeDescriptionServiceTracker.get()).close();
        }
    }
}

