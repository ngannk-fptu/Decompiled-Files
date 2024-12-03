/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.templaterenderer.TemplateContextFactory
 *  com.atlassian.templaterenderer.TemplateRenderer
 */
package com.atlassian.templaterenderer.velocity.one.six.internal;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.templaterenderer.TemplateContextFactory;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.atlassian.templaterenderer.velocity.one.six.VelocityTemplateRendererFactory;
import com.atlassian.templaterenderer.velocity.one.six.internal.VelocityTemplateRendererImpl;
import java.util.Collections;
import java.util.Map;

public class VelocityTemplateRendererFactoryImpl
implements VelocityTemplateRendererFactory {
    private final TemplateContextFactory templateContextFactory;
    private final EventPublisher eventPublisher;
    private final String pluginKey;
    private final ClassLoader classLoader;

    public VelocityTemplateRendererFactoryImpl(TemplateContextFactory templateContextFactory, EventPublisher eventPublisher, String pluginKey, ClassLoader classLoader) {
        this.templateContextFactory = templateContextFactory;
        this.eventPublisher = eventPublisher;
        this.pluginKey = pluginKey;
        this.classLoader = classLoader;
    }

    public TemplateRenderer getInstance(ClassLoader classLoader) {
        return this.getInstance(classLoader, Collections.emptyMap());
    }

    @Override
    public TemplateRenderer getInstance(ClassLoader classLoader, Map<String, String> properties) {
        return new VelocityTemplateRendererImpl(classLoader, this.eventPublisher, this.pluginKey, properties, this.templateContextFactory);
    }

    @Override
    public TemplateRenderer getInstance(Map<String, String> properties) {
        return new VelocityTemplateRendererImpl(this.classLoader, this.eventPublisher, this.pluginKey, properties, this.templateContextFactory);
    }
}

