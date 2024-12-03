/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.dom;

import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.dom.AbstractProcessingInstruction;
import org.w3c.dom.Node;

public class GenericProcessingInstruction
extends AbstractProcessingInstruction {
    protected String target;
    protected boolean readonly;

    protected GenericProcessingInstruction() {
    }

    public GenericProcessingInstruction(String target, String data, AbstractDocument owner) {
        this.ownerDocument = owner;
        this.setTarget(target);
        this.setData(data);
    }

    @Override
    public void setNodeName(String v) {
        this.setTarget(v);
    }

    @Override
    public boolean isReadonly() {
        return this.readonly;
    }

    @Override
    public void setReadonly(boolean v) {
        this.readonly = v;
    }

    @Override
    public String getTarget() {
        return this.target;
    }

    public void setTarget(String v) {
        this.target = v;
    }

    @Override
    protected Node export(Node n, AbstractDocument d) {
        GenericProcessingInstruction p = (GenericProcessingInstruction)super.export(n, d);
        p.setTarget(this.getTarget());
        return p;
    }

    @Override
    protected Node deepExport(Node n, AbstractDocument d) {
        GenericProcessingInstruction p = (GenericProcessingInstruction)super.deepExport(n, d);
        p.setTarget(this.getTarget());
        return p;
    }

    @Override
    protected Node copyInto(Node n) {
        GenericProcessingInstruction p = (GenericProcessingInstruction)super.copyInto(n);
        p.setTarget(this.getTarget());
        return p;
    }

    @Override
    protected Node deepCopyInto(Node n) {
        GenericProcessingInstruction p = (GenericProcessingInstruction)super.deepCopyInto(n);
        p.setTarget(this.getTarget());
        return p;
    }

    @Override
    protected Node newNode() {
        return new GenericProcessingInstruction();
    }
}

