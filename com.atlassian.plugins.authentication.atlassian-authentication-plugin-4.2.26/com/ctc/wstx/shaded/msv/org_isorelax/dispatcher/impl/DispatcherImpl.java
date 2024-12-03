/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.impl;

import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.Dispatcher;
import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.ElementDecl;
import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.IslandVerifier;
import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.SchemaProvider;
import java.util.Enumeration;
import java.util.Vector;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.NamespaceSupport;

public class DispatcherImpl
implements Dispatcher {
    private int depth = 1;
    protected Locator documentLocator = null;
    protected final NamespaceSupport nsMap = new NamespaceSupport();
    protected ErrorHandler errorHandler;
    private IslandVerifier currentHandler = null;
    protected final SchemaProvider schema;
    protected Transponder transponder;
    protected Context contextStack = null;
    protected final Vector unparsedEntityDecls = new Vector();
    protected final Vector notationDecls = new Vector();

    public SchemaProvider getSchemaProvider() {
        return this.schema;
    }

    public DispatcherImpl(SchemaProvider schema) {
        this.schema = schema;
        this.transponder = new Transponder();
        this.currentHandler = schema.createTopLevelVerifier();
        this.currentHandler.setDispatcher(this);
    }

    public void attachXMLReader(XMLReader reader) {
        reader.setContentHandler(this.transponder);
    }

    public void switchVerifier(IslandVerifier newVerifier) throws SAXException {
        this.contextStack = new Context(this.currentHandler, this.depth, this.contextStack);
        this.currentHandler = newVerifier;
        this.currentHandler.setDispatcher(this);
        this.currentHandler.setDocumentLocator(this.documentLocator);
        this.depth = 0;
        Enumeration<String> e = this.nsMap.getDeclaredPrefixes();
        while (e.hasMoreElements()) {
            String prefix = e.nextElement();
            this.currentHandler.startPrefixMapping(prefix, this.nsMap.getURI(prefix));
        }
    }

    public void setErrorHandler(ErrorHandler handler) {
        this.errorHandler = handler;
    }

    public ErrorHandler getErrorHandler() {
        return this.errorHandler;
    }

    public int countUnparsedEntityDecls() {
        return this.unparsedEntityDecls.size();
    }

    public Dispatcher.UnparsedEntityDecl getUnparsedEntityDecl(int index) {
        return (Dispatcher.UnparsedEntityDecl)this.unparsedEntityDecls.get(index);
    }

    public int countNotationDecls() {
        return this.notationDecls.size();
    }

    public Dispatcher.NotationDecl getNotationDecl(int index) {
        return (Dispatcher.NotationDecl)this.notationDecls.get(index);
    }

    private class Transponder
    implements ContentHandler,
    DTDHandler {
        private Transponder() {
        }

        public void unparsedEntityDecl(String name, String systemId, String publicId, String notation) {
            DispatcherImpl.this.unparsedEntityDecls.add(new Dispatcher.UnparsedEntityDecl(name, systemId, publicId, notation));
        }

        public void notationDecl(String name, String systemId, String publicId) {
            DispatcherImpl.this.notationDecls.add(new Dispatcher.NotationDecl(name, systemId, publicId));
        }

        public void setDocumentLocator(Locator locator) {
            DispatcherImpl.this.documentLocator = locator;
            DispatcherImpl.this.currentHandler.setDocumentLocator(locator);
        }

        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            DispatcherImpl.this.currentHandler.startElement(uri, localName, qName, attributes);
            DispatcherImpl.this.depth++;
            DispatcherImpl.this.nsMap.pushContext();
        }

        public void endElement(String uri, String localName, String qName) throws SAXException {
            DispatcherImpl.this.nsMap.popContext();
            DispatcherImpl.this.currentHandler.endElement(uri, localName, qName);
            if (--DispatcherImpl.this.depth == 0) {
                Enumeration<String> e = DispatcherImpl.this.nsMap.getDeclaredPrefixes();
                while (e.hasMoreElements()) {
                    DispatcherImpl.this.currentHandler.endPrefixMapping(e.nextElement());
                }
                ElementDecl[] results = DispatcherImpl.this.currentHandler.endIsland();
                DispatcherImpl.this.depth = DispatcherImpl.this.contextStack.depth;
                DispatcherImpl.this.currentHandler = DispatcherImpl.this.contextStack.handler;
                DispatcherImpl.this.contextStack = DispatcherImpl.this.contextStack.previous;
                DispatcherImpl.this.currentHandler.endChildIsland(uri, results);
            }
        }

        public void characters(char[] ch, int start, int length) throws SAXException {
            DispatcherImpl.this.currentHandler.characters(ch, start, length);
        }

        public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
            DispatcherImpl.this.currentHandler.ignorableWhitespace(ch, start, length);
        }

        public void processingInstruction(String target, String data) throws SAXException {
            DispatcherImpl.this.currentHandler.processingInstruction(target, data);
        }

        public void skippedEntity(String name) throws SAXException {
            DispatcherImpl.this.currentHandler.skippedEntity(name);
        }

        public void startDocument() {
        }

        public void endDocument() {
        }

        public void startPrefixMapping(String prefix, String uri) throws SAXException {
            DispatcherImpl.this.nsMap.declarePrefix(prefix, uri);
            DispatcherImpl.this.currentHandler.startPrefixMapping(prefix, uri);
        }

        public void endPrefixMapping(String prefix) throws SAXException {
            DispatcherImpl.this.currentHandler.endPrefixMapping(prefix);
        }
    }

    protected static final class Context {
        public final IslandVerifier handler;
        public final int depth;
        public final Context previous;

        public Context(IslandVerifier handler, int depth, Context previous) {
            this.handler = handler;
            this.depth = depth;
            this.previous = previous;
        }
    }
}

