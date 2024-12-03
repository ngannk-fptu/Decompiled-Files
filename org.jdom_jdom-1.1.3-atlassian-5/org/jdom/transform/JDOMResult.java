/*
 * Decompiled with CFR 0.152.
 */
package org.jdom.transform;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.transform.sax.SAXResult;
import org.jdom.DefaultJDOMFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMFactory;
import org.jdom.input.SAXHandler;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.XMLFilterImpl;

public class JDOMResult
extends SAXResult {
    private static final String CVS_ID = "@(#) $RCSfile: JDOMResult.java,v $ $Revision: 1.24 $ $Date: 2007/11/10 05:29:02 $ $Name:  $";
    public static final String JDOM_FEATURE = "http://org.jdom.transform.JDOMResult/feature";
    private Object result = null;
    private boolean queried = false;
    private JDOMFactory factory = null;

    public JDOMResult() {
        DocumentBuilder builder = new DocumentBuilder();
        super.setHandler(builder);
        super.setLexicalHandler(builder);
    }

    public void setResult(List result) {
        this.result = result;
        this.queried = false;
    }

    public List getResult() {
        ArrayList nodes = Collections.EMPTY_LIST;
        this.retrieveResult();
        if (this.result instanceof List) {
            nodes = (List)this.result;
        } else if (this.result instanceof Document && !this.queried) {
            List content = ((Document)this.result).getContent();
            nodes = new ArrayList(content.size());
            while (content.size() != 0) {
                Object o = content.remove(0);
                nodes.add(o);
            }
            this.result = nodes;
        }
        this.queried = true;
        return nodes;
    }

    public void setDocument(Document document) {
        this.result = document;
        this.queried = false;
    }

    public Document getDocument() {
        Document doc = null;
        this.retrieveResult();
        if (this.result instanceof Document) {
            doc = (Document)this.result;
        } else if (this.result instanceof List && !this.queried) {
            try {
                JDOMFactory f = this.getFactory();
                if (f == null) {
                    f = new DefaultJDOMFactory();
                }
                doc = f.document(null);
                doc.setContent((List)this.result);
                this.result = doc;
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
        if (this.result == null) {
            this.setResult(((DocumentBuilder)this.getHandler()).getResult());
        }
    }

    @Override
    public void setHandler(ContentHandler handler) {
    }

    @Override
    public void setLexicalHandler(LexicalHandler handler) {
    }

    private class DocumentBuilder
    extends XMLFilterImpl
    implements LexicalHandler {
        private FragmentHandler saxHandler = null;
        private boolean startDocumentReceived = false;

        public List getResult() {
            List result = null;
            if (this.saxHandler != null) {
                result = this.saxHandler.getResult();
                this.saxHandler = null;
                this.startDocumentReceived = false;
            }
            return result;
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

    private static class FragmentHandler
    extends SAXHandler {
        private Element dummyRoot = new Element("root", null, null);

        public FragmentHandler(JDOMFactory factory) {
            super(factory);
            this.pushElement(this.dummyRoot);
        }

        public List getResult() {
            try {
                this.flushCharacters();
            }
            catch (SAXException sAXException) {
                // empty catch block
            }
            return this.getDetachedContent(this.dummyRoot);
        }

        private List getDetachedContent(Element elt) {
            List content = elt.getContent();
            ArrayList nodes = new ArrayList(content.size());
            while (content.size() != 0) {
                Object o = content.remove(0);
                nodes.add(o);
            }
            return nodes;
        }
    }
}

