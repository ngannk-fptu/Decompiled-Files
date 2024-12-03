/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.AccessType
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlSchemaType
 *  javax.xml.bind.annotation.XmlType
 *  javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter
 */
package org.hibernate.boot.jaxb.mapping.spi;

import java.io.Serializable;
import javax.persistence.AccessType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.hibernate.boot.jaxb.mapping.spi.Adapter1;
import org.hibernate.boot.jaxb.mapping.spi.JaxbEmptyType;
import org.hibernate.boot.jaxb.mapping.spi.JaxbEntityListeners;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="persistence-unit-defaults", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm", propOrder={"description", "schema", "catalog", "delimitedIdentifiers", "access", "cascadePersist", "entityListeners"})
public class JaxbPersistenceUnitDefaults
implements Serializable {
    @XmlElement(namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected String description;
    @XmlElement(namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected String schema;
    @XmlElement(namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected String catalog;
    @XmlElement(name="delimited-identifiers", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected JaxbEmptyType delimitedIdentifiers;
    @XmlElement(namespace="http://xmlns.jcp.org/xml/ns/persistence/orm", type=String.class)
    @XmlJavaTypeAdapter(value=Adapter1.class)
    @XmlSchemaType(name="token")
    protected AccessType access;
    @XmlElement(name="cascade-persist", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected JaxbEmptyType cascadePersist;
    @XmlElement(name="entity-listeners", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected JaxbEntityListeners entityListeners;

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String value) {
        this.description = value;
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

    public JaxbEmptyType getDelimitedIdentifiers() {
        return this.delimitedIdentifiers;
    }

    public void setDelimitedIdentifiers(JaxbEmptyType value) {
        this.delimitedIdentifiers = value;
    }

    public AccessType getAccess() {
        return this.access;
    }

    public void setAccess(AccessType value) {
        this.access = value;
    }

    public JaxbEmptyType getCascadePersist() {
        return this.cascadePersist;
    }

    public void setCascadePersist(JaxbEmptyType value) {
        this.cascadePersist = value;
    }

    public JaxbEntityListeners getEntityListeners() {
        return this.entityListeners;
    }

    public void setEntityListeners(JaxbEntityListeners value) {
        this.entityListeners = value;
    }
}

