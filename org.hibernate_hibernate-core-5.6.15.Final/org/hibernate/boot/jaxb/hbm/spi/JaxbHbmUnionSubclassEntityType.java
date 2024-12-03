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
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmArrayType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmBagCollectionType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmBasicAttributeType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmCompositeAttributeType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmCustomSqlDmlType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmDynamicComponentType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmFetchProfileType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmIdBagCollectionType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmListType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmLoaderType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmManyToOneType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmMapType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmNamedNativeQueryType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmNamedQueryType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmOneToOneType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmPrimitiveArrayType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmPropertiesType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmResultSetMappingType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmSetType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmSubclassEntityBaseDefinition;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmSynchronizeType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmTuplizerType;
import org.hibernate.boot.jaxb.hbm.spi.SubEntityInfo;
import org.hibernate.boot.jaxb.hbm.spi.TableInformationContainer;
import org.hibernate.boot.jaxb.hbm.spi.ToolingHintContainer;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="UnionSubclassEntityType", namespace="http://www.hibernate.org/xsd/orm/hbm", propOrder={"tuplizer", "subselect", "synchronize", "comment", "attributes", "unionSubclass", "loader", "sqlInsert", "sqlUpdate", "sqlDelete", "fetchProfile", "resultset", "query", "sqlQuery"})
public class JaxbHbmUnionSubclassEntityType
extends JaxbHbmSubclassEntityBaseDefinition
implements Serializable,
SubEntityInfo,
TableInformationContainer,
ToolingHintContainer {
    @XmlElement(namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected List<JaxbHbmTuplizerType> tuplizer;
    @XmlElement(namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected String subselect;
    @XmlElement(namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected List<JaxbHbmSynchronizeType> synchronize;
    @XmlElement(namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected String comment;
    @XmlElements(value={@XmlElement(name="idbag", namespace="http://www.hibernate.org/xsd/orm/hbm", type=JaxbHbmIdBagCollectionType.class), @XmlElement(name="property", namespace="http://www.hibernate.org/xsd/orm/hbm", type=JaxbHbmBasicAttributeType.class), @XmlElement(name="many-to-one", namespace="http://www.hibernate.org/xsd/orm/hbm", type=JaxbHbmManyToOneType.class), @XmlElement(name="one-to-one", namespace="http://www.hibernate.org/xsd/orm/hbm", type=JaxbHbmOneToOneType.class), @XmlElement(name="component", namespace="http://www.hibernate.org/xsd/orm/hbm", type=JaxbHbmCompositeAttributeType.class), @XmlElement(name="dynamic-component", namespace="http://www.hibernate.org/xsd/orm/hbm", type=JaxbHbmDynamicComponentType.class), @XmlElement(name="properties", namespace="http://www.hibernate.org/xsd/orm/hbm", type=JaxbHbmPropertiesType.class), @XmlElement(name="any", namespace="http://www.hibernate.org/xsd/orm/hbm", type=JaxbHbmAnyAssociationType.class), @XmlElement(name="map", namespace="http://www.hibernate.org/xsd/orm/hbm", type=JaxbHbmMapType.class), @XmlElement(name="set", namespace="http://www.hibernate.org/xsd/orm/hbm", type=JaxbHbmSetType.class), @XmlElement(name="list", namespace="http://www.hibernate.org/xsd/orm/hbm", type=JaxbHbmListType.class), @XmlElement(name="bag", namespace="http://www.hibernate.org/xsd/orm/hbm", type=JaxbHbmBagCollectionType.class), @XmlElement(name="array", namespace="http://www.hibernate.org/xsd/orm/hbm", type=JaxbHbmArrayType.class), @XmlElement(name="primitive-array", namespace="http://www.hibernate.org/xsd/orm/hbm", type=JaxbHbmPrimitiveArrayType.class)})
    protected List<Serializable> attributes;
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
    @XmlElement(name="fetch-profile", namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected List<JaxbHbmFetchProfileType> fetchProfile;
    @XmlElement(namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected List<JaxbHbmResultSetMappingType> resultset;
    @XmlElement(namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected List<JaxbHbmNamedQueryType> query;
    @XmlElement(name="sql-query", namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected List<JaxbHbmNamedNativeQueryType> sqlQuery;
    @XmlAttribute(name="check")
    protected String check;
    @XmlAttribute(name="schema")
    protected String schema;
    @XmlAttribute(name="catalog")
    protected String catalog;
    @XmlAttribute(name="table")
    protected String table;
    @XmlAttribute(name="subselect")
    protected String subselectAttribute;

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

    @Override
    public List<Serializable> getAttributes() {
        if (this.attributes == null) {
            this.attributes = new ArrayList<Serializable>();
        }
        return this.attributes;
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

    public String getCheck() {
        return this.check;
    }

    public void setCheck(String value) {
        this.check = value;
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

