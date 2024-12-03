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
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.util.EventReaderDelegate;
import org.hibernate.boot.xsd.LocalXsdResolver;
import org.hibernate.boot.xsd.MappingXsdSupport;

public class JpaOrmXmlEventReader
extends EventReaderDelegate {
    private static final List<String> NAMESPACE_URIS_TO_MAP = Collections.singletonList("http://java.sun.com/xml/ns/persistence/orm");
    private static final String ROOT_ELEMENT_NAME = "entity-mappings";
    private static final String VERSION_ATTRIBUTE_NAME = "version";
    private final XMLEventFactory xmlEventFactory;

    public JpaOrmXmlEventReader(XMLEventReader reader) {
        this(reader, XMLEventFactory.newInstance());
    }

    public JpaOrmXmlEventReader(XMLEventReader reader, XMLEventFactory xmlEventFactory) {
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
        if (event != null) {
            if (event.isStartElement()) {
                return this.wrap(event.asStartElement());
            }
            if (event.isEndElement()) {
                return this.wrap(event.asEndElement());
            }
        }
        return event;
    }

    private StartElement wrap(StartElement startElement) {
        List<Attribute> newElementAttributeList = this.mapAttributes(startElement);
        List<Namespace> newNamespaceList = this.mapNamespaces(startElement);
        this.xmlEventFactory.setLocation(startElement.getLocation());
        return this.xmlEventFactory.createStartElement(new QName(MappingXsdSupport.INSTANCE.latestJpaDescriptor().getNamespaceUri(), startElement.getName().getLocalPart()), newElementAttributeList.iterator(), newNamespaceList.iterator());
    }

    private List<Attribute> mapAttributes(StartElement startElement) {
        ArrayList<Attribute> mappedAttributes = new ArrayList<Attribute>();
        Iterator<Attribute> existingAttributesIterator = this.existingXmlAttributesIterator(startElement);
        while (existingAttributesIterator.hasNext()) {
            Attribute originalAttribute = existingAttributesIterator.next();
            Attribute attributeToUse = this.mapAttribute(startElement, originalAttribute);
            mappedAttributes.add(attributeToUse);
        }
        return mappedAttributes;
    }

    private Iterator<Attribute> existingXmlAttributesIterator(StartElement startElement) {
        return startElement.getAttributes();
    }

    private Attribute mapAttribute(StartElement startElement, Attribute originalAttribute) {
        if (ROOT_ELEMENT_NAME.equals(startElement.getName().getLocalPart()) && VERSION_ATTRIBUTE_NAME.equals(originalAttribute.getName().getLocalPart())) {
            String specifiedVersion = originalAttribute.getValue();
            if (!LocalXsdResolver.isValidJpaVersion(specifiedVersion)) {
                throw new BadVersionException(specifiedVersion);
            }
            return this.xmlEventFactory.createAttribute(VERSION_ATTRIBUTE_NAME, LocalXsdResolver.latestJpaVerison());
        }
        return originalAttribute;
    }

    private List<Namespace> mapNamespaces(StartElement startElement) {
        return this.mapNamespaces(this.existingXmlNamespacesIterator(startElement));
    }

    private List<Namespace> mapNamespaces(Iterator<Namespace> originalNamespaceIterator) {
        ArrayList<Namespace> mappedNamespaces = new ArrayList<Namespace>();
        while (originalNamespaceIterator.hasNext()) {
            Namespace originalNamespace = originalNamespaceIterator.next();
            Namespace mappedNamespace = this.mapNamespace(originalNamespace);
            mappedNamespaces.add(mappedNamespace);
        }
        if (mappedNamespaces.isEmpty()) {
            mappedNamespaces.add(this.xmlEventFactory.createNamespace(MappingXsdSupport.INSTANCE.latestJpaDescriptor().getNamespaceUri()));
        }
        return mappedNamespaces;
    }

    private Iterator<Namespace> existingXmlNamespacesIterator(StartElement startElement) {
        return startElement.getNamespaces();
    }

    private Namespace mapNamespace(Namespace originalNamespace) {
        if (NAMESPACE_URIS_TO_MAP.contains(originalNamespace.getNamespaceURI())) {
            return this.xmlEventFactory.createNamespace(originalNamespace.getPrefix(), MappingXsdSupport.INSTANCE.latestJpaDescriptor().getNamespaceUri());
        }
        return originalNamespace;
    }

    private XMLEvent wrap(EndElement endElement) {
        List<Namespace> targetNamespaces = this.mapNamespaces(this.existingXmlNamespacesIterator(endElement));
        this.xmlEventFactory.setLocation(endElement.getLocation());
        return this.xmlEventFactory.createEndElement(new QName(MappingXsdSupport.INSTANCE.latestJpaDescriptor().getNamespaceUri(), endElement.getName().getLocalPart()), targetNamespaces.iterator());
    }

    private Iterator<Namespace> existingXmlNamespacesIterator(EndElement endElement) {
        return endElement.getNamespaces();
    }

    public static class BadVersionException
    extends RuntimeException {
        private final String requestedVersion;

        public BadVersionException(String requestedVersion) {
            this.requestedVersion = requestedVersion;
        }

        public String getRequestedVersion() {
            return this.requestedVersion;
        }
    }
}

