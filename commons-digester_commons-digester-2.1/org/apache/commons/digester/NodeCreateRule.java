/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.digester;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.digester.Rule;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class NodeCreateRule
extends Rule {
    private DocumentBuilder documentBuilder = null;
    private int nodeType = 1;

    public NodeCreateRule() throws ParserConfigurationException {
        this(1);
    }

    public NodeCreateRule(DocumentBuilder documentBuilder) {
        this(1, documentBuilder);
    }

    public NodeCreateRule(int nodeType) throws ParserConfigurationException {
        this(nodeType, DocumentBuilderFactory.newInstance().newDocumentBuilder());
    }

    public NodeCreateRule(int nodeType, DocumentBuilder documentBuilder) {
        if (nodeType != 11 && nodeType != 1) {
            throw new IllegalArgumentException("Can only create nodes of type DocumentFragment and Element");
        }
        this.nodeType = nodeType;
        this.documentBuilder = documentBuilder;
    }

    public void begin(String namespaceURI, String name, Attributes attributes) throws Exception {
        Document doc = this.documentBuilder.newDocument();
        NodeBuilder builder = null;
        if (this.nodeType == 1) {
            Element element = null;
            if (this.getDigester().getNamespaceAware()) {
                element = doc.createElementNS(namespaceURI, name);
                for (int i = 0; i < attributes.getLength(); ++i) {
                    element.setAttributeNS(attributes.getURI(i), attributes.getQName(i), attributes.getValue(i));
                }
            } else {
                element = doc.createElement(name);
                for (int i = 0; i < attributes.getLength(); ++i) {
                    element.setAttribute(attributes.getQName(i), attributes.getValue(i));
                }
            }
            builder = new NodeBuilder(doc, element);
        } else {
            builder = new NodeBuilder(doc, doc.createDocumentFragment());
        }
        this.getDigester().setCustomContentHandler(builder);
    }

    public void end() throws Exception {
        this.digester.pop();
    }

    private class NodeBuilder
    extends DefaultHandler {
        protected ContentHandler oldContentHandler = null;
        protected int depth = 0;
        protected Document doc = null;
        protected Node root = null;
        protected Node top = null;
        protected StringBuffer topText = new StringBuffer();

        public NodeBuilder(Document doc, Node root) throws ParserConfigurationException, SAXException {
            this.doc = doc;
            this.root = root;
            this.top = root;
            this.oldContentHandler = NodeCreateRule.this.digester.getCustomContentHandler();
        }

        private void addTextIfPresent() throws SAXException {
            if (this.topText.length() > 0) {
                String str = this.topText.toString();
                this.topText.setLength(0);
                if (str.trim().length() > 0) {
                    try {
                        this.top.appendChild(this.doc.createTextNode(str));
                    }
                    catch (DOMException e) {
                        throw new SAXException(e.getMessage());
                    }
                }
            }
        }

        public void characters(char[] ch, int start, int length) throws SAXException {
            this.topText.append(ch, start, length);
        }

        public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
            this.addTextIfPresent();
            try {
                if (this.depth == 0) {
                    NodeCreateRule.this.getDigester().setCustomContentHandler(this.oldContentHandler);
                    NodeCreateRule.this.getDigester().push(this.root);
                    NodeCreateRule.this.getDigester().endElement(namespaceURI, localName, qName);
                }
                this.top = this.top.getParentNode();
                --this.depth;
            }
            catch (DOMException e) {
                throw new SAXException(e.getMessage());
            }
        }

        public void processingInstruction(String target, String data) throws SAXException {
            try {
                this.top.appendChild(this.doc.createProcessingInstruction(target, data));
            }
            catch (DOMException e) {
                throw new SAXException(e.getMessage());
            }
        }

        public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
            this.addTextIfPresent();
            try {
                Node previousTop = this.top;
                this.top = localName == null || localName.length() == 0 ? this.doc.createElement(qName) : this.doc.createElementNS(namespaceURI, localName);
                for (int i = 0; i < atts.getLength(); ++i) {
                    Attr attr = null;
                    if (atts.getLocalName(i) == null || atts.getLocalName(i).length() == 0) {
                        attr = this.doc.createAttribute(atts.getQName(i));
                        attr.setNodeValue(atts.getValue(i));
                        ((Element)this.top).setAttributeNode(attr);
                        continue;
                    }
                    attr = this.doc.createAttributeNS(atts.getURI(i), atts.getLocalName(i));
                    attr.setNodeValue(atts.getValue(i));
                    ((Element)this.top).setAttributeNodeNS(attr);
                }
                previousTop.appendChild(this.top);
                ++this.depth;
            }
            catch (DOMException e) {
                throw new SAXException(e.getMessage());
            }
        }
    }
}

