/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.dom;

import org.apache.batik.dom.AbstractChildNode;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.dom.AbstractParentNode;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;

public abstract class AbstractProcessingInstruction
extends AbstractChildNode
implements ProcessingInstruction {
    protected String data;

    @Override
    public String getNodeName() {
        return this.getTarget();
    }

    @Override
    public short getNodeType() {
        return 7;
    }

    @Override
    public String getNodeValue() throws DOMException {
        return this.getData();
    }

    @Override
    public void setNodeValue(String nodeValue) throws DOMException {
        this.setData(nodeValue);
    }

    @Override
    public String getData() {
        return this.data;
    }

    @Override
    public void setData(String data) throws DOMException {
        if (this.isReadonly()) {
            throw this.createDOMException((short)7, "readonly.node", new Object[]{(int)this.getNodeType(), this.getNodeName()});
        }
        String val = this.data;
        this.data = data;
        this.fireDOMCharacterDataModifiedEvent(val, this.data);
        if (this.getParentNode() != null) {
            ((AbstractParentNode)this.getParentNode()).fireDOMSubtreeModifiedEvent();
        }
    }

    @Override
    public String getTextContent() {
        return this.getNodeValue();
    }

    @Override
    protected Node export(Node n, AbstractDocument d) {
        AbstractProcessingInstruction p = (AbstractProcessingInstruction)super.export(n, d);
        p.data = this.data;
        return p;
    }

    @Override
    protected Node deepExport(Node n, AbstractDocument d) {
        AbstractProcessingInstruction p = (AbstractProcessingInstruction)super.deepExport(n, d);
        p.data = this.data;
        return p;
    }

    @Override
    protected Node copyInto(Node n) {
        AbstractProcessingInstruction p = (AbstractProcessingInstruction)super.copyInto(n);
        p.data = this.data;
        return p;
    }

    @Override
    protected Node deepCopyInto(Node n) {
        AbstractProcessingInstruction p = (AbstractProcessingInstruction)super.deepCopyInto(n);
        p.data = this.data;
        return p;
    }
}

