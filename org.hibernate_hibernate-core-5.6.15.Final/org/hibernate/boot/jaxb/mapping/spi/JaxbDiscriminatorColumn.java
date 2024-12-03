/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.DiscriminatorType
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlType
 *  javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter
 */
package org.hibernate.boot.jaxb.mapping.spi;

import java.io.Serializable;
import javax.persistence.DiscriminatorType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.hibernate.boot.jaxb.mapping.spi.Adapter3;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="discriminator-column", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
public class JaxbDiscriminatorColumn
implements Serializable {
    @XmlAttribute(name="name")
    protected String name;
    @XmlAttribute(name="discriminator-type")
    @XmlJavaTypeAdapter(value=Adapter3.class)
    protected DiscriminatorType discriminatorType;
    @XmlAttribute(name="column-definition")
    protected String columnDefinition;
    @XmlAttribute(name="length")
    protected Integer length;

    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public DiscriminatorType getDiscriminatorType() {
        return this.discriminatorType;
    }

    public void setDiscriminatorType(DiscriminatorType value) {
        this.discriminatorType = value;
    }

    public String getColumnDefinition() {
        return this.columnDefinition;
    }

    public void setColumnDefinition(String value) {
        this.columnDefinition = value;
    }

    public Integer getLength() {
        return this.length;
    }

    public void setLength(Integer value) {
        this.length = value;
    }
}

