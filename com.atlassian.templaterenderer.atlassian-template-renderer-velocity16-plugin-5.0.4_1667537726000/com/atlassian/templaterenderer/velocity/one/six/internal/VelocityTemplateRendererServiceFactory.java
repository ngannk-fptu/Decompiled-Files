/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.templaterenderer.BundleClassLoaderAccessor
 *  com.atlassian.templaterenderer.TemplateContextFactory
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.ServiceFactory
 *  org.osgi.framework.ServiceRegistration
 */
package com.atlassian.templaterenderer.velocity.one.six.internal;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.templaterenderer.BundleClassLoaderAccessor;
import com.atlassian.templaterenderer.TemplateContextFactory;
import com.atlassian.templaterenderer.velocity.one.six.internal.VelocityTemplateRendererImpl;
import java.util.Collections;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;

public class VelocityTemplateRendererServiceFactory
implements ServiceFactory {
    private static final String ATLASSIAN_PLUGIN_KEY = "Atlassian-Plugin-Key";
    private final EventPublisher eventPublisher;
    private final TemplateContextFactory templateContextFactory;

    public VelocityTemplateRendererServiceFactory(EventPublisher eventPublisher, TemplateContextFactory templateContextFactory) {
        this.eventPublisher = eventPublisher;
        this.templateContextFactory = templateContextFactory;
    }

    public Object getService(Bundle bundle, ServiceRegistration serviceRegistration) {
        String pluginKey = (String)bundle.getHeaders().get(ATLASSIAN_PLUGIN_KEY);
        ClassLoader bundleClassLoader = BundleClassLoaderAccessor.getClassLoader((Bundle)bundle);
        return new VelocityTemplateRendererImpl(bundleClassLoader, this.eventPublisher, pluginKey, Collections.emptyMap(), this.templateContextFactory);
    }

    public void ungetService(Bundle bundle, ServiceRegistration serviceRegistration, Object service) {
    }
}

