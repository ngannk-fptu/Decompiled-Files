/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.jaxb.internal.stax;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.util.EventReaderDelegate;
import org.hibernate.boot.xsd.MappingXsdSupport;
import org.hibernate.internal.util.StringHelper;

public class HbmEventReader
extends EventReaderDelegate {
    private static final List<String> NAMESPACE_URIS_TO_MAP = Collections.singletonList("http://www.hibernate.org/xsd/hibernate-mapping");
    private final XMLEventFactory xmlEventFactory;

    public HbmEventReader(XMLEventReader reader) {
        this(reader, XMLEventFactory.newInstance());
    }

    public HbmEventReader(XMLEventReader reader, XMLEventFactory xmlEventFactory) {
        super(reader);
        this.xmlEventFactory = xmlEventFactory;
    }

    @Override
    public XMLEvent peek() throws XMLStreamException {
        return this.wrap(super.peek());
    }

    @Override
    public XMLEvent nextEvent() throws XMLStreamException {
        return this.wrap(super.nextEvent());
    }

    private XMLEvent wrap(XMLEvent event) {
        if (event != null && event.isStartElement()) {
            return this.applyNamespace(event.asStartElement());
        }
        return event;
    }

    private StartElement applyNamespace(StartElement startElement) {
        ArrayList<Namespace> targetNamespaces = new ArrayList<Namespace>();
        if (StringHelper.isEmpty(startElement.getName().getNamespaceURI())) {
            targetNamespaces.add(this.xmlEventFactory.createNamespace(MappingXsdSupport.INSTANCE.hbmXsd().getNamespaceUri()));
        }
        Iterator<Namespace> originalNamespaces = startElement.getNamespaces();
        while (originalNamespaces.hasNext()) {
            Namespace namespace = originalNamespaces.next();
            if (NAMESPACE_URIS_TO_MAP.contains(namespace.getNamespaceURI())) {
                namespace = this.xmlEventFactory.createNamespace(namespace.getPrefix(), MappingXsdSupport.INSTANCE.hbmXsd().getNamespaceUri());
            }
            targetNamespaces.add(namespace);
        }
        this.xmlEventFactory.setLocation(startElement.getLocation());
        return this.xmlEventFactory.createStartElement(new QName(MappingXsdSupport.INSTANCE.hbmXsd().getNamespaceUri(), startElement.getName().getLocalPart()), startElement.getAttributes(), targetNamespaces.iterator());
    }
}

