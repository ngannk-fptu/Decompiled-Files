/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlType
 */
package org.hibernate.boot.jaxb.hbm.spi;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="FilterParameterType", namespace="http://www.hibernate.org/xsd/orm/hbm")
public class JaxbHbmFilterParameterType
implements Serializable {
    @XmlAttribute(name="name", required=true)
    protected String parameterName;
    @XmlAttribute(name="type", required=true)
    protected String parameterValueTypeName;

    public String getParameterName() {
        return this.parameterName;
    }

    public void setParameterName(String value) {
        this.parameterName = value;
    }

    public String getParameterValueTypeName() {
        return this.parameterValueTypeName;
    }

    public void setParameterValueTypeName(String value) {
        this.parameterValueTypeName = value;
    }
}

