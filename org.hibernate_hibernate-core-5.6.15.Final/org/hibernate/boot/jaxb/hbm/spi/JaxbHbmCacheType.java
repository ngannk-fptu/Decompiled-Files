/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlType
 *  javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter
 */
package org.hibernate.boot.jaxb.hbm.spi;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.hibernate.boot.jaxb.hbm.spi.Adapter2;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmCacheInclusionEnum;
import org.hibernate.cache.spi.access.AccessType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="CacheType", namespace="http://www.hibernate.org/xsd/orm/hbm")
public class JaxbHbmCacheType
implements Serializable {
    @XmlAttribute(name="include")
    protected JaxbHbmCacheInclusionEnum include;
    @XmlAttribute(name="region")
    protected String region;
    @XmlAttribute(name="usage", required=true)
    @XmlJavaTypeAdapter(value=Adapter2.class)
    protected AccessType usage;

    public JaxbHbmCacheInclusionEnum getInclude() {
        if (this.include == null) {
            return JaxbHbmCacheInclusionEnum.ALL;
        }
        return this.include;
    }

    public void setInclude(JaxbHbmCacheInclusionEnum value) {
        this.include = value;
    }

    public String getRegion() {
        return this.region;
    }

    public void setRegion(String value) {
        this.region = value;
    }

    public AccessType getUsage() {
        return this.usage;
    }

    public void setUsage(AccessType value) {
        this.usage = value;
    }
}

