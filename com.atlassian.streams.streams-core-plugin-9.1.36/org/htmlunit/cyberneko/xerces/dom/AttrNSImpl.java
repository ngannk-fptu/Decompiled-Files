/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.xerces.dom;

import org.htmlunit.cyberneko.xerces.dom.AttrImpl;
import org.htmlunit.cyberneko.xerces.dom.CoreDocumentImpl;
import org.htmlunit.cyberneko.xerces.dom.DOMMessageFormatter;
import org.w3c.dom.DOMException;

public class AttrNSImpl
extends AttrImpl {
    static final String xmlnsURI = "http://www.w3.org/2000/xmlns/";
    static final String xmlURI = "http://www.w3.org/XML/1998/namespace";
    private String namespaceURI_;
    private String localName_;

    protected AttrNSImpl(CoreDocumentImpl ownerDocument, String namespaceURI, String qualifiedName) {
        super(ownerDocument, qualifiedName);
        this.setName(namespaceURI, qualifiedName);
    }

    private void setName(String namespaceURI, String qname) {
        CoreDocumentImpl ownerDocument = this.ownerDocument();
        this.namespaceURI_ = namespaceURI;
        if (namespaceURI != null) {
            this.namespaceURI_ = namespaceURI.length() == 0 ? null : namespaceURI;
        }
        int colon1 = qname.indexOf(58);
        int colon2 = qname.lastIndexOf(58);
        ownerDocument.checkNamespaceWF(qname, colon1, colon2);
        if (colon1 < 0) {
            this.localName_ = qname;
            if (ownerDocument.errorChecking) {
                ownerDocument.checkQName(null, this.localName_);
                if ("xmlns".equals(qname) && (namespaceURI == null || !namespaceURI.equals(xmlnsURI)) || namespaceURI != null && namespaceURI.equals(xmlnsURI) && !"xmlns".equals(qname)) {
                    String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null);
                    throw new DOMException(14, msg);
                }
            }
        } else {
            String prefix = qname.substring(0, colon1);
            this.localName_ = qname.substring(colon2 + 1);
            ownerDocument.checkQName(prefix, this.localName_);
            ownerDocument.checkDOMNSErr(prefix, namespaceURI);
        }
    }

    public AttrNSImpl(CoreDocumentImpl ownerDocument, String namespaceURI, String qualifiedName, String localName) {
        super(ownerDocument, qualifiedName);
        this.localName_ = localName;
        this.namespaceURI_ = namespaceURI;
    }

    void rename(String namespaceURI, String qualifiedName) {
        super.rename(qualifiedName);
        this.setName(namespaceURI, qualifiedName);
    }

    @Override
    public String getNamespaceURI() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.namespaceURI_;
    }

    @Override
    public String getPrefix() {
        int index;
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return (index = this.name.indexOf(58)) < 0 ? null : this.name.substring(0, index);
    }

    @Override
    public void setPrefix(String prefix) throws DOMException {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (this.ownerDocument().errorChecking && prefix != null && prefix.length() != 0) {
            if (!CoreDocumentImpl.isXMLName(prefix, this.ownerDocument().isXML11Version())) {
                String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null);
                throw new DOMException(5, msg);
            }
            if (this.namespaceURI_ == null || prefix.indexOf(58) >= 0) {
                String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null);
                throw new DOMException(14, msg);
            }
            if ("xmlns".equals(prefix)) {
                if (!this.namespaceURI_.equals(xmlnsURI)) {
                    String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null);
                    throw new DOMException(14, msg);
                }
            } else if ("xml".equals(prefix)) {
                if (!this.namespaceURI_.equals(xmlURI)) {
                    String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null);
                    throw new DOMException(14, msg);
                }
            } else if ("xmlns".equals(this.name)) {
                String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null);
                throw new DOMException(14, msg);
            }
        }
        this.name = prefix != null && prefix.length() != 0 ? prefix + ":" + this.localName_ : this.localName_;
    }

    @Override
    public String getLocalName() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.localName_;
    }

    @Override
    public String getTypeName() {
        return this.type;
    }

    @Override
    public boolean isDerivedFrom(String typeNamespaceArg, String typeNameArg, int derivationMethod) {
        return false;
    }

    @Override
    public String getTypeNamespace() {
        if (this.type != null) {
            return "http://www.w3.org/TR/REC-xml";
        }
        return null;
    }
}

