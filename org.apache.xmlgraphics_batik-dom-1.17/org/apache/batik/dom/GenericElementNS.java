/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.dom;

import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.dom.AbstractElementNS;
import org.w3c.dom.Node;

public class GenericElementNS
extends AbstractElementNS {
    protected String nodeName;
    protected boolean readonly;

    protected GenericElementNS() {
    }

    public GenericElementNS(String nsURI, String name, AbstractDocument owner) {
        super(nsURI, name, owner);
        this.nodeName = name;
    }

    @Override
    public void setNodeName(String v) {
        this.nodeName = v;
    }

    @Override
    public String getNodeName() {
        return this.nodeName;
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
    protected Node export(Node n, AbstractDocument d) {
        GenericElementNS ge = (GenericElementNS)super.export(n, d);
        ge.nodeName = this.nodeName;
        return n;
    }

    @Override
    protected Node deepExport(Node n, AbstractDocument d) {
        GenericElementNS ge = (GenericElementNS)super.deepExport(n, d);
        ge.nodeName = this.nodeName;
        return n;
    }

    @Override
    protected Node copyInto(Node n) {
        GenericElementNS ge = (GenericElementNS)super.copyInto(n);
        ge.nodeName = this.nodeName;
        return n;
    }

    @Override
    protected Node deepCopyInto(Node n) {
        GenericElementNS ge = (GenericElementNS)super.deepCopyInto(n);
        ge.nodeName = this.nodeName;
        return n;
    }

    @Override
    protected Node newNode() {
        return new GenericElementNS();
    }
}

