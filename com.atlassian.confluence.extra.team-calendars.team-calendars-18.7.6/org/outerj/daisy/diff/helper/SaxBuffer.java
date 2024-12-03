/*
 * Decompiled with CFR 0.152.
 */
package org.outerj.daisy.diff.helper;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.AttributesImpl;

public class SaxBuffer
implements ContentHandler,
LexicalHandler,
Serializable {
    protected List<SaxBit> saxbits = new ArrayList<SaxBit>();

    public SaxBuffer() {
    }

    public SaxBuffer(SaxBuffer saxBuffer) {
        this.saxbits.addAll(saxBuffer.saxbits);
    }

    @Override
    public void skippedEntity(String name) throws SAXException {
        this.saxbits.add(new SkippedEntity(name));
    }

    @Override
    public void setDocumentLocator(Locator locator) {
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        this.saxbits.add(new IgnorableWhitespace(ch, start, length));
    }

    @Override
    public void processingInstruction(String target, String data) throws SAXException {
        this.saxbits.add(new PI(target, data));
    }

    @Override
    public void startDocument() throws SAXException {
        this.saxbits.add(StartDocument.SINGLETON);
    }

    @Override
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
        this.saxbits.add(new StartElement(namespaceURI, localName, qName, atts));
    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
        this.saxbits.add(new EndPrefixMapping(prefix));
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        this.saxbits.add(new Characters(ch, start, length));
    }

    @Override
    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        this.saxbits.add(new EndElement(namespaceURI, localName, qName));
    }

    @Override
    public void endDocument() throws SAXException {
        this.saxbits.add(EndDocument.SINGLETON);
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        this.saxbits.add(new StartPrefixMapping(prefix, uri));
    }

    @Override
    public void endCDATA() throws SAXException {
        this.saxbits.add(EndCDATA.SINGLETON);
    }

    @Override
    public void comment(char[] ch, int start, int length) throws SAXException {
        this.saxbits.add(new Comment(ch, start, length));
    }

    @Override
    public void startEntity(String name) throws SAXException {
        this.saxbits.add(new StartEntity(name));
    }

    @Override
    public void endDTD() throws SAXException {
        this.saxbits.add(EndDTD.SINGLETON);
    }

    @Override
    public void startDTD(String name, String publicId, String systemId) throws SAXException {
        this.saxbits.add(new StartDTD(name, publicId, systemId));
    }

    @Override
    public void startCDATA() throws SAXException {
        this.saxbits.add(StartCDATA.SINGLETON);
    }

    @Override
    public void endEntity(String name) throws SAXException {
        this.saxbits.add(new EndEntity(name));
    }

    protected final void addBit(SaxBit bit) {
        this.saxbits.add(bit);
    }

    protected final Iterator bits() {
        return this.saxbits.iterator();
    }

    public boolean isEmpty() {
        return this.saxbits.isEmpty();
    }

    public List<SaxBit> getBits() {
        return Collections.unmodifiableList(this.saxbits);
    }

    public void toSAX(ContentHandler contentHandler) throws SAXException {
        for (SaxBit saxbit : this.saxbits) {
            saxbit.send(contentHandler);
        }
    }

    public String toString() {
        StringBuilder value = new StringBuilder();
        for (SaxBit saxbit : this.saxbits) {
            if (!(saxbit instanceof Characters)) continue;
            ((Characters)saxbit).toString(value);
        }
        return value.toString();
    }

    public void recycle() {
        this.saxbits.clear();
    }

    public void dump(Writer writer) throws IOException {
        for (SaxBit saxbit : this.saxbits) {
            saxbit.dump(writer);
        }
        writer.flush();
    }

    public static final class IgnorableWhitespace
    implements SaxBit,
    Serializable {
        public final char[] ch;

        public IgnorableWhitespace(char[] ch, int start, int length) {
            this.ch = new char[length];
            System.arraycopy(ch, start, this.ch, 0, length);
        }

        @Override
        public void send(ContentHandler contentHandler) throws SAXException {
            contentHandler.ignorableWhitespace(this.ch, 0, this.ch.length);
        }

        @Override
        public void dump(Writer writer) throws IOException {
            writer.write("IgnorableWhitespace] ch=" + new String(this.ch) + "\n");
        }
    }

    public static final class EndCDATA
    implements SaxBit,
    Serializable {
        public static final EndCDATA SINGLETON = new EndCDATA();

        @Override
        public void send(ContentHandler contentHandler) throws SAXException {
            if (contentHandler instanceof LexicalHandler) {
                ((LexicalHandler)((Object)contentHandler)).endCDATA();
            }
        }

        @Override
        public void dump(Writer writer) throws IOException {
            writer.write("[EndCDATA]\n");
        }
    }

    public static final class StartCDATA
    implements SaxBit,
    Serializable {
        public static final StartCDATA SINGLETON = new StartCDATA();

        @Override
        public void send(ContentHandler contentHandler) throws SAXException {
            if (contentHandler instanceof LexicalHandler) {
                ((LexicalHandler)((Object)contentHandler)).startCDATA();
            }
        }

        @Override
        public void dump(Writer writer) throws IOException {
            writer.write("[StartCDATA]\n");
        }
    }

    public static final class Comment
    implements SaxBit,
    Serializable {
        public final char[] ch;

        public Comment(char[] ch, int start, int length) {
            this.ch = new char[length];
            System.arraycopy(ch, start, this.ch, 0, length);
        }

        @Override
        public void send(ContentHandler contentHandler) throws SAXException {
            if (contentHandler instanceof LexicalHandler) {
                ((LexicalHandler)((Object)contentHandler)).comment(this.ch, 0, this.ch.length);
            }
        }

        @Override
        public void dump(Writer writer) throws IOException {
            writer.write("[Comment] ch=" + new String(this.ch) + "\n");
        }
    }

    public static final class Characters
    implements SaxBit,
    Serializable {
        public final char[] ch;

        public Characters(char[] ch, int start, int length) {
            this.ch = new char[length];
            System.arraycopy(ch, start, this.ch, 0, length);
        }

        @Override
        public void send(ContentHandler contentHandler) throws SAXException {
            contentHandler.characters(this.ch, 0, this.ch.length);
        }

        public void toString(StringBuilder value) {
            value.append(this.ch);
        }

        @Override
        public void dump(Writer writer) throws IOException {
            writer.write("[Characters] ch=" + new String(this.ch) + "\n");
        }
    }

    public static final class EndElement
    implements SaxBit,
    Serializable {
        public final String namespaceURI;
        public final String localName;
        public final String qName;

        public EndElement(String namespaceURI, String localName, String qName) {
            this.namespaceURI = namespaceURI;
            this.localName = localName;
            this.qName = qName;
        }

        @Override
        public void send(ContentHandler contentHandler) throws SAXException {
            contentHandler.endElement(this.namespaceURI, this.localName, this.qName);
        }

        @Override
        public void dump(Writer writer) throws IOException {
            writer.write("[EndElement] namespaceURI=" + this.namespaceURI + ",localName=" + this.localName + ",qName=" + this.qName + "\n");
        }
    }

    public static final class StartElement
    implements SaxBit,
    Serializable {
        public final String namespaceURI;
        public final String localName;
        public final String qName;
        public final Attributes attrs;

        public StartElement(String namespaceURI, String localName, String qName, Attributes attrs) {
            this.namespaceURI = namespaceURI;
            this.localName = localName;
            this.qName = qName;
            this.attrs = new AttributesImpl(attrs);
        }

        @Override
        public void send(ContentHandler contentHandler) throws SAXException {
            contentHandler.startElement(this.namespaceURI, this.localName, this.qName, this.attrs);
        }

        @Override
        public void dump(Writer writer) throws IOException {
            writer.write("[StartElement] namespaceURI=" + this.namespaceURI + ",localName=" + this.localName + ",qName=" + this.qName + "\n");
            for (int i = 0; i < this.attrs.getLength(); ++i) {
                writer.write("      [Attribute] namespaceURI=" + this.attrs.getURI(i) + ",localName=" + this.attrs.getLocalName(i) + ",qName=" + this.attrs.getQName(i) + ",type=" + this.attrs.getType(i) + ",value=" + this.attrs.getValue(i) + "\n");
            }
        }
    }

    public static final class EndPrefixMapping
    implements SaxBit,
    Serializable {
        public final String prefix;

        public EndPrefixMapping(String prefix) {
            this.prefix = prefix;
        }

        @Override
        public void send(ContentHandler contentHandler) throws SAXException {
            contentHandler.endPrefixMapping(this.prefix);
        }

        @Override
        public void dump(Writer writer) throws IOException {
            writer.write("[EndPrefixMapping] prefix=" + this.prefix + "\n");
        }
    }

    public static final class StartPrefixMapping
    implements SaxBit,
    Serializable {
        public final String prefix;
        public final String uri;

        public StartPrefixMapping(String prefix, String uri) {
            this.prefix = prefix;
            this.uri = uri;
        }

        @Override
        public void send(ContentHandler contentHandler) throws SAXException {
            contentHandler.startPrefixMapping(this.prefix, this.uri);
        }

        @Override
        public void dump(Writer writer) throws IOException {
            writer.write("[StartPrefixMapping] prefix=" + this.prefix + ",uri=" + this.uri + "\n");
        }
    }

    public static final class SkippedEntity
    implements SaxBit,
    Serializable {
        public final String name;

        public SkippedEntity(String name) {
            this.name = name;
        }

        @Override
        public void send(ContentHandler contentHandler) throws SAXException {
            contentHandler.skippedEntity(this.name);
        }

        @Override
        public void dump(Writer writer) throws IOException {
            writer.write("[SkippedEntity] name=" + this.name + "\n");
        }
    }

    public static final class EndEntity
    implements SaxBit,
    Serializable {
        public final String name;

        public EndEntity(String name) {
            this.name = name;
        }

        @Override
        public void send(ContentHandler contentHandler) throws SAXException {
            if (contentHandler instanceof LexicalHandler) {
                ((LexicalHandler)((Object)contentHandler)).endEntity(this.name);
            }
        }

        @Override
        public void dump(Writer writer) throws IOException {
            writer.write("[EndEntity] name=" + this.name + "\n");
        }
    }

    public static final class StartEntity
    implements SaxBit,
    Serializable {
        public final String name;

        public StartEntity(String name) {
            this.name = name;
        }

        @Override
        public void send(ContentHandler contentHandler) throws SAXException {
            if (contentHandler instanceof LexicalHandler) {
                ((LexicalHandler)((Object)contentHandler)).startEntity(this.name);
            }
        }

        @Override
        public void dump(Writer writer) throws IOException {
            writer.write("[StartEntity] name=" + this.name + "\n");
        }
    }

    public static final class EndDTD
    implements SaxBit,
    Serializable {
        public static final EndDTD SINGLETON = new EndDTD();

        @Override
        public void send(ContentHandler contentHandler) throws SAXException {
            if (contentHandler instanceof LexicalHandler) {
                ((LexicalHandler)((Object)contentHandler)).endDTD();
            }
        }

        @Override
        public void dump(Writer writer) throws IOException {
            writer.write("[EndDTD]\n");
        }
    }

    public static final class StartDTD
    implements SaxBit,
    Serializable {
        public final String name;
        public final String publicId;
        public final String systemId;

        public StartDTD(String name, String publicId, String systemId) {
            this.name = name;
            this.publicId = publicId;
            this.systemId = systemId;
        }

        @Override
        public void send(ContentHandler contentHandler) throws SAXException {
            if (contentHandler instanceof LexicalHandler) {
                ((LexicalHandler)((Object)contentHandler)).startDTD(this.name, this.publicId, this.systemId);
            }
        }

        @Override
        public void dump(Writer writer) throws IOException {
            writer.write("[StartDTD] name=" + this.name + ",publicId=" + this.publicId + ",systemId=" + this.systemId + "\n");
        }
    }

    public static final class PI
    implements SaxBit,
    Serializable {
        public final String target;
        public final String data;

        public PI(String target, String data) {
            this.target = target;
            this.data = data;
        }

        @Override
        public void send(ContentHandler contentHandler) throws SAXException {
            contentHandler.processingInstruction(this.target, this.data);
        }

        @Override
        public void dump(Writer writer) throws IOException {
            writer.write("[ProcessingInstruction] target=" + this.target + ",data=" + this.data + "\n");
        }
    }

    public static final class EndDocument
    implements SaxBit,
    Serializable {
        public static final EndDocument SINGLETON = new EndDocument();

        @Override
        public void send(ContentHandler contentHandler) throws SAXException {
            contentHandler.endDocument();
        }

        @Override
        public void dump(Writer writer) throws IOException {
            writer.write("[EndDocument]\n");
        }
    }

    public static final class StartDocument
    implements SaxBit,
    Serializable {
        public static final StartDocument SINGLETON = new StartDocument();

        @Override
        public void send(ContentHandler contentHandler) throws SAXException {
            contentHandler.startDocument();
        }

        @Override
        public void dump(Writer writer) throws IOException {
            writer.write("[StartDocument]\n");
        }
    }

    public static interface SaxBit {
        public void send(ContentHandler var1) throws SAXException;

        public void dump(Writer var1) throws IOException;
    }
}

