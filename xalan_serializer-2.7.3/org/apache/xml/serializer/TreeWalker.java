/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.serializer;

import java.io.File;
import org.apache.xml.serializer.SerializationHandler;
import org.apache.xml.serializer.utils.AttList;
import org.apache.xml.serializer.utils.DOM2Helper;
import org.w3c.dom.Comment;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.LocatorImpl;

public final class TreeWalker {
    private final ContentHandler m_contentHandler;
    private final SerializationHandler m_Serializer;
    protected final DOM2Helper m_dh;
    private final LocatorImpl m_locator = new LocatorImpl();
    boolean nextIsRaw = false;

    public ContentHandler getContentHandler() {
        return this.m_contentHandler;
    }

    public TreeWalker(ContentHandler ch) {
        this(ch, null);
    }

    public TreeWalker(ContentHandler contentHandler, String systemId) {
        this.m_contentHandler = contentHandler;
        this.m_Serializer = this.m_contentHandler instanceof SerializationHandler ? (SerializationHandler)this.m_contentHandler : null;
        this.m_contentHandler.setDocumentLocator(this.m_locator);
        if (systemId != null) {
            this.m_locator.setSystemId(systemId);
        } else {
            try {
                this.m_locator.setSystemId(System.getProperty("user.dir") + File.separator + "dummy.xsl");
            }
            catch (SecurityException securityException) {
                // empty catch block
            }
        }
        if (this.m_contentHandler != null) {
            this.m_contentHandler.setDocumentLocator(this.m_locator);
        }
        try {
            this.m_locator.setSystemId(System.getProperty("user.dir") + File.separator + "dummy.xsl");
        }
        catch (SecurityException securityException) {
            // empty catch block
        }
        this.m_dh = new DOM2Helper();
    }

    public void traverse(Node pos) throws SAXException {
        this.m_contentHandler.startDocument();
        Node top = pos;
        while (null != pos) {
            this.startNode(pos);
            Node nextNode = pos.getFirstChild();
            while (null == nextNode) {
                this.endNode(pos);
                if (top.equals(pos)) break;
                nextNode = pos.getNextSibling();
                if (null != nextNode || null != (pos = pos.getParentNode()) && !top.equals(pos)) continue;
                if (null != pos) {
                    this.endNode(pos);
                }
                nextNode = null;
                break;
            }
            pos = nextNode;
        }
        this.m_contentHandler.endDocument();
    }

    public void traverse(Node pos, Node top) throws SAXException {
        this.m_contentHandler.startDocument();
        while (null != pos) {
            this.startNode(pos);
            Node nextNode = pos.getFirstChild();
            while (null == nextNode) {
                this.endNode(pos);
                if (null != top && top.equals(pos)) break;
                nextNode = pos.getNextSibling();
                if (null != nextNode || null != (pos = pos.getParentNode()) && (null == top || !top.equals(pos))) continue;
                nextNode = null;
                break;
            }
            pos = nextNode;
        }
        this.m_contentHandler.endDocument();
    }

    private final void dispatachChars(Node node) throws SAXException {
        if (this.m_Serializer != null) {
            this.m_Serializer.characters(node);
        } else {
            String data = ((Text)node).getData();
            this.m_contentHandler.characters(data.toCharArray(), 0, data.length());
        }
    }

