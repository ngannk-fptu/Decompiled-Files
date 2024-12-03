/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.pdf;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import org.w3c.dom.Document;
import org.xhtmlrenderer.pdf.SAXEventRecorder;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class DocumentSplitter
implements ContentHandler {
    private static final String HEAD_ELEMENT_NAME = "head";
    private List _processingInstructions = new LinkedList();
    private SAXEventRecorder _head = new SAXEventRecorder();
    private boolean _inHead = false;
    private int _depth = 0;
    private boolean _needNewNSScope = false;
    private NamespaceScope _currentNSScope = new NamespaceScope();
    private boolean _needNSScopePop;
    private Locator _locator;
    private TransformerHandler _handler;
    private boolean _inDocument = false;
    private List _documents = new LinkedList();
    private boolean _replayedHead = false;

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (this._inHead) {
            this._head.characters(ch, start, length);
        } else if (this._inDocument) {
            this._handler.characters(ch, start, length);
        }
    }

    @Override
    public void endDocument() throws SAXException {
    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
        if (this._inHead) {
            this._head.endPrefixMapping(prefix);
        } else if (this._inDocument) {
            this._handler.endPrefixMapping(prefix);
        } else {
            this._needNSScopePop = true;
        }
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        if (this._inHead) {
            this._head.ignorableWhitespace(ch, start, length);
        } else if (this._inDocument) {
            this._handler.ignorableWhitespace(ch, start, length);
        }
    }

    @Override
    public void processingInstruction(String target, String data) throws SAXException {
        this._processingInstructions.add(new ProcessingInstruction(target, data));
    }

    @Override
    public void setDocumentLocator(Locator locator) {
        this._locator = locator;
    }

    @Override
    public void skippedEntity(String name) throws SAXException {
        if (this._inHead) {
            this._head.skippedEntity(name);
        } else if (this._inDocument) {
            this._handler.skippedEntity(name);
        }
    }

    @Override
    public void startDocument() throws SAXException {
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        if (this._inHead) {
            this._head.startElement(uri, localName, qName, atts);
        } else if (this._inDocument) {
            if (this._depth == 2 && !this._replayedHead) {
                if (HEAD_ELEMENT_NAME.equalsIgnoreCase(qName)) {
                    this._handler.startElement(uri, localName, qName, atts);
                    this._head.replay(this._handler);
                } else {
                    this._handler.startElement("", HEAD_ELEMENT_NAME, HEAD_ELEMENT_NAME, new AttributesImpl());
                    this._head.replay(this._handler);
                    this._handler.endElement("", HEAD_ELEMENT_NAME, HEAD_ELEMENT_NAME);
                    this._handler.startElement(uri, localName, qName, atts);
                }
                this._replayedHead = true;
            } else {
                this._handler.startElement(uri, localName, qName, atts);
            }
        } else {
            if (this._needNewNSScope) {
                this._needNewNSScope = false;
                this._currentNSScope = new NamespaceScope(this._currentNSScope);
            }
            if (this._depth == 1) {
                if (HEAD_ELEMENT_NAME.equalsIgnoreCase(qName)) {
                    this._inHead = true;
                    this._currentNSScope.replay(this._head, true);
                } else {
                    try {
                        this._inDocument = true;
                        this._replayedHead = false;
                        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                        factory.setNamespaceAware(true);
                        factory.setValidating(false);
                        Document doc = factory.newDocumentBuilder().newDocument();
                        this._documents.add(doc);
                        this._handler = ((SAXTransformerFactory)SAXTransformerFactory.newInstance()).newTransformerHandler();
                        this._handler.setResult(new DOMResult(doc));
                        this._handler.startDocument();
                        this._handler.setDocumentLocator(this._locator);
                        for (ProcessingInstruction pI : this._processingInstructions) {
                            this._handler.processingInstruction(pI.getTarget(), pI.getData());
                        }
                        this._currentNSScope.replay(this._handler, true);
                        this._handler.startElement(uri, localName, qName, atts);
                    }
                    catch (ParserConfigurationException e) {
                        throw new SAXException(e.getMessage(), e);
                    }
                    catch (TransformerConfigurationException e) {
                        throw new SAXException(e.getMessage(), e);
                    }
                }
            }
        }
        ++this._depth;
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        --this._depth;
        if (this._needNSScopePop) {
            this._needNSScopePop = false;
            this._currentNSScope = this._currentNSScope.getParent();
        }
        if (this._inHead) {
            if (this._depth == 1) {
                this._currentNSScope.replay(this._head, false);
                this._inHead = false;
            } else {
                this._head.endElement(uri, localName, qName);
            }
        } else if (this._inDocument) {
            if (this._depth == 1) {
                this._currentNSScope.replay(this._handler, false);
                this._handler.endElement(uri, localName, qName);
                this._handler.endDocument();
                this._inDocument = false;
            } else {
                this._handler.endElement(uri, localName, qName);
            }
        }
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        if (this._inHead) {
            this._head.startPrefixMapping(prefix, uri);
        } else if (this._inDocument) {
            this._handler.startPrefixMapping(prefix, uri);
        } else {
            this._needNewNSScope = true;
            this._currentNSScope.addNamespace(new Namespace(prefix, uri));
        }
    }

    public List getDocuments() {
        return this._documents;
    }

    private static class ProcessingInstruction {
        private String _target;
        private String _data;

        public ProcessingInstruction(String target, String data) {
            this._target = target;
            this._data = data;
        }

        public String getData() {
            return this._data;
        }

        public String getTarget() {
            return this._target;
        }
    }

    private static final class NamespaceScope {
        private NamespaceScope _parent;
        private List _namespaces = new LinkedList();

        public NamespaceScope() {
        }

        public NamespaceScope(NamespaceScope parent) {
            this._parent = parent;
        }

        public void addNamespace(Namespace namespace) {
            this._namespaces.add(namespace);
        }

        public void replay(ContentHandler contentHandler, boolean start) throws SAXException {
            this.replay(contentHandler, new HashSet(), start);
        }

        private void replay(ContentHandler contentHandler, Set seen, boolean start) throws SAXException {
            for (Namespace ns : this._namespaces) {
                if (seen.contains(ns.getPrefix())) continue;
                seen.add(ns.getPrefix());
                if (start) {
                    contentHandler.startPrefixMapping(ns.getPrefix(), ns.getUri());
                    continue;
                }
                contentHandler.endPrefixMapping(ns.getPrefix());
            }
            if (this._parent != null) {
                this._parent.replay(contentHandler, seen, start);
            }
        }

        public NamespaceScope getParent() {
            return this._parent;
        }
    }

    private static final class Namespace {
        private String _prefix;
        private String _uri;

        public Namespace(String prefix, String uri) {
            this._prefix = prefix;
            this._uri = uri;
        }

        public String getPrefix() {
            return this._prefix;
        }

        public String getUri() {
            return this._uri;
        }
    }
}

