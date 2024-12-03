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

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class Stax2EventFactoryImpl
extends XMLEventFactory2 {
    protected Location mLocation;

    @Override
    public Attribute createAttribute(QName qName, String string) {
        return new AttributeEventImpl(this.mLocation, qName, string, true);
    }

    @Override
    public Attribute createAttribute(String string, String string2) {
        return new AttributeEventImpl(this.mLocation, string, null, null, string2, true);
    }

    @Override
    public Attribute createAttribute(String string, String string2, String string3, String string4) {
        return new AttributeEventImpl(this.mLocation, string3, string2, string, string4, true);
    }

    @Override
    public Characters createCData(String string) {
        return new CharactersEventImpl(this.mLocation, string, true);
    }

    @Override
    public Characters createCharacters(String string) {
        return new CharactersEventImpl(this.mLocation, string, false);
    }

    @Override
    public Comment createComment(String string) {
        return new CommentEventImpl(this.mLocation, string);
    }

    @Override
    public DTD createDTD(String string) {
        return new DTDEventImpl(this.mLocation, string);
    }

    @Override
    public EndDocument createEndDocument() {
        return new EndDocumentEventImpl(this.mLocation);
    }

    public EndElement createEndElement(QName qName, Iterator iterator) {
        return new EndElementEventImpl(this.mLocation, qName, iterator);
    }

    @Override
    public EndElement createEndElement(String string, String string2, String string3) {
        return this.createEndElement(this.createQName(string2, string3, string), (Iterator)null);
    }

    public EndElement createEndElement(String string, String string2, String string3, Iterator iterator) {
        return this.createEndElement(this.createQName(string2, string3, string), iterator);
    }

    @Override
    public EntityReference createEntityReference(String string, EntityDeclaration entityDeclaration) {
        return new EntityReferenceEventImpl(this.mLocation, entityDeclaration);
    }

    @Override
    public Characters createIgnorableSpace(String string) {
        return CharactersEventImpl.createIgnorableWS(this.mLocation, string);
    }

    @Override
    public Namespace createNamespace(String string) {
        return NamespaceEventImpl.constructDefaultNamespace(this.mLocation, string);
    }

    @Override
    public Namespace createNamespace(String string, String string2) {
        return NamespaceEventImpl.constructNamespace(this.mLocation, string, string2);
    }

    @Override
    public ProcessingInstruction createProcessingInstruction(String string, String string2) {
        return new ProcInstrEventImpl(this.mLocation, string, string2);
    }

    @Override
    public Characters createSpace(String string) {
        return CharactersEventImpl.createNonIgnorableWS(this.mLocation, string);
    }

    @Override
    public StartDocument createStartDocument() {
        return new StartDocumentEventImpl(this.mLocation);
    }

    @Override
    public StartDocument createStartDocument(String string) {
        return new StartDocumentEventImpl(this.mLocation, string);
    }

    @Override
    public StartDocument createStartDocument(String string, String string2) {
        return new StartDocumentEventImpl(this.mLocation, string, string2);
    }

    @Override
    public StartDocument createStartDocument(String string, String string2, boolean bl) {
        return new StartDocumentEventImpl(this.mLocation, string, string2, true, bl);
    }

    public StartElement createStartElement(QName qName, Iterator iterator, Iterator iterator2) {
        return this.createStartElement(qName, iterator, iterator2, null);
    }

    @Override
    public StartElement createStartElement(String string, String string2, String string3) {
        return this.createStartElement(this.createQName(string2, string3, string), null, null, null);
    }

    public StartElement createStartElement(String string, String string2, String string3, Iterator iterator, Iterator iterator2) {
        return this.createStartElement(this.createQName(string2, string3, string), iterator, iterator2, null);
    }

    public StartElement createStartElement(String string, String string2, String string3, Iterator iterator, Iterator iterator2, NamespaceContext namespaceContext) {
        return this.createStartElement(this.createQName(string2, string3, string), iterator, iterator2, namespaceContext);
    }

    @Override
    public void setLocation(Location location) {
        this.mLocation = location;
    }

    @Override
    public DTD2 createDTD(String string, String string2, String string3, String string4) {
        return new DTDEventImpl(this.mLocation, string, string2, string3, string4, null);
    }

    @Override
    public DTD2 createDTD(String string, String string2, String string3, String string4, Object object) {
        return new DTDEventImpl(this.mLocation, string, string2, string3, string4, object);
    }

    protected abstract QName createQName(String var1, String var2);

    protected abstract QName createQName(String var1, String var2, String var3);

    protected StartElement createStartElement(QName qName, Iterator<?> iterator, Iterator<?> iterator2, NamespaceContext namespaceContext) {
        return StartElementEventImpl.construct(this.mLocation, qName, iterator, iterator2, namespaceContext);
    }
}

