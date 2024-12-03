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
 *  javax.xml.bind.annotation.XmlElementRef
 *  javax.xml.bind.annotation.XmlElementRefs
 *  javax.xml.bind.annotation.XmlSchemaType
 *  javax.xml.bind.annotation.XmlType
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
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="LinkType", propOrder={"titleOrPropertyOrAny"})
public class LinkType {
    @XmlElementRefs(value={@XmlElementRef(name="Property", namespace="http://docs.oasis-open.org/ns/xri/xrd-1.0", type=JAXBElement.class), @XmlElementRef(name="Title", namespace="http://docs.oasis-open.org/ns/xri/xrd-1.0", type=JAXBElement.class)})
    @XmlAnyElement(lax=true)
    protected List<Object> titleOrPropertyOrAny;
    @XmlAttribute
    @XmlSchemaType(name="anyURI")
    protected String rel;
    @XmlAttribute
    protected String type;
    @XmlAttribute
    @XmlSchemaType(name="anyURI")
    protected String href;
    @XmlAttribute
    protected String template;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    public List<Object> getTitleOrPropertyOrAny() {
        if (this.titleOrPropertyOrAny == null) {
            this.titleOrPropertyOrAny = new ArrayList<Object>();
        }
        return this.titleOrPropertyOrAny;
    }

    public String getRel() {
        return this.rel;
    }

    public void setRel(String value) {
        this.rel = value;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String value) {
        this.type = value;
    }

    public String getHref() {
        return this.href;
    }

    public void setHref(String value) {
        this.href = value;
    }

    public String getTemplate() {
        return this.template;
    }

    public void setTemplate(String value) {
        this.template = value;
    }

    public Map<QName, String> getOtherAttributes() {
        return this.otherAttributes;
    }
}

