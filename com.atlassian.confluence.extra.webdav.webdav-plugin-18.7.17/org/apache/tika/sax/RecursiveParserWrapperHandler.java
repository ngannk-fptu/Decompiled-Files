/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.sax;

import java.util.LinkedList;
import java.util.List;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.metadata.filter.MetadataFilter;
import org.apache.tika.metadata.filter.NoOpFilter;
import org.apache.tika.sax.AbstractRecursiveParserWrapperHandler;
import org.apache.tika.sax.ContentHandlerFactory;
import org.apache.tika.utils.ParserUtils;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class RecursiveParserWrapperHandler
extends AbstractRecursiveParserWrapperHandler {
    protected final List<Metadata> metadataList = new LinkedList<Metadata>();
    private final MetadataFilter metadataFilter;

    public RecursiveParserWrapperHandler(ContentHandlerFactory contentHandlerFactory) {
        this(contentHandlerFactory, -1, NoOpFilter.NOOP_FILTER);
    }

    public RecursiveParserWrapperHandler(ContentHandlerFactory contentHandlerFactory, int maxEmbeddedResources) {
        this(contentHandlerFactory, maxEmbeddedResources, NoOpFilter.NOOP_FILTER);
    }

    public RecursiveParserWrapperHandler(ContentHandlerFactory contentHandlerFactory, int maxEmbeddedResources, MetadataFilter metadataFilter) {
        super(contentHandlerFactory, maxEmbeddedResources);
        this.metadataFilter = metadataFilter;
    }

    @Override
    public void startEmbeddedDocument(ContentHandler contentHandler, Metadata metadata) throws SAXException {
        super.startEmbeddedDocument(contentHandler, metadata);
    }

    @Override
    public void endEmbeddedDocument(ContentHandler contentHandler, Metadata metadata) throws SAXException {
        super.endEmbeddedDocument(contentHandler, metadata);
        this.addContent(contentHandler, metadata);
        try {
            this.metadataFilter.filter(metadata);
        }
        catch (TikaException e) {
            throw new SAXException(e);
        }
        if (metadata.size() > 0) {
            this.metadataList.add(ParserUtils.cloneMetadata(metadata));
        }
    }

    @Override
    public void endDocument(ContentHandler contentHandler, Metadata metadata) throws SAXException {
        super.endDocument(contentHandler, metadata);
        this.addContent(contentHandler, metadata);
        try {
            this.metadataFilter.filter(metadata);
        }
        catch (TikaException e) {
            throw new SAXException(e);
        }
        if (metadata.size() > 0) {
            this.metadataList.add(0, ParserUtils.cloneMetadata(metadata));
        }
    }

    public List<Metadata> getMetadataList() {
        return this.metadataList;
    }

    void addContent(ContentHandler handler, Metadata metadata) {
        String content;
        if (!handler.getClass().equals(DefaultHandler.class) && (content = handler.toString()) != null && content.trim().length() > 0) {
            metadata.add(TikaCoreProperties.TIKA_CONTENT, content);
            metadata.add(TikaCoreProperties.TIKA_CONTENT_HANDLER, handler.getClass().getSimpleName());
        }
    }
}

