/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.jaxp.validation;

import java.util.Iterator;
import java.util.NoSuchElementException;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.Comment;
import javax.xml.stream.events.DTD;
import javax.xml.stream.events.EndDocument;
import javax.xml.stream.events.EntityReference;
import javax.xml.stream.events.ProcessingInstruction;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.stax.StAXResult;
import org.apache.xerces.jaxp.validation.StAXDocumentHandler;
import org.apache.xerces.jaxp.validation.StAXValidatorHelper;
import org.apache.xerces.util.JAXPNamespaceContextWrapper;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLDocumentSource;

final class StAXEventResultBuilder
implements StAXDocumentHandler {
    private XMLEventWriter fEventWriter;
    private final XMLEventFactory fEventFactory;
    private final StAXValidatorHelper fStAXValidatorHelper;
    private final JAXPNamespaceContextWrapper fNamespaceContext;
    private boolean fIgnoreChars;
    private boolean fInCDATA;
    private final QName fAttrName = new QName();
    private static final Iterator EMPTY_COLLECTION_ITERATOR = new Iterator(){

        @Override
        public boolean hasNext() {
            return false;
        }

        public Object next() {
            throw new NoSuchElementException();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    };

    public StAXEventResultBuilder(StAXValidatorHelper stAXValidatorHelper, JAXPNamespaceContextWrapper jAXPNamespaceContextWrapper) {
        this.fStAXValidatorHelper = stAXValidatorHelper;
        this.fNamespaceContext = jAXPNamespaceContextWrapper;
        this.fEventFactory = XMLEventFactory.newInstance();
    }

    @Override
    public void setStAXResult(StAXResult stAXResult) {
        this.fIgnoreChars = false;
        this.fInCDATA = false;
        this.fEventWriter = stAXResult != null ? stAXResult.getXMLEventWriter() : null;
    }

    @Override
    public void startDocument(XMLStreamReader xMLStreamReader) throws XMLStreamException {
        String string = xMLStreamReader.getVersion();
        String string2 = xMLStreamReader.getCharacterEncodingScheme();
        boolean bl = xMLStreamReader.standaloneSet();
        this.fEventWriter.add(this.fEventFactory.createStartDocument(string2 != null ? string2 : "UTF-8", string != null ? string : "1.0", bl));
    }

    @Override
    public void endDocument(XMLStreamReader xMLStreamReader) throws XMLStreamException {
        this.fEventWriter.add(this.fEventFactory.createEndDocument());
        this.fEventWriter.flush();
    }

    @Override
    public void comment(XMLStreamReader xMLStreamReader) throws XMLStreamException {
        this.fEventWriter.add(this.fEventFactory.createComment(xMLStreamReader.getText()));
    }

    @Override
    public void processingInstruction(XMLStreamReader xMLStreamReader) throws XMLStreamException {
        String string = xMLStreamReader.getPIData();
        this.fEventWriter.add(this.fEventFactory.createProcessingInstruction(xMLStreamReader.getPITarget(), string != null ? string : ""));
    }

    @Override
    public void entityReference(XMLStreamReader xMLStreamReader) throws XMLStreamException {
        String string = xMLStreamReader.getLocalName();
        this.fEventWriter.add(this.fEventFactory.createEntityReference(string, this.fStAXValidatorHelper.getEntityDeclaration(string)));
    }

    @Override
    public void startDocument(StartDocument startDocument) throws XMLStreamException {
        this.fEventWriter.add(startDocument);
    }

    @Override
    public void endDocument(EndDocument endDocument) throws XMLStreamException {
        this.fEventWriter.add(endDocument);
        this.fEventWriter.flush();
    }

    @Override
    public void doctypeDecl(DTD dTD) throws XMLStreamException {
        this.fEventWriter.add(dTD);
    }

    @Override
    public void characters(Characters characters) throws XMLStreamException {
        this.fEventWriter.add(characters);
    }

    @Override
    public void cdata(Characters characters) throws XMLStreamException {
        this.fEventWriter.add(characters);
    }

    @Override
    public void comment(Comment comment) throws XMLStreamException {
        this.fEventWriter.add(comment);
    }

    @Override
    public void processingInstruction(ProcessingInstruction processingInstruction) throws XMLStreamException {
        this.fEventWriter.add(processingInstruction);
    }

    @Override
    public void entityReference(EntityReference entityReference) throws XMLStreamException {
        this.fEventWriter.add(entityReference);
    }

    @Override
    public void setIgnoringCharacters(boolean bl) {
        this.fIgnoreChars = bl;
    }

    @Override
    public void startDocument(XMLLocator xMLLocator, String string, NamespaceContext namespaceContext, Augmentations augmentations) throws XNIException {
    }

    @Override
    public void xmlDecl(String string, String string2, String string3, Augmentations augmentations) throws XNIException {
    }

    @Override
    public void doctypeDecl(String string, String string2, String string3, Augmentations augmentations) throws XNIException {
    }

    @Override
    public void comment(XMLString xMLString, Augmentations augmentations) throws XNIException {
    }

    @Override
    public void processingInstruction(String string, XMLString xMLString, Augmentations augmentations) throws XNIException {
    }

    @Override
    public void startElement(QName qName, XMLAttributes xMLAttributes, Augmentations augmentations) throws XNIException {
        try {
            XMLEvent xMLEvent;
            int n = xMLAttributes.getLength();
            if (n == 0 && (xMLEvent = this.fStAXValidatorHelper.getCurrentEvent()) != null) {
                this.fEventWriter.add(xMLEvent);
                return;
            }
            this.fEventWriter.add(this.fEventFactory.createStartElement(qName.prefix, qName.uri != null ? qName.uri : "", qName.localpart, this.getAttributeIterator(xMLAttributes, n), this.getNamespaceIterator(), this.fNamespaceContext.getNamespaceContext()));
        }
        catch (XMLStreamException xMLStreamException) {
            throw new XNIException(xMLStreamException);
        }
    }

    @Override
    public void emptyElement(QName qName, XMLAttributes xMLAttributes, Augmentations augmentations) throws XNIException {
        this.startElement(qName, xMLAttributes, augmentations);
        this.endElement(qName, augmentations);
    }

    @Override
    public void startGeneralEntity(String string, XMLResourceIdentifier xMLResourceIdentifier, String string2, Augmentations augmentations) throws XNIException {
    }

    @Override
    public void textDecl(String string, String string2, Augmentations augmentations) throws XNIException {
    }

    @Override
    public void endGeneralEntity(String string, Augmentations augmentations) throws XNIException {
    }

    @Override
    public void characters(XMLString xMLString, Augmentations augmentations) throws XNIException {
        if (!this.fIgnoreChars) {
            try {
                if (!this.fInCDATA) {
                    this.fEventWriter.add(this.fEventFactory.createCharacters(xMLString.toString()));
                } else {
                    this.fEventWriter.add(this.fEventFactory.createCData(xMLString.toString()));
                }
            }
            catch (XMLStreamException xMLStreamException) {
                throw new XNIException(xMLStreamException);
            }
        }
    }

    @Override
    public void ignorableWhitespace(XMLString xMLString, Augmentations augmentations) throws XNIException {
        this.characters(xMLString, augmentations);
    }

    @Override
    public void endElement(QName qName, Augmentations augmentations) throws XNIException {
        try {
            XMLEvent xMLEvent = this.fStAXValidatorHelper.getCurrentEvent();
            if (xMLEvent != null) {
                this.fEventWriter.add(xMLEvent);
            } else {
                this.fEventWriter.add(this.fEventFactory.createEndElement(qName.prefix, qName.uri, qName.localpart, this.getNamespaceIterator()));
            }
        }
        catch (XMLStreamException xMLStreamException) {
            throw new XNIException(xMLStreamException);
        }
    }

    @Override
    public void startCDATA(Augmentations augmentations) throws XNIException {
        this.fInCDATA = true;
    }

    @Override
    public void endCDATA(Augmentations augmentations) throws XNIException {
        this.fInCDATA = false;
    }

    @Override
    public void endDocument(Augmentations augmentations) throws XNIException {
    }

    @Override
    public void setDocumentSource(XMLDocumentSource xMLDocumentSource) {
    }

    @Override
    public XMLDocumentSource getDocumentSource() {
        return null;
    }

    private Iterator getAttributeIterator(XMLAttributes xMLAttributes, int n) {
        return n > 0 ? new AttributeIterator(xMLAttributes, n) : EMPTY_COLLECTION_ITERATOR;
    }

    private Iterator getNamespaceIterator() {
        int n = this.fNamespaceContext.getDeclaredPrefixCount();
        return n > 0 ? new NamespaceIterator(n) : EMPTY_COLLECTION_ITERATOR;
    }

    final class NamespaceIterator
    implements Iterator {
        javax.xml.namespace.NamespaceContext fNC;
        int fIndex;
        int fEnd;

        NamespaceIterator(int n) {
            this.fNC = StAXEventResultBuilder.this.fNamespaceContext.getNamespaceContext();
            this.fIndex = 0;
            this.fEnd = n;
        }

        @Override
        public boolean hasNext() {
            if (this.fIndex < this.fEnd) {
                return true;
            }
            this.fNC = null;
            return false;
        }

        public Object next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            String string = StAXEventResultBuilder.this.fNamespaceContext.getDeclaredPrefixAt(this.fIndex++);
            String string2 = this.fNC.getNamespaceURI(string);
            if (string.length() == 0) {
                return StAXEventResultBuilder.this.fEventFactory.createNamespace(string2 != null ? string2 : "");
            }
            return StAXEventResultBuilder.this.fEventFactory.createNamespace(string, string2 != null ? string2 : "");
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    final class AttributeIterator
    implements Iterator {
        XMLAttributes fAttributes;
        int fIndex;
        int fEnd;

        AttributeIterator(XMLAttributes xMLAttributes, int n) {
            this.fAttributes = xMLAttributes;
            this.fIndex = 0;
            this.fEnd = n;
        }

        @Override
        public boolean hasNext() {
            if (this.fIndex < this.fEnd) {
                return true;
            }
            this.fAttributes = null;
            return false;
        }

        public Object next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            this.fAttributes.getName(this.fIndex, StAXEventResultBuilder.this.fAttrName);
            return StAXEventResultBuilder.this.fEventFactory.createAttribute(((StAXEventResultBuilder)StAXEventResultBuilder.this).fAttrName.prefix, ((StAXEventResultBuilder)StAXEventResultBuilder.this).fAttrName.uri != null ? ((StAXEventResultBuilder)StAXEventResultBuilder.this).fAttrName.uri : "", ((StAXEventResultBuilder)StAXEventResultBuilder.this).fAttrName.localpart, this.fAttributes.getValue(this.fIndex++));
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}

