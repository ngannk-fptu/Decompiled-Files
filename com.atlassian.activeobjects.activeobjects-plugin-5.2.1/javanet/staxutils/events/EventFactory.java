/*
 * Decompiled with CFR 0.152.
 */
package javanet.staxutils.events;

import java.util.Iterator;
import javanet.staxutils.events.AttributeEvent;
import javanet.staxutils.events.BaseXMLEventFactory;
import javanet.staxutils.events.CDataEvent;
import javanet.staxutils.events.CharactersEvent;
import javanet.staxutils.events.CommentEvent;
import javanet.staxutils.events.DTDEvent;
import javanet.staxutils.events.EndDocumentEvent;
import javanet.staxutils.events.EndElementEvent;
import javanet.staxutils.events.EntityReferenceEvent;
import javanet.staxutils.events.IgnorableSpaceEvent;
import javanet.staxutils.events.NamespaceEvent;
import javanet.staxutils.events.ProcessingInstructionEvent;
import javanet.staxutils.events.StartDocumentEvent;
import javanet.staxutils.events.StartElementEvent;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.Comment;
import javax.xml.stream.events.DTD;
import javax.xml.stream.events.EndDocument;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.EntityDeclaration;
import javax.xml.stream.events.EntityReference;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.ProcessingInstruction;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;

public class EventFactory
extends BaseXMLEventFactory {
    public Attribute createAttribute(QName name, String value, Location location, QName schemaType) {
        return new AttributeEvent(name, value, location, schemaType);
    }

    public Characters createCData(String content, Location location, QName schemaType) {
        return new CDataEvent(content, location, schemaType);
    }

    public Characters createCharacters(String content, Location location, QName schemaType) {
        return new CharactersEvent(content, location, schemaType);
    }

    public Comment createComment(String text, Location location) {
        return new CommentEvent(text, location);
    }

    public DTD createDTD(String dtd, Location location) {
        return new DTDEvent(dtd, location);
    }

    public EndDocument createEndDocument(Location location) {
        return new EndDocumentEvent(location);
    }

    public EndElement createEndElement(QName name, Iterator namespaces, Location location, QName schemaType) {
        return new EndElementEvent(name, namespaces, location, schemaType);
    }

    public EntityReference createEntityReference(String name, EntityDeclaration declaration, Location location) {
        return new EntityReferenceEvent(name, declaration, location);
    }

    public Characters createIgnorableSpace(String content, Location location) {
        return new IgnorableSpaceEvent(content, location);
    }

    public Namespace createNamespace(String prefix, String namespaceUri, Location location) {
        return new NamespaceEvent(prefix, namespaceUri, location);
    }

    public ProcessingInstruction createProcessingInstruction(String target, String data, Location location) {
        return new ProcessingInstructionEvent(target, data, location);
    }

    public Characters createSpace(String content, Location location) {
        return new IgnorableSpaceEvent(content, location);
    }

    public StartDocument createStartDocument(String encoding, String version, Boolean standalone, Location location, QName schemaType) {
        return new StartDocumentEvent(encoding, standalone, version, location, schemaType);
    }

    public StartElement createStartElement(QName name, Iterator attributes, Iterator namespaces, NamespaceContext namespaceCtx, Location location, QName schemaType) {
        return new StartElementEvent(name, attributes, namespaces, namespaceCtx, location, schemaType);
    }
}

