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

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="unique-constraint", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm", propOrder={"columnName"})
public class JaxbUniqueConstraint
implements Serializable {
    @XmlElement(name="column-name", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm", required=true)
    protected List<String> columnName;
    @XmlAttribute(name="name")
    protected String name;

    public List<String> getColumnName() {
        if (this.columnName == null) {
            this.columnName = new ArrayList<String>();
        }
        return this.columnName;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }
}

