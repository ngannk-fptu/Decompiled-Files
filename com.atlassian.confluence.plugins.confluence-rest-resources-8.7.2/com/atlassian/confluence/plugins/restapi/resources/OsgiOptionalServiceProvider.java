/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.PreDestroy
 *  org.osgi.framework.BundleContext
 *  org.osgi.util.tracker.ServiceTracker
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.restapi.resources;

import com.atlassian.confluence.plugins.restapi.resources.OptionalServiceProvider;
import java.util.Optional;
import javax.annotation.PreDestroy;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="optionalServiceProvider")
public class OsgiOptionalServiceProvider
implements OptionalServiceProvider {
    private final ServiceTracker<Object, Object> invocationHistoryServiceTracker;
    private final ServiceTracker<Object, Object> webhooksServiceTracker;

    @Autowired
    public OsgiOptionalServiceProvider(BundleContext bundleContext) {
        this.invocationHistoryServiceTracker = new ServiceTracker(bundleContext, "com.atlassian.webhooks.history.InvocationHistoryService", null);
        this.webhooksServiceTracker = new ServiceTracker(bundleContext, "com.atlassian.webhooks.WebhookService", null);
        this.invocationHistoryServiceTracker.open();
        this.webhooksServiceTracker.open();
    }

    @PreDestroy
    public void onDestroy() {
        this.invocationHistoryServiceTracker.close();
        this.webhooksServiceTracker.close();
    }

    @Override
    public Optional<Object> getInvocationHistoryService() {
        return Optional.ofNullable(this.invocationHistoryServiceTracker.getService());
    }

    @Override
    public Optional<Object> getWebhookService() {
        return Optional.ofNullable(this.webhooksServiceTracker.getService());
    }
}

