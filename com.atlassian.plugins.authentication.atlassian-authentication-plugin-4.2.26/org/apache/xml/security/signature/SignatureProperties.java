/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.signature;

import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.signature.SignatureProperty;
import org.apache.xml.security.signature.XMLSignatureException;
import org.apache.xml.security.utils.SignatureElementProxy;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SignatureProperties
extends SignatureElementProxy {
    public SignatureProperties(Document doc) {
        super(doc);
        this.addReturnToSelf();
    }

    public SignatureProperties(Element element, String baseURI) throws XMLSecurityException {
        super(element, baseURI);
        Element[] propertyElems;
        Attr attr = element.getAttributeNodeNS(null, "Id");
        if (attr != null) {
            element.setIdAttributeNode(attr, true);
        }
        for (Element propertyElem : propertyElems = XMLUtils.selectDsNodes(this.getFirstChild(), "SignatureProperty")) {
            Attr propertyAttr = propertyElem.getAttributeNodeNS(null, "Id");
            if (propertyAttr == null) continue;
            propertyElem.setIdAttributeNode(propertyAttr, true);
        }
    }

    public int getLength() {
        Element[] propertyElems = XMLUtils.selectDsNodes(this.getFirstChild(), "SignatureProperty");
        return propertyElems.length;
    }

    public SignatureProperty item(int i) throws XMLSignatureException {
        try {
            Element propertyElem = XMLUtils.selectDsNode(this.getFirstChild(), "SignatureProperty", i);
            if (propertyElem == null) {
                return null;
            }
            return new SignatureProperty(propertyElem, this.baseURI);
        }
        catch (XMLSecurityException ex) {
            throw new XMLSignatureException(ex);
        }
    }

    public void setId(String Id) {
        if (Id != null) {
            this.setLocalIdAttribute("Id", Id);
        }
    }

    public String getId() {
        return this.getLocalAttribute("Id");
    }

    public void addSignatureProperty(SignatureProperty sp) {
        this.appendSelf(sp);
        this.addReturnToSelf();
    }

    @Override
    public String getBaseLocalName() {
        return "SignatureProperties";
    }
}

