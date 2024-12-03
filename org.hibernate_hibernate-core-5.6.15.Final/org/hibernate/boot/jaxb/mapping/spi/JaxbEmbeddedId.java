/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.AccessType
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlType
 *  javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter
 */
package org.hibernate.boot.jaxb.mapping.spi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.AccessType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.hibernate.boot.jaxb.mapping.spi.Adapter1;
import org.hibernate.boot.jaxb.mapping.spi.JaxbAttributeOverride;
import org.hibernate.boot.jaxb.mapping.spi.PersistentAttribute;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="embedded-id", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm", propOrder={"attributeOverride"})
public class JaxbEmbeddedId
implements Serializable,
PersistentAttribute {
    @XmlElement(name="attribute-override", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected List<JaxbAttributeOverride> attributeOverride;
    @XmlAttribute(name="name", required=true)
    protected String name;
    @XmlAttribute(name="access")
    @XmlJavaTypeAdapter(value=Adapter1.class)
    protected AccessType access;

    public List<JaxbAttributeOverride> getAttributeOverride() {
        if (this.attributeOverride == null) {
            this.attributeOverride = new ArrayList<JaxbAttributeOverride>();
        }
        return this.attributeOverride;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }

    @Override
    public AccessType getAccess() {
        return this.access;
    }

    @Override
    public void setAccess(AccessType value) {
        this.access = value;
    }
}

