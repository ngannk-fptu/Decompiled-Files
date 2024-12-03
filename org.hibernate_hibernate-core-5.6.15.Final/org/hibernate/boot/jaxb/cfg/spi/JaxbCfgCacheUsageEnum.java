/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlEnum
 *  javax.xml.bind.annotation.XmlType
 */
package org.hibernate.boot.jaxb.cfg.spi;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name="CacheUsageEnum", namespace="http://www.hibernate.org/xsd/orm/cfg")
@XmlEnum
public enum JaxbCfgCacheUsageEnum {
    NONSTRICT_READ_WRITE("nonstrict-read-write"),
    READ_ONLY("read-only"),
    READ_WRITE("read-write"),
    TRANSACTIONAL("transactional");

    private final String value;

    private JaxbCfgCacheUsageEnum(String v) {
        this.value = v;
    }

    public String value() {
        return this.value;
    }

    public static JaxbCfgCacheUsageEnum fromValue(String v) {
        for (JaxbCfgCacheUsageEnum c : JaxbCfgCacheUsageEnum.values()) {
            if (!c.value.equals(v)) continue;
            return c;
        }
        throw new IllegalArgumentException(v);
    }
}

