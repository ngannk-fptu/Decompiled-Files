/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.transform;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.transform.sax.SAXResult;
import org.jdom2.Content;
import org.jdom2.DefaultJDOMFactory;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMFactory;
import org.jdom2.input.sax.SAXHandler;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.XMLFilterImpl;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class JDOMResult
extends SAXResult {
    public static final String JDOM_FEATURE = "http://jdom.org/jdom2/transform/JDOMResult/feature";
    private List<Content> resultlist = null;
    private Document resultdoc = null;
    private boolean queried = false;
    private JDOMFactory factory = null;

    public JDOMResult() {
        DocumentBuilder builder = new DocumentBuilder();
        super.setHandler(builder);
        super.setLexicalHandler(builder);
    }

    public void setResult(List<Content> result) {
        this.resultlist = result;
        this.queried = false;
    }

    public List<Content> getResult() {
        List<Content> nodes = Collections.emptyList();
        this.retrieveResult();
        if (this.resultlist != null) {
            nodes = this.resultlist;
        } else if (this.resultdoc != null && !this.queried) {
            List<Content> content = this.resultdoc.getContent();
            nodes = new ArrayList(content.size());
            while (content.size() != 0) {
                Content o = content.remove(0);
                nodes.add(o);
            }
            this.resultlist = nodes;
            this.resultdoc = null;
        }
        this.queried = true;
        return nodes;
    }

    public void setDocument(Document document) {
        this.resultdoc = document;
        this.resultlist = null;
        this.queried = false;
    }

    public Document getDocument() {
        Document doc = null;
        this.retrieveResult();
        if (this.resultdoc != null) {
            doc = this.resultdoc;
        } else if (this.resultlist != null && !this.queried) {
            try {
                JDOMFactory f = this.getFactory();
                if (f == null) {
                    f = new DefaultJDOMFactory();
                }
                doc = f.document(null);
                doc.setContent(this.resultlist);
                this.resultdoc = doc;
                this.resultlist = null;
            }
            catch (RuntimeException ex1) {
                return null;
            }
        }
        this.queried = true;
        return doc;
    }

    public void setFactory(JDOMFactory factory) {
        this.factory = factory;
    }

    public JDOMFactory getFactory() {
        return this.factory;
    }

    private void retrieveResult() {
        if (this.resultlist == null && this.resultdoc == null) {
            this.setResult(((DocumentBuilder)this.getHandler()).getResult());
        }
    }

    @Override
    public void setHandler(ContentHandler handler) {
    }

    @Override
    public void setLexicalHandler(LexicalHandler handler) {
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private class DocumentBuilder
    extends XMLFilterImpl
    implements LexicalHandler {
        private FragmentHandler saxHandler = null;
        private boolean startDocumentReceived = false;

        public List<Content> getResult() {
            List<Content> mresult = null;
            if (this.saxHandler != null) {
                mresult = this.saxHandler.getResult();
                this.saxHandler = null;
                this.startDocumentReceived = false;
            }
            return mresult;
        }

        private void ensureInitialization() throws SAXException {
            if (!this.startDocumentReceived) {
                this.startDocument();
            }
        }

        @Override
        public void startDocument() throws SAXException {
            this.startDocumentReceived = true;
            JDOMResult.this.setResult(null);
            this.saxHandler = new FragmentHandler(JDOMResult.this.getFactory());
            super.setContentHandler(this.saxHandler);
            super.startDocument();
        }

        @Override
        public void startElement(String nsURI, String localName, String qName, Attributes atts) throws SAXException {
            this.ensureInitialization();
            super.startElement(nsURI, localName, qName, atts);
        }

        @Override
        public void startPrefixMapping(String prefix, String uri) throws SAXException {
            this.ensureInitialization();
            super.startPrefixMapping(prefix, uri);
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            this.ensureInitialization();
            super.characters(ch, start, length);
        }

        @Override
        public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
            this.ensureInitialization();
            super.ignorableWhitespace(ch, start, length);
        }

        @Override
        public void processingInstruction(String target, String data) throws SAXException {
            this.ensureInitialization();
            super.processingInstruction(target, data);
        }

        @Override
        public void skippedEntity(String name) throws SAXException {
            this.ensureInitialization();
            super.skippedEntity(name);
        }

        @Override
        public void startDTD(String name, String publicId, String systemId) throws SAXException {
            this.ensureInitialization();
            this.saxHandler.startDTD(name, publicId, systemId);
        }

        @Override
        public void endDTD() throws SAXException {
            this.saxHandler.endDTD();
        }

        @Override
        public void startEntity(String name) throws SAXException {
            this.ensureInitialization();
            this.saxHandler.startEntity(name);
        }

        @Override
        public void endEntity(String name) throws SAXException {
            this.saxHandler.endEntity(name);
        }

        @Override
        public void startCDATA() throws SAXException {
            this.ensureInitialization();
            this.saxHandler.startCDATA();
        }

        @Override
        public void endCDATA() throws SAXException {
            this.saxHandler.endCDATA();
        }

        @Override
        public void comment(char[] ch, int start, int length) throws SAXException {
            this.ensureInitialization();
            this.saxHandler.comment(ch, start, length);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class FragmentHandler
    extends SAXHandler {
        private Element dummyRoot = new Element("root", null, null);

        public FragmentHandler(JDOMFactory factory) {
            super(factory);
            this.pushElement(this.dummyRoot);
        }

        public List<Content> getResult() {
            try {
                this.flushCharacters();
            }
            catch (SAXException sAXException) {
                // empty catch block
            }
            return this.getDetachedContent(this.dummyRoot);
        }

        private List<Content> getDetachedContent(Element elt) {
            List<Content> content = elt.getContent();
            ArrayList<Content> nodes = new ArrayList<Content>(content.size());
            while (content.size() != 0) {
                Content o = content.remove(0);
                nodes.add(o);
            }
            return nodes;
        }
    }
}

