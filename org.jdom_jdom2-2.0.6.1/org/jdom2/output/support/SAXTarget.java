/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.output.support;

import org.jdom2.output.JDOMLocator;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;

public final class SAXTarget {
    private final ContentHandler contentHandler;
    private final ErrorHandler errorHandler;
    private final DTDHandler dtdHandler;
    private final EntityResolver entityResolver;
    private final LexicalHandler lexicalHandler;
    private final DeclHandler declHandler;
    private final SAXLocator locator;
    private final boolean declareNamespaces;
    private final boolean reportDtdEvents;

    public SAXTarget(ContentHandler contentHandler, ErrorHandler errorHandler, DTDHandler dtdHandler, EntityResolver entityResolver, LexicalHandler lexicalHandler, DeclHandler declHandler, boolean declareNamespaces, boolean reportDtdEvents, String publicID, String systemID) {
        this.contentHandler = contentHandler;
        this.errorHandler = errorHandler;
        this.dtdHandler = dtdHandler;
        this.entityResolver = entityResolver;
        this.lexicalHandler = lexicalHandler;
        this.declHandler = declHandler;
        this.declareNamespaces = declareNamespaces;
        this.reportDtdEvents = reportDtdEvents;
        this.locator = new SAXLocator(publicID, systemID);
    }

    public ContentHandler getContentHandler() {
        return this.contentHandler;
    }

    public ErrorHandler getErrorHandler() {
        return this.errorHandler;
    }

    public DTDHandler getDTDHandler() {
        return this.dtdHandler;
    }

    public EntityResolver getEntityResolver() {
        return this.entityResolver;
    }

    public LexicalHandler getLexicalHandler() {
        return this.lexicalHandler;
    }

    public DeclHandler getDeclHandler() {
        return this.declHandler;
    }

    public boolean isDeclareNamespaces() {
        return this.declareNamespaces;
    }

    public boolean isReportDTDEvents() {
        return this.reportDtdEvents;
    }

    public SAXLocator getLocator() {
        return this.locator;
    }

    public static final class SAXLocator
    implements JDOMLocator {
        private final String publicid;
        private final String systemid;
        private Object node = null;

        public SAXLocator(String publicid, String systemid) {
            this.publicid = publicid;
            this.systemid = systemid;
        }

        public int getColumnNumber() {
            return -1;
        }

        public int getLineNumber() {
            return -1;
        }

        public String getPublicId() {
            return this.publicid;
        }

        public String getSystemId() {
            return this.systemid;
        }

        public Object getNode() {
            return this.node;
        }

        public void setNode(Object node) {
            this.node = node;
        }
    }
}

