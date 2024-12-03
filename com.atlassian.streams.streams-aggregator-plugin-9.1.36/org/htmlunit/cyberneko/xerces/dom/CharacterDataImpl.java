/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.xerces.dom;

import org.htmlunit.cyberneko.xerces.dom.ChildNode;
import org.htmlunit.cyberneko.xerces.dom.CoreDocumentImpl;
import org.htmlunit.cyberneko.xerces.dom.DOMMessageFormatter;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class CharacterDataImpl
extends ChildNode {
    protected String data_;
    private static final NodeList singletonNodeList = new NodeList(){

        @Override
        public Node item(int index) {
            return null;
        }

        @Override
        public int getLength() {
            return 0;
        }
    };

    protected CharacterDataImpl(CoreDocumentImpl ownerDocument, String data) {
        super(ownerDocument);
        this.data_ = data;
    }

    @Override
    public NodeList getChildNodes() {
        return singletonNodeList;
    }

    @Override
    public String getNodeValue() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.data_;
    }

    protected void setNodeValueInternal(String value) {
        this.setNodeValueInternal(value, false);
    }

    protected void setNodeValueInternal(String value, boolean replace) {
        CoreDocumentImpl ownerDocument = this.ownerDocument();
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        String oldvalue = this.data_;
        ownerDocument.modifyingCharacterData(this, replace);
        this.data_ = value;
        ownerDocument.modifiedCharacterData(this, oldvalue, value, replace);
    }

    @Override
    public void setNodeValue(String value) {
        this.setNodeValueInternal(value);
        this.ownerDocument().replacedText(this);
    }

    public String getData() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.data_;
    }

    @Override
    public int getLength() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.data_.length();
    }

    public void appendData(String data) {
        if (data == null) {
            return;
        }
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        this.setNodeValue(this.data_ + data);
    }

    public void deleteData(int offset, int count) throws DOMException {
        this.internalDeleteData(offset, count, false);
    }

    void internalDeleteData(int offset, int count, boolean replace) throws DOMException {
        CoreDocumentImpl ownerDocument = this.ownerDocument();
        if (ownerDocument.errorChecking && count < 0) {
            String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INDEX_SIZE_ERR", null);
            throw new DOMException(1, msg);
        }
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        int tailLength = Math.max(this.data_.length() - count - offset, 0);
        try {
            String value = this.data_.substring(0, offset) + (tailLength > 0 ? this.data_.substring(offset + count, offset + count + tailLength) : "");
            this.setNodeValueInternal(value, replace);
            ownerDocument.deletedText(this, offset, count);
        }
        catch (StringIndexOutOfBoundsException e) {
            String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INDEX_SIZE_ERR", null);
            throw new DOMException(1, msg);
        }
    }

    public void insertData(int offset, String data) throws DOMException {
        this.internalInsertData(offset, data, false);
    }

    void internalInsertData(int offset, String data, boolean replace) throws DOMException {
        CoreDocumentImpl ownerDocument = this.ownerDocument();
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        try {
            String value = new StringBuilder(this.data_).insert(offset, data).toString();
            this.setNodeValueInternal(value, replace);
            ownerDocument.insertedText(this, offset, data.length());
        }
        catch (StringIndexOutOfBoundsException e) {
            String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INDEX_SIZE_ERR", null);
            throw new DOMException(1, msg);
        }
    }

    public void replaceData(int offset, int count, String data) throws DOMException {
        CoreDocumentImpl ownerDocument = this.ownerDocument();
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        ownerDocument.replacingData(this);
        String oldvalue = this.data_;
        this.internalDeleteData(offset, count, true);
        this.internalInsertData(offset, data, true);
        ownerDocument.replacedCharacterData(this, oldvalue, this.data_);
    }

    public void setData(String value) throws DOMException {
        this.setNodeValue(value);
    }

    public String substringData(int offset, int count) throws DOMException {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        int length = this.data_.length();
        if (count < 0 || offset < 0 || offset > length - 1) {
            String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INDEX_SIZE_ERR", null);
            throw new DOMException(1, msg);
        }
        int tailIndex = Math.min(offset + count, length);
        return this.data_.substring(offset, tailIndex);
    }
}

