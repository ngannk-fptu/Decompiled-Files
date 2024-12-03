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
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmSecondaryTableType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmSetType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmSubclassEntityBaseDefinition;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmSynchronizeType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmTuplizerType;
import org.hibernate.boot.jaxb.hbm.spi.SecondaryTableContainer;
import org.hibernate.boot.jaxb.hbm.spi.SubEntityInfo;
import org.hibernate.boot.jaxb.hbm.spi.ToolingHintContainer;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="DiscriminatorSubclassEntityType", namespace="http://www.hibernate.org/xsd/orm/hbm", propOrder={"tuplizer", "synchronize", "attributes", "join", "subclass", "loader", "sqlInsert", "sqlUpdate", "sqlDelete", "fetchProfile", "resultset", "query", "sqlQuery"})
public class JaxbHbmDiscriminatorSubclassEntityType
extends JaxbHbmSubclassEntityBaseDefinition
implements Serializable,
SecondaryTableContainer,
SubEntityInfo,
ToolingHintContainer {
    @XmlElement(namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected List<JaxbHbmTuplizerType> tuplizer;
    @XmlElement(namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected List<JaxbHbmSynchronizeType> synchronize;
    @XmlElements(value={@XmlElement(name="property", namespace="http://www.hibernate.org/xsd/orm/hbm", type=JaxbHbmBasicAttributeType.class), @XmlElement(name="many-to-one", namespace="http://www.hibernate.org/xsd/orm/hbm", type=JaxbHbmManyToOneType.class), @XmlElement(name="one-to-one", namespace="http://www.hibernate.org/xsd/orm/hbm", type=JaxbHbmOneToOneType.class), @XmlElement(name="component", namespace="http://www.hibernate.org/xsd/orm/hbm", type=JaxbHbmCompositeAttributeType.class), @XmlElement(name="dynamic-component", namespace="http://www.hibernate.org/xsd/orm/hbm", type=JaxbHbmDynamicComponentType.class), @XmlElement(name="properties", namespace="http://www.hibernate.org/xsd/orm/hbm", type=JaxbHbmPropertiesType.class), @XmlElement(name="any", namespace="http://www.hibernate.org/xsd/orm/hbm", type=JaxbHbmAnyAssociationType.class), @XmlElement(name="map", namespace="http://www.hibernate.org/xsd/orm/hbm", type=JaxbHbmMapType.class), @XmlElement(name="set", namespace="http://www.hibernate.org/xsd/orm/hbm", type=JaxbHbmSetType.class), @XmlElement(name="list", namespace="http://www.hibernate.org/xsd/orm/hbm", type=JaxbHbmListType.class), @XmlElement(name="bag", namespace="http://www.hibernate.org/xsd/orm/hbm", type=JaxbHbmBagCollectionType.class), @XmlElement(name="array", namespace="http://www.hibernate.org/xsd/orm/hbm", type=JaxbHbmArrayType.class), @XmlElement(name="primitive-array", namespace="http://www.hibernate.org/xsd/orm/hbm", type=JaxbHbmPrimitiveArrayType.class), @XmlElement(name="idbag", namespace="http://www.hibernate.org/xsd/orm/hbm", type=JaxbHbmIdBagCollectionType.class)})
    protected List<Serializable> attributes;
    @XmlElement(namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected List<JaxbHbmSecondaryTableType> join;
    @XmlElement(namespace="http://www.hibernate.org/xsd/orm/hbm")
    protected List<JaxbHbmDiscriminatorSubclassEntityType> subclass;
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
    @XmlAttribute(name="discriminator-value")
    protected String discriminatorValue;

    @Override
    public List<JaxbHbmTuplizerType> getTuplizer() {
        if (this.tuplizer == null) {
            this.tuplizer = new ArrayList<JaxbHbmTuplizerType>();
        }
        return this.tuplizer;
    }

    @Override
    public List<JaxbHbmSynchronizeType> getSynchronize() {
        if (this.synchronize == null) {
            this.synchronize = new ArrayList<JaxbHbmSynchronizeType>();
        }
        return this.synchronize;
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

    public String getDiscriminatorValue() {
        return this.discriminatorValue;
    }

    public void setDiscriminatorValue(String value) {
        this.discriminatorValue = value;
    }
}

