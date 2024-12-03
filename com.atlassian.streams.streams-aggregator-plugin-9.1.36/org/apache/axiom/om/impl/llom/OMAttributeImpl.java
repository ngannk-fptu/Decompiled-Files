/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om.impl.llom;

import javax.xml.namespace.QName;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMCloneOptions;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMInformationItem;
import org.apache.axiom.om.OMNamespace;

public class OMAttributeImpl
implements OMAttribute {
    private String localName;
    private String value;
    private String type;
    private OMNamespace namespace;
    private QName qName;
    private OMFactory factory;
    protected OMElement owner;

    public OMAttributeImpl(String localName, OMNamespace ns, String value, OMFactory factory) {
        if (localName == null || localName.trim().length() == 0) {
            throw new IllegalArgumentException("Local name may not be null or empty");
        }
        if (ns != null && ns.getNamespaceURI().length() == 0) {
            if (ns.getPrefix().length() > 0) {
                throw new IllegalArgumentException("Cannot create a prefixed attribute with an empty namespace name");
            }
            ns = null;
        }
        this.localName = localName;
        this.value = value;
        this.namespace = ns;
        this.type = "CDATA";
        this.factory = factory;
    }

    public QName getQName() {
        if (this.qName != null) {
            return this.qName;
        }
        this.qName = this.namespace != null ? new QName(this.namespace.getNamespaceURI(), this.localName, this.namespace.getPrefix()) : new QName(this.localName);
        return this.qName;
    }

    public String getLocalName() {
        return this.localName;
    }

    public void setLocalName(String localName) {
        if (localName == null || localName.trim().length() == 0) {
            throw new IllegalArgumentException("Local name may not be null or empty");
        }
        this.localName = localName;
        this.qName = null;
    }

    public String getAttributeValue() {
        return this.value;
    }

    public void setAttributeValue(String value) {
        this.value = value;
    }

    public String getAttributeType() {
        return this.type;
    }

    public void setAttributeType(String type) {
        this.type = type;
    }

    public void setOMNamespace(OMNamespace omNamespace) {
        this.namespace = omNamespace;
        this.qName = null;
    }

    public OMNamespace getNamespace() {
        return this.namespace;
    }

    public String getPrefix() {
        OMNamespace ns = this.getNamespace();
        if (ns == null) {
            return null;
        }
        String prefix = ns.getPrefix();
        return prefix.length() == 0 ? null : prefix;
    }

    public String getNamespaceURI() {
        OMNamespace ns = this.getNamespace();
        if (ns == null) {
            return null;
        }
        String namespaceURI = ns.getNamespaceURI();
        return namespaceURI.length() == 0 ? null : namespaceURI;
    }

    public OMFactory getOMFactory() {
        return this.factory;
    }

    public OMElement getOwner() {
        return this.owner;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof OMAttribute)) {
            return false;
        }
        OMAttribute other = (OMAttribute)obj;
        return this.namespace == null ? other.getNamespace() == null : this.namespace.equals(other.getNamespace()) && this.localName.equals(other.getLocalName()) && (this.value == null ? other.getAttributeValue() == null : this.value.equals(other.getAttributeValue()));
    }

    public int hashCode() {
        return this.localName.hashCode() ^ (this.value != null ? this.value.hashCode() : 0) ^ (this.namespace != null ? this.namespace.hashCode() : 0);
    }

    public OMInformationItem clone(OMCloneOptions options) {
        return new OMAttributeImpl(this.localName, this.namespace, this.value, this.factory);
    }
}

