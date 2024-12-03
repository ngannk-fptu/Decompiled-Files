/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.dom;

import org.apache.batik.dom.AbstractChildNode;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.dom.AbstractParentNode;
import org.w3c.dom.CharacterData;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

public abstract class AbstractCharacterData
extends AbstractChildNode
implements CharacterData {
    protected String nodeValue = "";

    @Override
    public String getNodeValue() throws DOMException {
        return this.nodeValue;
    }

    @Override
    public void setNodeValue(String nodeValue) throws DOMException {
        if (this.isReadonly()) {
            throw this.createDOMException((short)7, "readonly.node", new Object[]{(int)this.getNodeType(), this.getNodeName()});
        }
        String val = this.nodeValue;
        this.nodeValue = nodeValue == null ? "" : nodeValue;
        this.fireDOMCharacterDataModifiedEvent(val, this.nodeValue);
        if (this.getParentNode() != null) {
            ((AbstractParentNode)this.getParentNode()).fireDOMSubtreeModifiedEvent();
        }
    }

    @Override
    public String getData() throws DOMException {
        return this.getNodeValue();
    }

    @Override
    public void setData(String data) throws DOMException {
        this.setNodeValue(data);
    }

    @Override
    public int getLength() {
        return this.nodeValue.length();
    }

    @Override
    public String substringData(int offset, int count) throws DOMException {
        this.checkOffsetCount(offset, count);
        String v = this.getNodeValue();
        return v.substring(offset, Math.min(v.length(), offset + count));
    }

    @Override
    public void appendData(String arg) throws DOMException {
        if (this.isReadonly()) {
            throw this.createDOMException((short)7, "readonly.node", new Object[]{(int)this.getNodeType(), this.getNodeName()});
        }
        this.setNodeValue(this.getNodeValue() + (arg == null ? "" : arg));
    }

    @Override
    public void insertData(int offset, String arg) throws DOMException {
        if (this.isReadonly()) {
            throw this.createDOMException((short)7, "readonly.node", new Object[]{(int)this.getNodeType(), this.getNodeName()});
        }
        if (offset < 0 || offset > this.getLength()) {
            throw this.createDOMException((short)1, "offset", new Object[]{offset});
        }
        String v = this.getNodeValue();
        this.setNodeValue(v.substring(0, offset) + arg + v.substring(offset, v.length()));
    }

    @Override
    public void deleteData(int offset, int count) throws DOMException {
        if (this.isReadonly()) {
            throw this.createDOMException((short)7, "readonly.node", new Object[]{(int)this.getNodeType(), this.getNodeName()});
        }
        this.checkOffsetCount(offset, count);
        String v = this.getNodeValue();
        this.setNodeValue(v.substring(0, offset) + v.substring(Math.min(v.length(), offset + count), v.length()));
    }

    @Override
    public void replaceData(int offset, int count, String arg) throws DOMException {
        if (this.isReadonly()) {
            throw this.createDOMException((short)7, "readonly.node", new Object[]{(int)this.getNodeType(), this.getNodeName()});
        }
        this.checkOffsetCount(offset, count);
        String v = this.getNodeValue();
        this.setNodeValue(v.substring(0, offset) + arg + v.substring(Math.min(v.length(), offset + count), v.length()));
    }

    protected void checkOffsetCount(int offset, int count) throws DOMException {
        if (offset < 0 || offset >= this.getLength()) {
            throw this.createDOMException((short)1, "offset", new Object[]{offset});
        }
        if (count < 0) {
            throw this.createDOMException((short)1, "negative.count", new Object[]{count});
        }
    }

    @Override
    protected Node export(Node n, AbstractDocument d) {
        super.export(n, d);
        AbstractCharacterData cd = (AbstractCharacterData)n;
        cd.nodeValue = this.nodeValue;
        return n;
    }

    @Override
    protected Node deepExport(Node n, AbstractDocument d) {
        super.deepExport(n, d);
        AbstractCharacterData cd = (AbstractCharacterData)n;
        cd.nodeValue = this.nodeValue;
        return n;
    }

    @Override
    protected Node copyInto(Node n) {
        super.copyInto(n);
        AbstractCharacterData cd = (AbstractCharacterData)n;
        cd.nodeValue = this.nodeValue;
        return n;
    }

    @Override
    protected Node deepCopyInto(Node n) {
        super.deepCopyInto(n);
        AbstractCharacterData cd = (AbstractCharacterData)n;
        cd.nodeValue = this.nodeValue;
        return n;
    }
}

