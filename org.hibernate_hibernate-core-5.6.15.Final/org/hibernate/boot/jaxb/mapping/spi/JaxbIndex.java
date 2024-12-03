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
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="index", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm", propOrder={"description"})
public class JaxbIndex
implements Serializable {
    @XmlElement(namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected String description;
    @XmlAttribute(name="name")
    protected String name;
    @XmlAttribute(name="column-list", required=true)
    protected String columnList;
    @XmlAttribute(name="unique")
    protected Boolean unique;

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String value) {
        this.description = value;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public String getColumnList() {
        return this.columnList;
    }

    public void setColumnList(String value) {
        this.columnList = value;
    }

    public Boolean isUnique() {
        return this.unique;
    }

    public void setUnique(Boolean value) {
        this.unique = value;
    }
}

