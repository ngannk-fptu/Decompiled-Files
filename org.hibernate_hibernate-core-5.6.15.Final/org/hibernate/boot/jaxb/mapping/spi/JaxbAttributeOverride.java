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
import org.hibernate.boot.jaxb.mapping.spi.JaxbColumn;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="attribute-override", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm", propOrder={"description", "column"})
public class JaxbAttributeOverride
implements Serializable {
    @XmlElement(namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected String description;
    @XmlElement(namespace="http://xmlns.jcp.org/xml/ns/persistence/orm", required=true)
    protected JaxbColumn column;
    @XmlAttribute(name="name", required=true)
    protected String name;

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String value) {
        this.description = value;
    }

    public JaxbColumn getColumn() {
        return this.column;
    }

    public void setColumn(JaxbColumn value) {
        this.column = value;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }
}

