/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.webhooks.WebhookRequestEnricher
 *  com.atlassian.webhooks.WebhooksConfiguration
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.ServiceRegistration
 */
package com.atlassian.webhooks.internal;

import com.atlassian.webhooks.WebhookRequestEnricher;
import com.atlassian.webhooks.WebhooksConfiguration;
import com.atlassian.webhooks.internal.WebhooksLifecycleAware;
import com.atlassian.webhooks.internal.history.WebhookInvocationHistorian;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class PlatformConfigurer
implements WebhooksLifecycleAware {
    private final BundleContext bundleContext;
    private final WebhookInvocationHistorian historian;
    private final List<ServiceRegistration<?>> serviceRegistrations;

    public PlatformConfigurer(WebhookInvocationHistorian historian, BundleContext bundleContext) {
        this.bundleContext = bundleContext;
        this.historian = historian;
        this.serviceRegistrations = new ArrayList();
    }

    @Override
    public void onStart(WebhooksConfiguration configuration) {
        if (configuration.isInvocationHistoryEnabled()) {
            ServiceRegistration registration = this.bundleContext.registerService(WebhookRequestEnricher.class, (Object)this.historian, new Hashtable());
            this.serviceRegistrations.add(registration);
        }
    }

    @Override
    public void onStop() {
        this.serviceRegistrations.forEach(ServiceRegistration::unregister);
        this.serviceRegistrations.clear();
    }
}

