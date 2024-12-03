/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlElements
 *  javax.xml.bind.annotation.XmlType
 */
package org.hibernate.boot.jaxb.hbm.spi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmAnyAssociationType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmBasicAttributeType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmCompositeAttributeType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmCustomSqlDmlType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmDynamicComponentType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmFetchStyleEnum;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmKeyType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmManyToOneType;
import org.hibernate.boot.jaxb.hbm.spi.TableInformationContainer;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="SecondaryTableType", namespace="http://www.hibernate.org/xsd/orm/hbm", propOrder={"subselect", "comment", "key", "attributes", "sqlInsert", "sqlUpdate", "sqlDelete"})
public class JaxbHbmSecondaryTableType
implements Serializable,
TableInformationContainer {
    @XmlElement(namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected String subselect;
    @XmlElement(namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected String comment;
    @XmlElement(namespace="http://www.hibernate.org/xsd/orm/hbm", required=true)
    protected JaxbHbmKeyType key;
    @XmlElements(value={@XmlElement(name="property", namespace="http://www.hibernate.org/xsd/orm/hbm", type=JaxbHbmBasicAttributeType.class), @XmlElement(name="many-to-one", namespace="http://www.hibernate.org/xsd/orm/hbm", type=JaxbHbmManyToOneType.class), @XmlElement(name="component", namespace="http://www.hibernate.org/xsd/orm/hbm", type=JaxbHbmCompositeAttributeType.class), @XmlElement(name="dynamic-component", namespace="http://www.hibernate.org/xsd/orm/hbm", type=JaxbHbmDynamicComponentType.class), @XmlElement(name="any", namespace="http://www.hibernate.org/xsd/orm/hbm", type=JaxbHbmAnyAssociationType.class)})
    protected List<Serializable> attributes;
    @XmlElement(name="sql-insert", namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected JaxbHbmCustomSqlDmlType sqlInsert;
    @XmlElement(name="sql-update", namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected JaxbHbmCustomSqlDmlType sqlUpdate;
    @XmlElement(name="sql-delete", namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected JaxbHbmCustomSqlDmlType sqlDelete;
    @XmlAttribute(name="fetch")
    protected JaxbHbmFetchStyleEnum fetch;
    @XmlAttribute(name="inverse")
    protected Boolean inverse;
    @XmlAttribute(name="optional")
    protected Boolean optional;
    @XmlAttribute(name="schema")
    protected String schema;
    @XmlAttribute(name="catalog")
    protected String catalog;
    @XmlAttribute(name="table")
    protected String table;
    @XmlAttribute(name="subselect")
    protected String subselectAttribute;

    @Override
    public String getSubselect() {
        return this.subselect;
    }

    public void setSubselect(String value) {
        this.subselect = value;
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(String value) {
        this.comment = value;
    }

    public JaxbHbmKeyType getKey() {
        return this.key;
    }

    public void setKey(JaxbHbmKeyType value) {
        this.key = value;
    }

    public List<Serializable> getAttributes() {
        if (this.attributes == null) {
            this.attributes = new ArrayList<Serializable>();
        }
        return this.attributes;
    }

    public JaxbHbmCustomSqlDmlType getSqlInsert() {
        return this.sqlInsert;
    }

    public void setSqlInsert(JaxbHbmCustomSqlDmlType value) {
        this.sqlInsert = value;
    }

    public JaxbHbmCustomSqlDmlType getSqlUpdate() {
        return this.sqlUpdate;
    }

    public void setSqlUpdate(JaxbHbmCustomSqlDmlType value) {
        this.sqlUpdate = value;
    }

    public JaxbHbmCustomSqlDmlType getSqlDelete() {
        return this.sqlDelete;
    }

    public void setSqlDelete(JaxbHbmCustomSqlDmlType value) {
        this.sqlDelete = value;
    }

    public JaxbHbmFetchStyleEnum getFetch() {
        if (this.fetch == null) {
            return JaxbHbmFetchStyleEnum.JOIN;
        }
        return this.fetch;
    }

    public void setFetch(JaxbHbmFetchStyleEnum value) {
        this.fetch = value;
    }

    public boolean isInverse() {
        if (this.inverse == null) {
            return false;
        }
        return this.inverse;
    }

    public void setInverse(Boolean value) {
        this.inverse = value;
    }

    public boolean isOptional() {
        if (this.optional == null) {
            return false;
        }
        return this.optional;
    }

    public void setOptional(Boolean value) {
        this.optional = value;
    }

    @Override
    public String getSchema() {
        return this.schema;
    }

    public void setSchema(String value) {
        this.schema = value;
    }

    @Override
    public String getCatalog() {
        return this.catalog;
    }

    public void setCatalog(String value) {
        this.catalog = value;
    }

    @Override
    public String getTable() {
        return this.table;
    }

    public void setTable(String value) {
        this.table = value;
    }

    @Override
    public String getSubselectAttribute() {
        return this.subselectAttribute;
    }

    public void setSubselectAttribute(String value) {
        this.subselectAttribute = value;
    }
}

