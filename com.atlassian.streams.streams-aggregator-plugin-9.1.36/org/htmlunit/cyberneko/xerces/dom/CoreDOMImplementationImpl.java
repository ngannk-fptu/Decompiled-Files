/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.xerces.dom;

import org.htmlunit.cyberneko.xerces.dom.CoreDocumentImpl;
import org.htmlunit.cyberneko.xerces.dom.DOMMessageFormatter;
import org.htmlunit.cyberneko.xerces.dom.DocumentTypeImpl;
import org.htmlunit.cyberneko.xerces.util.XMLChar;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;

public class CoreDOMImplementationImpl
implements DOMImplementation {
    private int docAndDoctypeCounter_ = 0;
    private static final CoreDOMImplementationImpl singleton = new CoreDOMImplementationImpl();

    public static DOMImplementation getDOMImplementation() {
        return singleton;
    }

    @Override
    public boolean hasFeature(String feature, String version) {
        boolean anyVersion;
        boolean bl = anyVersion = version == null || version.length() == 0;
        if (feature.startsWith("+")) {
            feature = feature.substring(1);
        }
        return "Core".equalsIgnoreCase(feature) && (anyVersion || "1.0".equals(version) || "2.0".equals(version) || "3.0".equals(version)) || "XML".equalsIgnoreCase(feature) && (anyVersion || "1.0".equals(version) || "2.0".equals(version) || "3.0".equals(version)) || "XMLVersion".equalsIgnoreCase(feature) && (anyVersion || "1.0".equals(version) || "1.1".equals(version)) || "LS".equalsIgnoreCase(feature) && (anyVersion || "3.0".equals(version)) || "ElementTraversal".equalsIgnoreCase(feature) && (anyVersion || "1.0".equals(version));
    }

    @Override
    public DocumentType createDocumentType(String qualifiedName, String publicID, String systemID) {
        this.checkQName(qualifiedName);
        return new DocumentTypeImpl(null, qualifiedName, publicID, systemID);
    }

    final void checkQName(String qname) {
        int i;
        int index = qname.indexOf(58);
        int lastIndex = qname.lastIndexOf(58);
        int length = qname.length();
        if (index == 0 || index == length - 1 || lastIndex != index) {
            String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null);
            throw new DOMException(14, msg);
        }
        int start = 0;
        if (index > 0) {
            if (!XMLChar.isNCNameStart(qname.charAt(start))) {
                String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null);
                throw new DOMException(5, msg);
            }
            for (i = 1; i < index; ++i) {
                if (XMLChar.isNCName(qname.charAt(i))) continue;
                String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null);
                throw new DOMException(5, msg);
            }
            start = index + 1;
        }
        if (!XMLChar.isNCNameStart(qname.charAt(start))) {
            String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null);
            throw new DOMException(5, msg);
        }
        for (i = start + 1; i < length; ++i) {
            if (XMLChar.isNCName(qname.charAt(i))) continue;
            String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null);
            throw new DOMException(5, msg);
        }
    }

    @Override
    public Document createDocument(String namespaceURI, String qualifiedName, DocumentType doctype) throws DOMException {
        if (doctype != null && doctype.getOwnerDocument() != null) {
            String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null);
            throw new DOMException(4, msg);
        }
        CoreDocumentImpl doc = this.createDocument(doctype);
        if (qualifiedName != null || namespaceURI != null) {
            Element e = doc.createElementNS(namespaceURI, qualifiedName);
            doc.appendChild(e);
        }
        return doc;
    }

    protected CoreDocumentImpl createDocument(DocumentType doctype) {
        return new CoreDocumentImpl(doctype);
    }

    @Override
    public Object getFeature(String feature, String version) {
        if (singleton.hasFeature(feature, version)) {
            return singleton;
        }
        return null;
    }

    protected synchronized int assignDocumentNumber() {
        return ++this.docAndDoctypeCounter_;
    }

    protected synchronized int assignDocTypeNumber() {
        return ++this.docAndDoctypeCounter_;
    }
}

