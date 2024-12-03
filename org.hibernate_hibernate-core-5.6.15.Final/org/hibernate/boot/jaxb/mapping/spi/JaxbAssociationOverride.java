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
import org.hibernate.boot.jaxb.mapping.spi.JaxbForeignKey;
import org.hibernate.boot.jaxb.mapping.spi.JaxbJoinColumn;
import org.hibernate.boot.jaxb.mapping.spi.JaxbJoinTable;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="association-override", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm", propOrder={"description", "joinColumn", "foreignKey", "joinTable"})
public class JaxbAssociationOverride
implements Serializable {
    @XmlElement(namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected String description;
    @XmlElement(name="join-column", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected List<JaxbJoinColumn> joinColumn;
    @XmlElement(name="foreign-key", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected JaxbForeignKey foreignKey;
    @XmlElement(name="join-table", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected JaxbJoinTable joinTable;
    @XmlAttribute(name="name", required=true)
    protected String name;

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String value) {
        this.description = value;
    }

    public List<JaxbJoinColumn> getJoinColumn() {
        if (this.joinColumn == null) {
            this.joinColumn = new ArrayList<JaxbJoinColumn>();
        }
        return this.joinColumn;
    }

    public JaxbForeignKey getForeignKey() {
        return this.foreignKey;
    }

    public void setForeignKey(JaxbForeignKey value) {
        this.foreignKey = value;
    }

    public JaxbJoinTable getJoinTable() {
        return this.joinTable;
    }

    public void setJoinTable(JaxbJoinTable value) {
        this.joinTable = value;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }
}

