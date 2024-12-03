/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.sax;

import java.io.IOException;
import java.util.LinkedList;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.sax.ContentHandlerDecorator;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class SecureContentHandler
extends ContentHandlerDecorator {
    private final TikaInputStream stream;
    private long characterCount = 0L;
    private int currentDepth = 0;
    private LinkedList<Integer> packageEntryDepths = new LinkedList();
    private long threshold = 1000000L;
    private long ratio = 100L;
    private int maxDepth = 100;
    private int maxPackageEntryDepth = 10;

    public SecureContentHandler(ContentHandler handler, TikaInputStream stream) {
        super(handler);
        this.stream = stream;
    }

    public long getOutputThreshold() {
        return this.threshold;
    }

    public void setOutputThreshold(long threshold) {
        this.threshold = threshold;
    }

    public long getMaximumCompressionRatio() {
        return this.ratio;
    }

    public void setMaximumCompressionRatio(long ratio) {
        this.ratio = ratio;
    }

    public int getMaximumDepth() {
        return this.maxDepth;
    }

    public void setMaximumPackageEntryDepth(int depth) {
        this.maxPackageEntryDepth = depth;
    }

    public int getMaximumPackageEntryDepth() {
        return this.maxPackageEntryDepth;
    }

    public void setMaximumDepth(int depth) {
        this.maxDepth = depth;
    }

    public void throwIfCauseOf(SAXException e) throws TikaException {
        if (e instanceof SecureSAXException && ((SecureSAXException)e).isCausedBy(this)) {
            throw new TikaException("Zip bomb detected!", e);
        }
    }

    private long getByteCount() throws SAXException {
        try {
            if (this.stream.hasLength()) {
                return this.stream.getLength();
            }
            return this.stream.getPosition();
        }
        catch (IOException e) {
            throw new SAXException("Unable to get stream length", e);
        }
    }

    protected void advance(int length) throws SAXException {
        this.characterCount += (long)length;
        long byteCount = this.getByteCount();
        if (this.characterCount > this.threshold && this.characterCount > byteCount * this.ratio) {
            throw new SecureSAXException("Suspected zip bomb: " + byteCount + " input bytes produced " + this.characterCount + " output characters");
        }
    }

    @Override
    public void startElement(String uri, String localName, String name, Attributes atts) throws SAXException {
        ++this.currentDepth;
        if (this.currentDepth >= this.maxDepth) {
            throw new SecureSAXException("Suspected zip bomb: " + this.currentDepth + " levels of XML element nesting");
        }
        if ("div".equals(name) && "package-entry".equals(atts.getValue("class"))) {
            this.packageEntryDepths.addLast(this.currentDepth);
            if (this.packageEntryDepths.size() >= this.maxPackageEntryDepth) {
                throw new SecureSAXException("Suspected zip bomb: " + this.packageEntryDepths.size() + " levels of package entry nesting");
            }
        }
        super.startElement(uri, localName, name, atts);
    }

    @Override
    public void endElement(String uri, String localName, String name) throws SAXException {
        super.endElement(uri, localName, name);
        if (!this.packageEntryDepths.isEmpty() && this.packageEntryDepths.getLast() == this.currentDepth) {
            this.packageEntryDepths.removeLast();
        }
        --this.currentDepth;
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        this.advance(length);
        super.characters(ch, start, length);
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        this.advance(length);
        super.ignorableWhitespace(ch, start, length);
    }

    private class SecureSAXException
    extends SAXException {
        private static final long serialVersionUID = 2285245380321771445L;

        public SecureSAXException(String message) throws SAXException {
            super(message);
        }

        public boolean isCausedBy(SecureContentHandler handler) {
            return SecureContentHandler.this == handler;
        }
    }
}

