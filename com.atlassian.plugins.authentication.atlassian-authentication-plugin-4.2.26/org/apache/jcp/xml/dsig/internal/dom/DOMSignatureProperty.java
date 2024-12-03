/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jcp.xml.dsig.internal.dom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dsig.SignatureProperty;
import org.apache.jcp.xml.dsig.internal.dom.DOMStructure;
import org.apache.jcp.xml.dsig.internal.dom.DOMUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public final class DOMSignatureProperty
extends DOMStructure
implements SignatureProperty {
    private final String id;
    private final String target;
    private final List<XMLStructure> content;

    public DOMSignatureProperty(List<? extends XMLStructure> content, String target, String id) {
        if (target == null) {
            throw new NullPointerException("target cannot be null");
        }
        if (content == null) {
            throw new NullPointerException("content cannot be null");
        }
        if (content.isEmpty()) {
            throw new IllegalArgumentException("content cannot be empty");
        }
        this.content = Collections.unmodifiableList(new ArrayList<XMLStructure>(content));
        int size = this.content.size();
        for (int i = 0; i < size; ++i) {
            if (this.content.get(i) instanceof XMLStructure) continue;
            throw new ClassCastException("content[" + i + "] is not a valid type");
        }
        this.target = target;
        this.id = id;
    }

    public DOMSignatureProperty(Element propElem) throws MarshalException {
        this.target = DOMUtils.getAttributeValue(propElem, "Target");
        if (this.target == null) {
            throw new MarshalException("target cannot be null");
        }
        Attr attr = propElem.getAttributeNodeNS(null, "Id");
        if (attr != null) {
            this.id = attr.getValue();
            propElem.setIdAttributeNode(attr, true);
        } else {
            this.id = null;
        }
        ArrayList<javax.xml.crypto.dom.DOMStructure> newContent = new ArrayList<javax.xml.crypto.dom.DOMStructure>();
        for (Node firstChild = propElem.getFirstChild(); firstChild != null; firstChild = firstChild.getNextSibling()) {
            newContent.add(new javax.xml.crypto.dom.DOMStructure(firstChild));
        }
        if (newContent.isEmpty()) {
            throw new MarshalException("content cannot be empty");
        }
        this.content = Collections.unmodifiableList(newContent);
    }

    @Override
    public List<XMLStructure> getContent() {
        return this.content;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getTarget() {
        return this.target;
    }

    @Override
    public void marshal(Node parent, String dsPrefix, DOMCryptoContext context) throws MarshalException {
        Document ownerDoc = DOMUtils.getOwnerDocument(parent);
        Element propElem = DOMUtils.createElement(ownerDoc, "SignatureProperty", "http://www.w3.org/2000/09/xmldsig#", dsPrefix);
        DOMUtils.setAttributeID(propElem, "Id", this.id);
        DOMUtils.setAttribute(propElem, "Target", this.target);
        for (XMLStructure property : this.content) {
            DOMUtils.appendChild(propElem, ((javax.xml.crypto.dom.DOMStructure)property).getNode());
        }
        parent.appendChild(propElem);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SignatureProperty)) {
            return false;
        }
        SignatureProperty osp = (SignatureProperty)o;
        boolean idsEqual = this.id == null ? osp.getId() == null : this.id.equals(osp.getId());
        List<XMLStructure> ospContent = osp.getContent();
        return this.equalsContent(this.content, ospContent) && this.target.equals(osp.getTarget()) && idsEqual;
    }

    public int hashCode() {
        int result = 17;
        if (this.id != null) {
            result = 31 * result + this.id.hashCode();
        }
        result = 31 * result + this.target.hashCode();
        result = 31 * result + this.content.hashCode();
        return result;
    }
}

