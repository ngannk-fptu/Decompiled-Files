/*
 * Decompiled with CFR 0.152.
 */
package org.apache.html.dom;

import java.util.Vector;
import org.apache.html.dom.HTMLDocumentImpl;
import org.apache.xerces.dom.ElementImpl;
import org.apache.xerces.dom.ProcessingInstructionImpl;
import org.w3c.dom.Node;
import org.w3c.dom.html.HTMLDocument;
import org.xml.sax.AttributeList;
import org.xml.sax.DocumentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class HTMLBuilder
implements DocumentHandler {
    protected HTMLDocumentImpl _document;
    protected ElementImpl _current;
    private boolean _ignoreWhitespace = true;
    private boolean _done = true;
    protected Vector _preRootNodes;

    @Override
    public void startDocument() throws SAXException {
        if (!this._done) {
            throw new SAXException("HTM001 State error: startDocument fired twice on one builder.");
        }
        this._document = null;
        this._done = false;
    }

    @Override
    public void endDocument() throws SAXException {
        if (this._document == null) {
            throw new SAXException("HTM002 State error: document never started or missing document element.");
        }
        if (this._current != null) {
            throw new SAXException("HTM003 State error: document ended before end of document element.");
        }
        this._current = null;
        this._done = true;
    }

    @Override
    public synchronized void startElement(String string, AttributeList attributeList) throws SAXException {
        int n;
        ElementImpl elementImpl;
        if (string == null) {
            throw new SAXException("HTM004 Argument 'tagName' is null.");
        }
        if (this._document == null) {
            this._document = new HTMLDocumentImpl();
            this._current = elementImpl = (ElementImpl)this._document.getDocumentElement();
            if (this._current == null) {
                throw new SAXException("HTM005 State error: Document.getDocumentElement returns null.");
            }
            if (this._preRootNodes != null) {
                n = this._preRootNodes.size();
                while (n-- > 0) {
                    this._document.insertBefore((Node)this._preRootNodes.elementAt(n), elementImpl);
                }
                this._preRootNodes = null;
            }
        } else {
            if (this._current == null) {
                throw new SAXException("HTM006 State error: startElement called after end of document element.");
            }
            elementImpl = (ElementImpl)this._document.createElement(string);
            this._current.appendChild(elementImpl);
            this._current = elementImpl;
        }
        if (attributeList != null) {
            for (n = 0; n < attributeList.getLength(); ++n) {
                elementImpl.setAttribute(attributeList.getName(n), attributeList.getValue(n));
            }
        }
    }

    @Override
    public void endElement(String string) throws SAXException {
        if (this._current == null) {
            throw new SAXException("HTM007 State error: endElement called with no current node.");
        }
        if (!this._current.getNodeName().equalsIgnoreCase(string)) {
            throw new SAXException("HTM008 State error: mismatch in closing tag name " + string + "\n" + string);
        }
        this._current = this._current.getParentNode() == this._current.getOwnerDocument() ? null : (ElementImpl)this._current.getParentNode();
    }

    public void characters(String string) throws SAXException {
        if (this._current == null) {
            throw new SAXException("HTM009 State error: character data found outside of root element.");
        }
        this._current.appendChild(this._document.createTextNode(string));
    }

    @Override
    public void characters(char[] cArray, int n, int n2) throws SAXException {
        if (this._current == null) {
            throw new SAXException("HTM010 State error: character data found outside of root element.");
        }
        this._current.appendChild(this._document.createTextNode(new String(cArray, n, n2)));
    }

    @Override
    public void ignorableWhitespace(char[] cArray, int n, int n2) throws SAXException {
        if (!this._ignoreWhitespace) {
            this._current.appendChild(this._document.createTextNode(new String(cArray, n, n2)));
        }
    }

    @Override
    public void processingInstruction(String string, String string2) throws SAXException {
        if (this._current == null && this._document == null) {
            if (this._preRootNodes == null) {
                this._preRootNodes = new Vector();
            }
            this._preRootNodes.addElement(new ProcessingInstructionImpl(null, string, string2));
        } else if (this._current == null && this._document != null) {
            this._document.appendChild(this._document.createProcessingInstruction(string, string2));
        } else {
            this._current.appendChild(this._document.createProcessingInstruction(string, string2));
        }
    }

    public HTMLDocument getHTMLDocument() {
        return this._document;
    }

    @Override
    public void setDocumentLocator(Locator locator) {
    }
}

