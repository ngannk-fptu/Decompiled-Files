/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.AccessType
 *  javax.persistence.EnumType
 *  javax.persistence.FetchType
 *  javax.persistence.TemporalType
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlSchemaType
 *  javax.xml.bind.annotation.XmlType
 *  javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter
 */
package org.hibernate.boot.jaxb.mapping.spi;

import java.io.Serializable;
import javax.persistence.AccessType;
import javax.persistence.EnumType;
import javax.persistence.FetchType;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.hibernate.boot.jaxb.mapping.spi.Adapter1;
import org.hibernate.boot.jaxb.mapping.spi.Adapter10;
import org.hibernate.boot.jaxb.mapping.spi.Adapter4;
import org.hibernate.boot.jaxb.mapping.spi.Adapter5;
import org.hibernate.boot.jaxb.mapping.spi.JaxbColumn;
import org.hibernate.boot.jaxb.mapping.spi.JaxbConvert;
import org.hibernate.boot.jaxb.mapping.spi.JaxbLob;
import org.hibernate.boot.jaxb.mapping.spi.PersistentAttribute;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="basic", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm", propOrder={"column", "lob", "temporal", "enumerated", "convert"})
public class JaxbBasic
implements Serializable,
PersistentAttribute {
    @XmlElement(namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected JaxbColumn column;
    @XmlElement(namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected JaxbLob lob;
    @XmlElement(namespace="http://xmlns.jcp.org/xml/ns/persistence/orm", type=String.class)
    @XmlJavaTypeAdapter(value=Adapter10.class)
    @XmlSchemaType(name="token")
    protected TemporalType temporal;
    @XmlElement(namespace="http://xmlns.jcp.org/xml/ns/persistence/orm", type=String.class)
    @XmlJavaTypeAdapter(value=Adapter4.class)
    @XmlSchemaType(name="token")
    protected EnumType enumerated;
    @XmlElement(namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected JaxbConvert convert;
    @XmlAttribute(name="name", required=true)
    protected String name;
    @XmlAttribute(name="fetch")
    @XmlJavaTypeAdapter(value=Adapter5.class)
    protected FetchType fetch;
    @XmlAttribute(name="optional")
    protected Boolean optional;
    @XmlAttribute(name="access")
    @XmlJavaTypeAdapter(value=Adapter1.class)
    protected AccessType access;

    public JaxbColumn getColumn() {
        return this.column;
    }

    public void setColumn(JaxbColumn value) {
        this.column = value;
    }

    public JaxbLob getLob() {
        return this.lob;
    }

    public void setLob(JaxbLob value) {
        this.lob = value;
    }

    public TemporalType getTemporal() {
        return this.temporal;
    }

    public void setTemporal(TemporalType value) {
        this.temporal = value;
    }

    public EnumType getEnumerated() {
        return this.enumerated;
    }

    public void setEnumerated(EnumType value) {
        this.enumerated = value;
    }

    public JaxbConvert getConvert() {
        return this.convert;
    }

    public void setConvert(JaxbConvert value) {
        this.convert = value;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public FetchType getFetch() {
        return this.fetch;
    }

    public void setFetch(FetchType value) {
        this.fetch = value;
    }

    public Boolean isOptional() {
        return this.optional;
    }

    public void setOptional(Boolean value) {
        this.optional = value;
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

