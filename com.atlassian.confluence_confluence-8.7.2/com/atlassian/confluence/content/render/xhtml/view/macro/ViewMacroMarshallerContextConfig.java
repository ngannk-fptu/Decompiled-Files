/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.spring.AvailableToPlugins
 *  javax.annotation.Resource
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.confluence.content.render.xhtml.view.macro;

import com.atlassian.confluence.content.render.xhtml.HtmlToXmlConverter;
import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.compatibility.LegacyV2RendererContextInitialiser;
import com.atlassian.confluence.content.render.xhtml.editor.macro.PlaceholderUrlFactory;
import com.atlassian.confluence.content.render.xhtml.view.macro.DefaultViewMacroWrapper;
import com.atlassian.confluence.content.render.xhtml.view.macro.MacroAsyncRenderWhitelist;
import com.atlassian.confluence.content.render.xhtml.view.macro.ViewMacroErrorPlaceholder;
import com.atlassian.confluence.content.render.xhtml.view.macro.ViewMacroMarshallerFactory;
import com.atlassian.confluence.content.render.xhtml.view.macro.ViewMacroMarshallerFactoryImpl;
import com.atlassian.confluence.content.render.xhtml.view.macro.ViewMacroWrapper;
import com.atlassian.confluence.impl.macro.schema.MacroSchemaMigrator;
import com.atlassian.confluence.internal.diagnostics.MacroRenderingMonitor;
import com.atlassian.confluence.macro.browser.MacroMetadataManager;
import com.atlassian.confluence.macro.xhtml.MacroManager;
import com.atlassian.confluence.util.profiling.ConfluenceMonitoring;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.spring.AvailableToPlugins;
import javax.annotation.Resource;
import javax.xml.stream.XMLOutputFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class ViewMacroMarshallerContextConfig {
    @Resource
    private MacroManager xhtmlMacroManager;
    @Resource
    private ViewMacroErrorPlaceholder viewMacroErrorPlaceholder;
    @Resource
    private LegacyV2RendererContextInitialiser legacyV2RendererContextInitialiser;
    @Resource
    private EventPublisher eventPublisher;
    @Resource
    private XMLOutputFactory xmlFragmentOutputFactory;
    @Resource
    private PlaceholderUrlFactory placeholderUrlFactory;
    @Resource
    private ConfluenceMonitoring confluenceMonitoring;
    @Resource
    private MacroAsyncRenderWhitelist macroAsyncRenderWhiteList;
    @Resource
    private MacroMetadataManager macroMetadataManager;
    @Resource
    private MacroSchemaMigrator macroSchemaMigrator;
    @Resource
    private MacroRenderingMonitor macroRenderingMonitor;
    @Resource
    private HtmlToXmlConverter htmlToXmlConverter;
    @Resource
    private PluginEventManager pluginEventManager;

    ViewMacroMarshallerContextConfig() {
    }

    @Bean
    @AvailableToPlugins
    ViewMacroMarshallerFactory viewMacroMarshallerFactory() {
        return new ViewMacroMarshallerFactoryImpl(this.xhtmlMacroManager, this.viewMacroErrorPlaceholder, this.legacyV2RendererContextInitialiser, this.eventPublisher, this.xmlFragmentOutputFactory, this.placeholderUrlFactory, this.confluenceMonitoring, this.macroAsyncRenderWhiteList, this.macroMetadataManager, this.macroSchemaMigrator, this.viewMacroWrapper(), this.macroRenderingMonitor);
    }

    @Bean
    ViewMacroWrapper viewMacroWrapper() {
        return new DefaultViewMacroWrapper(this.htmlToXmlConverter, this.pluginEventManager);
    }

    @Bean
    Marshaller<MacroDefinition> viewMacroMarshaller() {
        return this.viewMacroMarshallerFactory().newMacroMarshaller();
    }
}

