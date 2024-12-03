/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.ConstraintMode
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlType
 *  javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter
 */
package org.hibernate.boot.jaxb.mapping.spi;

import java.io.Serializable;
import javax.persistence.ConstraintMode;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.hibernate.boot.jaxb.mapping.spi.Adapter2;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="foreign-key", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm", propOrder={"description"})
public class JaxbForeignKey
implements Serializable {
    @XmlElement(namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected String description;
    @XmlAttribute(name="name")
    protected String name;
    @XmlAttribute(name="constraint-mode")
    @XmlJavaTypeAdapter(value=Adapter2.class)
    protected ConstraintMode constraintMode;
    @XmlAttribute(name="foreign-key-definition")
    protected String foreignKeyDefinition;

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

    public ConstraintMode getConstraintMode() {
        return this.constraintMode;
    }

    public void setConstraintMode(ConstraintMode value) {
        this.constraintMode = value;
    }

    public String getForeignKeyDefinition() {
        return this.foreignKeyDefinition;
    }

    public void setForeignKeyDefinition(String value) {
        this.foreignKeyDefinition = value;
    }
}

