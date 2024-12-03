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
@XmlType(name="convert", namespace="http://xmlns.jcp.org/xml/ns/persistence/orm", propOrder={"description"})
public class JaxbConvert
implements Serializable {
    @XmlElement(namespace="http://xmlns.jcp.org/xml/ns/persistence/orm")
    protected String description;
    @XmlAttribute(name="converter")
    protected String converter;
    @XmlAttribute(name="attribute-name")
    protected String attributeName;
    @XmlAttribute(name="disable-conversion")
    protected Boolean disableConversion;

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String value) {
        this.description = value;
    }

    public String getConverter() {
        return this.converter;
    }

    public void setConverter(String value) {
        this.converter = value;
    }

    public String getAttributeName() {
        return this.attributeName;
    }

    public void setAttributeName(String value) {
        this.attributeName = value;
    }

    public Boolean isDisableConversion() {
        return this.disableConversion;
    }

    public void setDisableConversion(Boolean value) {
        this.disableConversion = value;
    }
}

