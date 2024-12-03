/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.Collections2
 *  org.apache.commons.collections.CollectionUtils
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.content.render.xhtml.transformers;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.ElementTransformer;
import com.atlassian.confluence.content.render.xhtml.ElementTransformingXmlEventReader;
import com.atlassian.confluence.content.render.xhtml.ResettableXmlEventReader;
import com.atlassian.confluence.content.render.xhtml.StaxUtils;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XhtmlTimeoutException;
import com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory;
import com.atlassian.confluence.content.render.xhtml.XmlOutputFactory;
import com.atlassian.confluence.content.render.xhtml.migration.MigrationAware;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformationErrorHandler;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer;
import com.atlassian.event.api.EventPublisher;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import org.apache.commons.collections.CollectionUtils;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultFragmentTransformer
implements FragmentTransformer,
MigrationAware {
    private static final Logger log = LoggerFactory.getLogger(DefaultFragmentTransformer.class);
    private final XmlOutputFactory xmlFragmentOutputFactory;
    private final XmlEventReaderFactory xmlEventReaderFactory;
    private final List<? extends FragmentTransformer> fragmentTransformers;
    private final List<ElementTransformer> elementTransformers;
    private final FragmentTransformationErrorHandler fragmentTransformationErrorHandler;
    private final @Nullable EventPublisher eventPublisher;

    public static DefaultFragmentTransformer createMigrationAwareFragmentTransformer(List<? extends FragmentTransformer> fragmentTransformers, XmlOutputFactory xmlFragmentOutputFactory, XmlEventReaderFactory xmlEventReaderFactory, FragmentTransformationErrorHandler fragmentTransformationErrorHandler, EventPublisher eventPublisher) {
        Preconditions.checkArgument((boolean)Collections2.filter(fragmentTransformers, (Predicate)Predicates.not((Predicate)Predicates.instanceOf(MigrationAware.class))).isEmpty(), (Object)"All FragmentTransformers must implement MigrationAware");
        return new DefaultFragmentTransformer(fragmentTransformers, xmlFragmentOutputFactory, xmlEventReaderFactory, fragmentTransformationErrorHandler, eventPublisher);
    }

    public DefaultFragmentTransformer(List<? extends FragmentTransformer> fragmentTransformers, XmlOutputFactory xmlFragmentOutputFactory, XmlEventReaderFactory xmlEventReaderFactory, FragmentTransformationErrorHandler fragmentTransformationErrorHandler, @Nullable EventPublisher eventPublisher) {
        this(Collections.emptyList(), fragmentTransformers, xmlFragmentOutputFactory, xmlEventReaderFactory, fragmentTransformationErrorHandler, eventPublisher);
    }

    public DefaultFragmentTransformer(List<ElementTransformer> elementTransformers, List<? extends FragmentTransformer> fragmentTransformers, XmlOutputFactory xmlFragmentOutputFactory, XmlEventReaderFactory xmlEventReaderFactory, FragmentTransformationErrorHandler fragmentTransformationErrorHandler, @Nullable EventPublisher eventPublisher) {
        this.fragmentTransformers = fragmentTransformers;
        this.xmlFragmentOutputFactory = xmlFragmentOutputFactory;
        this.xmlEventReaderFactory = xmlEventReaderFactory;
        this.fragmentTransformationErrorHandler = fragmentTransformationErrorHandler;
        this.elementTransformers = elementTransformers;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Streamable transform(XMLEventReader reader, FragmentTransformer defaultFragmentTransformer, ConversionContext conversionContext) throws XhtmlException {
        XMLEvent eventBeingWritten = null;
        ArrayList<Substreamable> results = new ArrayList<Substreamable>();
        try {
            if (CollectionUtils.isNotEmpty(this.elementTransformers)) {
                reader = new ElementTransformingXmlEventReader(reader, this.elementTransformers);
            }
            XmlEventSink xmlEventSink = new XmlEventSink();
            while (reader.hasNext()) {
                FragmentTransformer fragmentTransformer = this.getFragmentTransformer(reader.peek(), conversionContext);
                if (fragmentTransformer == null) {
                    eventBeingWritten = reader.nextEvent();
                    xmlEventSink.add(eventBeingWritten);
                    continue;
                }
                results.add(xmlEventSink.drain());
                results.add(new NonXmlSubstreamable(this.transformFragment(reader, fragmentTransformer, defaultFragmentTransformer, conversionContext)));
            }
            results.add(xmlEventSink.drain());
        }
        catch (XMLStreamException e) {
            throw new XhtmlException("Error occurred in rendering. Event being written: [" + this.toString(eventBeingWritten) + "]", e);
        }
        finally {
            StaxUtils.closeQuietly(reader);
        }
        return new AggregatedXmlStreamable(results);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Streamable transformFragment(XMLEventReader reader, FragmentTransformer fragmentTransformer, FragmentTransformer defaultFragmentTransformer, ConversionContext conversionContext) throws XMLStreamException, XhtmlTimeoutException {
        Streamable transformOutput;
        try (ResettableXmlEventReader fragmentReader = new ResettableXmlEventReader(this.xmlEventReaderFactory.createXmlFragmentEventReader(reader));){
            transformOutput = fragmentTransformer.transform(fragmentReader, defaultFragmentTransformer, conversionContext);
            if (conversionContext != null) {
                conversionContext.checkTimeout();
            }
        }
        return transformOutput;
    }

    @Override
    public boolean handles(StartElement startElementEvent, ConversionContext conversionContext) {
        return this.getFragmentTransformer(startElementEvent, conversionContext) == null;
    }

    private FragmentTransformer getFragmentTransformer(XMLEvent xmlEvent, ConversionContext conversionContext) {
        if (xmlEvent.isStartElement()) {
            StartElement startElementEvent = xmlEvent.asStartElement();
            for (FragmentTransformer fragmentTransformer : this.fragmentTransformers) {
                if (!fragmentTransformer.handles(startElementEvent, conversionContext)) continue;
                return fragmentTransformer;
            }
        }
        return null;
    }

    private String toString(XMLEvent event) {
        if (event == null) {
            return "null";
        }
        switch (event.getEventType()) {
            case 1: {
                StartElement startElement = event.asStartElement();
                return startElement.getName().toString();
            }
            case 2: {
                EndElement endElement = event.asEndElement();
                return endElement.getName().toString();
            }
        }
        return event.toString();
    }

    @Override
    public boolean wasMigrationPerformed(ConversionContext conversionContext) {
        for (FragmentTransformer fragmentTransformer : Collections2.filter(this.fragmentTransformers, (Predicate)Predicates.instanceOf(MigrationAware.class))) {
            if (!((MigrationAware)((Object)fragmentTransformer)).wasMigrationPerformed(conversionContext)) continue;
            return true;
        }
        return false;
    }

    private class AggregatedXmlStreamable
    implements Streamable {
        private final List<Substreamable> substreamables;

        private AggregatedXmlStreamable(List<Substreamable> substreamables) {
            this.substreamables = substreamables;
        }

        @Override
        public void writeTo(Writer out) throws IOException {
            XMLEventWriter xmlEventWriter = null;
            try {
                xmlEventWriter = DefaultFragmentTransformer.this.xmlFragmentOutputFactory.createXMLEventWriter(out);
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
            StaxUtils.closeQuietly(xmlEventWriter);
        }
    }

    private static class NonXmlSubstreamable
    implements Substreamable {
        private final Streamable streamable;

        private NonXmlSubstreamable(Streamable streamable) {
            this.streamable = streamable;
        }

        @Override
        public void writeTo(Writer underlyingWriter, XMLEventWriter xmlEventWriter) throws IOException, XMLStreamException {
            StaxUtils.flushEventWriter(xmlEventWriter);
            this.streamable.writeTo(underlyingWriter);
        }
    }

    private static class XmlEventStreamable
    implements Substreamable {
        private List<XMLEvent> events = new ArrayList<XMLEvent>();

        public XmlEventStreamable(List<XMLEvent> events) {
            this.events = new ArrayList<XMLEvent>(events);
        }

        @Override
        public void writeTo(Writer underlyingWriter, XMLEventWriter xmlEventWriter) throws IOException, XMLStreamException {
            for (XMLEvent event : this.events) {
                xmlEventWriter.add(event);
            }
        }
    }

    private static interface Substreamable {
        public void writeTo(Writer var1, XMLEventWriter var2) throws IOException, XMLStreamException;
    }

    private static class XmlEventSink {
        private List<XMLEvent> events = new ArrayList<XMLEvent>();

        private XmlEventSink() {
        }

        public void add(XMLEvent event) {
            this.events.add(event);
        }

        public Substreamable drain() {
            XmlEventStreamable result = new XmlEventStreamable(this.events);
            this.events.clear();
            return result;
        }
    }
}

