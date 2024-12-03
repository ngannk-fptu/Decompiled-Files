/*
 * Decompiled with CFR 0.152.
 */
package javanet.staxutils.events;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javanet.staxutils.events.AttributeEvent;
import javanet.staxutils.events.BaseXMLEventAllocator;
import javanet.staxutils.events.CDataEvent;
import javanet.staxutils.events.CharactersEvent;
import javanet.staxutils.events.CommentEvent;
import javanet.staxutils.events.DTDEvent;
import javanet.staxutils.events.EndDocumentEvent;
import javanet.staxutils.events.EndElementEvent;
import javanet.staxutils.events.EntityDeclarationEvent;
import javanet.staxutils.events.EntityReferenceEvent;
import javanet.staxutils.events.IgnorableSpaceEvent;
import javanet.staxutils.events.NamespaceEvent;
import javanet.staxutils.events.ProcessingInstructionEvent;
import javanet.staxutils.events.StartDocumentEvent;
import javanet.staxutils.events.StartElementEvent;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.Comment;
import javax.xml.stream.events.DTD;
import javax.xml.stream.events.EndDocument;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.EntityDeclaration;
import javax.xml.stream.events.EntityReference;
import javax.xml.stream.events.ProcessingInstruction;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.util.XMLEventAllocator;

public class EventAllocator
extends BaseXMLEventAllocator {
    public XMLEventAllocator newInstance() {
        return new EventAllocator();
    }

    public StartElement allocateStartElement(XMLStreamReader reader) throws XMLStreamException {
        Location location = this.createStableLocation(reader);
        QName name = reader.getName();
        List attributes = this.allocateAttributes(location, reader);
        List namespaces = this.allocateNamespaces(location, reader);
        NamespaceContext nsCtx = this.createStableNamespaceContext(reader);
        QName schemaType = this.determineSchemaType(reader);
        return new StartElementEvent(name, attributes.iterator(), namespaces.iterator(), nsCtx, location, schemaType);
    }

    public EndElement allocateEndElement(XMLStreamReader reader) throws XMLStreamException {
        Location location = this.createStableLocation(reader);
        QName name = reader.getName();
        List namespaces = this.allocateNamespaces(location, reader);
        QName schemaType = this.determineSchemaType(reader);
        return new EndElementEvent(name, namespaces.iterator(), location, schemaType);
    }

    public List allocateAttributes(Location location, XMLStreamReader reader) throws XMLStreamException {
        ArrayList<AttributeEvent> attributes = null;
        int s = reader.getAttributeCount();
        for (int i = 0; i < s; ++i) {
            QName name = reader.getAttributeName(i);
            String value = reader.getAttributeValue(i);
            String dtdType = reader.getAttributeType(i);
            boolean specified = reader.isAttributeSpecified(i);
            QName schemaType = this.determineAttributeSchemaType(reader, i);
            AttributeEvent attr = new AttributeEvent(name, value, specified, dtdType, location, schemaType);
            if (attributes == null) {
                attributes = new ArrayList<AttributeEvent>();
            }
            attributes.add(attr);
        }
        return attributes != null ? attributes : Collections.EMPTY_LIST;
    }

    public List allocateNamespaces(Location location, XMLStreamReader reader) throws XMLStreamException {
        ArrayList<NamespaceEvent> namespaces = null;
        int s = reader.getNamespaceCount();
        for (int i = 0; i < s; ++i) {
            String prefix = reader.getNamespacePrefix(i);
            String nsURI = reader.getNamespaceURI(i);
            NamespaceEvent ns = new NamespaceEvent(prefix, nsURI, location);
            if (namespaces == null) {
                namespaces = new ArrayList<NamespaceEvent>();
            }
            namespaces.add(ns);
        }
        return namespaces != null ? namespaces : Collections.EMPTY_LIST;
    }

    public Characters allocateCData(XMLStreamReader reader) throws XMLStreamException {
        Location location = this.createStableLocation(reader);
        String text = reader.getText();
        QName schemaType = this.determineSchemaType(reader);
        return new CDataEvent(text, location, schemaType);
    }

    public Characters allocateCharacters(XMLStreamReader reader) throws XMLStreamException {
        Location location = this.createStableLocation(reader);
        String text = reader.getText();
        QName schemaType = this.determineSchemaType(reader);
        return new CharactersEvent(text, location, schemaType);
    }

    public Characters allocateIgnorableSpace(XMLStreamReader reader) throws XMLStreamException {
        Location location = this.createStableLocation(reader);
        String text = reader.getText();
        QName schemaType = this.determineSchemaType(reader);
        return new IgnorableSpaceEvent(text, location, schemaType);
    }

    public Comment allocateComment(XMLStreamReader reader) throws XMLStreamException {
        Location location = this.createStableLocation(reader);
        String text = reader.getText();
        return new CommentEvent(text, location);
    }

    public DTD allocateDTD(XMLStreamReader reader) throws XMLStreamException {
        Location location = this.createStableLocation(reader);
        List entities = (List)reader.getProperty("javax.xml.stream.entities");
        List notations = (List)reader.getProperty("javax.xml.stream.notations");
        String text = reader.getText();
        return new DTDEvent(text, entities, notations, location);
    }

    public StartDocument allocateStartDocument(XMLStreamReader reader) throws XMLStreamException {
        Location location = this.createStableLocation(reader);
        String encoding = reader.getCharacterEncodingScheme();
        String version = reader.getVersion();
        Boolean standalone = reader.standaloneSet() ? Boolean.valueOf(reader.isStandalone()) : null;
        QName schemaType = this.determineSchemaType(reader);
        return new StartDocumentEvent(encoding, standalone, version, location, schemaType);
    }

    public EndDocument allocateEndDocument(XMLStreamReader reader) throws XMLStreamException {
        Location location = this.createStableLocation(reader);
        QName schemaType = this.determineSchemaType(reader);
        return new EndDocumentEvent(location, schemaType);
    }

    public EntityReference allocateEntityReference(XMLStreamReader reader) throws XMLStreamException {
        Location location = this.createStableLocation(reader);
        String name = reader.getLocalName();
        EntityDeclaration decl = this.determineEntityDeclaration(name, reader);
        return new EntityReferenceEvent(name, decl, location);
    }

    public ProcessingInstruction allocateProcessingInstruction(XMLStreamReader reader) throws XMLStreamException {
        Location location = this.createStableLocation(reader);
        String target = reader.getPITarget();
        String data = reader.getPIData();
        return new ProcessingInstructionEvent(target, data, location);
    }

    public QName determineSchemaType(XMLStreamReader reader) {
        return null;
    }

    public QName determineAttributeSchemaType(XMLStreamReader reader, int index) {
        return null;
    }

    public EntityDeclaration determineEntityDeclaration(String name, XMLStreamReader reader) {
        return new EntityDeclarationEvent(name, reader.getText(), null);
    }

    public Location createStableLocation(XMLStreamReader reader) {
        return reader.getLocation();
    }

    public NamespaceContext createStableNamespaceContext(XMLStreamReader reader) {
        return reader.getNamespaceContext();
    }
}

