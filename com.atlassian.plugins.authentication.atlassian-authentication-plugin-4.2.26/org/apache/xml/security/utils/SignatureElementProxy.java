/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.utils;

import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.utils.ElementProxy;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class SignatureElementProxy
extends ElementProxy {
    protected SignatureElementProxy() {
    }

    public SignatureElementProxy(Document doc) {
        if (doc == null) {
            throw new RuntimeException("Document is null");
        }
        this.setDocument(doc);
        this.setElement(XMLUtils.createElementInSignatureSpace(doc, this.getBaseLocalName()));
    }

    public SignatureElementProxy(Element element, String baseURI) throws XMLSecurityException {
        super(element, baseURI);
    }

    @Override
    public String getBaseNamespace() {
        return "http://www.w3.org/2000/09/xmldsig#";
    }
}

