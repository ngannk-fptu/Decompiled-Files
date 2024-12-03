/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.trax;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Stack;
import org.apache.xalan.xsltc.dom.SAXImpl;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.AttributesImpl;

public class DOM2SAX
implements XMLReader,
Locator {
    private static final String EMPTYSTRING = "";
    private static final String XMLNS_PREFIX = "xmlns";
    private Node _dom = null;
    private ContentHandler _sax = null;
    private LexicalHandler _lex = null;
    private SAXImpl _saxImpl = null;
    private Hashtable _nsPrefixes = new Hashtable();

    public DOM2SAX(Node root) {
        this._dom = root;
    }

    @Override
    public ContentHandler getContentHandler() {
        return this._sax;
    }

    @Override
    public void setContentHandler(ContentHandler handler) throws NullPointerException {
        this._sax = handler;
        if (handler instanceof LexicalHandler) {
            this._lex = (LexicalHandler)((Object)handler);
        }
        if (handler instanceof SAXImpl) {
            this._saxImpl = (SAXImpl)handler;
        }
    }

    private boolean startPrefixMapping(String prefix, String uri) throws SAXException {
        boolean pushed = true;
        Stack<String> uriStack = (Stack<String>)this._nsPrefixes.get(prefix);
        if (uriStack != null) {
            if (uriStack.isEmpty()) {
                this._sax.startPrefixMapping(prefix, uri);
                uriStack.push(uri);
            } else {
                String lastUri = (String)uriStack.peek();
                if (!lastUri.equals(uri)) {
                    this._sax.startPrefixMapping(prefix, uri);
                    uriStack.push(uri);
                } else {
                    pushed = false;
                }
            }
        } else {
            this._sax.startPrefixMapping(prefix, uri);
            uriStack = new Stack<String>();
            this._nsPrefixes.put(prefix, uriStack);
            uriStack.push(uri);
        }
        return pushed;
    }

    private void endPrefixMapping(String prefix) throws SAXException {
        Stack uriStack = (Stack)this._nsPrefixes.get(prefix);
        if (uriStack != null) {
            this._sax.endPrefixMapping(prefix);
            uriStack.pop();
        }
    }

    private static String getLocalName(Node node) {
        String localName = node.getLocalName();
        if (localName == null) {
            String qname = node.getNodeName();
            int col = qname.lastIndexOf(58);
            return col > 0 ? qname.substring(col + 1) : qname;
        }
        return localName;
    }

    @Override
    public void parse(InputSource unused) throws IOException, SAXException {
        this.parse(this._dom);
    }

    public void parse() throws IOException, SAXException {
        if (this._dom != null) {
            boolean isIncomplete;
            boolean bl = isIncomplete = this._dom.getNodeType() != 9;
            if (isIncomplete) {
                this._sax.startDocument();
                this.parse(this._dom);
                this._sax.endDocument();
            } else {
                this.parse(this._dom);
            }
        }
    }

    private void parse(Node node) throws IOException, SAXException {
        Object first = null;
        if (node == null) {
            return;
        }
        switch (node.getNodeType()) {
            case 2: 
            case 5: 
            case 6: 
            case 10: 
            case 11: 
            case 12: {
                break;
            }
            case 4: {
                String cdata = node.getNodeValue();
                if (this._lex != null) {
                    this._lex.startCDATA();
                    this._sax.characters(cdata.toCharArray(), 0, cdata.length());
                    this._lex.endCDATA();
                    break;
                }
                this._sax.characters(cdata.toCharArray(), 0, cdata.length());
                break;
            }
            case 8: {
                if (this._lex == null) break;
                String value = node.getNodeValue();
                this._lex.comment(value.toCharArray(), 0, value.length());
                break;
            }
            case 9: {
                this._sax.setDocumentLocator(this);
                this._sax.startDocument();
                for (Node next = node.getFirstChild(); next != null; next = next.getNextSibling()) {
                    this.parse(next);
                }
                this._sax.endDocument();
                break;
            }
            case 1: {
                String prefix;
                String uriAttr;
                String qnameAttr;
                Node attr;
                int i;
                ArrayList<String> pushedPrefixes = new ArrayList<String>();
                AttributesImpl attrs = new AttributesImpl();
                NamedNodeMap map = node.getAttributes();
                int length = map.getLength();
                for (i = 0; i < length; ++i) {
                    attr = map.item(i);
                    qnameAttr = attr.getNodeName();
                    if (!qnameAttr.startsWith(XMLNS_PREFIX)) continue;
                    uriAttr = attr.getNodeValue();
                    int colon = qnameAttr.lastIndexOf(58);
                    String string = prefix = colon > 0 ? qnameAttr.substring(colon + 1) : EMPTYSTRING;
                    if (!this.startPrefixMapping(prefix, uriAttr)) continue;
                    pushedPrefixes.add(prefix);
                }
                for (i = 0; i < length; ++i) {
                    attr = map.item(i);
                    qnameAttr = attr.getNodeName();
                    if (qnameAttr.startsWith(XMLNS_PREFIX)) continue;
                    uriAttr = attr.getNamespaceURI();
                    String localNameAttr = DOM2SAX.getLocalName(attr);
                    if (uriAttr != null) {
                        int colon = qnameAttr.lastIndexOf(58);
                        String string = prefix = colon > 0 ? qnameAttr.substring(0, colon) : EMPTYSTRING;
                        if (this.startPrefixMapping(prefix, uriAttr)) {
                            pushedPrefixes.add(prefix);
                        }
                    }
                    attrs.addAttribute(attr.getNamespaceURI(), DOM2SAX.getLocalName(attr), qnameAttr, "CDATA", attr.getNodeValue());
                }
                String qname = node.getNodeName();
                String uri = node.getNamespaceURI();
                String localName = DOM2SAX.getLocalName(node);
                if (uri != null) {
                    int colon = qname.lastIndexOf(58);
                    String string = prefix = colon > 0 ? qname.substring(0, colon) : EMPTYSTRING;
                    if (this.startPrefixMapping(prefix, uri)) {
                        pushedPrefixes.add(prefix);
                    }
                }
                if (this._saxImpl != null) {
                    this._saxImpl.startElement(uri, localName, qname, attrs, node);
                } else {
                    this._sax.startElement(uri, localName, qname, attrs);
                }
                for (Node next = node.getFirstChild(); next != null; next = next.getNextSibling()) {
                    this.parse(next);
                }
                this._sax.endElement(uri, localName, qname);
                int nPushedPrefixes = pushedPrefixes.size();
                for (int i2 = 0; i2 < nPushedPrefixes; ++i2) {
                    this.endPrefixMapping((String)pushedPrefixes.get(i2));
                }
                break;
            }
            case 7: {
                this._sax.processingInstruction(node.getNodeName(), node.getNodeValue());
                break;
            }
            case 3: {
                String data = node.getNodeValue();
                this._sax.characters(data.toCharArray(), 0, data.length());
            }
        }
    }

    @Override
    public DTDHandler getDTDHandler() {
        return null;
    }

    @Override
    public ErrorHandler getErrorHandler() {
        return null;
    }

    @Override
    public boolean getFeature(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        return false;
    }

    @Override
    public void setFeature(String name, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
    }

    @Override
    public void parse(String sysId) throws IOException, SAXException {
        throw new IOException("This method is not yet implemented.");
    }

    @Override
    public void setDTDHandler(DTDHandler handler) throws NullPointerException {
    }

    @Override
    public void setEntityResolver(EntityResolver resolver) throws NullPointerException {
    }

    @Override
    public EntityResolver getEntityResolver() {
        return null;
    }

    @Override
    public void setErrorHandler(ErrorHandler handler) throws NullPointerException {
    }

    @Override
    public void setProperty(String name, Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
    }

    @Override
    public Object getProperty(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        return null;
    }

    @Override
    public int getColumnNumber() {
        return 0;
    }

    @Override
    public int getLineNumber() {
        return 0;
    }

    @Override
    public String getPublicId() {
        return null;
    }

    @Override
    public String getSystemId() {
        return null;
    }

    private String getNodeTypeFromCode(short code) {
        String retval = null;
        switch (code) {
            case 2: {
                retval = "ATTRIBUTE_NODE";
                break;
            }
            case 4: {
                retval = "CDATA_SECTION_NODE";
                break;
            }
            case 8: {
                retval = "COMMENT_NODE";
                break;
            }
            case 11: {
                retval = "DOCUMENT_FRAGMENT_NODE";
                break;
            }
            case 9: {
                retval = "DOCUMENT_NODE";
                break;
            }
            case 10: {
                retval = "DOCUMENT_TYPE_NODE";
                break;
            }
            case 1: {
                retval = "ELEMENT_NODE";
                break;
            }
            case 6: {
                retval = "ENTITY_NODE";
                break;
            }
            case 5: {
                retval = "ENTITY_REFERENCE_NODE";
                break;
            }
            case 12: {
                retval = "NOTATION_NODE";
                break;
            }
            case 7: {
                retval = "PROCESSING_INSTRUCTION_NODE";
                break;
            }
            case 3: {
                retval = "TEXT_NODE";
            }
        }
        return retval;
    }
}

