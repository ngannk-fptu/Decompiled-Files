/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.dom;

import org.apache.xerces.dom.ChildNode;
import org.apache.xerces.dom.CoreDocumentImpl;
import org.apache.xerces.dom.DOMMessageFormatter;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class CharacterDataImpl
extends ChildNode {
    static final long serialVersionUID = 7931170150428474230L;
    protected String data;
    private static final transient NodeList singletonNodeList = new NodeList(){

        @Override
        public Node item(int n) {
            return null;
        }

        @Override
        public int getLength() {
            return 0;
        }
    };

    public CharacterDataImpl() {
    }

    protected CharacterDataImpl(CoreDocumentImpl coreDocumentImpl, String string) {
        super(coreDocumentImpl);
        this.data = string;
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
        return this.data;
    }

    protected void setNodeValueInternal(String string) {
        this.setNodeValueInternal(string, false);
    }

    protected void setNodeValueInternal(String string, boolean bl) {
        CoreDocumentImpl coreDocumentImpl = this.ownerDocument();
        if (coreDocumentImpl.errorChecking && this.isReadOnly()) {
            String string2 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null);
            throw new DOMException(7, string2);
        }
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        String string3 = this.data;
        coreDocumentImpl.modifyingCharacterData(this, bl);
        this.data = string;
        coreDocumentImpl.modifiedCharacterData(this, string3, string, bl);
    }

    @Override
    public void setNodeValue(String string) {
        this.setNodeValueInternal(string);
        this.ownerDocument().replacedText(this);
    }

    public String getData() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.data;
    }

    @Override
    public int getLength() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.data.length();
    }

    public void appendData(String string) {
        if (this.isReadOnly()) {
            String string2 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null);
            throw new DOMException(7, string2);
        }
        if (string == null) {
            return;
        }
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        this.setNodeValue(this.data + string);
    }

    public void deleteData(int n, int n2) throws DOMException {
        this.internalDeleteData(n, n2, false);
    }

    void internalDeleteData(int n, int n2, boolean bl) throws DOMException {
        CoreDocumentImpl coreDocumentImpl = this.ownerDocument();
        if (coreDocumentImpl.errorChecking) {
            if (this.isReadOnly()) {
                String string = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null);
                throw new DOMException(7, string);
            }
            if (n2 < 0) {
                String string = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INDEX_SIZE_ERR", null);
                throw new DOMException(1, string);
            }
        }
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        int n3 = Math.max(this.data.length() - n2 - n, 0);
        try {
            String string = this.data.substring(0, n) + (n3 > 0 ? this.data.substring(n + n2, n + n2 + n3) : "");
            this.setNodeValueInternal(string, bl);
            coreDocumentImpl.deletedText(this, n, n2);
        }
        catch (StringIndexOutOfBoundsException stringIndexOutOfBoundsException) {
            String string = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INDEX_SIZE_ERR", null);
            throw new DOMException(1, string);
        }
    }

    public void insertData(int n, String string) throws DOMException {
        this.internalInsertData(n, string, false);
    }

    void internalInsertData(int n, String string, boolean bl) throws DOMException {
        CoreDocumentImpl coreDocumentImpl = this.ownerDocument();
        if (coreDocumentImpl.errorChecking && this.isReadOnly()) {
            String string2 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null);
            throw new DOMException(7, string2);
        }
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        try {
            String string3 = new StringBuffer(this.data).insert(n, string).toString();
            this.setNodeValueInternal(string3, bl);
            coreDocumentImpl.insertedText(this, n, string.length());
        }
        catch (StringIndexOutOfBoundsException stringIndexOutOfBoundsException) {
            String string4 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INDEX_SIZE_ERR", null);
            throw new DOMException(1, string4);
        }
    }

    public void replaceData(int n, int n2, String string) throws DOMException {
        CoreDocumentImpl coreDocumentImpl = this.ownerDocument();
        if (coreDocumentImpl.errorChecking && this.isReadOnly()) {
            String string2 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null);
            throw new DOMException(7, string2);
        }
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        coreDocumentImpl.replacingData(this);
        String string3 = this.data;
        this.internalDeleteData(n, n2, true);
        this.internalInsertData(n, string, true);
        coreDocumentImpl.replacedCharacterData(this, string3, this.data);
    }

    public void setData(String string) throws DOMException {
        this.setNodeValue(string);
    }

    public String substringData(int n, int n2) throws DOMException {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        int n3 = this.data.length();
        if (n2 < 0 || n < 0 || n > n3 - 1) {
            String string = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INDEX_SIZE_ERR", null);
            throw new DOMException(1, string);
        }
        int n4 = Math.min(n + n2, n3);
        return this.data.substring(n, n4);
    }
}

