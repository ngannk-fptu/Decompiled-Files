/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 */
package com.atlassian.confluence.content.render.xhtml.storage;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.Streamables;
import com.atlassian.confluence.content.render.xhtml.UnmarshalMarshalFragmentTransformer;
import com.atlassian.confluence.content.render.xhtml.Unmarshaller;
import com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory;
import com.atlassian.confluence.content.render.xhtml.XmlOutputFactory;
import com.atlassian.confluence.content.render.xhtml.storage.ContentTransformerFactory;
import com.atlassian.confluence.content.render.xhtml.storage.StorageXhtmlTransformer;
import com.atlassian.confluence.content.render.xhtml.transformers.DefaultFragmentTransformer;
import com.atlassian.confluence.content.render.xhtml.transformers.ThrowExceptionOnFragmentTransformationError;
import com.atlassian.confluence.content.render.xhtml.transformers.Transformer;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.atlassian.confluence.xhtml.api.MacroDefinitionHandler;
import com.atlassian.confluence.xhtml.api.MacroDefinitionMarshallingStrategy;
import com.atlassian.confluence.xhtml.api.MacroDefinitionReplacer;
import com.atlassian.confluence.xhtml.api.MacroDefinitionUpdater;
import com.atlassian.event.api.EventPublisher;
import java.util.Collections;

public class DefaultContentTransformerFactory
implements ContentTransformerFactory {
    private final Unmarshaller<MacroDefinition> macroDefinitionUnmarshaller;
    private final Marshaller<MacroDefinition> macroDefinitionMarshaller;
    private final XmlEventReaderFactory xmlEventReaderFactory;
    private final XmlOutputFactory xmlOutputFactory;
    private final EventPublisher eventPublisher;

    public DefaultContentTransformerFactory(Unmarshaller<MacroDefinition> macroDefinitionUnmarshaller, Marshaller<MacroDefinition> macroDefinitionMarshaller, XmlEventReaderFactory xmlEventReaderFactory, XmlOutputFactory xmlOutputFactory, EventPublisher eventPublisher) {
        this.macroDefinitionUnmarshaller = macroDefinitionUnmarshaller;
        this.macroDefinitionMarshaller = macroDefinitionMarshaller;
        this.xmlEventReaderFactory = xmlEventReaderFactory;
        this.xmlOutputFactory = xmlOutputFactory;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Transformer getTransformer(MacroDefinitionUpdater macroDefinitionUpdater) {
        return this.getTransformer((MacroDefinition macroDefinition, ConversionContext conversionContext) -> {
            macroDefinition = macroDefinitionUpdater.update((MacroDefinition)macroDefinition);
            return MacroDefinitionMarshallingStrategy.MARSHALL_MACRO.marshal((MacroDefinition)macroDefinition, conversionContext, this.macroDefinitionMarshaller);
        });
    }

    @Override
    public Transformer getTransformer(MacroDefinitionReplacer macroDefinitionReplacer) {
        return this.getTransformer((MacroDefinition macroDefinition, ConversionContext conversionContext) -> {
            String replacement = macroDefinitionReplacer.replace((MacroDefinition)macroDefinition);
            return macroDefinition == null ? Streamables.empty() : Streamables.from(replacement);
        });
    }

    @Override
    public Transformer getTransformer(MacroDefinitionHandler macroDefinitionHandler) {
        return this.getTransformer(macroDefinitionHandler, MacroDefinitionMarshallingStrategy.DISCARD_MACRO);
    }

    @Override
    public Transformer getTransformer(MacroDefinitionHandler handler, MacroDefinitionMarshallingStrategy strategy) {
        return this.getTransformer((MacroDefinition macroDefinition, ConversionContext conversionContext) -> {
            handler.handle((MacroDefinition)macroDefinition);
            return strategy.marshal((MacroDefinition)macroDefinition, conversionContext, this.macroDefinitionMarshaller);
        });
    }

    private Transformer getTransformer(Marshaller<MacroDefinition> macroMarshaller) {
        UnmarshalMarshalFragmentTransformer<MacroDefinition> macroDefinitionUnmarshalMarshalFragmentTransformer = new UnmarshalMarshalFragmentTransformer<MacroDefinition>(this.macroDefinitionUnmarshaller, macroMarshaller);
        DefaultFragmentTransformer defaulFragmentTransformer = new DefaultFragmentTransformer(Collections.singletonList(macroDefinitionUnmarshalMarshalFragmentTransformer), this.xmlOutputFactory, this.xmlEventReaderFactory, new ThrowExceptionOnFragmentTransformationError(), this.eventPublisher);
        return new StorageXhtmlTransformer(this.xmlEventReaderFactory, defaulFragmentTransformer);
    }
}

