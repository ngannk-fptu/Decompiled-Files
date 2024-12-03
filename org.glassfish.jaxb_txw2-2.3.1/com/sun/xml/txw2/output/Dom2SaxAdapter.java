/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.txw2.output;

import com.sun.xml.txw2.TxwException;
import java.util.ArrayList;
import java.util.Stack;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;

class Dom2SaxAdapter
implements ContentHandler,
LexicalHandler {
    private final Node _node;
    private final Stack _nodeStk = new Stack();
    private boolean inCDATA;
    private final Document _document;
    private ArrayList unprocessedNamespaces = new ArrayList();

    public final Element getCurrentElement() {
        return (Element)this._nodeStk.peek();
    }

    public Dom2SaxAdapter(Node node) {
        this._node = node;
        this._nodeStk.push(this._node);
        this._document = node instanceof Document ? (Document)node : node.getOwnerDocument();
    }

    public Dom2SaxAdapter() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setValidating(false);
        this._document = factory.newDocumentBuilder().newDocument();
        this._node = this._document;
        this._nodeStk.push(this._document);
    }

    public Node getDOM() {
        return this._node;
    }

    @Override
    public void startDocument() {
    }

    @Override
    public void endDocument() {
    }

    @Override
    public void startElement(String namespace, String localName, String qName, Attributes attrs) {
        Element element = this._document.createElementNS(namespace, qName);
        if (element == null) {
            throw new TxwException("Your DOM provider doesn't support the createElementNS method properly");
        }
        for (int i = 0; i < this.unprocessedNamespaces.size(); i += 2) {
            String prefix = (String)this.unprocessedNamespaces.get(i + 0);
            String uri = (String)this.unprocessedNamespaces.get(i + 1);
            String qname = "".equals(prefix) || prefix == null ? "xmlns" : "xmlns:" + prefix;
            if (element.hasAttributeNS("http://www.w3.org/2000/xmlns/", qname)) {
                element.removeAttributeNS("http://www.w3.org/2000/xmlns/", qname);
            }
            element.setAttributeNS("http://www.w3.org/2000/xmlns/", qname, uri);
        }
        this.unprocessedNamespaces.clear();
        int length = attrs.getLength();
        for (int i = 0; i < length; ++i) {
            String namespaceuri = attrs.getURI(i);
            String value = attrs.getValue(i);
            String qname = attrs.getQName(i);
            element.setAttributeNS(namespaceuri, qname, value);
        }
        this.getParent().appendChild(element);
        this._nodeStk.push(element);
    }

    private final Node getParent() {
        return (Node)this._nodeStk.peek();
    }

    @Override
    public void endElement(String namespace, String localName, String qName) {
        this._nodeStk.pop();
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        Text text = this.inCDATA ? this._document.createCDATASection(new String(ch, start, length)) : this._document.createTextNode(new String(ch, start, length));
        this.getParent().appendChild(text);
    }

    @Override
    public void comment(char[] ch, int start, int length) throws SAXException {
        this.getParent().appendChild(this._document.createComment(new String(ch, start, length)));
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) {
    }

    @Override
    public void processingInstruction(String target, String data) throws SAXException {
        ProcessingInstruction node = this._document.createProcessingInstruction(target, data);
        this.getParent().appendChild(node);
    }

    @Override
    public void setDocumentLocator(Locator locator) {
    }

    @Override
    public void skippedEntity(String name) {
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) {
        this.unprocessedNamespaces.add(prefix);
        this.unprocessedNamespaces.add(uri);
    }

    @Override
    public void endPrefixMapping(String prefix) {
    }

    @Override
    public void startDTD(String name, String publicId, String systemId) throws SAXException {
    }

    @Override
    public void endDTD() throws SAXException {
    }

    @Override
    public void startEntity(String name) throws SAXException {
    }

    @Override
    public void endEntity(String name) throws SAXException {
    }

    @Override
    public void startCDATA() throws SAXException {
        this.inCDATA = true;
    }

    @Override
    public void endCDATA() throws SAXException {
        this.inCDATA = false;
    }
}

