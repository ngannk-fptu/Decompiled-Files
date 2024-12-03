/*
 * Decompiled with CFR 0.152.
 */
package javanet.staxutils.events;

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

public abstract class BaseXMLEventFactory
extends XMLEventFactory {
    protected Location location;

    public void setLocation(Location location) {
        this.location = location;
    }

    public Attribute createAttribute(QName name, String value) {
        return this.createAttribute(name, value, this.location, null);
    }

    public Attribute createAttribute(String prefix, String namespaceURI, String localName, String value) {
        return this.createAttribute(new QName(namespaceURI, localName, prefix), value, this.location, null);
    }

    public Attribute createAttribute(String localName, String value) {
        return this.createAttribute(new QName(localName), value, this.location, null);
    }

    public abstract Attribute createAttribute(QName var1, String var2, Location var3, QName var4);

    public Characters createCData(String content) {
        return this.createCData(content, this.location, null);
    }

    public abstract Characters createCData(String var1, Location var2, QName var3);

    public Characters createCharacters(String content) {
        return this.createCharacters(content, this.location, null);
    }

    public abstract Characters createCharacters(String var1, Location var2, QName var3);

    public Comment createComment(String text) {
        return this.createComment(text, this.location);
    }

    public abstract Comment createComment(String var1, Location var2);

    public DTD createDTD(String dtd) {
        return this.createDTD(dtd, this.location);
    }

    public abstract DTD createDTD(String var1, Location var2);

    public EndDocument createEndDocument() {
        return this.createEndDocument(this.location);
    }

    public abstract EndDocument createEndDocument(Location var1);

    public EndElement createEndElement(QName name, Iterator namespaces) {
        return this.createEndElement(name, namespaces, this.location, null);
    }

    public EndElement createEndElement(String prefix, String namespaceUri, String localName, Iterator namespaces) {
        return this.createEndElement(new QName(namespaceUri, localName, prefix), namespaces, this.location, null);
    }

    public EndElement createEndElement(String prefix, String namespaceUri, String localName) {
        return this.createEndElement(new QName(namespaceUri, localName, prefix), null, this.location, null);
    }

    public abstract EndElement createEndElement(QName var1, Iterator var2, Location var3, QName var4);

    public EntityReference createEntityReference(String name, EntityDeclaration declaration) {
        return this.createEntityReference(name, declaration, this.location);
    }

    public abstract EntityReference createEntityReference(String var1, EntityDeclaration var2, Location var3);

    public Characters createIgnorableSpace(String content) {
        return this.createIgnorableSpace(content, this.location);
    }

    public abstract Characters createIgnorableSpace(String var1, Location var2);

    public Namespace createNamespace(String prefix, String namespaceUri) {
        return this.createNamespace(prefix, namespaceUri, this.location);
    }

    public Namespace createNamespace(String namespaceUri) {
        return this.createNamespace("", namespaceUri, this.location);
    }

    public abstract Namespace createNamespace(String var1, String var2, Location var3);

    public ProcessingInstruction createProcessingInstruction(String target, String data) {
        return this.createProcessingInstruction(target, data, this.location);
    }

    public abstract ProcessingInstruction createProcessingInstruction(String var1, String var2, Location var3);

    public Characters createSpace(String content) {
        return this.createSpace(content, this.location);
    }

    public abstract Characters createSpace(String var1, Location var2);

    public StartDocument createStartDocument() {
        return this.createStartDocument(null, null, null, this.location, null);
    }

    public StartDocument createStartDocument(String encoding, String version, boolean standalone) {
        return this.createStartDocument(encoding, version, standalone, this.location, null);
    }

    public StartDocument createStartDocument(String encoding, String version) {
        return this.createStartDocument(encoding, version, null, this.location, null);
    }

    public StartDocument createStartDocument(String encoding) {
        return this.createStartDocument(encoding, null, null, this.location, null);
    }

    public abstract StartDocument createStartDocument(String var1, String var2, Boolean var3, Location var4, QName var5);

    public StartElement createStartElement(QName name, Iterator attributes, Iterator namespaces) {
        return this.createStartElement(name, attributes, namespaces, null, this.location, null);
    }

    public StartElement createStartElement(String prefix, String namespaceUri, String localName, Iterator attributes, Iterator namespaces, NamespaceContext context) {
        return this.createStartElement(new QName(namespaceUri, localName, prefix), attributes, namespaces, context, this.location, null);
    }

    public StartElement createStartElement(String prefix, String namespaceUri, String localName, Iterator attributes, Iterator namespaces) {
        return this.createStartElement(new QName(namespaceUri, localName, prefix), attributes, namespaces, null, this.location, null);
    }

    public StartElement createStartElement(String prefix, String namespaceUri, String localName) {
        return this.createStartElement(new QName(namespaceUri, localName, prefix), null, null, null, this.location, null);
    }

    public abstract StartElement createStartElement(QName var1, Iterator var2, Iterator var3, NamespaceContext var4, Location var5, QName var6);
}

