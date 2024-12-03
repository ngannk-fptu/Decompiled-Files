/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.writer;

import java.util.Enumeration;
import org.xml.sax.AttributeList;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DocumentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.NamespaceSupport;

public class ContentHandlerAdaptor
implements DocumentHandler {
    private final NamespaceSupport nsSupport = new NamespaceSupport();
    private final ContentHandler contentHandler;
    private final AttributeListAdapter attAdapter = new AttributeListAdapter();
    private final AttributesImpl atts = new AttributesImpl();
    private final boolean namespaces = true;
    private final boolean prefixes = false;
    private final String[] nameParts = new String[3];

    public ContentHandlerAdaptor(ContentHandler handler) {
        this.contentHandler = handler;
    }

    public void setDocumentLocator(Locator locator) {
        if (this.contentHandler != null) {
            this.contentHandler.setDocumentLocator(locator);
        }
    }

    public void startDocument() throws SAXException {
        if (this.contentHandler != null) {
            this.contentHandler.startDocument();
        }
    }

    public void endDocument() throws SAXException {
        if (this.contentHandler != null) {
            this.contentHandler.endDocument();
        }
    }

    public void startElement(String qName, AttributeList qAtts) throws SAXException {
        String attQName;
        int i;
        this.nsSupport.pushContext();
        boolean seenDecl = false;
        this.atts.clear();
        int length = qAtts.getLength();
        for (i = 0; i < length; ++i) {
            attQName = qAtts.getName(i);
            String type = qAtts.getType(i);
            String value = qAtts.getValue(i);
            if (attQName.startsWith("xmlns")) {
                int n = attQName.indexOf(58);
                String prefix = n == -1 ? "" : attQName.substring(n + 1);
                if (!this.nsSupport.declarePrefix(prefix, value)) {
                    this.reportError("Illegal Namespace prefix: " + prefix);
                }
                if (this.contentHandler != null) {
                    this.contentHandler.startPrefixMapping(prefix, value);
                }
                seenDecl = true;
                continue;
            }
            String[] attName = this.processName(attQName, true);
            this.atts.addAttribute(attName[0], attName[1], attName[2], type, value);
        }
        if (seenDecl) {
            length = this.atts.getLength();
            for (i = 0; i < length; ++i) {
                attQName = this.atts.getQName(i);
                if (attQName.startsWith("xmlns")) continue;
                String[] attName = this.processName(attQName, true);
                this.atts.setURI(i, attName[0]);
                this.atts.setLocalName(i, attName[1]);
            }
        }
        if (this.contentHandler != null) {
            String[] name = this.processName(qName, false);
            this.contentHandler.startElement(name[0], name[1], name[2], this.atts);
        }
    }

    public void endElement(String qName) throws SAXException {
        String[] names = this.processName(qName, false);
        if (this.contentHandler != null) {
            this.contentHandler.endElement(names[0], names[1], names[2]);
            Enumeration<String> prefixes = this.nsSupport.getDeclaredPrefixes();
            while (prefixes.hasMoreElements()) {
                String prefix = prefixes.nextElement();
                this.contentHandler.endPrefixMapping(prefix);
            }
        }
        this.nsSupport.popContext();
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        if (this.contentHandler != null) {
            this.contentHandler.characters(ch, start, length);
        }
    }

    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        if (this.contentHandler != null) {
            this.contentHandler.ignorableWhitespace(ch, start, length);
        }
    }

    public void processingInstruction(String target, String data) throws SAXException {
        if (this.contentHandler != null) {
            this.contentHandler.processingInstruction(target, data);
        }
    }

    private String[] processName(String qName, boolean isAttribute) throws SAXException {
        String[] parts = this.nsSupport.processName(qName, this.nameParts, isAttribute);
        if (parts == null) {
            parts = new String[3];
            parts[2] = qName.intern();
            this.reportError("Undeclared prefix: " + qName);
        }
        return parts;
    }

    void reportError(String message) throws SAXException {
        throw new SAXParseException(message, null, null, -1, -1);
    }

    final class AttributeListAdapter
    implements Attributes {
        private AttributeList qAtts;

        AttributeListAdapter() {
        }

        void setAttributeList(AttributeList qAtts) {
            this.qAtts = qAtts;
        }

        public int getLength() {
            return this.qAtts.getLength();
        }

        public String getURI(int i) {
            return "";
        }

        public String getLocalName(int i) {
            return "";
        }

        public String getQName(int i) {
            return this.qAtts.getName(i).intern();
        }

        public String getType(int i) {
            return this.qAtts.getType(i).intern();
        }

        public String getValue(int i) {
            return this.qAtts.getValue(i);
        }

        public int getIndex(String uri, String localName) {
            return -1;
        }

        public int getIndex(String qName) {
            int max = ContentHandlerAdaptor.this.atts.getLength();
            for (int i = 0; i < max; ++i) {
                if (!this.qAtts.getName(i).equals(qName)) continue;
                return i;
            }
            return -1;
        }

        public String getType(String uri, String localName) {
            return null;
        }

        public String getType(String qName) {
            return this.qAtts.getType(qName).intern();
        }

        public String getValue(String uri, String localName) {
            return null;
        }

        public String getValue(String qName) {
            return this.qAtts.getValue(qName);
        }
    }
}

