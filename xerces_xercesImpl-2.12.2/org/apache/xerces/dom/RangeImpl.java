/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.dom;

import java.util.ArrayList;
import org.apache.xerces.dom.CharacterDataImpl;
import org.apache.xerces.dom.DOMMessageFormatter;
import org.apache.xerces.dom.DocumentImpl;
import org.apache.xerces.dom.RangeExceptionImpl;
import org.apache.xerces.dom.TextImpl;
import org.w3c.dom.CharacterData;
import org.w3c.dom.DOMException;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;
import org.w3c.dom.ranges.Range;
import org.w3c.dom.ranges.RangeException;

public class RangeImpl
implements Range {
    private DocumentImpl fDocument;
    private Node fStartContainer;
    private Node fEndContainer;
    private int fStartOffset;
    private int fEndOffset;
    private boolean fDetach = false;
    private Node fInsertNode = null;
    private Node fDeleteNode = null;
    private Node fSplitNode = null;
    private boolean fInsertedFromRange = false;
    private Node fRemoveChild = null;
    static final int EXTRACT_CONTENTS = 1;
    static final int CLONE_CONTENTS = 2;
    static final int DELETE_CONTENTS = 3;

    public RangeImpl(DocumentImpl documentImpl) {
        this.fDocument = documentImpl;
        this.fStartContainer = documentImpl;
        this.fEndContainer = documentImpl;
        this.fStartOffset = 0;
        this.fEndOffset = 0;
        this.fDetach = false;
    }

    @Override
    public Node getStartContainer() {
        if (this.fDetach) {
            throw new DOMException(11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null));
        }
        return this.fStartContainer;
    }

    @Override
    public int getStartOffset() {
        if (this.fDetach) {
            throw new DOMException(11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null));
        }
        return this.fStartOffset;
    }

    @Override
    public Node getEndContainer() {
        if (this.fDetach) {
            throw new DOMException(11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null));
        }
        return this.fEndContainer;
    }

    @Override
    public int getEndOffset() {
        if (this.fDetach) {
            throw new DOMException(11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null));
        }
        return this.fEndOffset;
    }

    @Override
    public boolean getCollapsed() {
        if (this.fDetach) {
            throw new DOMException(11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null));
        }
        return this.fStartContainer == this.fEndContainer && this.fStartOffset == this.fEndOffset;
    }

    @Override
    public Node getCommonAncestorContainer() {
        Node node;
        if (this.fDetach) {
            throw new DOMException(11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null));
        }
        ArrayList<Node> arrayList = new ArrayList<Node>();
        for (node = this.fStartContainer; node != null; node = node.getParentNode()) {
            arrayList.add(node);
        }
        ArrayList<Node> arrayList2 = new ArrayList<Node>();
        for (node = this.fEndContainer; node != null; node = node.getParentNode()) {
            arrayList2.add(node);
        }
        int n = arrayList.size() - 1;
        Object var6_6 = null;
        for (int i = arrayList2.size() - 1; n >= 0 && i >= 0 && arrayList.get(n) == arrayList2.get(i); --n, --i) {
            var6_6 = arrayList.get(n);
        }
        return var6_6;
    }

    @Override
    public void setStart(Node node, int n) throws RangeException, DOMException {
        if (this.fDocument.errorChecking) {
            if (this.fDetach) {
                throw new DOMException(11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null));
            }
            if (!this.isLegalContainer(node)) {
                throw new RangeExceptionImpl(2, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_NODE_TYPE_ERR", null));
            }
            if (this.fDocument != node.getOwnerDocument() && this.fDocument != node) {
                throw new DOMException(4, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null));
            }
        }
        this.checkIndex(node, n);
        this.fStartContainer = node;
        this.fStartOffset = n;
        if (this.getCommonAncestorContainer() == null || this.fStartContainer == this.fEndContainer && this.fEndOffset < this.fStartOffset) {
            this.collapse(true);
        }
    }

    @Override
    public void setEnd(Node node, int n) throws RangeException, DOMException {
        if (this.fDocument.errorChecking) {
            if (this.fDetach) {
                throw new DOMException(11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null));
            }
            if (!this.isLegalContainer(node)) {
                throw new RangeExceptionImpl(2, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_NODE_TYPE_ERR", null));
            }
            if (this.fDocument != node.getOwnerDocument() && this.fDocument != node) {
                throw new DOMException(4, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null));
            }
        }
        this.checkIndex(node, n);
        this.fEndContainer = node;
        this.fEndOffset = n;
        if (this.getCommonAncestorContainer() == null || this.fStartContainer == this.fEndContainer && this.fEndOffset < this.fStartOffset) {
            this.collapse(false);
        }
    }

    @Override
    public void setStartBefore(Node node) throws RangeException {
        if (this.fDocument.errorChecking) {
            if (this.fDetach) {
                throw new DOMException(11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null));
            }
            if (!this.hasLegalRootContainer(node) || !this.isLegalContainedNode(node)) {
                throw new RangeExceptionImpl(2, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_NODE_TYPE_ERR", null));
            }
            if (this.fDocument != node.getOwnerDocument() && this.fDocument != node) {
                throw new DOMException(4, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null));
            }
        }
        this.fStartContainer = node.getParentNode();
        int n = 0;
        for (Node node2 = node; node2 != null; node2 = node2.getPreviousSibling()) {
            ++n;
        }
        this.fStartOffset = n - 1;
        if (this.getCommonAncestorContainer() == null || this.fStartContainer == this.fEndContainer && this.fEndOffset < this.fStartOffset) {
            this.collapse(true);
        }
    }

    @Override
    public void setStartAfter(Node node) throws RangeException {
        if (this.fDocument.errorChecking) {
            if (this.fDetach) {
                throw new DOMException(11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null));
            }
            if (!this.hasLegalRootContainer(node) || !this.isLegalContainedNode(node)) {
                throw new RangeExceptionImpl(2, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_NODE_TYPE_ERR", null));
            }
            if (this.fDocument != node.getOwnerDocument() && this.fDocument != node) {
                throw new DOMException(4, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null));
            }
        }
        this.fStartContainer = node.getParentNode();
        int n = 0;
        for (Node node2 = node; node2 != null; node2 = node2.getPreviousSibling()) {
            ++n;
        }
        this.fStartOffset = n;
        if (this.getCommonAncestorContainer() == null || this.fStartContainer == this.fEndContainer && this.fEndOffset < this.fStartOffset) {
            this.collapse(true);
        }
    }

    @Override
    public void setEndBefore(Node node) throws RangeException {
        if (this.fDocument.errorChecking) {
            if (this.fDetach) {
                throw new DOMException(11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null));
            }
            if (!this.hasLegalRootContainer(node) || !this.isLegalContainedNode(node)) {
                throw new RangeExceptionImpl(2, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_NODE_TYPE_ERR", null));
            }
            if (this.fDocument != node.getOwnerDocument() && this.fDocument != node) {
                throw new DOMException(4, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null));
            }
        }
        this.fEndContainer = node.getParentNode();
        int n = 0;
        for (Node node2 = node; node2 != null; node2 = node2.getPreviousSibling()) {
            ++n;
        }
        this.fEndOffset = n - 1;
        if (this.getCommonAncestorContainer() == null || this.fStartContainer == this.fEndContainer && this.fEndOffset < this.fStartOffset) {
            this.collapse(false);
        }
    }

    @Override
    public void setEndAfter(Node node) throws RangeException {
        if (this.fDocument.errorChecking) {
            if (this.fDetach) {
                throw new DOMException(11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null));
            }
            if (!this.hasLegalRootContainer(node) || !this.isLegalContainedNode(node)) {
                throw new RangeExceptionImpl(2, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_NODE_TYPE_ERR", null));
            }
            if (this.fDocument != node.getOwnerDocument() && this.fDocument != node) {
                throw new DOMException(4, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null));
            }
        }
        this.fEndContainer = node.getParentNode();
        int n = 0;
        for (Node node2 = node; node2 != null; node2 = node2.getPreviousSibling()) {
            ++n;
        }
        this.fEndOffset = n;
        if (this.getCommonAncestorContainer() == null || this.fStartContainer == this.fEndContainer && this.fEndOffset < this.fStartOffset) {
            this.collapse(false);
        }
    }

    @Override
    public void collapse(boolean bl) {
        if (this.fDetach) {
            throw new DOMException(11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null));
        }
        if (bl) {
            this.fEndContainer = this.fStartContainer;
            this.fEndOffset = this.fStartOffset;
        } else {
            this.fStartContainer = this.fEndContainer;
            this.fStartOffset = this.fEndOffset;
        }
    }

    @Override
    public void selectNode(Node node) throws RangeException {
        Node node2;
        if (this.fDocument.errorChecking) {
            if (this.fDetach) {
                throw new DOMException(11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null));
            }
            if (!this.isLegalContainer(node.getParentNode()) || !this.isLegalContainedNode(node)) {
                throw new RangeExceptionImpl(2, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_NODE_TYPE_ERR", null));
            }
            if (this.fDocument != node.getOwnerDocument() && this.fDocument != node) {
                throw new DOMException(4, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null));
            }
        }
        if ((node2 = node.getParentNode()) != null) {
            this.fStartContainer = node2;
            this.fEndContainer = node2;
            int n = 0;
            for (Node node3 = node; node3 != null; node3 = node3.getPreviousSibling()) {
                ++n;
            }
            this.fStartOffset = n - 1;
            this.fEndOffset = this.fStartOffset + 1;
        }
    }

    @Override
    public void selectNodeContents(Node node) throws RangeException {
        if (this.fDocument.errorChecking) {
            if (this.fDetach) {
                throw new DOMException(11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null));
            }
            if (!this.isLegalContainer(node)) {
                throw new RangeExceptionImpl(2, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_NODE_TYPE_ERR", null));
            }
            if (this.fDocument != node.getOwnerDocument() && this.fDocument != node) {
                throw new DOMException(4, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null));
            }
        }
        this.fStartContainer = node;
        this.fEndContainer = node;
        Node node2 = node.getFirstChild();
        this.fStartOffset = 0;
        if (node2 == null) {
            this.fEndOffset = 0;
        } else {
            int n = 0;
            for (Node node3 = node2; node3 != null; node3 = node3.getNextSibling()) {
                ++n;
            }
            this.fEndOffset = n;
        }
    }

    @Override
    public short compareBoundaryPoints(short s, Range range) throws DOMException {
        Node node;
        int n;
        int n2;
        Node node2;
        Node node3;
        if (this.fDocument.errorChecking) {
            if (this.fDetach) {
                throw new DOMException(11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null));
            }
            if (this.fDocument != range.getStartContainer().getOwnerDocument() && this.fDocument != range.getStartContainer() && range.getStartContainer() != null || this.fDocument != range.getEndContainer().getOwnerDocument() && this.fDocument != range.getEndContainer() && range.getStartContainer() != null) {
                throw new DOMException(4, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null));
            }
        }
        if (s == 0) {
            node3 = range.getStartContainer();
            node2 = this.fStartContainer;
            n2 = range.getStartOffset();
            n = this.fStartOffset;
        } else if (s == 1) {
            node3 = range.getStartContainer();
            node2 = this.fEndContainer;
            n2 = range.getStartOffset();
            n = this.fEndOffset;
        } else if (s == 3) {
            node3 = range.getEndContainer();
            node2 = this.fStartContainer;
            n2 = range.getEndOffset();
            n = this.fStartOffset;
        } else {
            node3 = range.getEndContainer();
            node2 = this.fEndContainer;
            n2 = range.getEndOffset();
            n = this.fEndOffset;
        }
        if (node3 == node2) {
            if (n2 < n) {
                return 1;
            }
            if (n2 == n) {
                return 0;
            }
            return -1;
        }
        Node node4 = node2;
        for (node = node4.getParentNode(); node != null; node = node.getParentNode()) {
            if (node == node3) {
                int n3 = this.indexOf(node4, node3);
                if (n2 <= n3) {
                    return 1;
                }
                return -1;
            }
            node4 = node;
        }
        node4 = node3;
        for (node = node4.getParentNode(); node != null; node = node.getParentNode()) {
            if (node == node2) {
                int n4 = this.indexOf(node4, node2);
                if (n4 < n) {
                    return 1;
                }
                return -1;
            }
            node4 = node;
        }
        int n5 = 0;
        for (node = node3; node != null; node = node.getParentNode()) {
            ++n5;
        }
        for (node = node2; node != null; node = node.getParentNode()) {
            --n5;
        }
        while (n5 > 0) {
            node3 = node3.getParentNode();
            --n5;
        }
        while (n5 < 0) {
            node2 = node2.getParentNode();
            ++n5;
        }
        node = node3.getParentNode();
        for (Node node5 = node2.getParentNode(); node != node5; node = node.getParentNode(), node5 = node5.getParentNode()) {
            node3 = node;
            node2 = node5;
        }
        for (node = node3.getNextSibling(); node != null; node = node.getNextSibling()) {
            if (node != node2) continue;
            return 1;
        }
        return -1;
    }

    @Override
    public void deleteContents() throws DOMException {
        this.traverseContents(3);
    }

    @Override
    public DocumentFragment extractContents() throws DOMException {
        return this.traverseContents(1);
    }

    @Override
    public DocumentFragment cloneContents() throws DOMException {
        return this.traverseContents(2);
    }

    @Override
    public void insertNode(Node node) throws DOMException, RangeException {
        if (node == null) {
            return;
        }
        short s = node.getNodeType();
        if (this.fDocument.errorChecking) {
            if (this.fDetach) {
                throw new DOMException(11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null));
            }
            if (this.fDocument != node.getOwnerDocument()) {
                throw new DOMException(4, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null));
            }
            if (s == 2 || s == 6 || s == 12 || s == 9) {
                throw new RangeExceptionImpl(2, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_NODE_TYPE_ERR", null));
            }
        }
        int n = 0;
        this.fInsertedFromRange = true;
        if (this.fStartContainer.getNodeType() == 3) {
            Node node2 = this.fStartContainer.getParentNode();
            n = node2.getChildNodes().getLength();
            Node node3 = this.fStartContainer.cloneNode(false);
            ((TextImpl)node3).setNodeValueInternal(node3.getNodeValue().substring(this.fStartOffset));
            ((TextImpl)this.fStartContainer).setNodeValueInternal(this.fStartContainer.getNodeValue().substring(0, this.fStartOffset));
            Node node4 = this.fStartContainer.getNextSibling();
            if (node4 != null) {
                if (node2 != null) {
                    node2.insertBefore(node, node4);
                    node2.insertBefore(node3, node4);
                }
            } else if (node2 != null) {
                node2.appendChild(node);
                node2.appendChild(node3);
            }
            if (this.fEndContainer == this.fStartContainer) {
                this.fEndContainer = node3;
                this.fEndOffset -= this.fStartOffset;
            } else if (this.fEndContainer == node2) {
                this.fEndOffset += node2.getChildNodes().getLength() - n;
            }
            this.signalSplitData(this.fStartContainer, node3, this.fStartOffset);
        } else {
            if (this.fEndContainer == this.fStartContainer) {
                n = this.fEndContainer.getChildNodes().getLength();
            }
            Node node5 = this.fStartContainer.getFirstChild();
            int n2 = 0;
            for (n2 = 0; n2 < this.fStartOffset && node5 != null; node5 = node5.getNextSibling(), ++n2) {
            }
            if (node5 != null) {
                this.fStartContainer.insertBefore(node, node5);
            } else {
                this.fStartContainer.appendChild(node);
            }
            if (this.fEndContainer == this.fStartContainer && this.fEndOffset != 0) {
                this.fEndOffset += this.fEndContainer.getChildNodes().getLength() - n;
            }
        }
        this.fInsertedFromRange = false;
    }

    @Override
    public void surroundContents(Node node) throws DOMException, RangeException {
        if (node == null) {
            return;
        }
        short s = node.getNodeType();
        if (this.fDocument.errorChecking) {
            if (this.fDetach) {
                throw new DOMException(11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null));
            }
            if (s == 2 || s == 6 || s == 12 || s == 10 || s == 9 || s == 11) {
                throw new RangeExceptionImpl(2, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_NODE_TYPE_ERR", null));
            }
        }
        Node node2 = this.fStartContainer;
        Node node3 = this.fEndContainer;
        if (this.fStartContainer.getNodeType() == 3) {
            node2 = this.fStartContainer.getParentNode();
        }
        if (this.fEndContainer.getNodeType() == 3) {
            node3 = this.fEndContainer.getParentNode();
        }
        if (node2 != node3) {
            throw new RangeExceptionImpl(1, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "BAD_BOUNDARYPOINTS_ERR", null));
        }
        DocumentFragment documentFragment = this.extractContents();
        this.insertNode(node);
        node.appendChild(documentFragment);
        this.selectNode(node);
    }

    @Override
    public Range cloneRange() {
        if (this.fDetach) {
            throw new DOMException(11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null));
        }
        Range range = this.fDocument.createRange();
        range.setStart(this.fStartContainer, this.fStartOffset);
        range.setEnd(this.fEndContainer, this.fEndOffset);
        return range;
    }

    @Override
    public String toString() {
        int n;
        if (this.fDetach) {
            throw new DOMException(11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null));
        }
        Node node = this.fStartContainer;
        Node node2 = this.fEndContainer;
        StringBuffer stringBuffer = new StringBuffer();
        if (this.fStartContainer.getNodeType() == 3 || this.fStartContainer.getNodeType() == 4) {
            if (this.fStartContainer == this.fEndContainer) {
                stringBuffer.append(this.fStartContainer.getNodeValue().substring(this.fStartOffset, this.fEndOffset));
                return stringBuffer.toString();
            }
            stringBuffer.append(this.fStartContainer.getNodeValue().substring(this.fStartOffset));
            node = this.nextNode(node, true);
        } else {
            node = node.getFirstChild();
            if (this.fStartOffset > 0) {
                for (n = 0; n < this.fStartOffset && node != null; node = node.getNextSibling(), ++n) {
                }
            }
            if (node == null) {
                node = this.nextNode(this.fStartContainer, false);
            }
        }
        if (this.fEndContainer.getNodeType() != 3 && this.fEndContainer.getNodeType() != 4) {
            n = this.fEndOffset;
            for (node2 = this.fEndContainer.getFirstChild(); n > 0 && node2 != null; --n, node2 = node2.getNextSibling()) {
            }
            if (node2 == null) {
                node2 = this.nextNode(this.fEndContainer, false);
            }
        }
        while (node != node2 && node != null) {
            if (node.getNodeType() == 3 || node.getNodeType() == 4) {
                stringBuffer.append(node.getNodeValue());
            }
            node = this.nextNode(node, true);
        }
        if (this.fEndContainer.getNodeType() == 3 || this.fEndContainer.getNodeType() == 4) {
            stringBuffer.append(this.fEndContainer.getNodeValue().substring(0, this.fEndOffset));
        }
        return stringBuffer.toString();
    }

    @Override
    public void detach() {
        if (this.fDetach) {
            throw new DOMException(11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null));
        }
        this.fDetach = true;
        this.fDocument.removeRange(this);
    }

    void signalSplitData(Node node, Node node2, int n) {
        this.fSplitNode = node;
        this.fDocument.splitData(node, node2, n);
        this.fSplitNode = null;
    }

    void receiveSplitData(Node node, Node node2, int n) {
        if (node == null || node2 == null) {
            return;
        }
        if (this.fSplitNode == node) {
            return;
        }
        if (node == this.fStartContainer && this.fStartContainer.getNodeType() == 3 && this.fStartOffset > n) {
            this.fStartOffset -= n;
            this.fStartContainer = node2;
        }
        if (node == this.fEndContainer && this.fEndContainer.getNodeType() == 3 && this.fEndOffset > n) {
            this.fEndOffset -= n;
            this.fEndContainer = node2;
        }
    }

    void deleteData(CharacterData characterData, int n, int n2) {
        this.fDeleteNode = characterData;
        characterData.deleteData(n, n2);
        this.fDeleteNode = null;
    }

    void receiveDeletedText(CharacterDataImpl characterDataImpl, int n, int n2) {
        if (characterDataImpl == null) {
            return;
        }
        if (this.fDeleteNode == characterDataImpl) {
            return;
        }
        if (characterDataImpl == this.fStartContainer) {
            if (this.fStartOffset > n + n2) {
                this.fStartOffset = n + (this.fStartOffset - (n + n2));
            } else if (this.fStartOffset > n) {
                this.fStartOffset = n;
            }
        }
        if (characterDataImpl == this.fEndContainer) {
            if (this.fEndOffset > n + n2) {
                this.fEndOffset = n + (this.fEndOffset - (n + n2));
            } else if (this.fEndOffset > n) {
                this.fEndOffset = n;
            }
        }
    }

    void insertData(CharacterData characterData, int n, String string) {
        this.fInsertNode = characterData;
        characterData.insertData(n, string);
        this.fInsertNode = null;
    }

    void receiveInsertedText(CharacterDataImpl characterDataImpl, int n, int n2) {
        if (characterDataImpl == null) {
            return;
        }
        if (this.fInsertNode == characterDataImpl) {
            return;
        }
        if (characterDataImpl == this.fStartContainer && n < this.fStartOffset) {
            this.fStartOffset += n2;
        }
        if (characterDataImpl == this.fEndContainer && n < this.fEndOffset) {
            this.fEndOffset += n2;
        }
    }

    void receiveReplacedText(CharacterDataImpl characterDataImpl) {
        if (characterDataImpl == null) {
            return;
        }
        if (characterDataImpl == this.fStartContainer) {
            this.fStartOffset = 0;
        }
        if (characterDataImpl == this.fEndContainer) {
            this.fEndOffset = 0;
        }
    }

    public void insertedNodeFromDOM(Node node) {
        int n;
        if (node == null) {
            return;
        }
        if (this.fInsertNode == node) {
            return;
        }
        if (this.fInsertedFromRange) {
            return;
        }
        Node node2 = node.getParentNode();
        if (node2 == this.fStartContainer && (n = this.indexOf(node, this.fStartContainer)) < this.fStartOffset) {
            ++this.fStartOffset;
        }
        if (node2 == this.fEndContainer && (n = this.indexOf(node, this.fEndContainer)) < this.fEndOffset) {
            ++this.fEndOffset;
        }
    }

    Node removeChild(Node node, Node node2) {
        this.fRemoveChild = node2;
        Node node3 = node.removeChild(node2);
        this.fRemoveChild = null;
        return node3;
    }

    void removeNode(Node node) {
        int n;
        if (node == null) {
            return;
        }
        if (this.fRemoveChild == node) {
            return;
        }
        Node node2 = node.getParentNode();
        if (node2 == this.fStartContainer && (n = this.indexOf(node, this.fStartContainer)) < this.fStartOffset) {
            --this.fStartOffset;
        }
        if (node2 == this.fEndContainer && (n = this.indexOf(node, this.fEndContainer)) < this.fEndOffset) {
            --this.fEndOffset;
        }
        if (node2 != this.fStartContainer || node2 != this.fEndContainer) {
            if (this.isAncestorOf(node, this.fStartContainer)) {
                this.fStartContainer = node2;
                this.fStartOffset = this.indexOf(node, node2);
            }
            if (this.isAncestorOf(node, this.fEndContainer)) {
                this.fEndContainer = node2;
                this.fEndOffset = this.indexOf(node, node2);
            }
        }
    }

    private DocumentFragment traverseContents(int n) throws DOMException {
        int n2;
        Node node;
        Node node2;
        if (this.fStartContainer == null || this.fEndContainer == null) {
            return null;
        }
        if (this.fDetach) {
            throw new DOMException(11, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null));
        }
        if (this.fStartContainer == this.fEndContainer) {
            return this.traverseSameContainer(n);
        }
        int n3 = 0;
        Node node3 = this.fEndContainer;
        for (node2 = node3.getParentNode(); node2 != null; node2 = node2.getParentNode()) {
            if (node2 == this.fStartContainer) {
                return this.traverseCommonStartContainer(node3, n);
            }
            ++n3;
            node3 = node2;
        }
        int n4 = 0;
        node2 = this.fStartContainer;
        for (node = node2.getParentNode(); node != null; node = node.getParentNode()) {
            if (node == this.fEndContainer) {
                return this.traverseCommonEndContainer(node2, n);
            }
            ++n4;
            node2 = node;
        }
        node = this.fStartContainer;
        for (n2 = n4 - n3; n2 > 0; --n2) {
            node = node.getParentNode();
        }
        Node node4 = this.fEndContainer;
        while (n2 < 0) {
            node4 = node4.getParentNode();
            ++n2;
        }
        Node node5 = node.getParentNode();
        for (Node node6 = node4.getParentNode(); node5 != node6; node5 = node5.getParentNode(), node6 = node6.getParentNode()) {
            node = node5;
            node4 = node6;
        }
        return this.traverseCommonAncestors(node, node4, n);
    }

    private DocumentFragment traverseSameContainer(int n) {
        DocumentFragment documentFragment = null;
        if (n != 3) {
            documentFragment = this.fDocument.createDocumentFragment();
        }
        if (this.fStartOffset == this.fEndOffset) {
            return documentFragment;
        }
        short s = this.fStartContainer.getNodeType();
        if (s == 3 || s == 4 || s == 8 || s == 7) {
            String string = this.fStartContainer.getNodeValue();
            String string2 = string.substring(this.fStartOffset, this.fEndOffset);
            if (n != 2) {
                ((CharacterDataImpl)this.fStartContainer).deleteData(this.fStartOffset, this.fEndOffset - this.fStartOffset);
                this.collapse(true);
            }
            if (n == 3) {
                return null;
            }
            if (s == 3) {
                documentFragment.appendChild(this.fDocument.createTextNode(string2));
            } else if (s == 4) {
                documentFragment.appendChild(this.fDocument.createCDATASection(string2));
            } else if (s == 8) {
                documentFragment.appendChild(this.fDocument.createComment(string2));
            } else {
                documentFragment.appendChild(this.fDocument.createProcessingInstruction(this.fStartContainer.getNodeName(), string2));
            }
            return documentFragment;
        }
        Node node = this.getSelectedNode(this.fStartContainer, this.fStartOffset);
        for (int i = this.fEndOffset - this.fStartOffset; i > 0; --i) {
            Node node2 = node.getNextSibling();
            Node node3 = this.traverseFullySelected(node, n);
            if (documentFragment != null) {
                documentFragment.appendChild(node3);
            }
            node = node2;
        }
        if (n != 2) {
            this.collapse(true);
        }
        return documentFragment;
    }

    private DocumentFragment traverseCommonStartContainer(Node node, int n) {
        int n2;
        int n3;
        DocumentFragment documentFragment = null;
        if (n != 3) {
            documentFragment = this.fDocument.createDocumentFragment();
        }
        Node node2 = this.traverseRightBoundary(node, n);
        if (documentFragment != null) {
            documentFragment.appendChild(node2);
        }
        if ((n3 = (n2 = this.indexOf(node, this.fStartContainer)) - this.fStartOffset) <= 0) {
            if (n != 2) {
                this.setEndBefore(node);
                this.collapse(false);
            }
            return documentFragment;
        }
        node2 = node.getPreviousSibling();
        while (n3 > 0) {
            Node node3 = node2.getPreviousSibling();
            Node node4 = this.traverseFullySelected(node2, n);
            if (documentFragment != null) {
                documentFragment.insertBefore(node4, documentFragment.getFirstChild());
            }
            --n3;
            node2 = node3;
        }
        if (n != 2) {
            this.setEndBefore(node);
            this.collapse(false);
        }
        return documentFragment;
    }

    private DocumentFragment traverseCommonEndContainer(Node node, int n) {
        DocumentFragment documentFragment = null;
        if (n != 3) {
            documentFragment = this.fDocument.createDocumentFragment();
        }
        Node node2 = this.traverseLeftBoundary(node, n);
        if (documentFragment != null) {
            documentFragment.appendChild(node2);
        }
        int n2 = this.indexOf(node, this.fEndContainer);
        node2 = node.getNextSibling();
        for (int i = this.fEndOffset - ++n2; i > 0; --i) {
            Node node3 = node2.getNextSibling();
            Node node4 = this.traverseFullySelected(node2, n);
            if (documentFragment != null) {
                documentFragment.appendChild(node4);
            }
            node2 = node3;
        }
        if (n != 2) {
            this.setStartAfter(node);
            this.collapse(true);
        }
        return documentFragment;
    }

    private DocumentFragment traverseCommonAncestors(Node node, Node node2, int n) {
        DocumentFragment documentFragment = null;
        if (n != 3) {
            documentFragment = this.fDocument.createDocumentFragment();
        }
        Node node3 = this.traverseLeftBoundary(node, n);
        if (documentFragment != null) {
            documentFragment.appendChild(node3);
        }
        Node node4 = node.getParentNode();
        int n2 = this.indexOf(node, node4);
        int n3 = this.indexOf(node2, node4);
        Node node5 = node.getNextSibling();
        for (int i = n3 - ++n2; i > 0; --i) {
            Node node6 = node5.getNextSibling();
            node3 = this.traverseFullySelected(node5, n);
            if (documentFragment != null) {
                documentFragment.appendChild(node3);
            }
            node5 = node6;
        }
        node3 = this.traverseRightBoundary(node2, n);
        if (documentFragment != null) {
            documentFragment.appendChild(node3);
        }
        if (n != 2) {
            this.setStartAfter(node);
            this.collapse(true);
        }
        return documentFragment;
    }

    private Node traverseRightBoundary(Node node, int n) {
        boolean bl;
        Node node2 = this.getSelectedNode(this.fEndContainer, this.fEndOffset - 1);
        boolean bl2 = bl = node2 != this.fEndContainer;
        if (node2 == node) {
            return this.traverseNode(node2, bl, false, n);
        }
        Node node3 = node2.getParentNode();
        Node node4 = this.traverseNode(node3, false, false, n);
        while (node3 != null) {
            Node node5;
            while (node2 != null) {
                node5 = node2.getPreviousSibling();
                Node node6 = this.traverseNode(node2, bl, false, n);
                if (n != 3) {
                    node4.insertBefore(node6, node4.getFirstChild());
                }
                bl = true;
                node2 = node5;
            }
            if (node3 == node) {
                return node4;
            }
            node2 = node3.getPreviousSibling();
            node3 = node3.getParentNode();
            node5 = this.traverseNode(node3, false, false, n);
            if (n != 3) {
                node5.appendChild(node4);
            }
            node4 = node5;
        }
        return null;
    }

    private Node traverseLeftBoundary(Node node, int n) {
        boolean bl;
        Node node2 = this.getSelectedNode(this.getStartContainer(), this.getStartOffset());
        boolean bl2 = bl = node2 != this.getStartContainer();
        if (node2 == node) {
            return this.traverseNode(node2, bl, true, n);
        }
        Node node3 = node2.getParentNode();
        Node node4 = this.traverseNode(node3, false, true, n);
        while (node3 != null) {
            Node node5;
            while (node2 != null) {
                node5 = node2.getNextSibling();
                Node node6 = this.traverseNode(node2, bl, true, n);
                if (n != 3) {
                    node4.appendChild(node6);
                }
                bl = true;
                node2 = node5;
            }
            if (node3 == node) {
                return node4;
            }
            node2 = node3.getNextSibling();
            node3 = node3.getParentNode();
            node5 = this.traverseNode(node3, false, true, n);
            if (n != 3) {
                node5.appendChild(node4);
            }
            node4 = node5;
        }
        return null;
    }

    private Node traverseNode(Node node, boolean bl, boolean bl2, int n) {
        if (bl) {
            return this.traverseFullySelected(node, n);
        }
        short s = node.getNodeType();
        if (s == 3 || s == 4 || s == 8 || s == 7) {
            return this.traverseCharacterDataNode(node, bl2, n);
        }
        return this.traversePartiallySelected(node, n);
    }

    private Node traverseFullySelected(Node node, int n) {
        switch (n) {
            case 2: {
                return node.cloneNode(true);
            }
            case 1: {
                if (node.getNodeType() == 10) {
                    throw new DOMException(3, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "HIERARCHY_REQUEST_ERR", null));
                }
                return node;
            }
            case 3: {
                node.getParentNode().removeChild(node);
                return null;
            }
        }
        return null;
    }

    private Node traversePartiallySelected(Node node, int n) {
        switch (n) {
            case 3: {
                return null;
            }
            case 1: 
            case 2: {
                return node.cloneNode(false);
            }
        }
        return null;
    }

    private Node traverseCharacterDataNode(Node node, boolean bl, int n) {
        String string;
        String string2;
        int n2;
        String string3 = node.getNodeValue();
        if (bl) {
            n2 = this.getStartOffset();
            string2 = string3.substring(n2);
            string = string3.substring(0, n2);
        } else {
            n2 = this.getEndOffset();
            string2 = string3.substring(0, n2);
            string = string3.substring(n2);
        }
        if (n != 2) {
            node.setNodeValue(string);
        }
        if (n == 3) {
            return null;
        }
        Node node2 = node.cloneNode(false);
        node2.setNodeValue(string2);
        return node2;
    }

    void checkIndex(Node node, int n) throws DOMException {
        if (n < 0) {
            throw new DOMException(1, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INDEX_SIZE_ERR", null));
        }
        short s = node.getNodeType();
        if (s == 3 || s == 4 || s == 8 || s == 7 ? n > node.getNodeValue().length() : n > node.getChildNodes().getLength()) {
            throw new DOMException(1, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INDEX_SIZE_ERR", null));
        }
    }

    private Node getRootContainer(Node node) {
        if (node == null) {
            return null;
        }
        while (node.getParentNode() != null) {
            node = node.getParentNode();
        }
        return node;
    }

    private boolean isLegalContainer(Node node) {
        if (node == null) {
            return false;
        }
        while (node != null) {
            switch (node.getNodeType()) {
                case 6: 
                case 10: 
                case 12: {
                    return false;
                }
            }
            node = node.getParentNode();
        }
        return true;
    }

    private boolean hasLegalRootContainer(Node node) {
        if (node == null) {
            return false;
        }
        Node node2 = this.getRootContainer(node);
        switch (node2.getNodeType()) {
            case 2: 
            case 9: 
            case 11: {
                return true;
            }
        }
        return false;
    }

    private boolean isLegalContainedNode(Node node) {
        if (node == null) {
            return false;
        }
        switch (node.getNodeType()) {
            case 2: 
            case 6: 
            case 9: 
            case 11: 
            case 12: {
                return false;
            }
        }
        return true;
    }

    Node nextNode(Node node, boolean bl) {
        Node node2;
        if (node == null) {
            return null;
        }
        if (bl && (node2 = node.getFirstChild()) != null) {
            return node2;
        }
        node2 = node.getNextSibling();
        if (node2 != null) {
            return node2;
        }
        for (Node node3 = node.getParentNode(); node3 != null && node3 != this.fDocument; node3 = node3.getParentNode()) {
            node2 = node3.getNextSibling();
            if (node2 == null) continue;
            return node2;
        }
        return null;
    }

    boolean isAncestorOf(Node node, Node node2) {
        for (Node node3 = node2; node3 != null; node3 = node3.getParentNode()) {
            if (node3 != node) continue;
            return true;
        }
        return false;
    }

    int indexOf(Node node, Node node2) {
        if (node.getParentNode() != node2) {
            return -1;
        }
        int n = 0;
        for (Node node3 = node2.getFirstChild(); node3 != node; node3 = node3.getNextSibling()) {
            ++n;
        }
        return n;
    }

    private Node getSelectedNode(Node node, int n) {
        Node node2;
        if (node.getNodeType() == 3) {
            return node;
        }
        if (n < 0) {
            return node;
        }
        for (node2 = node.getFirstChild(); node2 != null && n > 0; --n, node2 = node2.getNextSibling()) {
        }
        if (node2 != null) {
            return node2;
        }
        return node;
    }
}

