/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBElement
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAnyAttribute
 *  javax.xml.bind.annotation.XmlAnyElement
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlElementRef
 *  javax.xml.bind.annotation.XmlElementRefs
 *  javax.xml.bind.annotation.XmlID
 *  javax.xml.bind.annotation.XmlSchemaType
 *  javax.xml.bind.annotation.XmlType
 *  javax.xml.bind.annotation.adapters.CollapsedStringAdapter
 *  javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter
 */
package org.oasis_open.docs.ns.xri.xrd_1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;
import org.oasis_open.docs.ns.xri.xrd_1.AnyURI;
import org.oasis_open.docs.ns.xri.xrd_1.ExpiresType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="XRDType", propOrder={"expires", "subject", "aliasOrPropertyOrLink"})
public class XRDType {
    @XmlElement(name="Expires")
    protected ExpiresType expires;
    @XmlElement(name="Subject")
    protected AnyURI subject;
    @XmlElementRefs(value={@XmlElementRef(name="Link", namespace="http://docs.oasis-open.org/ns/xri/xrd-1.0", type=JAXBElement.class), @XmlElementRef(name="Property", namespace="http://docs.oasis-open.org/ns/xri/xrd-1.0", type=JAXBElement.class), @XmlElementRef(name="Alias", namespace="http://docs.oasis-open.org/ns/xri/xrd-1.0", type=JAXBElement.class)})
    @XmlAnyElement(lax=true)
    protected List<Object> aliasOrPropertyOrLink;
    @XmlAttribute(namespace="http://www.w3.org/XML/1998/namespace")
    @XmlJavaTypeAdapter(value=CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name="ID")
    protected String id;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    public ExpiresType getExpires() {
        return this.expires;
    }

    public void setExpires(ExpiresType value) {
        this.expires = value;
    }

    public AnyURI getSubject() {
        return this.subject;
    }

    public void setSubject(AnyURI value) {
        this.subject = value;
    }

    public List<Object> getAliasOrPropertyOrLink() {
        if (this.aliasOrPropertyOrLink == null) {
            this.aliasOrPropertyOrLink = new ArrayList<Object>();
        }
        return this.aliasOrPropertyOrLink;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String value) {
        this.id = value;
    }

    public Map<QName, String> getOtherAttributes() {
        return this.otherAttributes;
    }
}

