/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.sax;

import java.io.OutputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.Property;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.sax.ContentHandlerFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public abstract class AbstractRecursiveParserWrapperHandler
extends DefaultHandler
implements Serializable {
    public static final Property EMBEDDED_RESOURCE_LIMIT_REACHED = Property.internalBoolean("X-TIKA:EXCEPTION:embedded_resource_limit_reached");
    private static final int MAX_DEPTH = 100;
    private final ContentHandlerFactory contentHandlerFactory;
    private final int maxEmbeddedResources;
    private int embeddedResources = 0;
    private int embeddedDepth = 0;

    public AbstractRecursiveParserWrapperHandler(ContentHandlerFactory contentHandlerFactory) {
        this(contentHandlerFactory, -1);
    }

    public AbstractRecursiveParserWrapperHandler(ContentHandlerFactory contentHandlerFactory, int maxEmbeddedResources) {
        this.contentHandlerFactory = contentHandlerFactory;
        this.maxEmbeddedResources = maxEmbeddedResources;
    }

    public ContentHandler getNewContentHandler() {
        return this.contentHandlerFactory.getNewContentHandler();
    }

    public ContentHandler getNewContentHandler(OutputStream os, Charset charset) {
        return this.contentHandlerFactory.getNewContentHandler(os, charset);
    }

    public void startEmbeddedDocument(ContentHandler contentHandler, Metadata metadata) throws SAXException {
        ++this.embeddedResources;
        ++this.embeddedDepth;
        if (this.embeddedDepth >= 100) {
            throw new SAXException("Max embedded depth reached: " + this.embeddedDepth);
        }
        metadata.set(TikaCoreProperties.EMBEDDED_DEPTH, this.embeddedDepth);
    }

    public void endEmbeddedDocument(ContentHandler contentHandler, Metadata metadata) throws SAXException {
        --this.embeddedDepth;
    }

    public void endDocument(ContentHandler contentHandler, Metadata metadata) throws SAXException {
        if (this.hasHitMaximumEmbeddedResources()) {
            metadata.set(EMBEDDED_RESOURCE_LIMIT_REACHED, "true");
        }
        metadata.set(TikaCoreProperties.EMBEDDED_DEPTH, 0);
    }

    public boolean hasHitMaximumEmbeddedResources() {
        return this.maxEmbeddedResources > -1 && this.embeddedResources >= this.maxEmbeddedResources;
    }

    public ContentHandlerFactory getContentHandlerFactory() {
        return this.contentHandlerFactory;
    }
}

