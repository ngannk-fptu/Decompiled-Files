/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.AccessType
 *  javax.persistence.FetchType
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
import javax.persistence.FetchType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.hibernate.boot.jaxb.mapping.spi.Adapter1;
import org.hibernate.boot.jaxb.mapping.spi.Adapter5;
import org.hibernate.boot.jaxb.mapping.spi.AssociationAttribute;
import org.hibernate.boot.jaxb.mapping.spi.FetchableAttribute;
import org.hibernate.boot.jaxb.mapping.spi.JaxbCascadeType;
import org.hibernate.boot.jaxb.mapping.spi.JaxbForeignKey;
import org.hibernate.boot.jaxb.mapping.spi.JaxbJoinColumn;
import org.hibernate.boot.jaxb.mapping.spi.JaxbJoinTable;
import org.hibernate.boot.jaxb.mapping.spi.PersistentAttribute;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="many-to-one", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm", propOrder={"joinColumn", "foreignKey", "joinTable", "cascade"})
public class JaxbManyToOne
implements Serializable,
AssociationAttribute,
FetchableAttribute,
PersistentAttribute {
    @XmlElement(name="join-column", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected List<JaxbJoinColumn> joinColumn;
    @XmlElement(name="foreign-key", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected JaxbForeignKey foreignKey;
    @XmlElement(name="join-table", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected JaxbJoinTable joinTable;
    @XmlElement(namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected JaxbCascadeType cascade;
    @XmlAttribute(name="name", required=true)
    protected String name;
    @XmlAttribute(name="target-entity")
    protected String targetEntity;
    @XmlAttribute(name="fetch")
    @XmlJavaTypeAdapter(value=Adapter5.class)
    protected FetchType fetch;
    @XmlAttribute(name="optional")
    protected Boolean optional;
    @XmlAttribute(name="access")
    @XmlJavaTypeAdapter(value=Adapter1.class)
    protected AccessType access;
    @XmlAttribute(name="maps-id")
    protected String mapsId;
    @XmlAttribute(name="id")
    protected Boolean id;

    public List<JaxbJoinColumn> getJoinColumn() {
        if (this.joinColumn == null) {
            this.joinColumn = new ArrayList<JaxbJoinColumn>();
        }
        return this.joinColumn;
    }

    public JaxbForeignKey getForeignKey() {
        return this.foreignKey;
    }

    public void setForeignKey(JaxbForeignKey value) {
        this.foreignKey = value;
    }

    @Override
    public JaxbJoinTable getJoinTable() {
        return this.joinTable;
    }

    @Override
    public void setJoinTable(JaxbJoinTable value) {
        this.joinTable = value;
    }

    @Override
    public JaxbCascadeType getCascade() {
        return this.cascade;
    }

    @Override
    public void setCascade(JaxbCascadeType value) {
        this.cascade = value;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }

    @Override
    public String getTargetEntity() {
        return this.targetEntity;
    }

    @Override
    public void setTargetEntity(String value) {
        this.targetEntity = value;
    }

    @Override
    public FetchType getFetch() {
        return this.fetch;
    }

    @Override
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

    public String getMapsId() {
        return this.mapsId;
    }

    public void setMapsId(String value) {
        this.mapsId = value;
    }

    public Boolean isId() {
        return this.id;
    }

    public void setId(Boolean value) {
        this.id = value;
    }
}

