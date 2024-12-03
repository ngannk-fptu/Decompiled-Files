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
package org.hibernate.boot.jaxb.mapping.spi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.hibernate.boot.jaxb.mapping.spi.JaxbColumnResult;
import org.hibernate.boot.jaxb.mapping.spi.JaxbConstructorResult;
import org.hibernate.boot.jaxb.mapping.spi.JaxbEntityResult;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="sql-result-set-mapping", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm", propOrder={"description", "entityResult", "constructorResult", "columnResult"})
public class JaxbSqlResultSetMapping
implements Serializable {
    @XmlElement(namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected String description;
    @XmlElement(name="entity-result", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected List<JaxbEntityResult> entityResult;
    @XmlElement(name="constructor-result", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected List<JaxbConstructorResult> constructorResult;
    @XmlElement(name="column-result", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected List<JaxbColumnResult> columnResult;
    @XmlAttribute(name="name", required=true)
    protected String name;

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String value) {
        this.description = value;
    }

    public List<JaxbEntityResult> getEntityResult() {
        if (this.entityResult == null) {
            this.entityResult = new ArrayList<JaxbEntityResult>();
        }
        return this.entityResult;
    }

    public List<JaxbConstructorResult> getConstructorResult() {
        if (this.constructorResult == null) {
            this.constructorResult = new ArrayList<JaxbConstructorResult>();
        }
        return this.constructorResult;
    }

    public List<JaxbColumnResult> getColumnResult() {
        if (this.columnResult == null) {
            this.columnResult = new ArrayList<JaxbColumnResult>();
        }
        return this.columnResult;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }
}

