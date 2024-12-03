/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.Marshaller
 *  com.atlassian.confluence.content.render.xhtml.MarshallingFragmentTransformer
 *  com.atlassian.confluence.content.render.xhtml.MarshallingRegistry
 *  com.atlassian.confluence.content.render.xhtml.MarshallingType
 *  com.atlassian.confluence.content.render.xhtml.ResettableXmlEventReader
 *  com.atlassian.confluence.content.render.xhtml.StaxUtils
 *  com.atlassian.confluence.content.render.xhtml.Streamable
 *  com.atlassian.confluence.content.render.xhtml.Unmarshaller
 *  com.atlassian.confluence.content.render.xhtml.XhtmlException
 *  com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory
 *  com.atlassian.confluence.content.render.xhtml.XmlOutputFactory
 *  com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.attachment.reconciliation.marshalling;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.MarshallingFragmentTransformer;
import com.atlassian.confluence.content.render.xhtml.MarshallingRegistry;
import com.atlassian.confluence.content.render.xhtml.MarshallingType;
import com.atlassian.confluence.content.render.xhtml.ResettableXmlEventReader;
import com.atlassian.confluence.content.render.xhtml.StaxUtils;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.Unmarshaller;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory;
import com.atlassian.confluence.content.render.xhtml.XmlOutputFactory;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer;
import com.atlassian.confluence.plugins.attachment.reconciliation.marshalling.RestoredUnknownAttachment;
import com.atlassian.confluence.plugins.attachment.reconciliation.marshalling.UnknownAttachmentFormatException;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnknownAttachmentMarshallingFragmentTransformer
extends MarshallingFragmentTransformer<RestoredUnknownAttachment> {
    private MarshallingRegistry marshallingRegistry;
    private MarshallingType marshallerMarshallingType;
    private MarshallingType unmarshallerMarshallingType;
    private XmlOutputFactory xmlOutputFactory;
    private XmlEventReaderFactory xmlEventReaderFactory;
    private static final Logger logger = LoggerFactory.getLogger(UnknownAttachmentMarshallingFragmentTransformer.class);

    public UnknownAttachmentMarshallingFragmentTransformer(MarshallingType marshallerMarshallingType, MarshallingType unmarshallerMarshallingType, MarshallingRegistry marshallingRegistry, XmlOutputFactory xmlOutputFactory, XmlEventReaderFactory xmlEventReaderFactory) {
        super(RestoredUnknownAttachment.class, marshallerMarshallingType, unmarshallerMarshallingType, marshallingRegistry);
        this.marshallerMarshallingType = marshallerMarshallingType;
        this.unmarshallerMarshallingType = unmarshallerMarshallingType;
        this.marshallingRegistry = marshallingRegistry;
        this.xmlOutputFactory = xmlOutputFactory;
        this.xmlEventReaderFactory = xmlEventReaderFactory;
    }

    private Marshaller<RestoredUnknownAttachment> getMarshaller() {
        return this.marshallingRegistry.getMarshaller(RestoredUnknownAttachment.class, this.marshallerMarshallingType);
    }

    private Unmarshaller<RestoredUnknownAttachment> getUnmarshaller() {
        return this.marshallingRegistry.getUnmarshaller(RestoredUnknownAttachment.class, this.unmarshallerMarshallingType);
    }

    public Streamable transform(XMLEventReader reader, FragmentTransformer mainFragmentTransformer, ConversionContext conversionContext) throws XhtmlException {
        AggregatedXmlStreamable untransformed;
        ResettableXmlEventReader resettableReader;
        ArrayList<XMLEvent> events = new ArrayList<XMLEvent>();
        try {
            resettableReader = new ResettableXmlEventReader(this.xmlEventReaderFactory.createXmlFragmentEventReader(reader));
            while (resettableReader.hasNext()) {
                events.add(resettableReader.nextEvent());
            }
            untransformed = new AggregatedXmlStreamable(Collections.singletonList(new XmlEventStreamable(events)));
            resettableReader.reset();
        }
        catch (XMLStreamException e) {
            throw new UnknownAttachmentFormatException("Unable to process Unknown attachment XML events");
        }
        RestoredUnknownAttachment attachment = (RestoredUnknownAttachment)this.getUnmarshaller().unmarshal((XMLEventReader)resettableReader, mainFragmentTransformer, conversionContext);
        if (RestoredUnknownAttachment.Status.INVALID_UNKNOWN_ATTACHMENT.equals((Object)attachment.getStatus())) {
            logger.debug("UnknownAttachmentMarshallingFragmentTransformer unmarshalled invalid unknown attachment. Returning untransformed fragment");
            return untransformed;
        }
        return this.getMarshaller().marshal((Object)attachment, conversionContext);
    }

    private class AggregatedXmlStreamable
    implements Streamable {
        private final List<Substreamable> substreamables;

        private AggregatedXmlStreamable(List<Substreamable> substreamables) {
            this.substreamables = substreamables;
        }

        public void writeTo(Writer out) throws IOException {
            XMLEventWriter xmlEventWriter = null;
            try {
                xmlEventWriter = UnknownAttachmentMarshallingFragmentTransformer.this.xmlOutputFactory.createXMLEventWriter(out);
                for (Substreamable substreamable : this.substreamables) {
                    substreamable.writeTo(out, xmlEventWriter);
                }
                xmlEventWriter.flush();
            }
            catch (XMLStreamException e) {
                try {
                    throw new IOException(e);
                }
                catch (Throwable throwable) {
                    StaxUtils.closeQuietly(xmlEventWriter);
                    throw throwable;
                }
            }
            StaxUtils.closeQuietly((XMLEventWriter)xmlEventWriter);
        }
    }

    private static interface Substreamable {
        public void writeTo(Writer var1, XMLEventWriter var2) throws IOException, XMLStreamException;
    }

    private static class XmlEventStreamable
    implements Substreamable {
        private List<XMLEvent> events;

        public XmlEventStreamable(List<XMLEvent> events) {
            this.events = new ArrayList<XMLEvent>(events);
        }

        @Override
        public void writeTo(Writer underlyingWriter, XMLEventWriter xmlEventWriter) throws XMLStreamException {
            for (XMLEvent event : this.events) {
                xmlEventWriter.add(event);
            }
        }
    }
}

