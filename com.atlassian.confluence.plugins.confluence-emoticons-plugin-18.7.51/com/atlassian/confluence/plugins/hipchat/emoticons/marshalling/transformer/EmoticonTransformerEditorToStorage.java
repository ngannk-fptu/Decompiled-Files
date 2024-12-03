/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.MarshallingRegistry
 *  com.atlassian.confluence.content.render.xhtml.MarshallingType
 *  com.atlassian.confluence.content.render.xhtml.XhtmlException
 *  com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory
 *  com.atlassian.confluence.content.render.xhtml.XmlOutputFactoryProvider
 *  com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformationErrorHandler
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 */
package com.atlassian.confluence.plugins.hipchat.emoticons.marshalling.transformer;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.MarshallingRegistry;
import com.atlassian.confluence.content.render.xhtml.MarshallingType;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory;
import com.atlassian.confluence.content.render.xhtml.XmlOutputFactoryProvider;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformationErrorHandler;
import com.atlassian.confluence.plugins.hipchat.emoticons.marshalling.transformer.HipchatEmoticonTransformerBase;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.io.Reader;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;

public class EmoticonTransformerEditorToStorage
extends HipchatEmoticonTransformerBase {
    public EmoticonTransformerEditorToStorage(@ComponentImport XmlOutputFactoryProvider xmlOutputFactoryProvider, @ComponentImport XmlEventReaderFactory xmlEventReaderFactory, @ComponentImport FragmentTransformationErrorHandler fragmentTransformationErrorHandler, @ComponentImport EventPublisher eventPublisher, @ComponentImport MarshallingRegistry marshallingRegistry) {
        super(MarshallingType.STORAGE, MarshallingType.EDITOR, xmlOutputFactoryProvider, xmlEventReaderFactory, fragmentTransformationErrorHandler, eventPublisher, marshallingRegistry);
    }

    @Override
    public String transform(Reader input, ConversionContext conversionContext) throws XhtmlException {
        return super.transform(input, conversionContext);
    }

    @Override
    protected XMLEventReader createXmlEventReader(Reader input) throws XMLStreamException {
        return this.xmlEventReaderFactory.createStorageXmlEventReader(input, false);
    }
}

