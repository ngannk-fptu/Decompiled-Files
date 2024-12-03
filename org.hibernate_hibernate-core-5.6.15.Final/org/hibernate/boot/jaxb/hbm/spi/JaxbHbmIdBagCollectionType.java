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
package org.hibernate.boot.jaxb.hbm.spi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmBasicCollectionElementType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmCacheType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmCollectionIdType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmCompositeCollectionElementType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmCustomSqlDmlType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmFetchStyleWithSubselectEnum;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmFilterType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmKeyType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmLazyWithExtraEnum;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmLoaderType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmManyToAnyCollectionElementType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmManyToManyCollectionElementType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmOuterJoinEnum;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmSynchronizeType;
import org.hibernate.boot.jaxb.hbm.spi.PluralAttributeInfo;
import org.hibernate.boot.jaxb.hbm.spi.PluralAttributeInfoIdBagAdapter;
import org.hibernate.boot.jaxb.hbm.spi.ToolingHintContainer;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="IdBagCollectionType", namespace="http://www.hibernate.org/xsd/orm/hbm", propOrder={"subselect", "cache", "synchronize", "comment", "collectionId", "key", "element", "manyToMany", "compositeElement", "manyToAny", "loader", "sqlInsert", "sqlUpdate", "sqlDelete", "sqlDeleteAll", "filter"})
public class JaxbHbmIdBagCollectionType
extends PluralAttributeInfoIdBagAdapter
implements Serializable,
PluralAttributeInfo,
ToolingHintContainer {
    @XmlElement(namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected String subselect;
    @XmlElement(namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected JaxbHbmCacheType cache;
    @XmlElement(namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected List<JaxbHbmSynchronizeType> synchronize;
    @XmlElement(namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected String comment;
    @XmlElement(name="collection-id", namespace="http://www.hibernate.org/xsd/orm/hbm", required=true)
    protected JaxbHbmCollectionIdType collectionId;
    @XmlElement(namespace="http://www.hibernate.org/xsd/orm/hbm", required=true)
    protected JaxbHbmKeyType key;
    @XmlElement(namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected JaxbHbmBasicCollectionElementType element;
    @XmlElement(name="many-to-many", namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected JaxbHbmManyToManyCollectionElementType manyToMany;
    @XmlElement(name="composite-element", namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected JaxbHbmCompositeCollectionElementType compositeElement;
    @XmlElement(name="many-to-any", namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected JaxbHbmManyToAnyCollectionElementType manyToAny;
    @XmlElement(namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected JaxbHbmLoaderType loader;
    @XmlElement(name="sql-insert", namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected JaxbHbmCustomSqlDmlType sqlInsert;
    @XmlElement(name="sql-update", namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected JaxbHbmCustomSqlDmlType sqlUpdate;
    @XmlElement(name="sql-delete", namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected JaxbHbmCustomSqlDmlType sqlDelete;
    @XmlElement(name="sql-delete-all", namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected JaxbHbmCustomSqlDmlType sqlDeleteAll;
    @XmlElement(namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected List<JaxbHbmFilterType> filter;
    @XmlAttribute(name="embed-xml")
    protected Boolean embedXml;
    @XmlAttribute(name="fetch")
    protected JaxbHbmFetchStyleWithSubselectEnum fetch;
    @XmlAttribute(name="lazy")
    protected JaxbHbmLazyWithExtraEnum lazy;
    @XmlAttribute(name="node")
    protected String node;
    @XmlAttribute(name="order-by")
    protected String orderBy;
    @XmlAttribute(name="outer-join")
    protected JaxbHbmOuterJoinEnum outerJoin;
    @XmlAttribute(name="schema")
    protected String schema;
    @XmlAttribute(name="catalog")
    protected String catalog;
    @XmlAttribute(name="table")
    protected String table;
    @XmlAttribute(name="subselect")
    protected String subselectAttribute;
    @XmlAttribute(name="name", required=true)
    protected String name;
    @XmlAttribute(name="access")
    protected String access;
    @XmlAttribute(name="check")
    protected String check;
    @XmlAttribute(name="where")
    protected String where;
    @XmlAttribute(name="cascade")
    protected String cascade;
    @XmlAttribute(name="batch-size")
    protected Integer batchSize;
    @XmlAttribute(name="inverse")
    protected Boolean inverse;
    @XmlAttribute(name="mutable")
    protected Boolean mutable;
    @XmlAttribute(name="optimistic-lock")
    protected Boolean optimisticLock;
    @XmlAttribute(name="collection-type")
    protected String collectionType;
    @XmlAttribute(name="persister")
    protected String persister;

    @Override
    public String getSubselect() {
        return this.subselect;
    }

    public void setSubselect(String value) {
        this.subselect = value;
    }

    @Override
    public JaxbHbmCacheType getCache() {
        return this.cache;
    }

    public void setCache(JaxbHbmCacheType value) {
        this.cache = value;
    }

    @Override
    public List<JaxbHbmSynchronizeType> getSynchronize() {
        if (this.synchronize == null) {
            this.synchronize = new ArrayList<JaxbHbmSynchronizeType>();
        }
        return this.synchronize;
    }

    @Override
    public String getComment() {
        return this.comment;
    }

    public void setComment(String value) {
        this.comment = value;
    }

    public JaxbHbmCollectionIdType getCollectionId() {
        return this.collectionId;
    }

    public void setCollectionId(JaxbHbmCollectionIdType value) {
        this.collectionId = value;
    }

    @Override
    public JaxbHbmKeyType getKey() {
        return this.key;
    }

    public void setKey(JaxbHbmKeyType value) {
        this.key = value;
    }

    @Override
    public JaxbHbmBasicCollectionElementType getElement() {
        return this.element;
    }

    public void setElement(JaxbHbmBasicCollectionElementType value) {
        this.element = value;
    }

    @Override
    public JaxbHbmManyToManyCollectionElementType getManyToMany() {
        return this.manyToMany;
    }

    public void setManyToMany(JaxbHbmManyToManyCollectionElementType value) {
        this.manyToMany = value;
    }

    @Override
    public JaxbHbmCompositeCollectionElementType getCompositeElement() {
        return this.compositeElement;
    }

    public void setCompositeElement(JaxbHbmCompositeCollectionElementType value) {
        this.compositeElement = value;
    }

    @Override
    public JaxbHbmManyToAnyCollectionElementType getManyToAny() {
        return this.manyToAny;
    }

    public void setManyToAny(JaxbHbmManyToAnyCollectionElementType value) {
        this.manyToAny = value;
    }

    @Override
    public JaxbHbmLoaderType getLoader() {
        return this.loader;
    }

    public void setLoader(JaxbHbmLoaderType value) {
        this.loader = value;
    }

    @Override
    public JaxbHbmCustomSqlDmlType getSqlInsert() {
        return this.sqlInsert;
    }

    public void setSqlInsert(JaxbHbmCustomSqlDmlType value) {
        this.sqlInsert = value;
    }

    @Override
    public JaxbHbmCustomSqlDmlType getSqlUpdate() {
        return this.sqlUpdate;
    }

    public void setSqlUpdate(JaxbHbmCustomSqlDmlType value) {
        this.sqlUpdate = value;
    }

    @Override
    public JaxbHbmCustomSqlDmlType getSqlDelete() {
        return this.sqlDelete;
    }

    public void setSqlDelete(JaxbHbmCustomSqlDmlType value) {
        this.sqlDelete = value;
    }

    @Override
    public JaxbHbmCustomSqlDmlType getSqlDeleteAll() {
        return this.sqlDeleteAll;
    }

    public void setSqlDeleteAll(JaxbHbmCustomSqlDmlType value) {
        this.sqlDeleteAll = value;
    }

    @Override
    public List<JaxbHbmFilterType> getFilter() {
        if (this.filter == null) {
            this.filter = new ArrayList<JaxbHbmFilterType>();
        }
        return this.filter;
    }

    public Boolean isEmbedXml() {
        return this.embedXml;
    }

    public void setEmbedXml(Boolean value) {
        this.embedXml = value;
    }

    @Override
    public JaxbHbmFetchStyleWithSubselectEnum getFetch() {
        return this.fetch;
    }

    public void setFetch(JaxbHbmFetchStyleWithSubselectEnum value) {
        this.fetch = value;
    }

    @Override
    public JaxbHbmLazyWithExtraEnum getLazy() {
        return this.lazy;
    }

    public void setLazy(JaxbHbmLazyWithExtraEnum value) {
        this.lazy = value;
    }

    public String getNode() {
        return this.node;
    }

    public void setNode(String value) {
        this.node = value;
    }

    public String getOrderBy() {
        return this.orderBy;
    }

    public void setOrderBy(String value) {
        this.orderBy = value;
    }

    @Override
    public JaxbHbmOuterJoinEnum getOuterJoin() {
        return this.outerJoin;
    }

    public void setOuterJoin(JaxbHbmOuterJoinEnum value) {
        this.outerJoin = value;
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

    @Override
    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }

    @Override
    public String getAccess() {
        return this.access;
    }

    public void setAccess(String value) {
        this.access = value;
    }

    @Override
    public String getCheck() {
        return this.check;
    }

    public void setCheck(String value) {
        this.check = value;
    }

    @Override
    public String getWhere() {
        return this.where;
    }

    public void setWhere(String value) {
        this.where = value;
    }

    @Override
    public String getCascade() {
        return this.cascade;
    }

    public void setCascade(String value) {
        this.cascade = value;
    }

    @Override
    public int getBatchSize() {
        if (this.batchSize == null) {
            return -1;
        }
        return this.batchSize;
    }

    public void setBatchSize(Integer value) {
        this.batchSize = value;
    }

    @Override
    public boolean isInverse() {
        if (this.inverse == null) {
            return false;
        }
        return this.inverse;
    }

    public void setInverse(Boolean value) {
        this.inverse = value;
    }

    @Override
    public boolean isMutable() {
        if (this.mutable == null) {
            return true;
        }
        return this.mutable;
    }

    public void setMutable(Boolean value) {
        this.mutable = value;
    }

    @Override
    public boolean isOptimisticLock() {
        if (this.optimisticLock == null) {
            return true;
        }
        return this.optimisticLock;
    }

    public void setOptimisticLock(Boolean value) {
        this.optimisticLock = value;
    }

    @Override
    public String getCollectionType() {
        return this.collectionType;
    }

    public void setCollectionType(String value) {
        this.collectionType = value;
    }

    @Override
    public String getPersister() {
        return this.persister;
    }

    public void setPersister(String value) {
        this.persister = value;
    }
}

