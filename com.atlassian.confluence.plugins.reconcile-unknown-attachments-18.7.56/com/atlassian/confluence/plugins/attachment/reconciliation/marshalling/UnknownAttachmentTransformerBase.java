/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.MarshallingRegistry
 *  com.atlassian.confluence.content.render.xhtml.MarshallingType
 *  com.atlassian.confluence.content.render.xhtml.Streamable
 *  com.atlassian.confluence.content.render.xhtml.Streamables
 *  com.atlassian.confluence.content.render.xhtml.XhtmlException
 *  com.atlassian.confluence.content.render.xhtml.XhtmlParsingException
 *  com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory
 *  com.atlassian.confluence.content.render.xhtml.XmlOutputFactoryProvider
 *  com.atlassian.confluence.content.render.xhtml.transformers.DefaultFragmentTransformer
 *  com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformationErrorHandler
 *  com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer
 *  com.atlassian.confluence.content.render.xhtml.transformers.Transformer
 *  com.atlassian.event.api.EventPublisher
 *  com.ctc.wstx.exc.WstxParsingException
 *  com.google.common.collect.Lists
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.confluence.plugins.attachment.reconciliation.marshalling;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.MarshallingRegistry;
import com.atlassian.confluence.content.render.xhtml.MarshallingType;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.Streamables;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XhtmlParsingException;
import com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory;
import com.atlassian.confluence.content.render.xhtml.XmlOutputFactoryProvider;
import com.atlassian.confluence.content.render.xhtml.transformers.DefaultFragmentTransformer;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformationErrorHandler;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer;
import com.atlassian.confluence.content.render.xhtml.transformers.Transformer;
import com.atlassian.confluence.plugins.attachment.reconciliation.marshalling.UnknownAttachmentFormatException;
import com.atlassian.confluence.plugins.attachment.reconciliation.marshalling.UnknownAttachmentFragmentTransformerErrorHandler;
import com.atlassian.confluence.plugins.attachment.reconciliation.marshalling.UnknownAttachmentMarshallingFragmentTransformer;
import com.atlassian.event.api.EventPublisher;
import com.ctc.wstx.exc.WstxParsingException;
import com.google.common.collect.Lists;
import java.io.Reader;
import java.util.Collections;
import java.util.List;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import org.springframework.beans.factory.annotation.Autowired;

abstract class UnknownAttachmentTransformerBase
extends DefaultFragmentTransformer
implements Transformer {
    final XmlEventReaderFactory xmlEventReaderFactory;

    @Autowired
    UnknownAttachmentTransformerBase(MarshallingType marshallingType, MarshallingType unmarshallingType, XmlOutputFactoryProvider xmlOutputFactoryProvider, XmlEventReaderFactory xmlEventReaderFactory, UnknownAttachmentFragmentTransformerErrorHandler fragmentTransformationErrorHandler, EventPublisher eventPublisher, MarshallingRegistry marshallingRegistry) {
        super(Collections.emptyList(), (List)Lists.newArrayList((Object[])new UnknownAttachmentMarshallingFragmentTransformer[]{new UnknownAttachmentMarshallingFragmentTransformer(marshallingType, unmarshallingType, marshallingRegistry, xmlOutputFactoryProvider.getXmlFragmentOutputFactory(), xmlEventReaderFactory)}), xmlOutputFactoryProvider.getXmlFragmentOutputFactory(), xmlEventReaderFactory, (FragmentTransformationErrorHandler)fragmentTransformationErrorHandler, eventPublisher);
        this.xmlEventReaderFactory = xmlEventReaderFactory;
    }

    public static XhtmlException convertToXhtmlException(Exception ex) {
        Throwable cause;
        Throwable throwable = cause = ex instanceof RuntimeException ? ex.getCause() : ex;
        if (cause instanceof WstxParsingException) {
            WstxParsingException wpe = (WstxParsingException)cause;
            return new XhtmlParsingException(wpe.getLocation().getLineNumber(), wpe.getLocation().getColumnNumber(), wpe.getMessage(), (Throwable)wpe);
        }
        return new UnknownAttachmentFormatException("Exception while transforming fragment in UnknownAttachmentTransformerBase", cause);
    }

    protected abstract XMLEventReader createXmlEventReader(Reader var1) throws XMLStreamException;

    public String transform(Reader input, ConversionContext conversionContext) throws XhtmlException {
        try {
            XMLEventReader xmlEventReader = this.createXmlEventReader(input);
            Streamable streamable = this.transform(xmlEventReader, (FragmentTransformer)this, conversionContext);
            return Streamables.writeToString((Streamable)streamable);
        }
        catch (RuntimeException | XMLStreamException e) {
            throw UnknownAttachmentTransformerBase.convertToXhtmlException(e);
        }
    }
}

