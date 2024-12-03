/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.anim.dom.SAXSVGDocumentFactory
 *  org.apache.batik.dom.svg.SVGDocumentFactory
 *  org.apache.batik.dom.util.DocumentDescriptor
 *  org.apache.batik.util.CleanerThread$SoftReferenceCleared
 *  org.w3c.dom.svg.SVGDocument
 */
package org.apache.batik.bridge;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.dom.svg.SVGDocumentFactory;
import org.apache.batik.dom.util.DocumentDescriptor;
import org.apache.batik.util.CleanerThread;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDocument;

public class DocumentLoader {
    protected SVGDocumentFactory documentFactory;
    protected HashMap cacheMap = new HashMap();
    protected UserAgent userAgent;

    protected DocumentLoader() {
    }

    public DocumentLoader(UserAgent userAgent) {
        this.userAgent = userAgent;
        this.documentFactory = new SAXSVGDocumentFactory(userAgent.getXMLParserClassName(), true);
        this.documentFactory.setValidating(userAgent.isXMLParserValidating());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Document checkCache(String uri) {
        DocumentState state;
        int n = uri.lastIndexOf(47);
        if (n == -1) {
            n = 0;
        }
        if ((n = uri.indexOf(35, n)) != -1) {
            uri = uri.substring(0, n);
        }
        HashMap hashMap = this.cacheMap;
        synchronized (hashMap) {
            state = (DocumentState)((Object)this.cacheMap.get(uri));
        }
        if (state != null) {
            return state.getDocument();
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Document loadDocument(String uri) throws IOException {
        Document ret = this.checkCache(uri);
        if (ret != null) {
            return ret;
        }
        SVGDocument document = this.documentFactory.createSVGDocument(uri);
        DocumentDescriptor desc = this.documentFactory.getDocumentDescriptor();
        DocumentState state = new DocumentState(uri, (Document)document, desc);
        HashMap hashMap = this.cacheMap;
        synchronized (hashMap) {
            this.cacheMap.put(uri, state);
        }
        return state.getDocument();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Document loadDocument(String uri, InputStream is) throws IOException {
        Document ret = this.checkCache(uri);
        if (ret != null) {
            return ret;
        }
        SVGDocument document = this.documentFactory.createSVGDocument(uri, is);
        DocumentDescriptor desc = this.documentFactory.getDocumentDescriptor();
        DocumentState state = new DocumentState(uri, (Document)document, desc);
        HashMap hashMap = this.cacheMap;
        synchronized (hashMap) {
            this.cacheMap.put(uri, state);
        }
        return state.getDocument();
    }

    public UserAgent getUserAgent() {
        return this.userAgent;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void dispose() {
        HashMap hashMap = this.cacheMap;
        synchronized (hashMap) {
            this.cacheMap.clear();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int getLineNumber(Element e) {
        DocumentState state;
        String uri = ((SVGDocument)e.getOwnerDocument()).getURL();
        HashMap hashMap = this.cacheMap;
        synchronized (hashMap) {
            state = (DocumentState)((Object)this.cacheMap.get(uri));
        }
        if (state == null) {
            return -1;
        }
        return state.desc.getLocationLine(e);
    }

    private class DocumentState
    extends CleanerThread.SoftReferenceCleared {
        private String uri;
        private DocumentDescriptor desc;

        public DocumentState(String uri, Document document, DocumentDescriptor desc) {
            super((Object)document);
            this.uri = uri;
            this.desc = desc;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void cleared() {
            HashMap hashMap = DocumentLoader.this.cacheMap;
            synchronized (hashMap) {
                DocumentLoader.this.cacheMap.remove(this.uri);
            }
        }

        public DocumentDescriptor getDocumentDescriptor() {
            return this.desc;
        }

        public String getURI() {
            return this.uri;
        }

        public Document getDocument() {
            return (Document)this.get();
        }
    }
}

