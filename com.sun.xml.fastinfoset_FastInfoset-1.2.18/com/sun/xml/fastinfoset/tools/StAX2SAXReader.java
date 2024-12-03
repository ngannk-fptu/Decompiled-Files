/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.fastinfoset.tools;

import com.sun.xml.fastinfoset.CommonResourceBundle;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.AttributesImpl;

public class StAX2SAXReader {
    ContentHandler _handler;
    LexicalHandler _lexicalHandler;
    XMLStreamReader _reader;

    public StAX2SAXReader(XMLStreamReader reader, ContentHandler handler) {
        this._handler = handler;
        this._reader = reader;
    }

    public StAX2SAXReader(XMLStreamReader reader) {
        this._reader = reader;
    }

    public void setContentHandler(ContentHandler handler) {
        this._handler = handler;
    }

    public void setLexicalHandler(LexicalHandler lexicalHandler) {
        this._lexicalHandler = lexicalHandler;
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public void adapt() throws XMLStreamException, SAXException {
        attrs = new AttributesImpl();
        this._handler.startDocument();
lbl3:
        // 6 sources

        try {
            block10: while (this._reader.hasNext()) {
                event = this._reader.next();
                switch (event) {
                    case 1: {
                        nsc = this._reader.getNamespaceCount();
                        for (i = 0; i < nsc; ++i) {
                            this._handler.startPrefixMapping(this._reader.getNamespacePrefix(i), this._reader.getNamespaceURI(i));
                        }
                        attrs.clear();
                        nat = this._reader.getAttributeCount();
                        for (i = 0; i < nat; ++i) {
                            q = this._reader.getAttributeName(i);
                            qName = this._reader.getAttributePrefix(i);
                            qName = qName == null || qName == "" ? q.getLocalPart() : qName + ":" + q.getLocalPart();
                            attrs.addAttribute(this._reader.getAttributeNamespace(i), q.getLocalPart(), qName, this._reader.getAttributeType(i), this._reader.getAttributeValue(i));
                        }
                        qname = this._reader.getName();
                        prefix = qname.getPrefix();
                        localPart = qname.getLocalPart();
                        this._handler.startElement(this._reader.getNamespaceURI(), localPart, prefix.length() > 0 ? prefix + ":" + localPart : localPart, attrs);
                        ** GOTO lbl3
                    }
                    case 2: {
                        qname = this._reader.getName();
                        prefix = qname.getPrefix();
                        localPart = qname.getLocalPart();
                        this._handler.endElement(this._reader.getNamespaceURI(), localPart, prefix.length() > 0 ? prefix + ":" + localPart : localPart);
                        nsc = this._reader.getNamespaceCount();
                        for (i = 0; i < nsc; ++i) {
                            this._handler.endPrefixMapping(this._reader.getNamespacePrefix(i));
                        }
                        continue block10;
                    }
                    case 4: {
                        this._handler.characters(this._reader.getTextCharacters(), this._reader.getTextStart(), this._reader.getTextLength());
                        ** GOTO lbl3
                    }
                    case 5: {
                        this._lexicalHandler.comment(this._reader.getTextCharacters(), this._reader.getTextStart(), this._reader.getTextLength());
                        ** GOTO lbl3
                    }
                    case 3: {
                        this._handler.processingInstruction(this._reader.getPITarget(), this._reader.getPIData());
                        ** GOTO lbl3
                    }
                    case 8: {
                        ** GOTO lbl3
                    }
                }
                throw new RuntimeException(CommonResourceBundle.getInstance().getString("message.StAX2SAXReader", new Object[]{event}));
            }
        }
        catch (XMLStreamException e) {
            this._handler.endDocument();
            throw e;
        }
        this._handler.endDocument();
    }
}

