/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jcp.xml.dsig.internal.dom;

import java.security.Provider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dsig.XMLObject;
import org.apache.jcp.xml.dsig.internal.dom.DOMManifest;
import org.apache.jcp.xml.dsig.internal.dom.DOMSignatureProperties;
import org.apache.jcp.xml.dsig.internal.dom.DOMStructure;
import org.apache.jcp.xml.dsig.internal.dom.DOMUtils;
import org.apache.jcp.xml.dsig.internal.dom.DOMX509Data;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public final class DOMXMLObject
extends DOMStructure
implements XMLObject {
    private final String id;
    private final String mimeType;
    private final String encoding;
    private final List<XMLStructure> content;
    private Element objectElem;

    public DOMXMLObject(List<? extends XMLStructure> content, String id, String mimeType, String encoding) {
        if (content == null || content.isEmpty()) {
            this.content = Collections.emptyList();
        } else {
            this.content = Collections.unmodifiableList(new ArrayList<XMLStructure>(content));
            int size = this.content.size();
            for (int i = 0; i < size; ++i) {
                if (this.content.get(i) instanceof XMLStructure) continue;
                throw new ClassCastException("content[" + i + "] is not a valid type");
            }
        }
        this.id = id;
        this.mimeType = mimeType;
        this.encoding = encoding;
    }

    public DOMXMLObject(Element objElem, XMLCryptoContext context, Provider provider) throws MarshalException {
        this.encoding = DOMUtils.getAttributeValue(objElem, "Encoding");
        Attr attr = objElem.getAttributeNodeNS(null, "Id");
        if (attr != null) {
            this.id = attr.getValue();
            objElem.setIdAttributeNode(attr, true);
        } else {
            this.id = null;
        }
        this.mimeType = DOMUtils.getAttributeValue(objElem, "MimeType");
        ArrayList<XMLStructure> newContent = new ArrayList<XMLStructure>();
        for (Node firstChild = objElem.getFirstChild(); firstChild != null; firstChild = firstChild.getNextSibling()) {
            if (firstChild.getNodeType() == 1) {
                Element childElem = (Element)firstChild;
                String tag = childElem.getLocalName();
                String namespace = childElem.getNamespaceURI();
                if ("Manifest".equals(tag) && "http://www.w3.org/2000/09/xmldsig#".equals(namespace)) {
                    newContent.add(new DOMManifest(childElem, context, provider));
                    continue;
                }
                if ("SignatureProperties".equals(tag) && "http://www.w3.org/2000/09/xmldsig#".equals(namespace)) {
                    newContent.add(new DOMSignatureProperties(childElem));
                    continue;
                }
                if ("X509Data".equals(tag) && "http://www.w3.org/2000/09/xmldsig#".equals(namespace)) {
                    newContent.add(new DOMX509Data(childElem));
                    continue;
                }
                newContent.add(new javax.xml.crypto.dom.DOMStructure(firstChild));
                continue;
            }
            newContent.add(new javax.xml.crypto.dom.DOMStructure(firstChild));
        }
        NamedNodeMap nnm = objElem.getAttributes();
        for (int idx = 0; idx < nnm.getLength(); ++idx) {
            Node nsDecl = nnm.item(idx);
            if (!DOMUtils.isNamespace(nsDecl)) continue;
            newContent.add(new javax.xml.crypto.dom.DOMStructure(nsDecl));
        }
        this.content = newContent.isEmpty() ? Collections.emptyList() : Collections.unmodifiableList(newContent);
        this.objectElem = objElem;
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
    public String getMimeType() {
        return this.mimeType;
    }

    @Override
    public String getEncoding() {
        return this.encoding;
    }

    @Override
    public void marshal(Node parent, String dsPrefix, DOMCryptoContext context) throws MarshalException {
        Document ownerDoc = DOMUtils.getOwnerDocument(parent);
        Element objElem = this.objectElem;
        if (objElem == null) {
            objElem = DOMUtils.createElement(ownerDoc, "Object", "http://www.w3.org/2000/09/xmldsig#", dsPrefix);
            DOMUtils.setAttributeID(objElem, "Id", this.id);
            DOMUtils.setAttribute(objElem, "MimeType", this.mimeType);
            DOMUtils.setAttribute(objElem, "Encoding", this.encoding);
            for (XMLStructure object : this.content) {
                if (object instanceof DOMStructure) {
                    ((DOMStructure)object).marshal(objElem, dsPrefix, context);
                    continue;
                }
                javax.xml.crypto.dom.DOMStructure domObject = (javax.xml.crypto.dom.DOMStructure)object;
                DOMUtils.appendChild(objElem, domObject.getNode());
            }
        }
        parent.appendChild(objElem);
    }

    public boolean equals(Object o) {
        boolean encodingsEqual;
        boolean idsEqual;
        if (this == o) {
            return true;
        }
        if (!(o instanceof XMLObject)) {
            return false;
        }
        XMLObject oxo = (XMLObject)o;
        boolean bl = this.id == null ? oxo.getId() == null : (idsEqual = this.id.equals(oxo.getId()));
        boolean bl2 = this.encoding == null ? oxo.getEncoding() == null : (encodingsEqual = this.encoding.equals(oxo.getEncoding()));
        boolean mimeTypesEqual = this.mimeType == null ? oxo.getMimeType() == null : this.mimeType.equals(oxo.getMimeType());
        return idsEqual && encodingsEqual && mimeTypesEqual && this.equalsContent(this.content, oxo.getContent());
    }

    public int hashCode() {
        int result = 17;
        if (this.id != null) {
            result = 31 * result + this.id.hashCode();
        }
        if (this.encoding != null) {
            result = 31 * result + this.encoding.hashCode();
        }
        if (this.mimeType != null) {
            result = 31 * result + this.mimeType.hashCode();
        }
        result = 31 * result + this.content.hashCode();
        return result;
    }
}

