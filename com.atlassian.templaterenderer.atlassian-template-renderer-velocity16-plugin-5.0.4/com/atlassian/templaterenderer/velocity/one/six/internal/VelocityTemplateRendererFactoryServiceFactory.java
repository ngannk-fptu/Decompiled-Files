/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.templaterenderer.BundleClassLoaderAccessor
 *  com.atlassian.templaterenderer.TemplateContextFactory
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.ServiceFactory
 *  org.osgi.framework.ServiceRegistration
 */
package com.atlassian.templaterenderer.velocity.one.six.internal;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.templaterenderer.BundleClassLoaderAccessor;
import com.atlassian.templaterenderer.TemplateContextFactory;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.atlassian.templaterenderer.velocity.one.six.VelocityTemplateRendererFactory;
import com.atlassian.templaterenderer.velocity.one.six.internal.VelocityTemplateRendererFactoryImpl;
import java.util.Map;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;

public class VelocityTemplateRendererFactoryServiceFactory
implements ServiceFactory,
VelocityTemplateRendererFactory {
    private static final String ATLASSIAN_PLUGIN_KEY = "Atlassian-Plugin-Key";
    private final TemplateContextFactory templateContextFactory;
    private final EventPublisher eventPublisher;

    public VelocityTemplateRendererFactoryServiceFactory(TemplateContextFactory templateContextFactory, EventPublisher eventPublisher) {
        this.templateContextFactory = templateContextFactory;
        this.eventPublisher = eventPublisher;
    }

    public Object getService(Bundle bundle, ServiceRegistration serviceRegistration) {
        String pluginKey = (String)bundle.getHeaders().get(ATLASSIAN_PLUGIN_KEY);
        ClassLoader bundleClassLoader = BundleClassLoaderAccessor.getClassLoader((Bundle)bundle);
        return new VelocityTemplateRendererFactoryImpl(this.templateContextFactory, this.eventPublisher, pluginKey, bundleClassLoader);
    }

    public void ungetService(Bundle bundle, ServiceRegistration serviceRegistration, Object o) {
    }

    @Override
    public TemplateRenderer getInstance(ClassLoader classLoader, Map<String, String> properties) {
        throw new UnsupportedOperationException();
    }

    @Override
    public TemplateRenderer getInstance(Map<String, String> properties) {
        throw new UnsupportedOperationException();
    }

    public TemplateRenderer getInstance(ClassLoader classLoader) {
        throw new UnsupportedOperationException();
    }
}

