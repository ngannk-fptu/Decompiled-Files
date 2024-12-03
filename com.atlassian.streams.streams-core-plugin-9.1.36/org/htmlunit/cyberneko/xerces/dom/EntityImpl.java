/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.xerces.dom;

import org.htmlunit.cyberneko.xerces.dom.CoreDocumentImpl;
import org.htmlunit.cyberneko.xerces.dom.ParentNode;
import org.w3c.dom.Entity;
import org.w3c.dom.Node;

public class EntityImpl
extends ParentNode
implements Entity {
    protected final String name;
    protected String publicId;
    protected String systemId;
    protected String encoding;
    protected String inputEncoding;
    protected String version;
    protected String notationName;
    protected String baseURI;

    public EntityImpl(CoreDocumentImpl ownerDoc, String name) {
        super(ownerDoc);
        this.name = name;
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
    public Node cloneNode(boolean deep) {
        EntityImpl newentity = (EntityImpl)super.cloneNode(deep);
        return newentity;
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

    public void setPublicId(String id) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        this.publicId = id;
    }

    public void setXmlEncoding(String value) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        this.encoding = value;
    }

    @Override
    public String getInputEncoding() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.inputEncoding;
    }

    public void setInputEncoding(String inputEncoding) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        this.inputEncoding = inputEncoding;
    }

    public void setXmlVersion(String value) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        this.version = value;
    }

    public void setSystemId(String id) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        this.systemId = id;
    }

    public void setNotationName(String name) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        this.notationName = name;
    }

    @Override
    public String getBaseURI() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.baseURI != null ? this.baseURI : this.getOwnerDocument().getBaseURI();
    }

    public void setBaseURI(String uri) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        this.baseURI = uri;
    }
}

