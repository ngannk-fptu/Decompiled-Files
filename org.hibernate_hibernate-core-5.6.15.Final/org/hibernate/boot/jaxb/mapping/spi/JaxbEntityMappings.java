/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.AccessType
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 *  javax.xml.bind.annotation.XmlSchemaType
 *  javax.xml.bind.annotation.XmlType
 *  javax.xml.bind.annotation.adapters.CollapsedStringAdapter
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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.hibernate.boot.jaxb.mapping.spi.Adapter1;
import org.hibernate.boot.jaxb.mapping.spi.JaxbConverter;
import org.hibernate.boot.jaxb.mapping.spi.JaxbEmbeddable;
import org.hibernate.boot.jaxb.mapping.spi.JaxbEntity;
import org.hibernate.boot.jaxb.mapping.spi.JaxbMappedSuperclass;
import org.hibernate.boot.jaxb.mapping.spi.JaxbNamedNativeQuery;
import org.hibernate.boot.jaxb.mapping.spi.JaxbNamedQuery;
import org.hibernate.boot.jaxb.mapping.spi.JaxbNamedStoredProcedureQuery;
import org.hibernate.boot.jaxb.mapping.spi.JaxbPersistenceUnitMetadata;
import org.hibernate.boot.jaxb.mapping.spi.JaxbSequenceGenerator;
import org.hibernate.boot.jaxb.mapping.spi.JaxbSqlResultSetMapping;
import org.hibernate.boot.jaxb.mapping.spi.JaxbTableGenerator;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="", propOrder={"description", "persistenceUnitMetadata", "_package", "schema", "catalog", "access", "sequenceGenerator", "tableGenerator", "namedQuery", "namedNativeQuery", "namedStoredProcedureQuery", "sqlResultSetMapping", "mappedSuperclass", "entity", "embeddable", "converter"})
@XmlRootElement(name="entity-mappings", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
public class JaxbEntityMappings
implements Serializable {
    @XmlElement(namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected String description;
    @XmlElement(name="persistence-unit-metadata", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected JaxbPersistenceUnitMetadata persistenceUnitMetadata;
    @XmlElement(name="package", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected String _package;
    @XmlElement(namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected String schema;
    @XmlElement(namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected String catalog;
    @XmlElement(namespace="http://xmlns.jcp.org/xml/ns/persistence/orm", type=String.class)
    @XmlJavaTypeAdapter(value=Adapter1.class)
    @XmlSchemaType(name="token")
    protected AccessType access;
    @XmlElement(name="sequence-generator", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected List<JaxbSequenceGenerator> sequenceGenerator;
    @XmlElement(name="table-generator", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected List<JaxbTableGenerator> tableGenerator;
    @XmlElement(name="named-query", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected List<JaxbNamedQuery> namedQuery;
    @XmlElement(name="named-native-query", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected List<JaxbNamedNativeQuery> namedNativeQuery;
    @XmlElement(name="named-stored-procedure-query", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected List<JaxbNamedStoredProcedureQuery> namedStoredProcedureQuery;
    @XmlElement(name="sql-result-set-mapping", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected List<JaxbSqlResultSetMapping> sqlResultSetMapping;
    @XmlElement(name="mapped-superclass", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected List<JaxbMappedSuperclass> mappedSuperclass;
    @XmlElement(namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected List<JaxbEntity> entity;
    @XmlElement(namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected List<JaxbEmbeddable> embeddable;
    @XmlElement(namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected List<JaxbConverter> converter;
    @XmlAttribute(name="version", required=true)
    @XmlJavaTypeAdapter(value=CollapsedStringAdapter.class)
    protected String version;

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String value) {
        this.description = value;
    }

    public JaxbPersistenceUnitMetadata getPersistenceUnitMetadata() {
        return this.persistenceUnitMetadata;
    }

    public void setPersistenceUnitMetadata(JaxbPersistenceUnitMetadata value) {
        this.persistenceUnitMetadata = value;
    }

    public String getPackage() {
        return this._package;
    }

    public void setPackage(String value) {
        this._package = value;
    }

    public String getSchema() {
        return this.schema;
    }

    public void setSchema(String value) {
        this.schema = value;
    }

    public String getCatalog() {
        return this.catalog;
    }

    public void setCatalog(String value) {
        this.catalog = value;
    }

    public AccessType getAccess() {
        return this.access;
    }

    public void setAccess(AccessType value) {
        this.access = value;
    }

    public List<JaxbSequenceGenerator> getSequenceGenerator() {
        if (this.sequenceGenerator == null) {
            this.sequenceGenerator = new ArrayList<JaxbSequenceGenerator>();
        }
        return this.sequenceGenerator;
    }

    public List<JaxbTableGenerator> getTableGenerator() {
        if (this.tableGenerator == null) {
            this.tableGenerator = new ArrayList<JaxbTableGenerator>();
        }
        return this.tableGenerator;
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

    public List<JaxbMappedSuperclass> getMappedSuperclass() {
        if (this.mappedSuperclass == null) {
            this.mappedSuperclass = new ArrayList<JaxbMappedSuperclass>();
        }
        return this.mappedSuperclass;
    }

    public List<JaxbEntity> getEntity() {
        if (this.entity == null) {
            this.entity = new ArrayList<JaxbEntity>();
        }
        return this.entity;
    }

    public List<JaxbEmbeddable> getEmbeddable() {
        if (this.embeddable == null) {
            this.embeddable = new ArrayList<JaxbEmbeddable>();
        }
        return this.embeddable;
    }

    public List<JaxbConverter> getConverter() {
        if (this.converter == null) {
            this.converter = new ArrayList<JaxbConverter>();
        }
        return this.converter;
    }

    public String getVersion() {
        if (this.version == null) {
            return "2.2";
        }
        return this.version;
    }

    public void setVersion(String value) {
        this.version = value;
    }
}

