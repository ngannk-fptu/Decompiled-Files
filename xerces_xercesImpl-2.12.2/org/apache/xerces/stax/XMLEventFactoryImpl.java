/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.stax;

import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLEventFactory;
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
import org.apache.xerces.stax.events.AttributeImpl;
import org.apache.xerces.stax.events.CharactersImpl;
import org.apache.xerces.stax.events.CommentImpl;
import org.apache.xerces.stax.events.DTDImpl;
import org.apache.xerces.stax.events.EndDocumentImpl;
import org.apache.xerces.stax.events.EndElementImpl;
import org.apache.xerces.stax.events.EntityReferenceImpl;
import org.apache.xerces.stax.events.NamespaceImpl;
import org.apache.xerces.stax.events.ProcessingInstructionImpl;
import org.apache.xerces.stax.events.StartDocumentImpl;
import org.apache.xerces.stax.events.StartElementImpl;

public final class XMLEventFactoryImpl
extends XMLEventFactory {
    private Location fLocation;

    @Override
    public void setLocation(Location location) {
        this.fLocation = location;
    }

    @Override
    public Attribute createAttribute(String string, String string2, String string3, String string4) {
        return this.createAttribute(new QName(string2, string3, string), string4);
    }

    @Override
    public Attribute createAttribute(String string, String string2) {
        return this.createAttribute(new QName(string), string2);
    }

    @Override
    public Attribute createAttribute(QName qName, String string) {
        return new AttributeImpl(qName, string, "CDATA", true, this.fLocation);
    }

    @Override
    public Namespace createNamespace(String string) {
        return this.createNamespace("", string);
    }

    @Override
    public Namespace createNamespace(String string, String string2) {
        return new NamespaceImpl(string, string2, this.fLocation);
    }

    public StartElement createStartElement(QName qName, Iterator iterator, Iterator iterator2) {
        return this.createStartElement(qName, iterator, iterator2, null);
    }

    @Override
    public StartElement createStartElement(String string, String string2, String string3) {
        return this.createStartElement(new QName(string2, string3, string), (Iterator)null, (Iterator)null);
    }

    public StartElement createStartElement(String string, String string2, String string3, Iterator iterator, Iterator iterator2) {
        return this.createStartElement(new QName(string2, string3, string), iterator, iterator2);
    }

    public StartElement createStartElement(String string, String string2, String string3, Iterator iterator, Iterator iterator2, NamespaceContext namespaceContext) {
        return this.createStartElement(new QName(string2, string3, string), iterator, iterator2, namespaceContext);
    }

    private StartElement createStartElement(QName qName, Iterator iterator, Iterator iterator2, NamespaceContext namespaceContext) {
        return new StartElementImpl(qName, iterator, iterator2, namespaceContext, this.fLocation);
    }

    public EndElement createEndElement(QName qName, Iterator iterator) {
        return new EndElementImpl(qName, iterator, this.fLocation);
    }

    @Override
    public EndElement createEndElement(String string, String string2, String string3) {
        return this.createEndElement(new QName(string2, string3, string), (Iterator)null);
    }

    public EndElement createEndElement(String string, String string2, String string3, Iterator iterator) {
        return this.createEndElement(new QName(string2, string3, string), iterator);
    }

    @Override
    public Characters createCharacters(String string) {
        return new CharactersImpl(string, 4, this.fLocation);
    }

    @Override
    public Characters createCData(String string) {
        return new CharactersImpl(string, 12, this.fLocation);
    }

    @Override
    public Characters createSpace(String string) {
        return this.createCharacters(string);
    }

    @Override
    public Characters createIgnorableSpace(String string) {
        return new CharactersImpl(string, 6, this.fLocation);
    }

    @Override
    public StartDocument createStartDocument() {
        return this.createStartDocument(null, null);
    }

    @Override
    public StartDocument createStartDocument(String string, String string2, boolean bl) {
        return new StartDocumentImpl(string, string != null, bl, true, string2, this.fLocation);
    }

    @Override
    public StartDocument createStartDocument(String string, String string2) {
        return new StartDocumentImpl(string, string != null, false, false, string2, this.fLocation);
    }

    @Override
    public StartDocument createStartDocument(String string) {
        return this.createStartDocument(string, null);
    }

    @Override
    public EndDocument createEndDocument() {
        return new EndDocumentImpl(this.fLocation);
    }

    @Override
    public EntityReference createEntityReference(String string, EntityDeclaration entityDeclaration) {
        return new EntityReferenceImpl(string, entityDeclaration, this.fLocation);
    }

    @Override
    public Comment createComment(String string) {
        return new CommentImpl(string, this.fLocation);
    }

    @Override
    public ProcessingInstruction createProcessingInstruction(String string, String string2) {
        return new ProcessingInstructionImpl(string, string2, this.fLocation);
    }

    @Override
    public DTD createDTD(String string) {
        return new DTDImpl(string, this.fLocation);
    }
}

