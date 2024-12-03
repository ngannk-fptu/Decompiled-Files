/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlType
 */
package org.hibernate.boot.jaxb.mapping.spi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.hibernate.boot.jaxb.mapping.spi.JaxbIndex;
import org.hibernate.boot.jaxb.mapping.spi.JaxbUniqueConstraint;
import org.hibernate.boot.jaxb.mapping.spi.SchemaAware;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="table-generator", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm", propOrder={"description", "uniqueConstraint", "index"})
public class JaxbTableGenerator
implements Serializable,
SchemaAware {
    @XmlElement(namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected String description;
    @XmlElement(name="unique-constraint", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected List<JaxbUniqueConstraint> uniqueConstraint;
    @XmlElement(namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected List<JaxbIndex> index;
    @XmlAttribute(name="name", required=true)
    protected String name;
    @XmlAttribute(name="table")
    protected String table;
    @XmlAttribute(name="catalog")
    protected String catalog;
    @XmlAttribute(name="schema")
    protected String schema;
    @XmlAttribute(name="pk-column-name")
    protected String pkColumnName;
    @XmlAttribute(name="value-column-name")
    protected String valueColumnName;
    @XmlAttribute(name="pk-column-value")
    protected String pkColumnValue;
    @XmlAttribute(name="initial-value")
    protected Integer initialValue;
    @XmlAttribute(name="allocation-size")
    protected Integer allocationSize;

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String value) {
        this.description = value;
    }

    public List<JaxbUniqueConstraint> getUniqueConstraint() {
        if (this.uniqueConstraint == null) {
            this.uniqueConstraint = new ArrayList<JaxbUniqueConstraint>();
        }
        return this.uniqueConstraint;
    }

    public List<JaxbIndex> getIndex() {
        if (this.index == null) {
            this.index = new ArrayList<JaxbIndex>();
        }
        return this.index;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public String getTable() {
        return this.table;
    }

    public void setTable(String value) {
        this.table = value;
    }

    @Override
    public String getCatalog() {
        return this.catalog;
    }

    @Override
    public void setCatalog(String value) {
        this.catalog = value;
    }

    @Override
    public String getSchema() {
        return this.schema;
    }

    @Override
    public void setSchema(String value) {
        this.schema = value;
    }

    public String getPkColumnName() {
        return this.pkColumnName;
    }

    public void setPkColumnName(String value) {
        this.pkColumnName = value;
    }

    public String getValueColumnName() {
        return this.valueColumnName;
    }

    public void setValueColumnName(String value) {
        this.valueColumnName = value;
    }

    public String getPkColumnValue() {
        return this.pkColumnValue;
    }

    public void setPkColumnValue(String value) {
        this.pkColumnValue = value;
    }

    public Integer getInitialValue() {
        return this.initialValue;
    }

    public void setInitialValue(Integer value) {
        this.initialValue = value;
    }

    public Integer getAllocationSize() {
        return this.allocationSize;
    }

    public void setAllocationSize(Integer value) {
        this.allocationSize = value;
    }
}

