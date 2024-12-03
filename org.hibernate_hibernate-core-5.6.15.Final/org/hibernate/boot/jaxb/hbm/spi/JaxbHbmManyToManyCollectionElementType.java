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
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmColumnType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmFetchStyleEnum;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmFilterType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmLazyEnum;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmNotFoundEnum;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmOuterJoinEnum;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmToolingHintContainer;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="ManyToManyCollectionElementType", namespace="http://www.hibernate.org/xsd/orm/hbm", propOrder={"columnOrFormula", "filter"})
public class JaxbHbmManyToManyCollectionElementType
extends JaxbHbmToolingHintContainer
implements Serializable {
    @XmlElements(value={@XmlElement(name="column", namespace="http://www.hibernate.org/xsd/orm/hbm", type=JaxbHbmColumnType.class), @XmlElement(name="formula", namespace="http://www.hibernate.org/xsd/orm/hbm", type=String.class)})
    protected List<Serializable> columnOrFormula;
    @XmlElement(namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected List<JaxbHbmFilterType> filter;
    @XmlAttribute(name="class")
    protected String clazz;
    @XmlAttribute(name="column")
    protected String columnAttribute;
    @XmlAttribute(name="embed-xml")
    protected Boolean embedXml;
    @XmlAttribute(name="entity-name")
    protected String entityName;
    @XmlAttribute(name="fetch")
    protected JaxbHbmFetchStyleEnum fetch;
    @XmlAttribute(name="foreign-key")
    protected String foreignKey;
    @XmlAttribute(name="formula")
    protected String formulaAttribute;
    @XmlAttribute(name="lazy")
    protected JaxbHbmLazyEnum lazy;
    @XmlAttribute(name="node")
    protected String node;
    @XmlAttribute(name="not-found")
    protected JaxbHbmNotFoundEnum notFound;
    @XmlAttribute(name="order-by")
    protected String orderBy;
    @XmlAttribute(name="outer-join")
    protected JaxbHbmOuterJoinEnum outerJoin;
    @XmlAttribute(name="property-ref")
    protected String propertyRef;
    @XmlAttribute(name="unique")
    protected Boolean unique;
    @XmlAttribute(name="where")
    protected String where;

    public List<Serializable> getColumnOrFormula() {
        if (this.columnOrFormula == null) {
            this.columnOrFormula = new ArrayList<Serializable>();
        }
        return this.columnOrFormula;
    }

    public List<JaxbHbmFilterType> getFilter() {
        if (this.filter == null) {
            this.filter = new ArrayList<JaxbHbmFilterType>();
        }
        return this.filter;
    }

    public String getClazz() {
        return this.clazz;
    }

    public void setClazz(String value) {
        this.clazz = value;
    }

    public String getColumnAttribute() {
        return this.columnAttribute;
    }

    public void setColumnAttribute(String value) {
        this.columnAttribute = value;
    }

    public Boolean isEmbedXml() {
        return this.embedXml;
    }

    public void setEmbedXml(Boolean value) {
        this.embedXml = value;
    }

    public String getEntityName() {
        return this.entityName;
    }

    public void setEntityName(String value) {
        this.entityName = value;
    }

    public JaxbHbmFetchStyleEnum getFetch() {
        return this.fetch;
    }

    public void setFetch(JaxbHbmFetchStyleEnum value) {
        this.fetch = value;
    }

    public String getForeignKey() {
        return this.foreignKey;
    }

    public void setForeignKey(String value) {
        this.foreignKey = value;
    }

    public String getFormulaAttribute() {
        return this.formulaAttribute;
    }

    public void setFormulaAttribute(String value) {
        this.formulaAttribute = value;
    }

    public JaxbHbmLazyEnum getLazy() {
        return this.lazy;
    }

    public void setLazy(JaxbHbmLazyEnum value) {
        this.lazy = value;
    }

    public String getNode() {
        return this.node;
    }

    public void setNode(String value) {
        this.node = value;
    }

    public JaxbHbmNotFoundEnum getNotFound() {
        if (this.notFound == null) {
            return JaxbHbmNotFoundEnum.EXCEPTION;
        }
        return this.notFound;
    }

    public void setNotFound(JaxbHbmNotFoundEnum value) {
        this.notFound = value;
    }

    public String getOrderBy() {
        return this.orderBy;
    }

    public void setOrderBy(String value) {
        this.orderBy = value;
    }

    public JaxbHbmOuterJoinEnum getOuterJoin() {
        return this.outerJoin;
    }

    public void setOuterJoin(JaxbHbmOuterJoinEnum value) {
        this.outerJoin = value;
    }

    public String getPropertyRef() {
        return this.propertyRef;
    }

    public void setPropertyRef(String value) {
        this.propertyRef = value;
    }

    public boolean isUnique() {
        if (this.unique == null) {
            return false;
        }
        return this.unique;
    }

    public void setUnique(Boolean value) {
        this.unique = value;
    }

    public String getWhere() {
        return this.where;
    }

    public void setWhere(String value) {
        this.where = value;
    }
}

