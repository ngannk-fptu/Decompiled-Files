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
@XmlType(name="NaturalIdCacheType", namespace="http://www.hibernate.org/xsd/orm/hbm")
public class JaxbHbmNaturalIdCacheType
implements Serializable {
    @XmlAttribute(name="region")
    protected String region;

    public String getRegion() {
        return this.region;
    }

    public void setRegion(String value) {
        this.region = value;
    }
}

