/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.dom;

import org.apache.xerces.dom.CoreDocumentImpl;
import org.apache.xerces.dom.DOMMessageFormatter;
import org.apache.xerces.dom.NodeImpl;
import org.apache.xerces.util.URI;
import org.w3c.dom.DOMException;
import org.w3c.dom.Notation;

public class NotationImpl
extends NodeImpl
implements Notation {
    static final long serialVersionUID = -764632195890658402L;
    protected String name;
    protected String publicId;
    protected String systemId;
    protected String baseURI;

    public NotationImpl(CoreDocumentImpl coreDocumentImpl, String string) {
        super(coreDocumentImpl);
        this.name = string;
    }

    @Override
    public short getNodeType() {
        return 12;
    }

    @Override
    public String getNodeName() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.name;
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

    public void setPublicId(String string) {
        if (this.isReadOnly()) {
            throw new DOMException(7, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null));
        }
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        this.publicId = string;
    }

    public void setSystemId(String string) {
        if (this.isReadOnly()) {
            throw new DOMException(7, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null));
        }
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        this.systemId = string;
    }

    @Override
    public String getBaseURI() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (this.baseURI != null && this.baseURI.length() != 0) {
            try {
                return new URI(this.baseURI).toString();
            }
            catch (URI.MalformedURIException malformedURIException) {
                return null;
            }
        }
        return this.baseURI;
    }

    public void setBaseURI(String string) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        this.baseURI = string;
    }
}

