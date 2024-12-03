/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.dom;

import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.dom.AbstractNode;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.Notation;

public abstract class AbstractNotation
extends AbstractNode
implements Notation {
    protected String nodeName;
    protected String publicId;
    protected String systemId;

    @Override
    public short getNodeType() {
        return 12;
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
    public String getPublicId() {
        return this.publicId;
    }

    public void setPublicId(String id) {
        this.publicId = id;
    }

    @Override
    public String getSystemId() {
        return this.systemId;
    }

    public void setSystemId(String id) {
        this.systemId = id;
    }

    @Override
    public void setTextContent(String s) throws DOMException {
    }

    @Override
    protected Node export(Node n, AbstractDocument d) {
        super.export(n, d);
        AbstractNotation an = (AbstractNotation)n;
        an.nodeName = this.nodeName;
        an.publicId = this.publicId;
        an.systemId = this.systemId;
        return n;
    }

    @Override
    protected Node deepExport(Node n, AbstractDocument d) {
        super.deepExport(n, d);
        AbstractNotation an = (AbstractNotation)n;
        an.nodeName = this.nodeName;
        an.publicId = this.publicId;
        an.systemId = this.systemId;
        return n;
    }

    @Override
    protected Node copyInto(Node n) {
        super.copyInto(n);
        AbstractNotation an = (AbstractNotation)n;
        an.nodeName = this.nodeName;
        an.publicId = this.publicId;
        an.systemId = this.systemId;
        return n;
    }

    @Override
    protected Node deepCopyInto(Node n) {
        super.deepCopyInto(n);
        AbstractNotation an = (AbstractNotation)n;
        an.nodeName = this.nodeName;
        an.publicId = this.publicId;
        an.systemId = this.systemId;
        return n;
    }
}

