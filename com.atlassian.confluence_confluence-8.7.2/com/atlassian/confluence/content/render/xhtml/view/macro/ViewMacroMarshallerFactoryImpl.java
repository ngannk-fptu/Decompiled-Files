/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 */
package com.atlassian.confluence.content.render.xhtml.view.macro;

import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.compatibility.LegacyV2RendererContextInitialiser;
import com.atlassian.confluence.content.render.xhtml.editor.macro.PlaceholderUrlFactory;
import com.atlassian.confluence.content.render.xhtml.view.macro.MacroAsyncRenderWhitelist;
import com.atlassian.confluence.content.render.xhtml.view.macro.ViewMacroErrorPlaceholder;
import com.atlassian.confluence.content.render.xhtml.view.macro.ViewMacroMarshaller;
import com.atlassian.confluence.content.render.xhtml.view.macro.ViewMacroMarshallerFactory;
import com.atlassian.confluence.content.render.xhtml.view.macro.ViewMacroWrapper;
import com.atlassian.confluence.content.render.xhtml.view.macro.ViewUnknownMacroMarshaller;
import com.atlassian.confluence.impl.macro.schema.MacroSchemaMigrator;
import com.atlassian.confluence.internal.diagnostics.MacroRenderingMonitor;
import com.atlassian.confluence.macro.browser.MacroMetadataManager;
import com.atlassian.confluence.macro.xhtml.MacroManager;
import com.atlassian.confluence.util.profiling.ConfluenceMonitoring;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.atlassian.event.api.EventPublisher;
import javax.xml.stream.XMLOutputFactory;

public class ViewMacroMarshallerFactoryImpl
implements ViewMacroMarshallerFactory {
    private final MacroManager macroManager;
    private final ViewMacroErrorPlaceholder viewMacroErrorPlaceholder;
    private final LegacyV2RendererContextInitialiser legacyV2RendererConfigurationPropertySetter;
    private final EventPublisher eventPublisher;
    private final XMLOutputFactory xmlOutputFactory;
    private final PlaceholderUrlFactory placeholderUrlFactory;
    private final ConfluenceMonitoring monitoring;
    private final MacroAsyncRenderWhitelist macroAsyncRenderWhitelist;
    private final MacroMetadataManager macroMetadataManager;
    private final MacroSchemaMigrator macroSchemaMigrator;
    private final ViewMacroWrapper viewMacroWrapper;
    private final MacroRenderingMonitor macroRenderingMonitor;

    public ViewMacroMarshallerFactoryImpl(MacroManager macroManager, ViewMacroErrorPlaceholder viewMacroErrorPlaceholder, LegacyV2RendererContextInitialiser legacyV2RendererConfigurationPropertySetter, EventPublisher eventPublisher, XMLOutputFactory xmlOutputFactory, PlaceholderUrlFactory placeholderUrlFactory, ConfluenceMonitoring monitoring, MacroAsyncRenderWhitelist macroAsyncRenderWhiteList, MacroMetadataManager macroMetadataManager, MacroSchemaMigrator macroSchemaMigrator, ViewMacroWrapper viewMacroWrapper, MacroRenderingMonitor macroRenderingMonitor) {
        this.macroManager = macroManager;
        this.viewMacroErrorPlaceholder = viewMacroErrorPlaceholder;
        this.legacyV2RendererConfigurationPropertySetter = legacyV2RendererConfigurationPropertySetter;
        this.eventPublisher = eventPublisher;
        this.xmlOutputFactory = xmlOutputFactory;
        this.placeholderUrlFactory = placeholderUrlFactory;
        this.monitoring = monitoring;
        this.macroMetadataManager = macroMetadataManager;
        this.macroSchemaMigrator = macroSchemaMigrator;
        this.macroAsyncRenderWhitelist = macroAsyncRenderWhiteList;
        this.viewMacroWrapper = viewMacroWrapper;
        this.macroRenderingMonitor = macroRenderingMonitor;
    }

    @Override
    public Marshaller<MacroDefinition> newMacroMarshaller() {
        return new ViewMacroMarshaller(this.macroManager, this.newUnknownMacroMarshaller(), this.viewMacroErrorPlaceholder, this.legacyV2RendererConfigurationPropertySetter, this.eventPublisher, this.monitoring, this.macroAsyncRenderWhitelist, this.macroMetadataManager, this.macroSchemaMigrator, this.viewMacroWrapper, this.macroRenderingMonitor);
    }

    @Override
    public Marshaller<MacroDefinition> newMacroMarshaller(MacroManager macroManager, Marshaller<MacroDefinition> unknownMacroMarshaller, ViewMacroErrorPlaceholder viewMacroErrorPlaceholder) {
        if (macroManager == null) {
            macroManager = this.macroManager;
        }
        if (unknownMacroMarshaller == null) {
            unknownMacroMarshaller = this.newUnknownMacroMarshaller();
        }
        if (viewMacroErrorPlaceholder == null) {
            viewMacroErrorPlaceholder = this.viewMacroErrorPlaceholder;
        }
        return new ViewMacroMarshaller(macroManager, unknownMacroMarshaller, viewMacroErrorPlaceholder, this.legacyV2RendererConfigurationPropertySetter, this.eventPublisher, this.monitoring, this.macroAsyncRenderWhitelist, this.macroMetadataManager, this.macroSchemaMigrator, this.viewMacroWrapper, this.macroRenderingMonitor);
    }

    @Override
    public Marshaller<MacroDefinition> newUnknownMacroMarshaller() {
        return new ViewUnknownMacroMarshaller(this.xmlOutputFactory, this.placeholderUrlFactory);
    }
}

