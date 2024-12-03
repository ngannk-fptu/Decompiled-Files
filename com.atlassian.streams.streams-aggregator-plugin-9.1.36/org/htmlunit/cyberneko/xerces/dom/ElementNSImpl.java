/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.xerces.dom;

import org.htmlunit.cyberneko.xerces.dom.CoreDocumentImpl;
import org.htmlunit.cyberneko.xerces.dom.DOMMessageFormatter;
import org.htmlunit.cyberneko.xerces.dom.ElementImpl;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;

public class ElementNSImpl
extends ElementImpl {
    static final String xmlURI = "http://www.w3.org/XML/1998/namespace";
    protected String namespaceURI;
    protected String localName;

    protected ElementNSImpl(CoreDocumentImpl ownerDocument, String namespaceURI, String qualifiedName) throws DOMException {
        super(ownerDocument, qualifiedName);
        this.setName(namespaceURI, qualifiedName);
    }

    private void setName(String namespaceURI, String qname) {
        this.namespaceURI = namespaceURI;
        if (namespaceURI != null) {
            String string = this.namespaceURI = namespaceURI.length() == 0 ? null : namespaceURI;
        }
        if (qname == null) {
            String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null);
            throw new DOMException(14, msg);
        }
        int colon1 = qname.indexOf(58);
        int colon2 = qname.lastIndexOf(58);
        this.ownerDocument.checkNamespaceWF(qname, colon1, colon2);
        if (colon1 < 0) {
            this.localName = qname;
            if (this.ownerDocument.errorChecking) {
                this.ownerDocument.checkQName(null, this.localName);
                if ("xmlns".equals(qname) && (namespaceURI == null || !namespaceURI.equals("http://www.w3.org/2000/xmlns/")) || namespaceURI != null && namespaceURI.equals("http://www.w3.org/2000/xmlns/") && !"xmlns".equals(qname)) {
                    String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null);
                    throw new DOMException(14, msg);
                }
            }
        } else {
            String prefix = qname.substring(0, colon1);
            this.localName = qname.substring(colon2 + 1);
            if (this.ownerDocument.errorChecking) {
                if (namespaceURI == null || "xml".equals(prefix) && !namespaceURI.equals(xmlURI)) {
                    String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null);
                    throw new DOMException(14, msg);
                }
                this.ownerDocument.checkQName(prefix, this.localName);
                this.ownerDocument.checkDOMNSErr(prefix, namespaceURI);
            }
        }
    }

    protected ElementNSImpl(CoreDocumentImpl ownerDocument, String namespaceURI, String qualifiedName, String localName) throws DOMException {
        super(ownerDocument, qualifiedName);
        this.localName = localName;
        this.namespaceURI = namespaceURI;
    }

    void rename(String namespaceURI, String qualifiedName) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        this.name = qualifiedName;
        this.setName(namespaceURI, qualifiedName);
    }

    @Override
    public String getNamespaceURI() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.namespaceURI;
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
        if (this.ownerDocument.errorChecking && prefix != null && prefix.length() != 0) {
            if (!CoreDocumentImpl.isXMLName(prefix, this.ownerDocument.isXML11Version())) {
                String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null);
                throw new DOMException(5, msg);
            }
            if (this.namespaceURI == null || prefix.indexOf(58) >= 0) {
                String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null);
                throw new DOMException(14, msg);
            }
            if ("xml".equals(prefix) && !this.namespaceURI.equals(xmlURI)) {
                String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null);
                throw new DOMException(14, msg);
            }
        }
        this.name = prefix != null && prefix.length() != 0 ? prefix + ":" + this.localName : this.localName;
    }

    @Override
    public String getLocalName() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.localName;
    }

    @Override
    protected Attr getXMLBaseAttribute() {
        return (Attr)this.attributes.getNamedItemNS(xmlURI, "base");
    }

    @Override
    public String getTypeName() {
        return null;
    }

    @Override
    public String getTypeNamespace() {
        return null;
    }

    @Override
    public boolean isDerivedFrom(String typeNamespaceArg, String typeNameArg, int derivationMethod) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return false;
    }
}

