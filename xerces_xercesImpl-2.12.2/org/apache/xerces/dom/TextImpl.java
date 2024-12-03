/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.dom;

import org.apache.xerces.dom.CharacterDataImpl;
import org.apache.xerces.dom.CoreDocumentImpl;
import org.apache.xerces.dom.DOMMessageFormatter;
import org.apache.xerces.dom.NodeImpl;
import org.w3c.dom.CharacterData;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

public class TextImpl
extends CharacterDataImpl
implements CharacterData,
Text {
    static final long serialVersionUID = -5294980852957403469L;

    public TextImpl() {
    }

    public TextImpl(CoreDocumentImpl coreDocumentImpl, String string) {
        super(coreDocumentImpl, string);
    }

    public void setValues(CoreDocumentImpl coreDocumentImpl, String string) {
        this.flags = 0;
        this.nextSibling = null;
        this.previousSibling = null;
        this.setOwnerDocument(coreDocumentImpl);
        this.data = string;
    }

    @Override
    public short getNodeType() {
        return 3;
    }

    @Override
    public String getNodeName() {
        return "#text";
    }

    public void setIgnorableWhitespace(boolean bl) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        this.isIgnorableWhitespace(bl);
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
        StringBuffer stringBuffer = new StringBuffer();
        if (this.data != null && this.data.length() != 0) {
            stringBuffer.append(this.data);
        }
        this.getWholeTextBackward(this.getPreviousSibling(), stringBuffer, this.getParentNode());
        String string = stringBuffer.toString();
        stringBuffer.setLength(0);
        this.getWholeTextForward(this.getNextSibling(), stringBuffer, this.getParentNode());
        return string + stringBuffer.toString();
    }

    protected void insertTextContent(StringBuffer stringBuffer) throws DOMException {
        String string = this.getNodeValue();
        if (string != null) {
            stringBuffer.insert(0, string);
        }
    }

    private boolean getWholeTextForward(Node node, StringBuffer stringBuffer, Node node2) {
        boolean bl = false;
        if (node2 != null) {
            boolean bl2 = bl = node2.getNodeType() == 5;
        }
        while (node != null) {
            short s = node.getNodeType();
            if (s == 5) {
                if (this.getWholeTextForward(node.getFirstChild(), stringBuffer, node)) {
                    return true;
                }
            } else if (s == 3 || s == 4) {
                ((NodeImpl)node).getTextContent(stringBuffer);
            } else {
                return true;
            }
            node = node.getNextSibling();
        }
        if (bl) {
            this.getWholeTextForward(node2.getNextSibling(), stringBuffer, node2.getParentNode());
            return true;
        }
        return false;
    }

    private boolean getWholeTextBackward(Node node, StringBuffer stringBuffer, Node node2) {
        boolean bl = false;
        if (node2 != null) {
            boolean bl2 = bl = node2.getNodeType() == 5;
        }
        while (node != null) {
            short s = node.getNodeType();
            if (s == 5) {
                if (this.getWholeTextBackward(node.getLastChild(), stringBuffer, node)) {
                    return true;
                }
            } else if (s == 3 || s == 4) {
                ((TextImpl)node).insertTextContent(stringBuffer);
            } else {
                return true;
            }
            node = node.getPreviousSibling();
        }
        if (bl) {
            this.getWholeTextBackward(node2.getPreviousSibling(), stringBuffer, node2.getParentNode());
            return true;
        }
        return false;
    }

    /*
     * WARNING - void declaration
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public Text replaceWholeText(String string) throws DOMException {
        void var3_6;
        Node node;
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        Node node2 = this.getParentNode();
        if (string == null || string.length() == 0) {
            if (node2 == null) return null;
            node2.removeChild(this);
            return null;
        }
        if (this.ownerDocument().errorChecking) {
            if (!this.canModifyPrev(this)) {
                throw new DOMException(7, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null));
            }
            if (!this.canModifyNext(this)) {
                throw new DOMException(7, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null));
            }
        }
        Object var3_3 = null;
        if (this.isReadOnly()) {
            node = this.ownerDocument().createTextNode(string);
            if (node2 == null) return node;
            node2.insertBefore(node, this);
            node2.removeChild(this);
            Node node3 = node;
        } else {
            this.setData(string);
            TextImpl textImpl = this;
        }
        for (node = var3_6.getPreviousSibling(); node != null && (node.getNodeType() == 3 || node.getNodeType() == 4 || node.getNodeType() == 5 && this.hasTextOnlyChildren(node)); node = node.getPreviousSibling()) {
            node2.removeChild(node);
            node = var3_6;
        }
        for (Node node4 = var3_6.getNextSibling(); node4 != null && (node4.getNodeType() == 3 || node4.getNodeType() == 4 || node4.getNodeType() == 5 && this.hasTextOnlyChildren(node4)); node4 = node4.getNextSibling()) {
            node2.removeChild(node4);
            node4 = var3_6;
        }
        return var3_6;
    }

    private boolean canModifyPrev(Node node) {
        boolean bl = false;
        for (Node node2 = node.getPreviousSibling(); node2 != null; node2 = node2.getPreviousSibling()) {
            short s = node2.getNodeType();
            if (s == 5) {
                Node node3 = node2.getLastChild();
                if (node3 == null) {
                    return false;
                }
                while (node3 != null) {
                    short s2 = node3.getNodeType();
                    if (s2 == 3 || s2 == 4) {
                        bl = true;
                    } else if (s2 == 5) {
                        if (!this.canModifyPrev(node3)) {
                            return false;
                        }
                        bl = true;
                    } else {
                        return !bl;
                    }
                    node3 = node3.getPreviousSibling();
                }
                continue;
            }
            if (s == 3 || s == 4) continue;
            return true;
        }
        return true;
    }

    private boolean canModifyNext(Node node) {
        boolean bl = false;
        for (Node node2 = node.getNextSibling(); node2 != null; node2 = node2.getNextSibling()) {
            short s = node2.getNodeType();
            if (s == 5) {
                Node node3 = node2.getFirstChild();
                if (node3 == null) {
                    return false;
                }
                while (node3 != null) {
                    short s2 = node3.getNodeType();
                    if (s2 == 3 || s2 == 4) {
                        bl = true;
                    } else if (s2 == 5) {
                        if (!this.canModifyNext(node3)) {
                            return false;
                        }
                        bl = true;
                    } else {
                        return !bl;
                    }
                    node3 = node3.getNextSibling();
                }
                continue;
            }
            if (s == 3 || s == 4) continue;
            return true;
        }
        return true;
    }

    private boolean hasTextOnlyChildren(Node node) {
        Node node2 = node;
        if (node2 == null) {
            return false;
        }
        for (node2 = node2.getFirstChild(); node2 != null; node2 = node2.getNextSibling()) {
            short s = node2.getNodeType();
            if (s == 5) {
                return this.hasTextOnlyChildren(node2);
            }
            if (s == 3 || s == 4 || s == 5) continue;
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
    public Text splitText(int n) throws DOMException {
        if (this.isReadOnly()) {
            throw new DOMException(7, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null));
        }
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (n < 0 || n > this.data.length()) {
            throw new DOMException(1, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INDEX_SIZE_ERR", null));
        }
        Text text = this.getOwnerDocument().createTextNode(this.data.substring(n));
        this.setNodeValue(this.data.substring(0, n));
        Node node = this.getParentNode();
        if (node != null) {
            node.insertBefore(text, this.nextSibling);
        }
        return text;
    }

    public void replaceData(String string) {
        this.data = string;
    }

    public String removeData() {
        String string = this.data;
        this.data = "";
        return string;
    }
}

