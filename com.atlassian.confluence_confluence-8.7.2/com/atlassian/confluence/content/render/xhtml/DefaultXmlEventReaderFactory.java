/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ctc.wstx.api.WstxInputProperties
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.io.CharSource
 *  com.google.common.io.CharStreams
 */
package com.atlassian.confluence.content.render.xhtml;

import com.atlassian.confluence.content.render.xhtml.Namespace;
import com.atlassian.confluence.content.render.xhtml.XhtmlConstants;
import com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory;
import com.atlassian.confluence.content.render.xhtml.XmlFragmentBodyEventReader;
import com.atlassian.confluence.content.render.xhtml.XmlFragmentEventReader;
import com.atlassian.confluence.xml.XMLEntityResolver;
import com.atlassian.confluence.xml.XhtmlEntityResolver;
import com.ctc.wstx.api.WstxInputProperties;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.CharSource;
import com.google.common.io.CharStreams;
import java.io.IOException;
import java.io.Reader;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.xml.stream.EventFilter;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

public class DefaultXmlEventReaderFactory
implements XmlEventReaderFactory {
    private static final int INPUT_BUFFER_LEN = Integer.getInteger("com.ctc.wstx.inputBufferLength", 64000);
    private static final int MAX_ATTRIBUTE_SIZE = Integer.getInteger("com.ctc.wstx.maxAttributeSize", 524288);
    private static final CharSource STORAGE_HEADER = CharSource.wrap((CharSequence)DefaultXmlEventReaderFactory.createNamespacesHeader(XhtmlConstants.STORAGE_NAMESPACES));
    private static final CharSource XHTML_HEADER = CharSource.wrap((CharSequence)DefaultXmlEventReaderFactory.createNamespacesHeader(Collections.singletonList(XhtmlConstants.XHTML_NAMESPACE)));
    private static final CharSource FOOTER = CharSource.wrap((CharSequence)"</xml>");
    private final XMLInputFactory xmlEntityReplacingInputFactory;
    private final XMLInputFactory xmlInputFactory;
    private final XMLInputFactory xmlFragmentInputFactory;
    private final XMLEntityResolver xmlResolver;
    private static final EventFilter IGNORED_EVENTS_FILTER = new EventFilter(){
        private final Set<Integer> IGNORED_EVENT_TYPES = ImmutableSet.of((Object)7, (Object)8, (Object)11);

        @Override
        public boolean accept(XMLEvent event) {
            return !this.IGNORED_EVENT_TYPES.contains(event.getEventType());
        }
    };

    public DefaultXmlEventReaderFactory() {
        this(new XhtmlEntityResolver());
    }

    public DefaultXmlEventReaderFactory(XMLEntityResolver resolver) {
        this.xmlResolver = resolver;
        this.xmlEntityReplacingInputFactory = this.createXmlEventReader(false, true);
        this.xmlInputFactory = this.createXmlEventReader(false, false);
        this.xmlFragmentInputFactory = this.createXmlEventReader(true, false);
    }

    @Override
    public XMLEventReader createXmlEventReader(Reader xml) throws XMLStreamException {
        return this.xmlInputFactory.createXMLEventReader(xml);
    }

    @Override
    public XMLEventReader createXmlFragmentEventReader(Reader xml) throws XMLStreamException {
        return this.xmlFragmentInputFactory.createFilteredReader(this.xmlFragmentInputFactory.createXMLEventReader(xml), IGNORED_EVENTS_FILTER);
    }

    @Override
    public XMLEventReader createStorageXmlEventReader(Reader xml) throws XMLStreamException {
        return this.createXmlEventReaderWithNamespaces(xml, STORAGE_HEADER, true);
    }

    @Override
    public XMLEventReader createStorageXmlEventReader(Reader xml, boolean entityReplacing) throws XMLStreamException {
        return this.createXmlEventReaderWithNamespaces(xml, STORAGE_HEADER, entityReplacing);
    }

    @Override
    public XMLEventReader createEditorXmlEventReader(Reader xml) throws XMLStreamException {
        return this.createXmlEventReaderWithNamespaces(xml, XHTML_HEADER, false);
    }

    @Override
    public XMLEventReader createXMLEventReader(Reader xml, List<Namespace> namespaces, boolean entityReplacing) throws XMLStreamException {
        return this.createXmlEventReaderWithNamespaces(xml, CharSource.wrap((CharSequence)DefaultXmlEventReaderFactory.createNamespacesHeader(namespaces)), entityReplacing);
    }

    @Override
    public XMLEventReader createXmlFragmentEventReader(XMLEventReader delegate) throws XMLStreamException {
        return new XmlFragmentEventReader(delegate);
    }

    @Override
    public XMLEventReader createXmlFragmentBodyEventReader(XMLEventReader delegate) throws XMLStreamException {
        return new XmlFragmentBodyEventReader(delegate);
    }

    private XMLEventReader createXmlEventReaderWithNamespaces(Reader xml, CharSource header, boolean entityReplacing) throws XMLStreamException {
        XMLInputFactory factory = entityReplacing ? this.xmlEntityReplacingInputFactory : this.xmlInputFactory;
        Reader reader = DefaultXmlEventReaderFactory.createNamespacedReader(header, xml);
        XMLEventReader documentReader = factory.createXMLEventReader(reader);
        XMLEventReader filteredReader = factory.createFilteredReader(documentReader, IGNORED_EVENTS_FILTER);
        return new XmlFragmentBodyEventReader(filteredReader);
    }

    private static Reader createNamespacedReader(CharSource header, Reader xml) {
        try {
            return CharSource.concat((CharSource[])new CharSource[]{header, CharSource.wrap((CharSequence)CharStreams.toString((Readable)xml)), FOOTER}).openStream();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String createNamespacesHeader(Iterable<Namespace> namespaces) {
        StringBuilder builder = new StringBuilder();
        builder.append("<!DOCTYPE xml SYSTEM \"xhtml.ent\">").append("<xml");
        for (Namespace namespace : namespaces) {
            builder.append(" xmlns");
            if (!namespace.isDefaultNamespace()) {
                builder.append(":").append(namespace.getPrefix());
            }
            builder.append("=\"").append(namespace.getUri()).append("\"");
        }
        builder.append(">");
        return builder.toString();
    }

    private XMLInputFactory createXmlEventReader(boolean fragmentParsing, boolean entityReplacing) {
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        if (fragmentParsing) {
            inputFactory.setProperty("com.ctc.wstx.fragmentMode", WstxInputProperties.PARSING_MODE_FRAGMENT);
        } else {
            inputFactory.setProperty("javax.xml.stream.supportDTD", Boolean.TRUE);
        }
        inputFactory.setProperty("javax.xml.stream.isReplacingEntityReferences", entityReplacing);
        inputFactory.setProperty("javax.xml.stream.isCoalescing", Boolean.FALSE);
        inputFactory.setProperty("com.ctc.wstx.inputBufferLength", INPUT_BUFFER_LEN);
        inputFactory.setProperty("com.ctc.wstx.maxAttributeSize", MAX_ATTRIBUTE_SIZE);
        inputFactory.setXMLResolver(this.xmlResolver);
        return inputFactory;
    }
}

