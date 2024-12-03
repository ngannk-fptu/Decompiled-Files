/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.dom;

import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.dom.AbstractParentNode;
import org.w3c.dom.Entity;
import org.w3c.dom.Node;

public abstract class AbstractEntity
extends AbstractParentNode
implements Entity {
    protected String nodeName;
    protected String publicId;
    protected String systemId;

    @Override
    public short getNodeType() {
        return 6;
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
    public String getNotationName() {
        return this.getNodeName();
    }

    public void setNotationName(String name) {
        this.setNodeName(name);
    }

    @Override
    public String getInputEncoding() {
        return null;
    }

    @Override
    public String getXmlEncoding() {
        return null;
    }

    @Override
    public String getXmlVersion() {
        return null;
    }

    @Override
    protected Node export(Node n, AbstractDocument d) {
        super.export(n, d);
        AbstractEntity ae = (AbstractEntity)n;
        ae.nodeName = this.nodeName;
        ae.publicId = this.publicId;
        ae.systemId = this.systemId;
        return n;
    }

    @Override
    protected Node deepExport(Node n, AbstractDocument d) {
        super.deepExport(n, d);
        AbstractEntity ae = (AbstractEntity)n;
        ae.nodeName = this.nodeName;
        ae.publicId = this.publicId;
        ae.systemId = this.systemId;
        return n;
    }

    @Override
    protected Node copyInto(Node n) {
        super.copyInto(n);
        AbstractEntity ae = (AbstractEntity)n;
        ae.nodeName = this.nodeName;
        ae.publicId = this.publicId;
        ae.systemId = this.systemId;
        return n;
    }

    @Override
    protected Node deepCopyInto(Node n) {
        super.deepCopyInto(n);
        AbstractEntity ae = (AbstractEntity)n;
        ae.nodeName = this.nodeName;
        ae.publicId = this.publicId;
        ae.systemId = this.systemId;
        return n;
    }

    @Override
    protected void checkChildType(Node n, boolean replace) {
        switch (n.getNodeType()) {
            case 1: 
            case 3: 
            case 4: 
            case 5: 
            case 7: 
            case 8: 
            case 11: {
                break;
            }
            default: {
                throw this.createDOMException((short)3, "child.type", new Object[]{(int)this.getNodeType(), this.getNodeName(), (int)n.getNodeType(), n.getNodeName()});
            }
        }
    }
}

