/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.dom;

import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.dom.AbstractElement;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

public class GenericElement
extends AbstractElement {
    protected String nodeName;
    protected boolean readonly;

    protected GenericElement() {
    }

    public GenericElement(String name, AbstractDocument owner) throws DOMException {
        super(name, owner);
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
        super.export(n, d);
        GenericElement ge = (GenericElement)n;
        ge.nodeName = this.nodeName;
        return n;
    }

    @Override
    protected Node deepExport(Node n, AbstractDocument d) {
        super.deepExport(n, d);
        GenericElement ge = (GenericElement)n;
        ge.nodeName = this.nodeName;
        return n;
    }

    @Override
    protected Node copyInto(Node n) {
        GenericElement ge = (GenericElement)super.copyInto(n);
        ge.nodeName = this.nodeName;
        return n;
    }

    @Override
    protected Node deepCopyInto(Node n) {
        GenericElement ge = (GenericElement)super.deepCopyInto(n);
        ge.nodeName = this.nodeName;
        return n;
    }

    @Override
    protected Node newNode() {
        return new GenericElement();
    }
}

