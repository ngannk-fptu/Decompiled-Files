/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlType
 */
package org.hibernate.boot.jaxb.mapping.spi;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="map-key-column", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
public class JaxbMapKeyColumn
implements Serializable {
    @XmlAttribute(name="name")
    protected String name;
    @XmlAttribute(name="unique")
    protected Boolean unique;
    @XmlAttribute(name="nullable")
    protected Boolean nullable;
    @XmlAttribute(name="insertable")
    protected Boolean insertable;
    @XmlAttribute(name="updatable")
    protected Boolean updatable;
    @XmlAttribute(name="column-definition")
    protected String columnDefinition;
    @XmlAttribute(name="table")
    protected String table;
    @XmlAttribute(name="length")
    protected Integer length;
    @XmlAttribute(name="precision")
    protected Integer precision;
    @XmlAttribute(name="scale")
    protected Integer scale;

    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public Boolean isUnique() {
        return this.unique;
    }

    public void setUnique(Boolean value) {
        this.unique = value;
    }

    public Boolean isNullable() {
        return this.nullable;
    }

    public void setNullable(Boolean value) {
        this.nullable = value;
    }

    public Boolean isInsertable() {
        return this.insertable;
    }

    public void setInsertable(Boolean value) {
        this.insertable = value;
    }

    public Boolean isUpdatable() {
        return this.updatable;
    }

    public void setUpdatable(Boolean value) {
        this.updatable = value;
    }

    public String getColumnDefinition() {
        return this.columnDefinition;
    }

    public void setColumnDefinition(String value) {
        this.columnDefinition = value;
    }

    public String getTable() {
        return this.table;
    }

    public void setTable(String value) {
        this.table = value;
    }

    public Integer getLength() {
        return this.length;
    }

    public void setLength(Integer value) {
        this.length = value;
    }

    public Integer getPrecision() {
        return this.precision;
    }

    public void setPrecision(Integer value) {
        this.precision = value;
    }

    public Integer getScale() {
        return this.scale;
    }

    public void setScale(Integer value) {
        this.scale = value;
    }
}

