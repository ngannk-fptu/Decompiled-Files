/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.dtm.ref;

import org.apache.xml.dtm.DTM;
import org.apache.xml.utils.NodeConsumer;
import org.apache.xml.utils.XMLString;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.AttributesImpl;

public class DTMTreeWalker {
    private ContentHandler m_contentHandler = null;
    protected DTM m_dtm;
    boolean nextIsRaw = false;

    public void setDTM(DTM dtm) {
        this.m_dtm = dtm;
    }

    public ContentHandler getcontentHandler() {
        return this.m_contentHandler;
    }

    public void setcontentHandler(ContentHandler ch) {
        this.m_contentHandler = ch;
    }

    public DTMTreeWalker() {
    }

    public DTMTreeWalker(ContentHandler contentHandler, DTM dtm) {
        this.m_contentHandler = contentHandler;
        this.m_dtm = dtm;
    }

    public void traverse(int pos) throws SAXException {
        int top = pos;
        while (-1 != pos) {
            this.startNode(pos);
            int nextNode = this.m_dtm.getFirstChild(pos);
            while (-1 == nextNode) {
                this.endNode(pos);
                if (top == pos) break;
                nextNode = this.m_dtm.getNextSibling(pos);
                if (-1 != nextNode || -1 != (pos = this.m_dtm.getParent(pos)) && top != pos) continue;
                if (-1 != pos) {
                    this.endNode(pos);
                }
                nextNode = -1;
                break;
            }
            pos = nextNode;
        }
    }

    public void traverse(int pos, int top) throws SAXException {
        while (-1 != pos) {
            this.startNode(pos);
            int nextNode = this.m_dtm.getFirstChild(pos);
            while (-1 == nextNode) {
                this.endNode(pos);
                if (-1 != top && top == pos) break;
                nextNode = this.m_dtm.getNextSibling(pos);
                if (-1 != nextNode || -1 != (pos = this.m_dtm.getParent(pos)) && (-1 == top || top != pos)) continue;
                nextNode = -1;
                break;
            }
            pos = nextNode;
        }
    }

    private final void dispatachChars(int node) throws SAXException {
        this.m_dtm.dispatchCharactersEvents(node, this.m_contentHandler, false);
    }

    protected void startNode(int node) throws SAXException {
        if (this.m_contentHandler instanceof NodeConsumer) {
            // empty if block
        }
        switch (this.m_dtm.getNodeType(node)) {
            case 8: {
                XMLString data = this.m_dtm.getStringValue(node);
                if (!(this.m_contentHandler instanceof LexicalHandler)) break;
                LexicalHandler lh = (LexicalHandler)((Object)this.m_contentHandler);
                data.dispatchAsComment(lh);
                break;
            }
            case 11: {
                break;
            }
            case 9: {
                this.m_contentHandler.startDocument();
                break;
            }
            case 1: {
                DTM dtm = this.m_dtm;
                int nsn = dtm.getFirstNamespaceNode(node, true);
                while (-1 != nsn) {
                    String prefix = dtm.getNodeNameX(nsn);
                    this.m_contentHandler.startPrefixMapping(prefix, dtm.getNodeValue(nsn));
                    nsn = dtm.getNextNamespaceNode(node, nsn, true);
                }
                String ns = dtm.getNamespaceURI(node);
                if (null == ns) {
                    ns = "";
                }
                AttributesImpl attrs = new AttributesImpl();
                int i = dtm.getFirstAttribute(node);
                while (i != -1) {
                    attrs.addAttribute(dtm.getNamespaceURI(i), dtm.getLocalName(i), dtm.getNodeName(i), "CDATA", dtm.getNodeValue(i));
                    i = dtm.getNextAttribute(i);
                }
                this.m_contentHandler.startElement(ns, this.m_dtm.getLocalName(node), this.m_dtm.getNodeName(node), attrs);
                break;
            }
            case 7: {
                String name = this.m_dtm.getNodeName(node);
                if (name.equals("xslt-next-is-raw")) {
                    this.nextIsRaw = true;
                    break;
                }
                this.m_contentHandler.processingInstruction(name, this.m_dtm.getNodeValue(node));
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
                if (!(this.m_contentHandler instanceof LexicalHandler)) break;
                ((LexicalHandler)((Object)this.m_contentHandler)).startEntity(this.m_dtm.getNodeName(node));
                break;
            }
        }
    }

    protected void endNode(int node) throws SAXException {
        switch (this.m_dtm.getNodeType(node)) {
            case 9: {
                this.m_contentHandler.endDocument();
                break;
            }
            case 1: {
                String ns = this.m_dtm.getNamespaceURI(node);
                if (null == ns) {
                    ns = "";
                }
                this.m_contentHandler.endElement(ns, this.m_dtm.getLocalName(node), this.m_dtm.getNodeName(node));
                int nsn = this.m_dtm.getFirstNamespaceNode(node, true);
                while (-1 != nsn) {
                    String prefix = this.m_dtm.getNodeNameX(nsn);
                    this.m_contentHandler.endPrefixMapping(prefix);
                    nsn = this.m_dtm.getNextNamespaceNode(node, nsn, true);
                }
                break;
            }
            case 4: {
                break;
            }
            case 5: {
                if (!(this.m_contentHandler instanceof LexicalHandler)) break;
                LexicalHandler lh = (LexicalHandler)((Object)this.m_contentHandler);
                lh.endEntity(this.m_dtm.getNodeName(node));
                break;
            }
        }
    }
}

