/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.utils;

import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.utils.ElementProxy;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class Signature11ElementProxy
extends ElementProxy {
    protected Signature11ElementProxy() {
    }

    public Signature11ElementProxy(Document doc) {
        if (doc == null) {
            throw new RuntimeException("Document is null");
        }
        this.setDocument(doc);
        this.setElement(XMLUtils.createElementInSignature11Space(doc, this.getBaseLocalName()));
        String prefix = ElementProxy.getDefaultPrefix(this.getBaseNamespace());
        if (prefix == null || prefix.length() == 0) {
            this.getElement().setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", this.getBaseNamespace());
        } else {
            this.getElement().setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + prefix, this.getBaseNamespace());
        }
    }

    public Signature11ElementProxy(Element element, String baseURI) throws XMLSecurityException {
        super(element, baseURI);
    }

    @Override
    public String getBaseNamespace() {
        return "http://www.w3.org/2009/xmldsig11#";
    }
}

