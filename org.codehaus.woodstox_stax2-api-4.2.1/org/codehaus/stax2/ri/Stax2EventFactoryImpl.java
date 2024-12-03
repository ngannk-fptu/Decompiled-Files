/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2.ri;

import java.util.Iterator;
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
import org.codehaus.stax2.evt.DTD2;
import org.codehaus.stax2.evt.XMLEventFactory2;
import org.codehaus.stax2.ri.evt.AttributeEventImpl;
import org.codehaus.stax2.ri.evt.CharactersEventImpl;
import org.codehaus.stax2.ri.evt.CommentEventImpl;
import org.codehaus.stax2.ri.evt.DTDEventImpl;
import org.codehaus.stax2.ri.evt.EndDocumentEventImpl;
import org.codehaus.stax2.ri.evt.EndElementEventImpl;
import org.codehaus.stax2.ri.evt.EntityReferenceEventImpl;
import org.codehaus.stax2.ri.evt.NamespaceEventImpl;
import org.codehaus.stax2.ri.evt.ProcInstrEventImpl;
import org.codehaus.stax2.ri.evt.StartDocumentEventImpl;
import org.codehaus.stax2.ri.evt.StartElementEventImpl;

public abstract class Stax2EventFactoryImpl
extends XMLEventFactory2 {
    protected Location mLocation;

    @Override
    public Attribute createAttribute(QName name, String value) {
        return new AttributeEventImpl(this.mLocation, name, value, true);
    }

    @Override
    public Attribute createAttribute(String localName, String value) {
        return new AttributeEventImpl(this.mLocation, localName, null, null, value, true);
    }

    @Override
    public Attribute createAttribute(String prefix, String nsURI, String localName, String value) {
        return new AttributeEventImpl(this.mLocation, localName, nsURI, prefix, value, true);
    }

    @Override
    public Characters createCData(String content) {
        return new CharactersEventImpl(this.mLocation, content, true);
    }

    @Override
    public Characters createCharacters(String content) {
        return new CharactersEventImpl(this.mLocation, content, false);
    }

    @Override
    public Comment createComment(String text) {
        return new CommentEventImpl(this.mLocation, text);
    }

    @Override
    public DTD createDTD(String dtd) {
        return new DTDEventImpl(this.mLocation, dtd);
    }

    @Override
    public EndDocument createEndDocument() {
        return new EndDocumentEventImpl(this.mLocation);
    }

    public EndElement createEndElement(QName name, Iterator namespaces) {
        return new EndElementEventImpl(this.mLocation, name, namespaces);
    }

    @Override
    public EndElement createEndElement(String prefix, String nsURI, String localName) {
        return this.createEndElement(this.createQName(nsURI, localName, prefix), (Iterator)null);
    }

    public EndElement createEndElement(String prefix, String nsURI, String localName, Iterator ns) {
        return this.createEndElement(this.createQName(nsURI, localName, prefix), ns);
    }

    @Override
    public EntityReference createEntityReference(String name, EntityDeclaration decl) {
        return new EntityReferenceEventImpl(this.mLocation, decl);
    }

    @Override
    public Characters createIgnorableSpace(String content) {
        return CharactersEventImpl.createIgnorableWS(this.mLocation, content);
    }

    @Override
    public Namespace createNamespace(String nsURI) {
        return NamespaceEventImpl.constructDefaultNamespace(this.mLocation, nsURI);
    }

    @Override
    public Namespace createNamespace(String prefix, String nsURI) {
        return NamespaceEventImpl.constructNamespace(this.mLocation, prefix, nsURI);
    }

    @Override
    public ProcessingInstruction createProcessingInstruction(String target, String data) {
        return new ProcInstrEventImpl(this.mLocation, target, data);
    }

    @Override
    public Characters createSpace(String content) {
        return CharactersEventImpl.createNonIgnorableWS(this.mLocation, content);
    }

    @Override
    public StartDocument createStartDocument() {
        return new StartDocumentEventImpl(this.mLocation);
    }

    @Override
    public StartDocument createStartDocument(String encoding) {
        return new StartDocumentEventImpl(this.mLocation, encoding);
    }

    @Override
    public StartDocument createStartDocument(String encoding, String version) {
        return new StartDocumentEventImpl(this.mLocation, encoding, version);
    }

    @Override
    public StartDocument createStartDocument(String encoding, String version, boolean standalone) {
        return new StartDocumentEventImpl(this.mLocation, encoding, version, true, standalone);
    }

    public StartElement createStartElement(QName name, Iterator attr, Iterator ns) {
        return this.createStartElement(name, attr, ns, null);
    }

    @Override
    public StartElement createStartElement(String prefix, String nsURI, String localName) {
        return this.createStartElement(this.createQName(nsURI, localName, prefix), null, null, null);
    }

    public StartElement createStartElement(String prefix, String nsURI, String localName, Iterator attr, Iterator ns) {
        return this.createStartElement(this.createQName(nsURI, localName, prefix), attr, ns, null);
    }

    public StartElement createStartElement(String prefix, String nsURI, String localName, Iterator attr, Iterator ns, NamespaceContext nsCtxt) {
        return this.createStartElement(this.createQName(nsURI, localName, prefix), attr, ns, nsCtxt);
    }

    @Override
    public void setLocation(Location loc) {
        this.mLocation = loc;
    }

    @Override
    public DTD2 createDTD(String rootName, String sysId, String pubId, String intSubset) {
        return new DTDEventImpl(this.mLocation, rootName, sysId, pubId, intSubset, null);
    }

    @Override
    public DTD2 createDTD(String rootName, String sysId, String pubId, String intSubset, Object processedDTD) {
        return new DTDEventImpl(this.mLocation, rootName, sysId, pubId, intSubset, processedDTD);
    }

    protected abstract QName createQName(String var1, String var2);

    protected abstract QName createQName(String var1, String var2, String var3);

    protected StartElement createStartElement(QName name, Iterator<?> attr, Iterator<?> ns, NamespaceContext ctxt) {
        return StartElementEventImpl.construct(this.mLocation, name, attr, ns, ctxt);
    }
}

