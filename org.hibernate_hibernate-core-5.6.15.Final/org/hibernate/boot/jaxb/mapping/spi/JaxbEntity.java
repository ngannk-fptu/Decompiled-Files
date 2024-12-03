/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.AccessType
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlType
 *  javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter
 */
package org.hibernate.boot.jaxb.mapping.spi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.AccessType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.hibernate.boot.jaxb.mapping.spi.Adapter1;
import org.hibernate.boot.jaxb.mapping.spi.EntityOrMappedSuperclass;
import org.hibernate.boot.jaxb.mapping.spi.JaxbAssociationOverride;
import org.hibernate.boot.jaxb.mapping.spi.JaxbAttributeOverride;
import org.hibernate.boot.jaxb.mapping.spi.JaxbAttributes;
import org.hibernate.boot.jaxb.mapping.spi.JaxbConvert;
import org.hibernate.boot.jaxb.mapping.spi.JaxbDiscriminatorColumn;
import org.hibernate.boot.jaxb.mapping.spi.JaxbEmptyType;
import org.hibernate.boot.jaxb.mapping.spi.JaxbEntityListeners;
import org.hibernate.boot.jaxb.mapping.spi.JaxbForeignKey;
import org.hibernate.boot.jaxb.mapping.spi.JaxbIdClass;
import org.hibernate.boot.jaxb.mapping.spi.JaxbInheritance;
import org.hibernate.boot.jaxb.mapping.spi.JaxbNamedEntityGraph;
import org.hibernate.boot.jaxb.mapping.spi.JaxbNamedNativeQuery;
import org.hibernate.boot.jaxb.mapping.spi.JaxbNamedQuery;
import org.hibernate.boot.jaxb.mapping.spi.JaxbNamedStoredProcedureQuery;
import org.hibernate.boot.jaxb.mapping.spi.JaxbPostLoad;
import org.hibernate.boot.jaxb.mapping.spi.JaxbPostPersist;
import org.hibernate.boot.jaxb.mapping.spi.JaxbPostRemove;
import org.hibernate.boot.jaxb.mapping.spi.JaxbPostUpdate;
import org.hibernate.boot.jaxb.mapping.spi.JaxbPrePersist;
import org.hibernate.boot.jaxb.mapping.spi.JaxbPreRemove;
import org.hibernate.boot.jaxb.mapping.spi.JaxbPreUpdate;
import org.hibernate.boot.jaxb.mapping.spi.JaxbPrimaryKeyJoinColumn;
import org.hibernate.boot.jaxb.mapping.spi.JaxbSecondaryTable;
import org.hibernate.boot.jaxb.mapping.spi.JaxbSequenceGenerator;
import org.hibernate.boot.jaxb.mapping.spi.JaxbSqlResultSetMapping;
import org.hibernate.boot.jaxb.mapping.spi.JaxbTable;
import org.hibernate.boot.jaxb.mapping.spi.JaxbTableGenerator;
import org.hibernate.boot.jaxb.mapping.spi.LifecycleCallbackContainer;
import org.hibernate.boot.jaxb.mapping.spi.ManagedType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="entity", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm", propOrder={"description", "table", "secondaryTable", "primaryKeyJoinColumn", "primaryKeyForeignKey", "idClass", "inheritance", "discriminatorValue", "discriminatorColumn", "sequenceGenerator", "tableGenerator", "namedQuery", "namedNativeQuery", "namedStoredProcedureQuery", "sqlResultSetMapping", "excludeDefaultListeners", "excludeSuperclassListeners", "entityListeners", "prePersist", "postPersist", "preRemove", "postRemove", "preUpdate", "postUpdate", "postLoad", "attributeOverride", "associationOverride", "convert", "namedEntityGraph", "attributes"})
public class JaxbEntity
implements Serializable,
EntityOrMappedSuperclass,
LifecycleCallbackContainer,
ManagedType {
    @XmlElement(namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected String description;
    @XmlElement(namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected JaxbTable table;
    @XmlElement(name="secondary-table", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected List<JaxbSecondaryTable> secondaryTable;
    @XmlElement(name="primary-key-join-column", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected List<JaxbPrimaryKeyJoinColumn> primaryKeyJoinColumn;
    @XmlElement(name="primary-key-foreign-key", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected JaxbForeignKey primaryKeyForeignKey;
    @XmlElement(name="id-class", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected JaxbIdClass idClass;
    @XmlElement(namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected JaxbInheritance inheritance;
    @XmlElement(name="discriminator-value", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected String discriminatorValue;
    @XmlElement(name="discriminator-column", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected JaxbDiscriminatorColumn discriminatorColumn;
    @XmlElement(name="sequence-generator", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected JaxbSequenceGenerator sequenceGenerator;
    @XmlElement(name="table-generator", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected JaxbTableGenerator tableGenerator;
    @XmlElement(name="named-query", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected List<JaxbNamedQuery> namedQuery;
    @XmlElement(name="named-native-query", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected List<JaxbNamedNativeQuery> namedNativeQuery;
    @XmlElement(name="named-stored-procedure-query", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected List<JaxbNamedStoredProcedureQuery> namedStoredProcedureQuery;
    @XmlElement(name="sql-result-set-mapping", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected List<JaxbSqlResultSetMapping> sqlResultSetMapping;
    @XmlElement(name="exclude-default-listeners", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected JaxbEmptyType excludeDefaultListeners;
    @XmlElement(name="exclude-superclass-listeners", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected JaxbEmptyType excludeSuperclassListeners;
    @XmlElement(name="entity-listeners", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected JaxbEntityListeners entityListeners;
    @XmlElement(name="pre-persist", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected JaxbPrePersist prePersist;
    @XmlElement(name="post-persist", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected JaxbPostPersist postPersist;
    @XmlElement(name="pre-remove", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected JaxbPreRemove preRemove;
    @XmlElement(name="post-remove", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected JaxbPostRemove postRemove;
    @XmlElement(name="pre-update", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected JaxbPreUpdate preUpdate;
    @XmlElement(name="post-update", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected JaxbPostUpdate postUpdate;
    @XmlElement(name="post-load", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected JaxbPostLoad postLoad;
    @XmlElement(name="attribute-override", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected List<JaxbAttributeOverride> attributeOverride;
    @XmlElement(name="association-override", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected List<JaxbAssociationOverride> associationOverride;
    @XmlElement(namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected List<JaxbConvert> convert;
    @XmlElement(name="named-entity-graph", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected List<JaxbNamedEntityGraph> namedEntityGraph;
    @XmlElement(namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected JaxbAttributes attributes;
    @XmlAttribute(name="name")
    protected String name;
    @XmlAttribute(name="class", required=true)
    protected String clazz;
    @XmlAttribute(name="access")
    @XmlJavaTypeAdapter(value=Adapter1.class)
    protected AccessType access;
    @XmlAttribute(name="cacheable")
    protected Boolean cacheable;
    @XmlAttribute(name="metadata-complete")
    protected Boolean metadataComplete;

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public void setDescription(String value) {
        this.description = value;
    }

    public JaxbTable getTable() {
        return this.table;
    }

    public void setTable(JaxbTable value) {
        this.table = value;
    }

    public List<JaxbSecondaryTable> getSecondaryTable() {
        if (this.secondaryTable == null) {
            this.secondaryTable = new ArrayList<JaxbSecondaryTable>();
        }
        return this.secondaryTable;
    }

    public List<JaxbPrimaryKeyJoinColumn> getPrimaryKeyJoinColumn() {
        if (this.primaryKeyJoinColumn == null) {
            this.primaryKeyJoinColumn = new ArrayList<JaxbPrimaryKeyJoinColumn>();
        }
        return this.primaryKeyJoinColumn;
    }

    public JaxbForeignKey getPrimaryKeyForeignKey() {
        return this.primaryKeyForeignKey;
    }

    public void setPrimaryKeyForeignKey(JaxbForeignKey value) {
        this.primaryKeyForeignKey = value;
    }

    @Override
    public JaxbIdClass getIdClass() {
        return this.idClass;
    }

    @Override
    public void setIdClass(JaxbIdClass value) {
        this.idClass = value;
    }

    public JaxbInheritance getInheritance() {
        return this.inheritance;
    }

    public void setInheritance(JaxbInheritance value) {
        this.inheritance = value;
    }

    public String getDiscriminatorValue() {
        return this.discriminatorValue;
    }

    public void setDiscriminatorValue(String value) {
        this.discriminatorValue = value;
    }

    public JaxbDiscriminatorColumn getDiscriminatorColumn() {
        return this.discriminatorColumn;
    }

    public void setDiscriminatorColumn(JaxbDiscriminatorColumn value) {
        this.discriminatorColumn = value;
    }

    public JaxbSequenceGenerator getSequenceGenerator() {
        return this.sequenceGenerator;
    }

    public void setSequenceGenerator(JaxbSequenceGenerator value) {
        this.sequenceGenerator = value;
    }

    public JaxbTableGenerator getTableGenerator() {
        return this.tableGenerator;
    }

    public void setTableGenerator(JaxbTableGenerator value) {
        this.tableGenerator = value;
    }

    public List<JaxbNamedQuery> getNamedQuery() {
        if (this.namedQuery == null) {
            this.namedQuery = new ArrayList<JaxbNamedQuery>();
        }
        return this.namedQuery;
    }

    public List<JaxbNamedNativeQuery> getNamedNativeQuery() {
        if (this.namedNativeQuery == null) {
            this.namedNativeQuery = new ArrayList<JaxbNamedNativeQuery>();
        }
        return this.namedNativeQuery;
    }

    public List<JaxbNamedStoredProcedureQuery> getNamedStoredProcedureQuery() {
        if (this.namedStoredProcedureQuery == null) {
            this.namedStoredProcedureQuery = new ArrayList<JaxbNamedStoredProcedureQuery>();
        }
        return this.namedStoredProcedureQuery;
    }

    public List<JaxbSqlResultSetMapping> getSqlResultSetMapping() {
        if (this.sqlResultSetMapping == null) {
            this.sqlResultSetMapping = new ArrayList<JaxbSqlResultSetMapping>();
        }
        return this.sqlResultSetMapping;
    }

    @Override
    public JaxbEmptyType getExcludeDefaultListeners() {
        return this.excludeDefaultListeners;
    }

    @Override
    public void setExcludeDefaultListeners(JaxbEmptyType value) {
        this.excludeDefaultListeners = value;
    }

    @Override
    public JaxbEmptyType getExcludeSuperclassListeners() {
        return this.excludeSuperclassListeners;
    }

    @Override
    public void setExcludeSuperclassListeners(JaxbEmptyType value) {
        this.excludeSuperclassListeners = value;
    }

    @Override
    public JaxbEntityListeners getEntityListeners() {
        return this.entityListeners;
    }

    @Override
    public void setEntityListeners(JaxbEntityListeners value) {
        this.entityListeners = value;
    }

    @Override
    public JaxbPrePersist getPrePersist() {
        return this.prePersist;
    }

    @Override
    public void setPrePersist(JaxbPrePersist value) {
        this.prePersist = value;
    }

    @Override
    public JaxbPostPersist getPostPersist() {
        return this.postPersist;
    }

    @Override
    public void setPostPersist(JaxbPostPersist value) {
        this.postPersist = value;
    }

    @Override
    public JaxbPreRemove getPreRemove() {
        return this.preRemove;
    }

    @Override
    public void setPreRemove(JaxbPreRemove value) {
        this.preRemove = value;
    }

    @Override
    public JaxbPostRemove getPostRemove() {
        return this.postRemove;
    }

    @Override
    public void setPostRemove(JaxbPostRemove value) {
        this.postRemove = value;
    }

    @Override
    public JaxbPreUpdate getPreUpdate() {
        return this.preUpdate;
    }

    @Override
    public void setPreUpdate(JaxbPreUpdate value) {
        this.preUpdate = value;
    }

    @Override
    public JaxbPostUpdate getPostUpdate() {
        return this.postUpdate;
    }

    @Override
    public void setPostUpdate(JaxbPostUpdate value) {
        this.postUpdate = value;
    }

    @Override
    public JaxbPostLoad getPostLoad() {
        return this.postLoad;
    }

    @Override
    public void setPostLoad(JaxbPostLoad value) {
        this.postLoad = value;
    }

    public List<JaxbAttributeOverride> getAttributeOverride() {
        if (this.attributeOverride == null) {
            this.attributeOverride = new ArrayList<JaxbAttributeOverride>();
        }
        return this.attributeOverride;
    }

    public List<JaxbAssociationOverride> getAssociationOverride() {
        if (this.associationOverride == null) {
            this.associationOverride = new ArrayList<JaxbAssociationOverride>();
        }
        return this.associationOverride;
    }

    public List<JaxbConvert> getConvert() {
        if (this.convert == null) {
            this.convert = new ArrayList<JaxbConvert>();
        }
        return this.convert;
    }

    public List<JaxbNamedEntityGraph> getNamedEntityGraph() {
        if (this.namedEntityGraph == null) {
            this.namedEntityGraph = new ArrayList<JaxbNamedEntityGraph>();
        }
        return this.namedEntityGraph;
    }

    @Override
    public JaxbAttributes getAttributes() {
        return this.attributes;
    }

    @Override
    public void setAttributes(JaxbAttributes value) {
        this.attributes = value;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }

    @Override
    public String getClazz() {
        return this.clazz;
    }

    @Override
    public void setClazz(String value) {
        this.clazz = value;
    }

    @Override
    public AccessType getAccess() {
        return this.access;
    }

    @Override
    public void setAccess(AccessType value) {
        this.access = value;
    }

    public Boolean isCacheable() {
        return this.cacheable;
    }

    public void setCacheable(Boolean value) {
        this.cacheable = value;
    }

    @Override
    public Boolean isMetadataComplete() {
        return this.metadataComplete;
    }

    @Override
    public void setMetadataComplete(Boolean value) {
        this.metadataComplete = value;
    }
}

