/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlType
 */
package org.hibernate.boot.jaxb.cfg.spi;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import org.hibernate.boot.jaxb.cfg.spi.JaxbCfgCacheUsageEnum;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="CollectionCacheType", namespace="http://www.hibernate.org/xsd/orm/cfg")
public class JaxbCfgCollectionCacheType {
    @XmlAttribute(name="collection", required=true)
    protected String collection;
    @XmlAttribute(name="region")
    protected String region;
    @XmlAttribute(name="usage", required=true)
    protected JaxbCfgCacheUsageEnum usage;

    public String getCollection() {
        return this.collection;
    }

    public void setCollection(String value) {
        this.collection = value;
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

