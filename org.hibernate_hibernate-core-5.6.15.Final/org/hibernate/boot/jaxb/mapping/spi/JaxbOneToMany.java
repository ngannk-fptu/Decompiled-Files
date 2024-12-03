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
import java.util.ArrayList;
import java.util.List;
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
import org.hibernate.boot.jaxb.mapping.spi.AssociationAttribute;
import org.hibernate.boot.jaxb.mapping.spi.CollectionAttribute;
import org.hibernate.boot.jaxb.mapping.spi.JaxbAttributeOverride;
import org.hibernate.boot.jaxb.mapping.spi.JaxbCascadeType;
import org.hibernate.boot.jaxb.mapping.spi.JaxbConvert;
import org.hibernate.boot.jaxb.mapping.spi.JaxbForeignKey;
import org.hibernate.boot.jaxb.mapping.spi.JaxbJoinColumn;
import org.hibernate.boot.jaxb.mapping.spi.JaxbJoinTable;
import org.hibernate.boot.jaxb.mapping.spi.JaxbMapKey;
import org.hibernate.boot.jaxb.mapping.spi.JaxbMapKeyClass;
import org.hibernate.boot.jaxb.mapping.spi.JaxbMapKeyColumn;
import org.hibernate.boot.jaxb.mapping.spi.JaxbMapKeyJoinColumn;
import org.hibernate.boot.jaxb.mapping.spi.JaxbOrderColumn;
import org.hibernate.boot.jaxb.mapping.spi.PersistentAttribute;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="one-to-many", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm", propOrder={"orderBy", "orderColumn", "mapKey", "mapKeyClass", "mapKeyTemporal", "mapKeyEnumerated", "mapKeyAttributeOverride", "mapKeyConvert", "mapKeyColumn", "mapKeyJoinColumn", "mapKeyForeignKey", "joinTable", "joinColumn", "foreignKey", "cascade"})
public class JaxbOneToMany
implements Serializable,
AssociationAttribute,
CollectionAttribute,
PersistentAttribute {
    @XmlElement(name="order-by", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected String orderBy;
    @XmlElement(name="order-column", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected JaxbOrderColumn orderColumn;
    @XmlElement(name="map-key", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected JaxbMapKey mapKey;
    @XmlElement(name="map-key-class", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected JaxbMapKeyClass mapKeyClass;
    @XmlElement(name="map-key-temporal", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm", type=String.class)
    @XmlJavaTypeAdapter(value=Adapter10.class)
    @XmlSchemaType(name="token")
    protected TemporalType mapKeyTemporal;
    @XmlElement(name="map-key-enumerated", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm", type=String.class)
    @XmlJavaTypeAdapter(value=Adapter4.class)
    @XmlSchemaType(name="token")
    protected EnumType mapKeyEnumerated;
    @XmlElement(name="map-key-attribute-override", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected List<JaxbAttributeOverride> mapKeyAttributeOverride;
    @XmlElement(name="map-key-convert", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected List<JaxbConvert> mapKeyConvert;
    @XmlElement(name="map-key-column", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected JaxbMapKeyColumn mapKeyColumn;
    @XmlElement(name="map-key-join-column", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected List<JaxbMapKeyJoinColumn> mapKeyJoinColumn;
    @XmlElement(name="map-key-foreign-key", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected JaxbForeignKey mapKeyForeignKey;
    @XmlElement(name="join-table", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected JaxbJoinTable joinTable;
    @XmlElement(name="join-column", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected List<JaxbJoinColumn> joinColumn;
    @XmlElement(name="foreign-key", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected JaxbForeignKey foreignKey;
    @XmlElement(namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected JaxbCascadeType cascade;
    @XmlAttribute(name="name", required=true)
    protected String name;
    @XmlAttribute(name="target-entity")
    protected String targetEntity;
    @XmlAttribute(name="fetch")
    @XmlJavaTypeAdapter(value=Adapter5.class)
    protected FetchType fetch;
    @XmlAttribute(name="access")
    @XmlJavaTypeAdapter(value=Adapter1.class)
    protected AccessType access;
    @XmlAttribute(name="mapped-by")
    protected String mappedBy;
    @XmlAttribute(name="orphan-removal")
    protected Boolean orphanRemoval;

    @Override
    public String getOrderBy() {
        return this.orderBy;
    }

    @Override
    public void setOrderBy(String value) {
        this.orderBy = value;
    }

    @Override
    public JaxbOrderColumn getOrderColumn() {
        return this.orderColumn;
    }

    @Override
    public void setOrderColumn(JaxbOrderColumn value) {
        this.orderColumn = value;
    }

    @Override
    public JaxbMapKey getMapKey() {
        return this.mapKey;
    }

    @Override
    public void setMapKey(JaxbMapKey value) {
        this.mapKey = value;
    }

    @Override
    public JaxbMapKeyClass getMapKeyClass() {
        return this.mapKeyClass;
    }

    @Override
    public void setMapKeyClass(JaxbMapKeyClass value) {
        this.mapKeyClass = value;
    }

    @Override
    public TemporalType getMapKeyTemporal() {
        return this.mapKeyTemporal;
    }

    @Override
    public void setMapKeyTemporal(TemporalType value) {
        this.mapKeyTemporal = value;
    }

    @Override
    public EnumType getMapKeyEnumerated() {
        return this.mapKeyEnumerated;
    }

    @Override
    public void setMapKeyEnumerated(EnumType value) {
        this.mapKeyEnumerated = value;
    }

    @Override
    public List<JaxbAttributeOverride> getMapKeyAttributeOverride() {
        if (this.mapKeyAttributeOverride == null) {
            this.mapKeyAttributeOverride = new ArrayList<JaxbAttributeOverride>();
        }
        return this.mapKeyAttributeOverride;
    }

    @Override
    public List<JaxbConvert> getMapKeyConvert() {
        if (this.mapKeyConvert == null) {
            this.mapKeyConvert = new ArrayList<JaxbConvert>();
        }
        return this.mapKeyConvert;
    }

    @Override
    public JaxbMapKeyColumn getMapKeyColumn() {
        return this.mapKeyColumn;
    }

    @Override
    public void setMapKeyColumn(JaxbMapKeyColumn value) {
        this.mapKeyColumn = value;
    }

    @Override
    public List<JaxbMapKeyJoinColumn> getMapKeyJoinColumn() {
        if (this.mapKeyJoinColumn == null) {
            this.mapKeyJoinColumn = new ArrayList<JaxbMapKeyJoinColumn>();
        }
        return this.mapKeyJoinColumn;
    }

    @Override
    public JaxbForeignKey getMapKeyForeignKey() {
        return this.mapKeyForeignKey;
    }

    @Override
    public void setMapKeyForeignKey(JaxbForeignKey value) {
        this.mapKeyForeignKey = value;
    }

    @Override
    public JaxbJoinTable getJoinTable() {
        return this.joinTable;
    }

    @Override
    public void setJoinTable(JaxbJoinTable value) {
        this.joinTable = value;
    }

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

    @Override
    public AccessType getAccess() {
        return this.access;
    }

    @Override
    public void setAccess(AccessType value) {
        this.access = value;
    }

    public String getMappedBy() {
        return this.mappedBy;
    }

    public void setMappedBy(String value) {
        this.mappedBy = value;
    }

    public Boolean isOrphanRemoval() {
        return this.orphanRemoval;
    }

    public void setOrphanRemoval(Boolean value) {
        this.orphanRemoval = value;
    }
}

