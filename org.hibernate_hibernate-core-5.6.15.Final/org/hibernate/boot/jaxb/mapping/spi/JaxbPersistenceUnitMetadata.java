/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlType
 */
package org.hibernate.boot.jaxb.mapping.spi;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.hibernate.boot.jaxb.mapping.spi.JaxbEmptyType;
import org.hibernate.boot.jaxb.mapping.spi.JaxbPersistenceUnitDefaults;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="persistence-unit-metadata", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm", propOrder={"description", "xmlMappingMetadataComplete", "persistenceUnitDefaults"})
public class JaxbPersistenceUnitMetadata
implements Serializable {
    @XmlElement(namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected String description;
    @XmlElement(name="xml-mapping-metadata-complete", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected JaxbEmptyType xmlMappingMetadataComplete;
    @XmlElement(name="persistence-unit-defaults", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected JaxbPersistenceUnitDefaults persistenceUnitDefaults;

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String value) {
        this.description = value;
    }

    public JaxbEmptyType getXmlMappingMetadataComplete() {
        return this.xmlMappingMetadataComplete;
    }

    public void setXmlMappingMetadataComplete(JaxbEmptyType value) {
        this.xmlMappingMetadataComplete = value;
    }

    public JaxbPersistenceUnitDefaults getPersistenceUnitDefaults() {
        return this.persistenceUnitDefaults;
    }

    public void setPersistenceUnitDefaults(JaxbPersistenceUnitDefaults value) {
        this.persistenceUnitDefaults = value;
    }
}

