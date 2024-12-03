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
@XmlType(name="converter", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm", propOrder={"description"})
public class JaxbConverter
implements Serializable {
    @XmlElement(namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected String description;
    @XmlAttribute(name="class", required=true)
    protected String clazz;
    @XmlAttribute(name="auto-apply")
    protected Boolean autoApply;

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String value) {
        this.description = value;
    }

    public String getClazz() {
        return this.clazz;
    }

    public void setClazz(String value) {
        this.clazz = value;
    }

    public Boolean isAutoApply() {
        return this.autoApply;
    }

    public void setAutoApply(Boolean value) {
        this.autoApply = value;
    }
}

