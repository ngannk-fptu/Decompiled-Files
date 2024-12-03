/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.tidy;

import org.w3c.dom.CharacterData;
import org.w3c.dom.DOMException;
import org.w3c.tidy.DOMNodeImpl;
import org.w3c.tidy.Node;
import org.w3c.tidy.TidyUtils;

public class DOMCharacterDataImpl
extends DOMNodeImpl
implements CharacterData {
    protected DOMCharacterDataImpl(Node adaptee) {
        super(adaptee);
    }

    public String getData() throws DOMException {
        return this.getNodeValue();
    }

    public int getLength() {
        int len = 0;
        if (this.adaptee.textarray != null && this.adaptee.start < this.adaptee.end) {
            len = this.adaptee.end - this.adaptee.start;
        }
        return len;
    }

    public String substringData(int offset, int count) throws DOMException {
        String value = null;
        if (count < 0) {
            throw new DOMException(1, "Invalid length");
        }
        if (this.adaptee.textarray != null && this.adaptee.start < this.adaptee.end) {
            if (this.adaptee.start + offset >= this.adaptee.end) {
                throw new DOMException(1, "Invalid offset");
            }
            int len = count;
            if (this.adaptee.start + offset + len - 1 >= this.adaptee.end) {
                len = this.adaptee.end - this.adaptee.start - offset;
            }
            value = TidyUtils.getString(this.adaptee.textarray, this.adaptee.start + offset, len);
        }
        return value;
    }

    public void setData(String data) throws DOMException {
        throw new DOMException(7, "Not supported");
    }

    public void appendData(String arg) throws DOMException {
        throw new DOMException(7, "Not supported");
    }

    public void insertData(int offset, String arg) throws DOMException {
        throw new DOMException(7, "Not supported");
    }

    public void deleteData(int offset, int count) throws DOMException {
        throw new DOMException(7, "Not supported");
    }

    public void replaceData(int offset, int count, String arg) throws DOMException {
        throw new DOMException(7, "Not supported");
    }
}

