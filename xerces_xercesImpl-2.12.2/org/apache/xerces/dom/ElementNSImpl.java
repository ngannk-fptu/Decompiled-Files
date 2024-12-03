/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.dom;

import org.apache.xerces.dom.CoreDocumentImpl;
import org.apache.xerces.dom.DOMMessageFormatter;
import org.apache.xerces.dom.ElementImpl;
import org.apache.xerces.impl.dv.xs.XSSimpleTypeDecl;
import org.apache.xerces.impl.xs.XSComplexTypeDecl;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xs.XSTypeDefinition;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;

public class ElementNSImpl
extends ElementImpl {
    static final long serialVersionUID = -9142310625494392642L;
    static final String xmlURI = "http://www.w3.org/XML/1998/namespace";
    protected String namespaceURI;
    protected String localName;
    transient XSTypeDefinition type;

    protected ElementNSImpl() {
    }

    protected ElementNSImpl(CoreDocumentImpl coreDocumentImpl, String string, String string2) throws DOMException {
        super(coreDocumentImpl, string2);
        this.setName(string, string2);
    }

    private void setName(String string, String string2) {
        this.namespaceURI = string;
        if (string != null) {
            String string3 = this.namespaceURI = string.length() == 0 ? null : string;
        }
        if (string2 == null) {
            String string4 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null);
            throw new DOMException(14, string4);
        }
        int n = string2.indexOf(58);
        int n2 = string2.lastIndexOf(58);
        this.ownerDocument.checkNamespaceWF(string2, n, n2);
        if (n < 0) {
            this.localName = string2;
            if (this.ownerDocument.errorChecking) {
                this.ownerDocument.checkQName(null, this.localName);
                if (string2.equals("xmlns") && (string == null || !string.equals(NamespaceContext.XMLNS_URI)) || string != null && string.equals(NamespaceContext.XMLNS_URI) && !string2.equals("xmlns")) {
                    String string5 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null);
                    throw new DOMException(14, string5);
                }
            }
        } else {
            String string6 = string2.substring(0, n);
            this.localName = string2.substring(n2 + 1);
            if (this.ownerDocument.errorChecking) {
                if (string == null || string6.equals("xml") && !string.equals(NamespaceContext.XML_URI)) {
                    String string7 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null);
                    throw new DOMException(14, string7);
                }
                this.ownerDocument.checkQName(string6, this.localName);
                this.ownerDocument.checkDOMNSErr(string6, string);
            }
        }
    }

    protected ElementNSImpl(CoreDocumentImpl coreDocumentImpl, String string, String string2, String string3) throws DOMException {
        super(coreDocumentImpl, string2);
        this.localName = string3;
        this.namespaceURI = string;
    }

    protected ElementNSImpl(CoreDocumentImpl coreDocumentImpl, String string) {
        super(coreDocumentImpl, string);
    }

    void rename(String string, String string2) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        this.name = string2;
        this.setName(string, string2);
        this.reconcileDefaultAttributes();
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
        int n;
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return (n = this.name.indexOf(58)) < 0 ? null : this.name.substring(0, n);
    }

    @Override
    public void setPrefix(String string) throws DOMException {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (this.ownerDocument.errorChecking) {
            if (this.isReadOnly()) {
                String string2 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null);
                throw new DOMException(7, string2);
            }
            if (string != null && string.length() != 0) {
                if (!CoreDocumentImpl.isXMLName(string, this.ownerDocument.isXML11Version())) {
                    String string3 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null);
                    throw new DOMException(5, string3);
                }
                if (this.namespaceURI == null || string.indexOf(58) >= 0) {
                    String string4 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null);
                    throw new DOMException(14, string4);
                }
                if (string.equals("xml") && !this.namespaceURI.equals(xmlURI)) {
                    String string5 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null);
                    throw new DOMException(14, string5);
                }
            }
        }
        this.name = string != null && string.length() != 0 ? string + ":" + this.localName : this.localName;
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
        if (this.type != null) {
            if (this.type instanceof XSSimpleTypeDecl) {
                return ((XSSimpleTypeDecl)this.type).getTypeName();
            }
            if (this.type instanceof XSComplexTypeDecl) {
                return ((XSComplexTypeDecl)this.type).getTypeName();
            }
        }
        return null;
    }

    @Override
    public String getTypeNamespace() {
        if (this.type != null) {
            return this.type.getNamespace();
        }
        return null;
    }

    @Override
    public boolean isDerivedFrom(String string, String string2, int n) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (this.type != null) {
            if (this.type instanceof XSSimpleTypeDecl) {
                return ((XSSimpleTypeDecl)this.type).isDOMDerivedFrom(string, string2, n);
            }
            if (this.type instanceof XSComplexTypeDecl) {
                return ((XSComplexTypeDecl)this.type).isDOMDerivedFrom(string, string2, n);
            }
        }
        return false;
    }

    public void setType(XSTypeDefinition xSTypeDefinition) {
        this.type = xSTypeDefinition;
    }
}

