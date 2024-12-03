/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.MarshallingRegistry
 *  com.atlassian.confluence.content.render.xhtml.MarshallingType
 *  com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory
 *  com.atlassian.confluence.content.render.xhtml.XmlOutputFactoryProvider
 *  com.atlassian.confluence.content.render.xhtml.transformers.ComparePluginFragmentTransformerMarker
 *  com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformationErrorHandler
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 */
package com.atlassian.confluence.plugins.hipchat.emoticons.marshalling.transformer;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.MarshallingRegistry;
import com.atlassian.confluence.content.render.xhtml.MarshallingType;
import com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory;
import com.atlassian.confluence.content.render.xhtml.XmlOutputFactoryProvider;
import com.atlassian.confluence.content.render.xhtml.transformers.ComparePluginFragmentTransformerMarker;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformationErrorHandler;
import com.atlassian.confluence.plugins.hipchat.emoticons.TwitterEmoji;
import com.atlassian.confluence.plugins.hipchat.emoticons.marshalling.transformer.TwitterEmojiTransformerBase;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import javax.xml.stream.events.StartElement;

public class TwitterEmojiTransformerStorageToView
extends TwitterEmojiTransformerBase
implements ComparePluginFragmentTransformerMarker {
    private final MarshallingRegistry marshallingRegistry;

    public TwitterEmojiTransformerStorageToView(@ComponentImport XmlOutputFactoryProvider xmlOutputFactoryProvider, @ComponentImport XmlEventReaderFactory xmlEventReaderFactory, @ComponentImport FragmentTransformationErrorHandler fragmentTransformationErrorHandler, @ComponentImport EventPublisher eventPublisher, @ComponentImport MarshallingRegistry marshallingRegistry) {
        super(MarshallingType.VIEW, MarshallingType.STORAGE, xmlOutputFactoryProvider, xmlEventReaderFactory, fragmentTransformationErrorHandler, eventPublisher, marshallingRegistry);
        this.marshallingRegistry = marshallingRegistry;
    }

    public boolean handles(StartElement startElementEvent, ConversionContext conversionContext) {
        return this.marshallingRegistry.getUnmarshaller(TwitterEmoji.class, this.getUnmarshallingType()).handles(startElementEvent, conversionContext);
    }
}

