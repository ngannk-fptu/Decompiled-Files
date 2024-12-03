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
 *  javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter
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
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.hibernate.boot.jaxb.hbm.spi.Adapter9;
import org.hibernate.boot.jaxb.hbm.spi.EntityInfo;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmAnyAssociationType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmArrayType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmBagCollectionType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmBasicAttributeType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmCacheType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmCompositeAttributeType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmCompositeIdType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmCustomSqlDmlType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmDiscriminatorSubclassEntityType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmDynamicComponentType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmEntityBaseDefinition;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmEntityDiscriminatorType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmFetchProfileType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmFilterType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmIdBagCollectionType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmJoinedSubclassEntityType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmListType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmLoaderType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmManyToOneType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmMapType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmMultiTenancyType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmNamedNativeQueryType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmNamedQueryType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmNaturalIdCacheType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmNaturalIdType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmOneToOneType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmPolymorphismEnum;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmPrimitiveArrayType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmPropertiesType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmResultSetMappingType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmSecondaryTableType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmSetType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmSimpleIdType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmSynchronizeType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmTimestampAttributeType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmTuplizerType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmUnionSubclassEntityType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmVersionAttributeType;
import org.hibernate.boot.jaxb.hbm.spi.SecondaryTableContainer;
import org.hibernate.boot.jaxb.hbm.spi.TableInformationContainer;
import org.hibernate.boot.jaxb.hbm.spi.ToolingHintContainer;
import org.hibernate.engine.OptimisticLockStyle;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="RootEntityType", namespace="http://www.hibernate.org/xsd/orm/hbm", propOrder={"tuplizer", "subselect", "cache", "naturalIdCache", "synchronize", "comment", "id", "compositeId", "discriminator", "naturalId", "version", "timestamp", "multiTenancy", "attributes", "join", "subclass", "joinedSubclass", "unionSubclass", "loader", "sqlInsert", "sqlUpdate", "sqlDelete", "filter", "fetchProfile", "resultset", "query", "sqlQuery"})
public class JaxbHbmRootEntityType
extends JaxbHbmEntityBaseDefinition
implements Serializable,
EntityInfo,
SecondaryTableContainer,
TableInformationContainer,
ToolingHintContainer {
    @XmlElement(namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected List<JaxbHbmTuplizerType> tuplizer;
    @XmlElement(namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected String subselect;
    @XmlElement(namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected JaxbHbmCacheType cache;
    @XmlElement(name="natural-id-cache", namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected JaxbHbmNaturalIdCacheType naturalIdCache;
    @XmlElement(namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected List<JaxbHbmSynchronizeType> synchronize;
    @XmlElement(namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected String comment;
    @XmlElement(namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected JaxbHbmSimpleIdType id;
    @XmlElement(name="composite-id", namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected JaxbHbmCompositeIdType compositeId;
    @XmlElement(namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected JaxbHbmEntityDiscriminatorType discriminator;
    @XmlElement(name="natural-id", namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected JaxbHbmNaturalIdType naturalId;
    @XmlElement(namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected JaxbHbmVersionAttributeType version;
    @XmlElement(namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected JaxbHbmTimestampAttributeType timestamp;
    @XmlElement(name="multi-tenancy", namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected JaxbHbmMultiTenancyType multiTenancy;
    @XmlElements(value={@XmlElement(name="idbag", namespace="http://www.hibernate.org/xsd/orm/hbm", type=JaxbHbmIdBagCollectionType.class), @XmlElement(name="property", namespace="http://www.hibernate.org/xsd/orm/hbm", type=JaxbHbmBasicAttributeType.class), @XmlElement(name="many-to-one", namespace="http://www.hibernate.org/xsd/orm/hbm", type=JaxbHbmManyToOneType.class), @XmlElement(name="one-to-one", namespace="http://www.hibernate.org/xsd/orm/hbm", type=JaxbHbmOneToOneType.class), @XmlElement(name="component", namespace="http://www.hibernate.org/xsd/orm/hbm", type=JaxbHbmCompositeAttributeType.class), @XmlElement(name="dynamic-component", namespace="http://www.hibernate.org/xsd/orm/hbm", type=JaxbHbmDynamicComponentType.class), @XmlElement(name="properties", namespace="http://www.hibernate.org/xsd/orm/hbm", type=JaxbHbmPropertiesType.class), @XmlElement(name="any", namespace="http://www.hibernate.org/xsd/orm/hbm", type=JaxbHbmAnyAssociationType.class), @XmlElement(name="map", namespace="http://www.hibernate.org/xsd/orm/hbm", type=JaxbHbmMapType.class), @XmlElement(name="set", namespace="http://www.hibernate.org/xsd/orm/hbm", type=JaxbHbmSetType.class), @XmlElement(name="list", namespace="http://www.hibernate.org/xsd/orm/hbm", type=JaxbHbmListType.class), @XmlElement(name="bag", namespace="http://www.hibernate.org/xsd/orm/hbm", type=JaxbHbmBagCollectionType.class), @XmlElement(name="array", namespace="http://www.hibernate.org/xsd/orm/hbm", type=JaxbHbmArrayType.class), @XmlElement(name="primitive-array", namespace="http://www.hibernate.org/xsd/orm/hbm", type=JaxbHbmPrimitiveArrayType.class)})
    protected List<Serializable> attributes;
    @XmlElement(namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected List<JaxbHbmSecondaryTableType> join;
    @XmlElement(namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected List<JaxbHbmDiscriminatorSubclassEntityType> subclass;
    @XmlElement(name="joined-subclass", namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected List<JaxbHbmJoinedSubclassEntityType> joinedSubclass;
    @XmlElement(name="union-subclass", namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected List<JaxbHbmUnionSubclassEntityType> unionSubclass;
    @XmlElement(namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected JaxbHbmLoaderType loader;
    @XmlElement(name="sql-insert", namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected JaxbHbmCustomSqlDmlType sqlInsert;
    @XmlElement(name="sql-update", namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected JaxbHbmCustomSqlDmlType sqlUpdate;
    @XmlElement(name="sql-delete", namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected JaxbHbmCustomSqlDmlType sqlDelete;
    @XmlElement(namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected List<JaxbHbmFilterType> filter;
    @XmlElement(name="fetch-profile", namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected List<JaxbHbmFetchProfileType> fetchProfile;
    @XmlElement(namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected List<JaxbHbmResultSetMappingType> resultset;
    @XmlElement(namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected List<JaxbHbmNamedQueryType> query;
    @XmlElement(name="sql-query", namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected List<JaxbHbmNamedNativeQueryType> sqlQuery;
    @XmlAttribute(name="catalog")
    protected String catalog;
    @XmlAttribute(name="check")
    protected String check;
    @XmlAttribute(name="discriminator-value")
    protected String discriminatorValue;
    @XmlAttribute(name="mutable")
    protected Boolean mutable;
    @XmlAttribute(name="optimistic-lock")
    @XmlJavaTypeAdapter(value=Adapter9.class)
    protected OptimisticLockStyle optimisticLock;
    @XmlAttribute(name="polymorphism")
    protected JaxbHbmPolymorphismEnum polymorphism;
    @XmlAttribute(name="rowid")
    protected String rowid;
    @XmlAttribute(name="schema")
    protected String schema;
    @XmlAttribute(name="subselect")
    protected String subselectAttribute;
    @XmlAttribute(name="table")
    protected String table;
    @XmlAttribute(name="where")
    protected String where;

    @Override
    public List<JaxbHbmTuplizerType> getTuplizer() {
        if (this.tuplizer == null) {
            this.tuplizer = new ArrayList<JaxbHbmTuplizerType>();
        }
        return this.tuplizer;
    }

    @Override
    public String getSubselect() {
        return this.subselect;
    }

    public void setSubselect(String value) {
        this.subselect = value;
    }

    public JaxbHbmCacheType getCache() {
        return this.cache;
    }

    public void setCache(JaxbHbmCacheType value) {
        this.cache = value;
    }

    public JaxbHbmNaturalIdCacheType getNaturalIdCache() {
        return this.naturalIdCache;
    }

    public void setNaturalIdCache(JaxbHbmNaturalIdCacheType value) {
        this.naturalIdCache = value;
    }

    @Override
    public List<JaxbHbmSynchronizeType> getSynchronize() {
        if (this.synchronize == null) {
            this.synchronize = new ArrayList<JaxbHbmSynchronizeType>();
        }
        return this.synchronize;
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(String value) {
        this.comment = value;
    }

    public JaxbHbmSimpleIdType getId() {
        return this.id;
    }

    public void setId(JaxbHbmSimpleIdType value) {
        this.id = value;
    }

    public JaxbHbmCompositeIdType getCompositeId() {
        return this.compositeId;
    }

    public void setCompositeId(JaxbHbmCompositeIdType value) {
        this.compositeId = value;
    }

    public JaxbHbmEntityDiscriminatorType getDiscriminator() {
        return this.discriminator;
    }

    public void setDiscriminator(JaxbHbmEntityDiscriminatorType value) {
        this.discriminator = value;
    }

    public JaxbHbmNaturalIdType getNaturalId() {
        return this.naturalId;
    }

    public void setNaturalId(JaxbHbmNaturalIdType value) {
        this.naturalId = value;
    }

    public JaxbHbmVersionAttributeType getVersion() {
        return this.version;
    }

    public void setVersion(JaxbHbmVersionAttributeType value) {
        this.version = value;
    }

    public JaxbHbmTimestampAttributeType getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(JaxbHbmTimestampAttributeType value) {
        this.timestamp = value;
    }

    public JaxbHbmMultiTenancyType getMultiTenancy() {
        return this.multiTenancy;
    }

    public void setMultiTenancy(JaxbHbmMultiTenancyType value) {
        this.multiTenancy = value;
    }

    @Override
    public List<Serializable> getAttributes() {
        if (this.attributes == null) {
            this.attributes = new ArrayList<Serializable>();
        }
        return this.attributes;
    }

    @Override
    public List<JaxbHbmSecondaryTableType> getJoin() {
        if (this.join == null) {
            this.join = new ArrayList<JaxbHbmSecondaryTableType>();
        }
        return this.join;
    }

    public List<JaxbHbmDiscriminatorSubclassEntityType> getSubclass() {
        if (this.subclass == null) {
            this.subclass = new ArrayList<JaxbHbmDiscriminatorSubclassEntityType>();
        }
        return this.subclass;
    }

    public List<JaxbHbmJoinedSubclassEntityType> getJoinedSubclass() {
        if (this.joinedSubclass == null) {
            this.joinedSubclass = new ArrayList<JaxbHbmJoinedSubclassEntityType>();
        }
        return this.joinedSubclass;
    }

    public List<JaxbHbmUnionSubclassEntityType> getUnionSubclass() {
        if (this.unionSubclass == null) {
            this.unionSubclass = new ArrayList<JaxbHbmUnionSubclassEntityType>();
        }
        return this.unionSubclass;
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

    public List<JaxbHbmFilterType> getFilter() {
        if (this.filter == null) {
            this.filter = new ArrayList<JaxbHbmFilterType>();
        }
        return this.filter;
    }

    @Override
    public List<JaxbHbmFetchProfileType> getFetchProfile() {
        if (this.fetchProfile == null) {
            this.fetchProfile = new ArrayList<JaxbHbmFetchProfileType>();
        }
        return this.fetchProfile;
    }

    @Override
    public List<JaxbHbmResultSetMappingType> getResultset() {
        if (this.resultset == null) {
            this.resultset = new ArrayList<JaxbHbmResultSetMappingType>();
        }
        return this.resultset;
    }

    @Override
    public List<JaxbHbmNamedQueryType> getQuery() {
        if (this.query == null) {
            this.query = new ArrayList<JaxbHbmNamedQueryType>();
        }
        return this.query;
    }

    @Override
    public List<JaxbHbmNamedNativeQueryType> getSqlQuery() {
        if (this.sqlQuery == null) {
            this.sqlQuery = new ArrayList<JaxbHbmNamedNativeQueryType>();
        }
        return this.sqlQuery;
    }

    @Override
    public String getCatalog() {
        return this.catalog;
    }

    public void setCatalog(String value) {
        this.catalog = value;
    }

    public String getCheck() {
        return this.check;
    }

    public void setCheck(String value) {
        this.check = value;
    }

    public String getDiscriminatorValue() {
        return this.discriminatorValue;
    }

    public void setDiscriminatorValue(String value) {
        this.discriminatorValue = value;
    }

    public boolean isMutable() {
        if (this.mutable == null) {
            return true;
        }
        return this.mutable;
    }

    public void setMutable(Boolean value) {
        this.mutable = value;
    }

    public OptimisticLockStyle getOptimisticLock() {
        if (this.optimisticLock == null) {
            return new Adapter9().unmarshal("version");
        }
        return this.optimisticLock;
    }

    public void setOptimisticLock(OptimisticLockStyle value) {
        this.optimisticLock = value;
    }

    public JaxbHbmPolymorphismEnum getPolymorphism() {
        if (this.polymorphism == null) {
            return JaxbHbmPolymorphismEnum.IMPLICIT;
        }
        return this.polymorphism;
    }

    public void setPolymorphism(JaxbHbmPolymorphismEnum value) {
        this.polymorphism = value;
    }

    public String getRowid() {
        return this.rowid;
    }

    public void setRowid(String value) {
        this.rowid = value;
    }

    @Override
    public String getSchema() {
        return this.schema;
    }

    public void setSchema(String value) {
        this.schema = value;
    }

    @Override
    public String getSubselectAttribute() {
        return this.subselectAttribute;
    }

    public void setSubselectAttribute(String value) {
        this.subselectAttribute = value;
    }

    @Override
    public String getTable() {
        return this.table;
    }

    public void setTable(String value) {
        this.table = value;
    }

    public String getWhere() {
        return this.where;
    }

    public void setWhere(String value) {
        this.where = value;
    }
}

