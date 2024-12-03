/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.sax;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.tika.sax.ToTextContentHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class ToXMLContentHandler
extends ToTextContentHandler {
    protected final Map<String, String> namespaces = new HashMap<String, String>();
    private final String encoding;
    protected boolean inStartElement = false;
    private ElementInfo currentElement;

    public ToXMLContentHandler(OutputStream stream, String encoding) throws UnsupportedEncodingException {
        super(stream, encoding);
        this.encoding = encoding;
    }

    public ToXMLContentHandler(String encoding) {
        this.encoding = encoding;
    }

    public ToXMLContentHandler() {
        this.encoding = null;
    }

    @Override
    public void startDocument() throws SAXException {
        if (this.encoding != null) {
            this.write("<?xml version=\"1.0\" encoding=\"");
            this.write(this.encoding);
            this.write("\"?>\n");
        }
        this.currentElement = null;
        this.namespaces.clear();
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        try {
            if (this.currentElement != null && prefix.equals(this.currentElement.getPrefix(uri))) {
                return;
            }
        }
        catch (SAXException sAXException) {
            // empty catch block
        }
        this.namespaces.put(uri, prefix);
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        this.lazyCloseStartElement();
        this.currentElement = new ElementInfo(this.currentElement, this.namespaces);
        this.write('<');
        this.write(this.currentElement.getQName(uri, localName));
        for (int i = 0; i < atts.getLength(); ++i) {
            this.write(' ');
            this.write(this.currentElement.getQName(atts.getURI(i), atts.getLocalName(i)));
            this.write('=');
            this.write('\"');
            char[] ch = atts.getValue(i).toCharArray();
            this.writeEscaped(ch, 0, ch.length, true);
            this.write('\"');
        }
        for (Map.Entry<String, String> entry : this.namespaces.entrySet()) {
            this.write(' ');
            this.write("xmlns");
            String prefix = entry.getValue();
            if (prefix.length() > 0) {
                this.write(':');
                this.write(prefix);
            }
            this.write('=');
            this.write('\"');
            char[] ch = entry.getKey().toCharArray();
            this.writeEscaped(ch, 0, ch.length, true);
            this.write('\"');
        }
        this.namespaces.clear();
        this.inStartElement = true;
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (this.inStartElement) {
            this.write(" />");
            this.inStartElement = false;
        } else {
            this.write("</");
            this.write(qName);
            this.write('>');
        }
        this.namespaces.clear();
        this.currentElement = this.currentElement.parent;
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        this.lazyCloseStartElement();
        this.writeEscaped(ch, start, start + length, false);
    }

    private void lazyCloseStartElement() throws SAXException {
        if (this.inStartElement) {
            this.write('>');
            this.inStartElement = false;
        }
    }

    protected void write(char ch) throws SAXException {
        super.characters(new char[]{ch}, 0, 1);
    }

    protected void write(String string) throws SAXException {
        super.characters(string.toCharArray(), 0, string.length());
    }

    private int writeCharsAndEntity(char[] ch, int from, int to, String entity) throws SAXException {
        super.characters(ch, from, to - from);
        this.write('&');
        this.write(entity);
        this.write(';');
        return to + 1;
    }

    private void writeEscaped(char[] ch, int from, int to, boolean attribute) throws SAXException {
        int pos = from;
        while (pos < to) {
            if (ch[pos] == '<') {
                from = pos = this.writeCharsAndEntity(ch, from, pos, "lt");
                continue;
            }
            if (ch[pos] == '>') {
                from = pos = this.writeCharsAndEntity(ch, from, pos, "gt");
                continue;
            }
            if (ch[pos] == '&') {
                from = pos = this.writeCharsAndEntity(ch, from, pos, "amp");
                continue;
            }
            if (attribute && ch[pos] == '\"') {
                from = pos = this.writeCharsAndEntity(ch, from, pos, "quot");
                continue;
            }
            ++pos;
        }
        super.characters(ch, from, to - from);
    }

    private static class ElementInfo {
        private final ElementInfo parent;
        private final Map<String, String> namespaces;

        public ElementInfo(ElementInfo parent, Map<String, String> namespaces) {
            this.parent = parent;
            this.namespaces = namespaces.isEmpty() ? Collections.emptyMap() : new HashMap<String, String>(namespaces);
        }

        public String getPrefix(String uri) throws SAXException {
            String prefix = this.namespaces.get(uri);
            if (prefix != null) {
                return prefix;
            }
            if (this.parent != null) {
                return this.parent.getPrefix(uri);
            }
            if (uri == null || uri.length() == 0) {
                return "";
            }
            throw new SAXException("Namespace " + uri + " not declared");
        }

        public String getQName(String uri, String localName) throws SAXException {
            String prefix = this.getPrefix(uri);
            if (prefix.length() > 0) {
                return prefix + ":" + localName;
            }
            return localName;
        }
    }
}