    protected void startNode(Node node) throws SAXException {
        if (node instanceof Locator) {
            Locator loc = (Locator)((Object)node);
            this.m_locator.setColumnNumber(loc.getColumnNumber());
            this.m_locator.setLineNumber(loc.getLineNumber());
            this.m_locator.setPublicId(loc.getPublicId());
            this.m_locator.setSystemId(loc.getSystemId());
        } else {
            this.m_locator.setColumnNumber(0);
            this.m_locator.setLineNumber(0);
        }
        switch (node.getNodeType()) {
            case 8: {
                String data = ((Comment)node).getData();
                if (!(this.m_contentHandler instanceof LexicalHandler)) break;
                LexicalHandler lh = (LexicalHandler)((Object)this.m_contentHandler);
                lh.comment(data.toCharArray(), 0, data.length());
                break;
            }
            case 11: {
                break;
            }
            case 9: {
                break;
            }
            case 1: {
                Element elem_node = (Element)node;
                String uri = elem_node.getNamespaceURI();
                if (uri != null) {
                    String prefix = elem_node.getPrefix();
                    if (prefix == null) {
                        prefix = "";
                    }
                    this.m_contentHandler.startPrefixMapping(prefix, uri);
                }
                NamedNodeMap atts = elem_node.getAttributes();
                int nAttrs = atts.getLength();
                for (int i = 0; i < nAttrs; ++i) {
                    String prefix;
                    Node attr = atts.item(i);
                    String attrName = attr.getNodeName();
                    int colon = attrName.indexOf(58);
                    if (attrName.equals("xmlns") || attrName.startsWith("xmlns:")) {
                        prefix = colon < 0 ? "" : attrName.substring(colon + 1);
                        this.m_contentHandler.startPrefixMapping(prefix, attr.getNodeValue());
                        continue;
                    }
                    if (colon <= 0) continue;
                    prefix = attrName.substring(0, colon);
                    String uri2 = attr.getNamespaceURI();
                    if (uri2 == null) continue;
                    this.m_contentHandler.startPrefixMapping(prefix, uri2);
                }
                String ns = this.m_dh.getNamespaceOfNode(node);
                if (null == ns) {
                    ns = "";
                }
                this.m_contentHandler.startElement(ns, this.m_dh.getLocalNameOfNode(node), node.getNodeName(), new AttList(atts, this.m_dh));
                break;
            }
            case 7: {
                ProcessingInstruction pi = (ProcessingInstruction)node;
                String name = pi.getNodeName();
                if (name.equals("xslt-next-is-raw")) {
                    this.nextIsRaw = true;
                    break;
                }
                this.m_contentHandler.processingInstruction(pi.getNodeName(), pi.getData());
                break;
            }
            case 4: {
                LexicalHandler lh;
                boolean isLexH = this.m_contentHandler instanceof LexicalHandler;
                LexicalHandler lexicalHandler = lh = isLexH ? (LexicalHandler)((Object)this.m_contentHandler) : null;
                if (isLexH) {
                    lh.startCDATA();
                }
                this.dispatachChars(node);
                if (!isLexH) break;
                lh.endCDATA();
                break;
            }
            case 3: {
                if (this.nextIsRaw) {
                    this.nextIsRaw = false;
                    this.m_contentHandler.processingInstruction("javax.xml.transform.disable-output-escaping", "");
                    this.dispatachChars(node);
                    this.m_contentHandler.processingInstruction("javax.xml.transform.enable-output-escaping", "");
                    break;
                }
                this.dispatachChars(node);
                break;
            }
            case 5: {
                EntityReference eref = (EntityReference)node;
                if (!(this.m_contentHandler instanceof LexicalHandler)) break;
                ((LexicalHandler)((Object)this.m_contentHandler)).startEntity(eref.getNodeName());
                break;
            }
        }
    }

    protected void endNode(Node node) throws SAXException {
        switch (node.getNodeType()) {
            case 9: {
                break;
            }
            case 1: {
                String ns = this.m_dh.getNamespaceOfNode(node);
                if (null == ns) {
                    ns = "";
                }
                this.m_contentHandler.endElement(ns, this.m_dh.getLocalNameOfNode(node), node.getNodeName());
                if (this.m_Serializer != null) break;
                Element elem_node = (Element)node;
                NamedNodeMap atts = elem_node.getAttributes();
                int nAttrs = atts.getLength();
                for (int i = nAttrs - 1; 0 <= i; --i) {
                    String prefix;
                    Node attr = atts.item(i);
                    String attrName = attr.getNodeName();
                    int colon = attrName.indexOf(58);
                    if (attrName.equals("xmlns") || attrName.startsWith("xmlns:")) {
                        prefix = colon < 0 ? "" : attrName.substring(colon + 1);
                        this.m_contentHandler.endPrefixMapping(prefix);
                        continue;
                    }
                    if (colon <= 0) continue;
                    prefix = attrName.substring(0, colon);
                    this.m_contentHandler.endPrefixMapping(prefix);
                }
                String uri = elem_node.getNamespaceURI();
                if (uri == null) break;
                String prefix = elem_node.getPrefix();
                if (prefix == null) {
                    prefix = "";
                }
                this.m_contentHandler.endPrefixMapping(prefix);
                break;
            }
            case 4: {
                break;
            }
            case 5: {
                EntityReference eref = (EntityReference)node;
                if (!(this.m_contentHandler instanceof LexicalHandler)) break;
                LexicalHandler lh = (LexicalHandler)((Object)this.m_contentHandler);
                lh.endEntity(eref.getNodeName());
                break;
            }
        }
    }
}

