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
import org.hibernate.boot.jaxb.mapping.spi.JaxbForeignKey;
import org.hibernate.boot.jaxb.mapping.spi.JaxbIndex;
import org.hibernate.boot.jaxb.mapping.spi.JaxbJoinColumn;
import org.hibernate.boot.jaxb.mapping.spi.JaxbUniqueConstraint;
import org.hibernate.boot.jaxb.mapping.spi.SchemaAware;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="collection-table", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm", propOrder={"joinColumn", "foreignKey", "uniqueConstraint", "index"})
public class JaxbCollectionTable
implements Serializable,
SchemaAware {
    @XmlElement(name="join-column", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected List<JaxbJoinColumn> joinColumn;
    @XmlElement(name="foreign-key", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected JaxbForeignKey foreignKey;
    @XmlElement(name="unique-constraint", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected List<JaxbUniqueConstraint> uniqueConstraint;
    @XmlElement(namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected List<JaxbIndex> index;
    @XmlAttribute(name="name")
    protected String name;
    @XmlAttribute(name="catalog")
    protected String catalog;
    @XmlAttribute(name="schema")
    protected String schema;

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
}

