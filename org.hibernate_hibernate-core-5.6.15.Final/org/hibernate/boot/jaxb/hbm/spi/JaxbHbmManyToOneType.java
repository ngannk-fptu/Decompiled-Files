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
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmLazyWithNoProxyEnum;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmNotFoundEnum;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmOnDeleteEnum;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmOuterJoinEnum;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmToolingHintContainer;
import org.hibernate.boot.jaxb.hbm.spi.SingularAttributeInfo;
import org.hibernate.boot.jaxb.hbm.spi.ToolingHintContainer;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="ManyToOneType", namespace="http://www.hibernate.org/xsd/orm/hbm", propOrder={"columnOrFormula"})
public class JaxbHbmManyToOneType
extends JaxbHbmToolingHintContainer
implements Serializable,
SingularAttributeInfo,
ToolingHintContainer {
    @XmlElements(value={@XmlElement(name="column", namespace="http://www.hibernate.org/xsd/orm/hbm", type=JaxbHbmColumnType.class), @XmlElement(name="formula", namespace="http://www.hibernate.org/xsd/orm/hbm", type=String.class)})
    protected List<Serializable> columnOrFormula;
    @XmlAttribute(name="access")
    protected String access;
    @XmlAttribute(name="cascade")
    protected String cascade;
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
    @XmlAttribute(name="index")
    protected String index;
    @XmlAttribute(name="insert")
    protected Boolean insert;
    @XmlAttribute(name="lazy")
    protected JaxbHbmLazyWithNoProxyEnum lazy;
    @XmlAttribute(name="name", required=true)
    protected String name;
    @XmlAttribute(name="node")
    protected String node;
    @XmlAttribute(name="not-found")
    protected JaxbHbmNotFoundEnum notFound;
    @XmlAttribute(name="not-null")
    protected Boolean notNull;
    @XmlAttribute(name="optimistic-lock")
    protected Boolean optimisticLock;
    @XmlAttribute(name="outer-join")
    protected JaxbHbmOuterJoinEnum outerJoin;
    @XmlAttribute(name="property-ref")
    protected String propertyRef;
    @XmlAttribute(name="unique")
    protected Boolean unique;
    @XmlAttribute(name="unique-key")
    protected String uniqueKey;
    @XmlAttribute(name="update")
    protected Boolean update;
    @XmlAttribute(name="on-delete")
    protected JaxbHbmOnDeleteEnum onDelete;

    public List<Serializable> getColumnOrFormula() {
        if (this.columnOrFormula == null) {
            this.columnOrFormula = new ArrayList<Serializable>();
        }
        return this.columnOrFormula;
    }

    @Override
    public String getAccess() {
        return this.access;
    }

    public void setAccess(String value) {
        this.access = value;
    }

    public String getCascade() {
        return this.cascade;
    }

    public void setCascade(String value) {
        this.cascade = value;
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

    public String getIndex() {
        return this.index;
    }

    public void setIndex(String value) {
        this.index = value;
    }

    public boolean isInsert() {
        if (this.insert == null) {
            return true;
        }
        return this.insert;
    }

    public void setInsert(Boolean value) {
        this.insert = value;
    }

    public JaxbHbmLazyWithNoProxyEnum getLazy() {
        return this.lazy;
    }

    public void setLazy(JaxbHbmLazyWithNoProxyEnum value) {
        this.lazy = value;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
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

    public Boolean isNotNull() {
        return this.notNull;
    }

    public void setNotNull(Boolean value) {
        this.notNull = value;
    }

    public boolean isOptimisticLock() {
        if (this.optimisticLock == null) {
            return true;
        }
        return this.optimisticLock;
    }

    public void setOptimisticLock(Boolean value) {
        this.optimisticLock = value;
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

    public String getUniqueKey() {
        return this.uniqueKey;
    }

    public void setUniqueKey(String value) {
        this.uniqueKey = value;
    }

    public boolean isUpdate() {
        if (this.update == null) {
            return true;
        }
        return this.update;
    }

    public void setUpdate(Boolean value) {
        this.update = value;
    }

    public JaxbHbmOnDeleteEnum getOnDelete() {
        if (this.onDelete == null) {
            return JaxbHbmOnDeleteEnum.NOACTION;
        }
        return this.onDelete;
    }

    public void setOnDelete(JaxbHbmOnDeleteEnum value) {
        this.onDelete = value;
    }
}

