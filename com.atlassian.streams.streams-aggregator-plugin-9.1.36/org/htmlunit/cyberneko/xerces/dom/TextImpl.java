/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.xerces.dom;

import org.htmlunit.cyberneko.xerces.dom.CharacterDataImpl;
import org.htmlunit.cyberneko.xerces.dom.CoreDocumentImpl;
import org.htmlunit.cyberneko.xerces.dom.DOMMessageFormatter;
import org.htmlunit.cyberneko.xerces.dom.NodeImpl;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

public class TextImpl
extends CharacterDataImpl
implements Text {
    public TextImpl(CoreDocumentImpl ownerDoc, String data) {
        super(ownerDoc, data);
    }

    @Override
    public short getNodeType() {
        return 3;
    }

    @Override
    public String getNodeName() {
        return "#text";
    }

    public void setIgnorableWhitespace(boolean ignore) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        this.isIgnorableWhitespace(ignore);
    }

    @Override
    public boolean isElementContentWhitespace() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.internalIsIgnorableWhitespace();
    }

    @Override
    public String getWholeText() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        StringBuilder builder = new StringBuilder();
        if (this.data_ != null && this.data_.length() != 0) {
            builder.append(this.data_);
        }
        this.getWholeTextBackward(this.getPreviousSibling(), builder, this.getParentNode());
        String temp = builder.toString();
        builder.setLength(0);
        this.getWholeTextForward(this.getNextSibling(), builder, this.getParentNode());
        return temp + builder;
    }

    protected void insertTextContent(StringBuilder builder) throws DOMException {
        String content = this.getNodeValue();
        if (content != null) {
            builder.insert(0, content);
        }
    }

    private boolean getWholeTextForward(Node node, StringBuilder builder, Node parent) {
        boolean inEntRef = false;
        if (parent != null) {
            boolean bl = inEntRef = parent.getNodeType() == 5;
        }
        while (node != null) {
            short type = node.getNodeType();
            if (type == 5) {
                if (this.getWholeTextForward(node.getFirstChild(), builder, node)) {
                    return true;
                }
            } else if (type == 3 || type == 4) {
                ((NodeImpl)node).getTextContent(builder);
            } else {
                return true;
            }
            node = node.getNextSibling();
        }
        if (inEntRef) {
            this.getWholeTextForward(parent.getNextSibling(), builder, parent.getParentNode());
            return true;
        }
        return false;
    }

    private boolean getWholeTextBackward(Node node, StringBuilder builder, Node parent) {
        boolean inEntRef = false;
        if (parent != null) {
            boolean bl = inEntRef = parent.getNodeType() == 5;
        }
        while (node != null) {
            short type = node.getNodeType();
            if (type == 5) {
                if (this.getWholeTextBackward(node.getLastChild(), builder, node)) {
                    return true;
                }
            } else if (type == 3 || type == 4) {
                ((TextImpl)node).insertTextContent(builder);
            } else {
                return true;
            }
            node = node.getPreviousSibling();
        }
        if (inEntRef) {
            this.getWholeTextBackward(parent.getPreviousSibling(), builder, parent.getParentNode());
            return true;
        }
        return false;
    }

    @Override
    public Text replaceWholeText(String content) throws DOMException {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        Node parent = this.getParentNode();
        if (content == null || content.length() == 0) {
            if (parent != null) {
                parent.removeChild(this);
            }
            return null;
        }
        if (!(!this.ownerDocument().errorChecking || this.canModifyPrev(this) && this.canModifyNext(this))) {
            throw new DOMException(7, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null));
        }
        this.setData(content);
        TextImpl currentNode = this;
        for (Node prev = currentNode.getPreviousSibling(); prev != null && (prev.getNodeType() == 3 || prev.getNodeType() == 4 || prev.getNodeType() == 5 && this.hasTextOnlyChildren(prev)); prev = prev.getPreviousSibling()) {
            parent.removeChild(prev);
            prev = currentNode;
        }
        for (Node next = currentNode.getNextSibling(); next != null && (next.getNodeType() == 3 || next.getNodeType() == 4 || next.getNodeType() == 5 && this.hasTextOnlyChildren(next)); next = next.getNextSibling()) {
            parent.removeChild(next);
            next = currentNode;
        }
        return currentNode;
    }

    private boolean canModifyPrev(Node node) {
        boolean textLastChild = false;
        for (Node prev = node.getPreviousSibling(); prev != null; prev = prev.getPreviousSibling()) {
            short type = prev.getNodeType();
            if (type == 5) {
                Node lastChild = prev.getLastChild();
                if (lastChild == null) {
                    return false;
                }
                while (lastChild != null) {
                    short lType = lastChild.getNodeType();
                    if (lType == 3 || lType == 4) {
                        textLastChild = true;
                    } else if (lType == 5) {
                        if (!this.canModifyPrev(lastChild)) {
                            return false;
                        }
                        textLastChild = true;
                    } else {
                        return !textLastChild;
                    }
                    lastChild = lastChild.getPreviousSibling();
                }
                continue;
            }
            if (type == 3 || type == 4) continue;
            return true;
        }
        return true;
    }

    private boolean canModifyNext(Node node) {
        boolean textFirstChild = false;
        for (Node next = node.getNextSibling(); next != null; next = next.getNextSibling()) {
            short type = next.getNodeType();
            if (type == 5) {
                Node firstChild = next.getFirstChild();
                if (firstChild == null) {
                    return false;
                }
                while (firstChild != null) {
                    short lType = firstChild.getNodeType();
                    if (lType == 3 || lType == 4) {
                        textFirstChild = true;
                    } else if (lType == 5) {
                        if (!this.canModifyNext(firstChild)) {
                            return false;
                        }
                        textFirstChild = true;
                    } else {
                        return !textFirstChild;
                    }
                    firstChild = firstChild.getNextSibling();
                }
                continue;
            }
            if (type == 3 || type == 4) continue;
            return true;
        }
        return true;
    }

    private boolean hasTextOnlyChildren(Node node) {
        Node child = node;
        if (child == null) {
            return false;
        }
        for (child = child.getFirstChild(); child != null; child = child.getNextSibling()) {
            short type = child.getNodeType();
            if (type == 5) {
                return this.hasTextOnlyChildren(child);
            }
            if (type == 3 || type == 4) continue;
            return false;
        }
        return true;
    }

    public boolean isIgnorableWhitespace() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.internalIsIgnorableWhitespace();
    }

    @Override
    public Text splitText(int offset) throws DOMException {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (offset < 0 || offset > this.data_.length()) {
            throw new DOMException(1, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INDEX_SIZE_ERR", null));
        }
        Text newText = this.getOwnerDocument().createTextNode(this.data_.substring(offset));
        this.setNodeValue(this.data_.substring(0, offset));
        Node parentNode = this.getParentNode();
        if (parentNode != null) {
            parentNode.insertBefore(newText, this.nextSibling);
        }
        return newText;
    }

    public void replaceData(String value) {
        this.data_ = value;
    }

    public String removeData() {
        String olddata = this.data_;
        this.data_ = "";
        return olddata;
    }
}

