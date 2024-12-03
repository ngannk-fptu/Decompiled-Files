/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.dom;

import org.apache.xerces.dom.CoreDocumentImpl;
import org.apache.xerces.dom.ParentNode;
import org.w3c.dom.Entity;
import org.w3c.dom.Node;

public class EntityImpl
extends ParentNode
implements Entity {
    static final long serialVersionUID = -3575760943444303423L;
    protected String name;
    protected String publicId;
    protected String systemId;
    protected String encoding;
    protected String inputEncoding;
    protected String version;
    protected String notationName;
    protected String baseURI;

    public EntityImpl(CoreDocumentImpl coreDocumentImpl, String string) {
        super(coreDocumentImpl);
        this.name = string;
        this.isReadOnly(true);
    }

    @Override
    public short getNodeType() {
        return 6;
    }

    @Override
    public String getNodeName() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.name;
    }

    @Override
    public Node cloneNode(boolean bl) {
        EntityImpl entityImpl = (EntityImpl)super.cloneNode(bl);
        entityImpl.setReadOnly(true, bl);
        return entityImpl;
    }

    @Override
    public String getPublicId() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.publicId;
    }

    @Override
    public String getSystemId() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.systemId;
    }

    @Override
    public String getXmlVersion() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.version;
    }

    @Override
    public String getXmlEncoding() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.encoding;
    }

    @Override
    public String getNotationName() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.notationName;
    }

    public void setPublicId(String string) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        this.publicId = string;
    }

    public void setXmlEncoding(String string) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        this.encoding = string;
    }

    @Override
    public String getInputEncoding() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.inputEncoding;
    }

    public void setInputEncoding(String string) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        this.inputEncoding = string;
    }

    public void setXmlVersion(String string) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        this.version = string;
    }

    public void setSystemId(String string) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        this.systemId = string;
    }

    public void setNotationName(String string) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        this.notationName = string;
    }

    @Override
    public String getBaseURI() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.baseURI != null ? this.baseURI : ((CoreDocumentImpl)this.getOwnerDocument()).getBaseURI();
    }

    public void setBaseURI(String string) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        this.baseURI = string;
    }
}

