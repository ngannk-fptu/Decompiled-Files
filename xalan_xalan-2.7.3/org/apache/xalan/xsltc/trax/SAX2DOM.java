/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.trax;

import java.util.Stack;
import java.util.Vector;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.xalan.xsltc.runtime.Constants;
import org.w3c.dom.Comment;
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

public class SAX2DOM
implements ContentHandler,
LexicalHandler,
Constants {
    private Node _root = null;
    private Document _document = null;
    private Node _nextSibling = null;
    private Stack _nodeStk = new Stack();
    private Vector _namespaceDecls = null;
    private Node _lastSibling = null;

    public SAX2DOM() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        this._document = factory.newDocumentBuilder().newDocument();
        this._root = this._document;
    }

    public SAX2DOM(Node root, Node nextSibling) throws ParserConfigurationException {
        this._root = root;
        if (root instanceof Document) {
            this._document = (Document)root;
        } else if (root != null) {
            this._document = root.getOwnerDocument();
        } else {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            this._document = factory.newDocumentBuilder().newDocument();
            this._root = this._document;
        }
        this._nextSibling = nextSibling;
    }

    public SAX2DOM(Node root) throws ParserConfigurationException {
        this(root, null);
    }

    public Node getDOM() {
        return this._root;
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        Node last = (Node)this._nodeStk.peek();
        if (last != this._document) {
            String text = new String(ch, start, length);
            if (this._lastSibling != null && this._lastSibling.getNodeType() == 3) {
                ((Text)this._lastSibling).appendData(text);
            } else {
                this._lastSibling = last == this._root && this._nextSibling != null ? last.insertBefore(this._document.createTextNode(text), this._nextSibling) : last.appendChild(this._document.createTextNode(text));
            }
        }
    }

    @Override
    public void startDocument() {
        this._nodeStk.push(this._root);
    }

    @Override
    public void endDocument() {
        this._nodeStk.pop();
    }

    @Override
    public void startElement(String namespace, String localName, String qName, Attributes attrs) {
        int i;
        Element tmp = this._document.createElementNS(namespace, qName);
        if (this._namespaceDecls != null) {
            int nDecls = this._namespaceDecls.size();
            for (i = 0; i < nDecls; ++i) {
                String prefix;
                if ((prefix = (String)this._namespaceDecls.elementAt(i++)) == null || prefix.equals("")) {
                    tmp.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", (String)this._namespaceDecls.elementAt(i));
                    continue;
                }
                tmp.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + prefix, (String)this._namespaceDecls.elementAt(i));
            }
            this._namespaceDecls.clear();
        }
        int nattrs = attrs.getLength();
        for (i = 0; i < nattrs; ++i) {
            if (attrs.getLocalName(i) == null) {
                tmp.setAttribute(attrs.getQName(i), attrs.getValue(i));
                continue;
            }
            tmp.setAttributeNS(attrs.getURI(i), attrs.getQName(i), attrs.getValue(i));
        }
        Node last = (Node)this._nodeStk.peek();
        if (last == this._root && this._nextSibling != null) {
            last.insertBefore(tmp, this._nextSibling);
        } else {
            last.appendChild(tmp);
        }
        this._nodeStk.push(tmp);
        this._lastSibling = null;
    }

    @Override
    public void endElement(String namespace, String localName, String qName) {
        this._nodeStk.pop();
        this._lastSibling = null;
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) {
        if (this._namespaceDecls == null) {
            this._namespaceDecls = new Vector(2);
        }
        this._namespaceDecls.addElement(prefix);
        this._namespaceDecls.addElement(uri);
    }

    @Override
    public void endPrefixMapping(String prefix) {
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) {
    }

    @Override
    public void processingInstruction(String target, String data) {
        Node last = (Node)this._nodeStk.peek();
        ProcessingInstruction pi = this._document.createProcessingInstruction(target, data);
        if (pi != null) {
            if (last == this._root && this._nextSibling != null) {
                last.insertBefore(pi, this._nextSibling);
            } else {
                last.appendChild(pi);
            }
            this._lastSibling = pi;
        }
    }

    @Override
    public void setDocumentLocator(Locator locator) {
    }

    @Override
    public void skippedEntity(String name) {
    }

    @Override
    public void comment(char[] ch, int start, int length) {
        Node last = (Node)this._nodeStk.peek();
        Comment comment = this._document.createComment(new String(ch, start, length));
        if (comment != null) {
            if (last == this._root && this._nextSibling != null) {
                last.insertBefore(comment, this._nextSibling);
            } else {
                last.appendChild(comment);
            }
            this._lastSibling = comment;
        }
    }

    @Override
    public void startCDATA() {
    }

    @Override
    public void endCDATA() {
    }

    @Override
    public void startEntity(String name) {
    }

    @Override
    public void endDTD() {
    }

    @Override
    public void endEntity(String name) {
    }

    @Override
    public void startDTD(String name, String publicId, String systemId) throws SAXException {
    }
}

