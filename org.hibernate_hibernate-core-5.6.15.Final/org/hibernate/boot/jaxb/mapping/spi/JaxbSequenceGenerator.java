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
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.hibernate.boot.jaxb.mapping.spi.SchemaAware;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="sequence-generator", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm", propOrder={"description"})
public class JaxbSequenceGenerator
implements Serializable,
SchemaAware {
    @XmlElement(namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected String description;
    @XmlAttribute(name="name", required=true)
    protected String name;
    @XmlAttribute(name="sequence-name")
    protected String sequenceName;
    @XmlAttribute(name="catalog")
    protected String catalog;
    @XmlAttribute(name="schema")
    protected String schema;
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

    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public String getSequenceName() {
        return this.sequenceName;
    }

    public void setSequenceName(String value) {
        this.sequenceName = value;
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

