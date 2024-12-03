/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.xml.serializer.NamespaceMappings
 *  org.apache.xml.serializer.SerializationHandler
 */
package org.apache.xalan.xsltc.trax;

import java.io.IOException;
import org.apache.xml.serializer.NamespaceMappings;
import org.apache.xml.serializer.SerializationHandler;
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

public class DOM2TO
implements XMLReader,
Locator {
    private static final String EMPTYSTRING = "";
    private static final String XMLNS_PREFIX = "xmlns";
    private Node _dom;
    private SerializationHandler _handler;

    public DOM2TO(Node root, SerializationHandler handler) {
        this._dom = root;
        this._handler = handler;
    }

    @Override
    public ContentHandler getContentHandler() {
        return null;
    }

    @Override
    public void setContentHandler(ContentHandler handler) {
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
                this._handler.startDocument();
                this.parse(this._dom);
                this._handler.endDocument();
            } else {
                this.parse(this._dom);
            }
        }
    }

    private void parse(Node node) throws IOException, SAXException {
        if (node == null) {
            return;
        }
        switch (node.getNodeType()) {
            case 2: 
            case 5: 
            case 6: 
            case 10: 
            case 12: {
                break;
            }
            case 4: {
                this._handler.startCDATA();
                this._handler.characters(node.getNodeValue());
                this._handler.endCDATA();
                break;
            }
            case 8: {
                this._handler.comment(node.getNodeValue());
                break;
            }
            case 9: {
                this._handler.startDocument();
                for (Node next = node.getFirstChild(); next != null; next = next.getNextSibling()) {
                    this.parse(next);
                }
                this._handler.endDocument();
                break;
            }
            case 11: {
                for (Node next = node.getFirstChild(); next != null; next = next.getNextSibling()) {
                    this.parse(next);
                }
                break;
            }
            case 1: {
                String prefix;
                int colon;
                String qname = node.getNodeName();
                this._handler.startElement(null, null, qname);
                NamedNodeMap map = node.getAttributes();
                int length = map.getLength();
                for (int i = 0; i < length; ++i) {
                    Node attr = map.item(i);
                    String qnameAttr = attr.getNodeName();
                    if (!qnameAttr.startsWith(XMLNS_PREFIX)) continue;
                    String uriAttr = attr.getNodeValue();
                    colon = qnameAttr.lastIndexOf(58);
                    prefix = colon > 0 ? qnameAttr.substring(colon + 1) : EMPTYSTRING;
                    this._handler.namespaceAfterStartElement(prefix, uriAttr);
                }
                NamespaceMappings nm = new NamespaceMappings();
                for (int i = 0; i < length; ++i) {
                    Node attr = map.item(i);
                    String qnameAttr = attr.getNodeName();
                    if (qnameAttr.startsWith(XMLNS_PREFIX)) continue;
                    String uriAttr = attr.getNamespaceURI();
                    if (uriAttr != null && !uriAttr.equals(EMPTYSTRING)) {
                        colon = qnameAttr.lastIndexOf(58);
                        String newPrefix = nm.lookupPrefix(uriAttr);
                        if (newPrefix == null) {
                            newPrefix = nm.generateNextPrefix();
                        }
                        prefix = colon > 0 ? qnameAttr.substring(0, colon) : newPrefix;
                        this._handler.namespaceAfterStartElement(prefix, uriAttr);
                        this._handler.addAttribute(prefix + ":" + qnameAttr, attr.getNodeValue());
                        continue;
                    }
                    this._handler.addAttribute(qnameAttr, attr.getNodeValue());
                }
                String uri = node.getNamespaceURI();
                String localName = node.getLocalName();
                if (uri != null) {
                    colon = qname.lastIndexOf(58);
                    prefix = colon > 0 ? qname.substring(0, colon) : EMPTYSTRING;
                    this._handler.namespaceAfterStartElement(prefix, uri);
                } else if (uri == null && localName != null) {
                    prefix = EMPTYSTRING;
                    this._handler.namespaceAfterStartElement(prefix, EMPTYSTRING);
                }
                for (Node next = node.getFirstChild(); next != null; next = next.getNextSibling()) {
                    this.parse(next);
                }
                this._handler.endElement(qname);
                break;
            }
            case 7: {
                this._handler.processingInstruction(node.getNodeName(), node.getNodeValue());
                break;
            }
            case 3: {
                this._handler.characters(node.getNodeValue());
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

