/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.sax;

import java.io.OutputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.Property;
import org.apache.tika.sax.ContentHandlerFactory;
import org.apache.tika.utils.ParserUtils;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public abstract class AbstractRecursiveParserWrapperHandler
extends DefaultHandler
implements Serializable {
    public static final Property TIKA_CONTENT = Property.internalText("X-TIKA:content");
    public static final Property TIKA_CONTENT_HANDLER = Property.internalText("X-TIKA:content_handler");
    public static final Property PARSE_TIME_MILLIS = Property.internalText("X-TIKA:parse_time_millis");
    public static final Property WRITE_LIMIT_REACHED = Property.internalBoolean("X-TIKA:EXCEPTION:write_limit_reached");
    public static final Property EMBEDDED_RESOURCE_LIMIT_REACHED = Property.internalBoolean("X-TIKA:EXCEPTION:embedded_resource_limit_reached");
    public static final Property EMBEDDED_EXCEPTION = ParserUtils.EMBEDDED_EXCEPTION;
    public static final Property CONTAINER_EXCEPTION = Property.internalText("X-TIKA:EXCEPTION:runtime");
    public static final Property EMBEDDED_RESOURCE_PATH = Property.internalText("X-TIKA:embedded_resource_path");
    public static final Property EMBEDDED_DEPTH = Property.internalInteger("X-TIKA:embedded_depth");
    private final ContentHandlerFactory contentHandlerFactory;
    private static final int MAX_DEPTH = 100;
    private final int maxEmbeddedResources;
    private final int totalWriteLimit;
    private int embeddedResources = 0;
    private int embeddedDepth = 0;

    public AbstractRecursiveParserWrapperHandler(ContentHandlerFactory contentHandlerFactory) {
        this(contentHandlerFactory, -1, -1);
    }

    public AbstractRecursiveParserWrapperHandler(ContentHandlerFactory contentHandlerFactory, int maxEmbeddedResources, int totalWriteLimit) {
        this.contentHandlerFactory = contentHandlerFactory;
        this.maxEmbeddedResources = maxEmbeddedResources;
        this.totalWriteLimit = totalWriteLimit;
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
        metadata.set(EMBEDDED_DEPTH, this.embeddedDepth);
    }

    public void endEmbeddedDocument(ContentHandler contentHandler, Metadata metadata) throws SAXException {
        --this.embeddedDepth;
    }

    public void endDocument(ContentHandler contentHandler, Metadata metadata) throws SAXException {
        if (this.hasHitMaximumEmbeddedResources()) {
            metadata.set(EMBEDDED_RESOURCE_LIMIT_REACHED, "true");
        }
        metadata.set(EMBEDDED_DEPTH, 0);
    }

    public boolean hasHitMaximumEmbeddedResources() {
        return this.maxEmbeddedResources > -1 && this.embeddedResources >= this.maxEmbeddedResources;
    }

    public ContentHandlerFactory getContentHandlerFactory() {
        return this.contentHandlerFactory;
    }

    public int getTotalWriteLimit() {
        return this.totalWriteLimit;
    }
}

