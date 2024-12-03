/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlType
 *  javax.xml.bind.annotation.adapters.CollapsedStringAdapter
 *  javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter
 */
package org.hibernate.boot.jaxb.cfg.spi;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.hibernate.boot.jaxb.cfg.spi.JaxbCfgCacheUsageEnum;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="EntityCacheType", namespace="http://www.hibernate.org/xsd/orm/cfg")
public class JaxbCfgEntityCacheType {
    @XmlAttribute(name="class", required=true)
    protected String clazz;
    @XmlAttribute(name="include")
    @XmlJavaTypeAdapter(value=CollapsedStringAdapter.class)
    protected String include;
    @XmlAttribute(name="region")
    protected String region;
    @XmlAttribute(name="usage", required=true)
    protected JaxbCfgCacheUsageEnum usage;

    public String getClazz() {
        return this.clazz;
    }

    public void setClazz(String value) {
        this.clazz = value;
    }

    public String getInclude() {
        if (this.include == null) {
            return "all";
        }
        return this.include;
    }

    public void setInclude(String value) {
        this.include = value;
    }

    public String getRegion() {
        return this.region;
    }

    public void setRegion(String value) {
        this.region = value;
    }

    public JaxbCfgCacheUsageEnum getUsage() {
        return this.usage;
    }

    public void setUsage(JaxbCfgCacheUsageEnum value) {
        this.usage = value;
    }
}

