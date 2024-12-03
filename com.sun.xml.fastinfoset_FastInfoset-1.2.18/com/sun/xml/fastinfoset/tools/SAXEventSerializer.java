/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.fastinfoset.tools;

import com.sun.xml.fastinfoset.CommonResourceBundle;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

public class SAXEventSerializer
extends DefaultHandler
implements LexicalHandler {
    private Writer _writer;
    private boolean _charactersAreCDATA;
    private StringBuffer _characters;
    private Stack _namespaceStack = new Stack();
    protected List _namespaceAttributes;

    public SAXEventSerializer(OutputStream s) throws IOException {
        this._writer = new OutputStreamWriter(s);
        this._charactersAreCDATA = false;
    }

    @Override
    public void startDocument() throws SAXException {
        try {
            this._writer.write("<sax xmlns=\"http://www.sun.com/xml/sax-events\">\n");
            this._writer.write("<startDocument/>\n");
            this._writer.flush();
        }
        catch (IOException e) {
            throw new SAXException(e);
        }
    }

    @Override
    public void endDocument() throws SAXException {
        try {
            this._writer.write("<endDocument/>\n");
            this._writer.write("</sax>");
            this._writer.flush();
            this._writer.close();
        }
        catch (IOException e) {
            throw new SAXException(e);
        }
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        if (this._namespaceAttributes == null) {
            this._namespaceAttributes = new ArrayList();
        }
        String qName = prefix.length() == 0 ? "xmlns" : "xmlns" + prefix;
        AttributeValueHolder attribute = new AttributeValueHolder(qName, prefix, uri, null, null);
        this._namespaceAttributes.add(attribute);
    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        try {
            int i;
            int i2;
            AttributeValueHolder[] attrsHolder;
            this.outputCharacters();
            if (this._namespaceAttributes != null) {
                attrsHolder = new AttributeValueHolder[]{};
                attrsHolder = this._namespaceAttributes.toArray(attrsHolder);
                this.quicksort(attrsHolder, 0, attrsHolder.length - 1);
                for (i2 = 0; i2 < attrsHolder.length; ++i2) {
                    this._writer.write("<startPrefixMapping prefix=\"" + attrsHolder[i2].localName + "\" uri=\"" + attrsHolder[i2].uri + "\"/>\n");
                    this._writer.flush();
                }
                this._namespaceStack.push(attrsHolder);
                this._namespaceAttributes = null;
            } else {
                this._namespaceStack.push(null);
            }
            attrsHolder = new AttributeValueHolder[attributes.getLength()];
            for (i2 = 0; i2 < attributes.getLength(); ++i2) {
                attrsHolder[i2] = new AttributeValueHolder(attributes.getQName(i2), attributes.getLocalName(i2), attributes.getURI(i2), attributes.getType(i2), attributes.getValue(i2));
            }
            this.quicksort(attrsHolder, 0, attrsHolder.length - 1);
            int attributeCount = 0;
            for (i = 0; i < attrsHolder.length; ++i) {
                if (attrsHolder[i].uri.equals("http://www.w3.org/2000/xmlns/")) continue;
                ++attributeCount;
            }
            if (attributeCount == 0) {
                this._writer.write("<startElement uri=\"" + uri + "\" localName=\"" + localName + "\" qName=\"" + qName + "\"/>\n");
                return;
            }
            this._writer.write("<startElement uri=\"" + uri + "\" localName=\"" + localName + "\" qName=\"" + qName + "\">\n");
            for (i = 0; i < attrsHolder.length; ++i) {
                if (attrsHolder[i].uri.equals("http://www.w3.org/2000/xmlns/")) continue;
                this._writer.write("  <attribute qName=\"" + attrsHolder[i].qName + "\" localName=\"" + attrsHolder[i].localName + "\" uri=\"" + attrsHolder[i].uri + "\" value=\"" + attrsHolder[i].value + "\"/>\n");
            }
            this._writer.write("</startElement>\n");
            this._writer.flush();
        }
        catch (IOException e) {
            throw new SAXException(e);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        try {
            this.outputCharacters();
            this._writer.write("<endElement uri=\"" + uri + "\" localName=\"" + localName + "\" qName=\"" + qName + "\"/>\n");
            this._writer.flush();
            AttributeValueHolder[] attrsHolder = (AttributeValueHolder[])this._namespaceStack.pop();
            if (attrsHolder != null) {
                for (int i = 0; i < attrsHolder.length; ++i) {
                    this._writer.write("<endPrefixMapping prefix=\"" + attrsHolder[i].localName + "\"/>\n");
                    this._writer.flush();
                }
            }
        }
        catch (IOException e) {
            throw new SAXException(e);
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (length == 0) {
            return;
        }
        if (this._characters == null) {
            this._characters = new StringBuffer();
        }
        this._characters.append(ch, start, length);
    }

    private void outputCharacters() throws SAXException {
        if (this._characters == null) {
            return;
        }
        try {
            this._writer.write("<characters>" + (this._charactersAreCDATA ? "<![CDATA[" : "") + this._characters + (this._charactersAreCDATA ? "]]>" : "") + "</characters>\n");
            this._writer.flush();
            this._characters = null;
        }
        catch (IOException e) {
            throw new SAXException(e);
        }
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        this.characters(ch, start, length);
    }

    @Override
    public void processingInstruction(String target, String data) throws SAXException {
        try {
            this.outputCharacters();
            this._writer.write("<processingInstruction target=\"" + target + "\" data=\"" + data + "\"/>\n");
            this._writer.flush();
        }
        catch (IOException e) {
            throw new SAXException(e);
        }
    }

    @Override
    public void startDTD(String name, String publicId, String systemId) throws SAXException {
    }

    @Override
    public void endDTD() throws SAXException {
    }

    @Override
    public void startEntity(String name) throws SAXException {
    }

    @Override
    public void endEntity(String name) throws SAXException {
    }

    @Override
    public void startCDATA() throws SAXException {
        this._charactersAreCDATA = true;
    }

    @Override
    public void endCDATA() throws SAXException {
        this._charactersAreCDATA = false;
    }

    @Override
    public void comment(char[] ch, int start, int length) throws SAXException {
        try {
            this.outputCharacters();
            this._writer.write("<comment>" + new String(ch, start, length) + "</comment>\n");
            this._writer.flush();
        }
        catch (IOException e) {
            throw new SAXException(e);
        }
    }

    private void quicksort(AttributeValueHolder[] attrs, int p, int r) {
        while (p < r) {
            int q = this.partition(attrs, p, r);
            this.quicksort(attrs, p, q);
            p = q + 1;
        }
    }

    private int partition(AttributeValueHolder[] attrs, int p, int r) {
        AttributeValueHolder x = attrs[p + r >>> 1];
        int i = p - 1;
        int j = r + 1;
        while (true) {
            if (x.compareTo(attrs[--j]) < 0) {
                continue;
            }
            while (x.compareTo(attrs[++i]) > 0) {
            }
            if (i >= j) break;
            AttributeValueHolder t = attrs[i];
            attrs[i] = attrs[j];
            attrs[j] = t;
        }
        return j;
    }

    public static class AttributeValueHolder
    implements Comparable {
        public final String qName;
        public final String localName;
        public final String uri;
        public final String type;
        public final String value;

        public AttributeValueHolder(String qName, String localName, String uri, String type, String value) {
            this.qName = qName;
            this.localName = localName;
            this.uri = uri;
            this.type = type;
            this.value = value;
        }

        public int compareTo(Object o) {
            try {
                return this.qName.compareTo(((AttributeValueHolder)o).qName);
            }
            catch (Exception e) {
                throw new RuntimeException(CommonResourceBundle.getInstance().getString("message.AttributeValueHolderExpected"));
            }
        }

        public boolean equals(Object o) {
            try {
                return o instanceof AttributeValueHolder && this.qName.equals(((AttributeValueHolder)o).qName);
            }
            catch (Exception e) {
                throw new RuntimeException(CommonResourceBundle.getInstance().getString("message.AttributeValueHolderExpected"));
            }
        }

        public int hashCode() {
            int hash = 7;
            hash = 97 * hash + (this.qName != null ? this.qName.hashCode() : 0);
            return hash;
        }
    }
}

