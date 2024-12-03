/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.fastinfoset.tools;

import com.sun.xml.fastinfoset.QualifiedName;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

public class SAX2StAXWriter
extends DefaultHandler
implements LexicalHandler {
    private static final Logger logger = Logger.getLogger(SAX2StAXWriter.class.getName());
    XMLStreamWriter _writer;
    ArrayList _namespaces = new ArrayList();

    public SAX2StAXWriter(XMLStreamWriter writer) {
        this._writer = writer;
    }

    public XMLStreamWriter getWriter() {
        return this._writer;
    }

    @Override
    public void startDocument() throws SAXException {
        try {
            this._writer.writeStartDocument();
        }
        catch (XMLStreamException e) {
            throw new SAXException(e);
        }
    }

    @Override
    public void endDocument() throws SAXException {
        try {
            this._writer.writeEndDocument();
            this._writer.flush();
        }
        catch (XMLStreamException e) {
            throw new SAXException(e);
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        try {
            this._writer.writeCharacters(ch, start, length);
        }
        catch (XMLStreamException e) {
            throw new SAXException(e);
        }
    }

    @Override
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
        try {
            int i;
            int k = qName.indexOf(58);
            String prefix = k > 0 ? qName.substring(0, k) : "";
            this._writer.writeStartElement(prefix, localName, namespaceURI);
            int length = this._namespaces.size();
            for (i = 0; i < length; ++i) {
                QualifiedName nsh = (QualifiedName)this._namespaces.get(i);
                this._writer.writeNamespace(nsh.prefix, nsh.namespaceName);
            }
            this._namespaces.clear();
            length = atts.getLength();
            for (i = 0; i < length; ++i) {
                this._writer.writeAttribute(atts.getURI(i), atts.getLocalName(i), atts.getValue(i));
            }
        }
        catch (XMLStreamException e) {
            throw new SAXException(e);
        }
    }

    @Override
    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        try {
            this._writer.writeEndElement();
        }
        catch (XMLStreamException e) {
            logger.log(Level.FINE, "Exception on endElement", e);
            throw new SAXException(e);
        }
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        this._namespaces.add(new QualifiedName(prefix, uri));
    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        this.characters(ch, start, length);
    }

    @Override
    public void processingInstruction(String target, String data) throws SAXException {
        try {
            this._writer.writeProcessingInstruction(target, data);
        }
        catch (XMLStreamException e) {
            throw new SAXException(e);
        }
    }

    @Override
    public void setDocumentLocator(Locator locator) {
    }

    @Override
    public void skippedEntity(String name) throws SAXException {
    }

    @Override
    public void comment(char[] ch, int start, int length) throws SAXException {
        try {
            this._writer.writeComment(new String(ch, start, length));
        }
        catch (XMLStreamException e) {
            throw new SAXException(e);
        }
    }

    @Override
    public void endCDATA() throws SAXException {
    }

    @Override
    public void endDTD() throws SAXException {
    }

    @Override
    public void endEntity(String name) throws SAXException {
    }

    @Override
    public void startCDATA() throws SAXException {
    }

    @Override
    public void startDTD(String name, String publicId, String systemId) throws SAXException {
    }

    @Override
    public void startEntity(String name) throws SAXException {
    }
}

