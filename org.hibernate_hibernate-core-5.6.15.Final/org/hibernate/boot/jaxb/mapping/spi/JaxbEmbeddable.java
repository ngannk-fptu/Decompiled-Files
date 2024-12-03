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
import javax.persistence.AccessType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.hibernate.boot.jaxb.mapping.spi.Adapter1;
import org.hibernate.boot.jaxb.mapping.spi.JaxbEmbeddableAttributes;
import org.hibernate.boot.jaxb.mapping.spi.ManagedType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="embeddable", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm", propOrder={"description", "attributes"})
public class JaxbEmbeddable
implements Serializable,
ManagedType {
    @XmlElement(namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected String description;
    @XmlElement(namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected JaxbEmbeddableAttributes attributes;
    @XmlAttribute(name="class", required=true)
    protected String clazz;
    @XmlAttribute(name="access")
    @XmlJavaTypeAdapter(value=Adapter1.class)
    protected AccessType access;
    @XmlAttribute(name="metadata-complete")
    protected Boolean metadataComplete;

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public void setDescription(String value) {
        this.description = value;
    }

    @Override
    public JaxbEmbeddableAttributes getAttributes() {
        return this.attributes;
    }

    public void setAttributes(JaxbEmbeddableAttributes value) {
        this.attributes = value;
    }

    @Override
    public String getClazz() {
        return this.clazz;
    }

    @Override
    public void setClazz(String value) {
        this.clazz = value;
    }

    @Override
    public AccessType getAccess() {
        return this.access;
    }

    @Override
    public void setAccess(AccessType value) {
        this.access = value;
    }

    @Override
    public Boolean isMetadataComplete() {
        return this.metadataComplete;
    }

    @Override
    public void setMetadataComplete(Boolean value) {
        this.metadataComplete = value;
    }
}

