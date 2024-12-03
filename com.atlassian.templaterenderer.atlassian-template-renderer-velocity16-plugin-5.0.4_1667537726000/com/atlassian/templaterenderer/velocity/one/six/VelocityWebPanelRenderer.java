/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.templaterenderer.TemplateContextFactory
 *  com.atlassian.templaterenderer.TemplateRenderer
 */
package com.atlassian.templaterenderer.velocity.one.six;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.templaterenderer.TemplateContextFactory;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.atlassian.templaterenderer.velocity.AbstractCachingWebPanelRenderer;
import com.atlassian.templaterenderer.velocity.one.six.internal.VelocityTemplateRendererImpl;
import java.util.Collections;

public class VelocityWebPanelRenderer
extends AbstractCachingWebPanelRenderer {
    private final EventPublisher eventPublisher;
    private final TemplateContextFactory templateContextFactory;

    public VelocityWebPanelRenderer(EventPublisher eventPublisher, TemplateContextFactory templateContextFactory, PluginEventManager pluginEventManager) {
        super(pluginEventManager);
        this.eventPublisher = eventPublisher;
        this.templateContextFactory = templateContextFactory;
    }

    @Override
    protected TemplateRenderer createRenderer(Plugin plugin) {
        return new VelocityTemplateRendererImpl(plugin.getClassLoader(), this.eventPublisher, plugin.getKey(), Collections.emptyMap(), this.templateContextFactory);
    }
}

