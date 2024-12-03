/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.LockModeType
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlSchemaType
 *  javax.xml.bind.annotation.XmlType
 *  javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter
 */
package org.hibernate.boot.jaxb.mapping.spi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.LockModeType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.hibernate.boot.jaxb.mapping.spi.Adapter8;
import org.hibernate.boot.jaxb.mapping.spi.JaxbQueryHint;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="named-query", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm", propOrder={"description", "query", "lockMode", "hint"})
public class JaxbNamedQuery
implements Serializable {
    @XmlElement(namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected String description;
    @XmlElement(namespace="http://xmlns.jcp.org/xml/ns/persistence/orm", required=true)
    protected String query;
    @XmlElement(name="lock-mode", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm", type=String.class)
    @XmlJavaTypeAdapter(value=Adapter8.class)
    @XmlSchemaType(name="token")
    protected LockModeType lockMode;
    @XmlElement(namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected List<JaxbQueryHint> hint;
    @XmlAttribute(name="name", required=true)
    protected String name;

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String value) {
        this.description = value;
    }

    public String getQuery() {
        return this.query;
    }

    public void setQuery(String value) {
        this.query = value;
    }

    public LockModeType getLockMode() {
        return this.lockMode;
    }

    public void setLockMode(LockModeType value) {
        this.lockMode = value;
    }

    public List<JaxbQueryHint> getHint() {
        if (this.hint == null) {
            this.hint = new ArrayList<JaxbQueryHint>();
        }
        return this.hint;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }
}

