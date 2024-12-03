/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.spring.AvailableToPlugins
 *  com.atlassian.renderer.WikiStyleRenderer
 *  javax.annotation.Resource
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.confluence.content.render.xhtml;

import com.atlassian.confluence.content.render.xhtml.DefaultRenderer;
import com.atlassian.confluence.content.render.xhtml.DeviceTypeAwareRenderer;
import com.atlassian.confluence.content.render.xhtml.Renderer;
import com.atlassian.confluence.content.render.xhtml.RenderingEventPublisher;
import com.atlassian.confluence.content.render.xhtml.compatibility.BodyTypeAwareRenderer;
import com.atlassian.confluence.content.render.xhtml.compatibility.LegacyV2RendererContextInitialiser;
import com.atlassian.confluence.content.render.xhtml.transformers.Transformer;
import com.atlassian.confluence.impl.content.render.prefetch.ContentResourcePrefetcher;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.spring.AvailableToPlugins;
import com.atlassian.renderer.WikiStyleRenderer;
import javax.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class RendererContextConfig {
    @Resource
    private I18NBeanFactory userI18NBeanFactory;
    @Resource
    private SettingsManager settingsManager;
    @Resource
    private LegacyV2RendererContextInitialiser legacyV2RendererContextInitialiser;
    @Resource
    private Transformer storageToViewTransformer;
    @Resource
    private Transformer storageToEditorTransformer;
    @Resource
    private RenderingEventPublisher marshallerMetricsAnalyticsEventPublisher;
    @Resource
    private ContentResourcePrefetcher contentResourcePrefetcher;
    @Resource
    private PluginEventManager pluginEventManager;
    @Resource
    private WikiStyleRenderer wikiStyleRenderer;

    RendererContextConfig() {
    }

    @Bean
    @AvailableToPlugins
    Renderer viewRenderer() {
        return new DeviceTypeAwareRenderer(new DefaultRenderer(this.storageToViewTransformer, this.userI18NBeanFactory, this.legacyV2RendererContextInitialiser, this.settingsManager, this.marshallerMetricsAnalyticsEventPublisher, this.contentResourcePrefetcher), this.pluginEventManager);
    }

    @Bean
    Renderer editRenderer() {
        return new DefaultRenderer(this.storageToEditorTransformer, this.userI18NBeanFactory, this.legacyV2RendererContextInitialiser, this.settingsManager, this.marshallerMetricsAnalyticsEventPublisher);
    }

    @Bean
    Renderer viewBodyTypeAwareRenderer() {
        return new BodyTypeAwareRenderer(this.viewRenderer(), this.wikiStyleRenderer);
    }
}

