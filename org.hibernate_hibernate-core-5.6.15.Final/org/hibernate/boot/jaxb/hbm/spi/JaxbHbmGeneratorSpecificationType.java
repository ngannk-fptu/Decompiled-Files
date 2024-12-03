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
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmConfigParameterContainer;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="GeneratorSpecificationType", namespace="http://www.hibernate.org/xsd/orm/hbm")
public class JaxbHbmGeneratorSpecificationType
extends JaxbHbmConfigParameterContainer
implements Serializable {
    @XmlAttribute(name="class", required=true)
    protected String clazz;

    public String getClazz() {
        return this.clazz;
    }

    public void setClazz(String value) {
        this.clazz = value;
    }
}

