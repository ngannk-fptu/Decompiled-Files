/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.MarshallingType
 *  com.atlassian.confluence.content.render.xhtml.Streamable
 *  com.atlassian.confluence.content.render.xhtml.Streamables
 *  com.atlassian.confluence.content.render.xhtml.XhtmlException
 *  com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory
 *  com.atlassian.confluence.content.render.xhtml.XmlOutputFactoryProvider
 *  com.atlassian.confluence.content.render.xhtml.transformers.DefaultFragmentTransformer
 *  com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformationErrorHandler
 *  com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer
 *  com.atlassian.confluence.content.render.xhtml.transformers.Transformer
 *  com.atlassian.event.api.EventPublisher
 */
package com.atlassian.confluence.plugins.hipchat.emoticons.marshalling.transformer;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.MarshallingType;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.Streamables;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory;
import com.atlassian.confluence.content.render.xhtml.XmlOutputFactoryProvider;
import com.atlassian.confluence.content.render.xhtml.transformers.DefaultFragmentTransformer;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformationErrorHandler;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer;
import com.atlassian.confluence.content.render.xhtml.transformers.Transformer;
import com.atlassian.event.api.EventPublisher;
import java.io.Reader;
import java.util.Collections;
import java.util.List;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;

abstract class EmoticonTransformerBase
extends DefaultFragmentTransformer
implements Transformer {
    private final MarshallingType unmarshallingType;
    final XmlEventReaderFactory xmlEventReaderFactory;

    @VisibleForTesting
    protected MarshallingType getUnmarshallingType() {
        return this.unmarshallingType;
    }

    EmoticonTransformerBase(MarshallingType unmarshallingType, XmlOutputFactoryProvider xmlOutputFactoryProvider, XmlEventReaderFactory xmlEventReaderFactory, FragmentTransformationErrorHandler fragmentTransformationErrorHandler, EventPublisher eventPublisher, List<? extends FragmentTransformer> fragmentTransformers) {
        super(Collections.emptyList(), fragmentTransformers, xmlOutputFactoryProvider.getXmlFragmentOutputFactory(), xmlEventReaderFactory, fragmentTransformationErrorHandler, eventPublisher);
        this.unmarshallingType = unmarshallingType;
        this.xmlEventReaderFactory = xmlEventReaderFactory;
    }

    public String transform(Reader input, ConversionContext conversionContext) throws XhtmlException {
        try {
            XMLEventReader xmlEventReader = this.createXmlEventReader(input);
            Streamable streamable = this.transform(xmlEventReader, (FragmentTransformer)this, conversionContext);
            return Streamables.writeToString((Streamable)streamable);
        }
        catch (XMLStreamException e) {
            throw new XhtmlException((Throwable)e);
        }
    }

    protected XMLEventReader createXmlEventReader(Reader input) throws XMLStreamException {
        return this.unmarshallingType == MarshallingType.STORAGE ? this.xmlEventReaderFactory.createStorageXmlEventReader(input) : this.xmlEventReaderFactory.createEditorXmlEventReader(input);
    }
}

