/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlType
 */
package org.hibernate.boot.jaxb.mapping.spi;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="primary-key-join-column", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
public class JaxbPrimaryKeyJoinColumn
implements Serializable {
    @XmlAttribute(name="name")
    protected String name;
    @XmlAttribute(name="referenced-column-name")
    protected String referencedColumnName;
    @XmlAttribute(name="column-definition")
    protected String columnDefinition;

    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public String getReferencedColumnName() {
        return this.referencedColumnName;
    }

    public void setReferencedColumnName(String value) {
        this.referencedColumnName = value;
    }

    public String getColumnDefinition() {
        return this.columnDefinition;
    }

    public void setColumnDefinition(String value) {
        this.columnDefinition = value;
    }
}

