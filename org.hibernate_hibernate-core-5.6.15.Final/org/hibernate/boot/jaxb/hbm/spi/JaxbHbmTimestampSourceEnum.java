/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlEnum
 *  javax.xml.bind.annotation.XmlType
 */
package org.hibernate.boot.jaxb.hbm.spi;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name="TimestampSourceEnum", namespace="http://www.hibernate.org/xsd/orm/hbm")
@XmlEnum
public enum JaxbHbmTimestampSourceEnum {
    DB("db"),
    VM("vm");

    private final String value;

    private JaxbHbmTimestampSourceEnum(String v) {
        this.value = v;
    }

    public String value() {
        return this.value;
    }

    public static JaxbHbmTimestampSourceEnum fromValue(String v) {
        for (JaxbHbmTimestampSourceEnum c : JaxbHbmTimestampSourceEnum.values()) {
            if (!c.value.equals(v)) continue;
            return c;
        }
        throw new IllegalArgumentException(v);
    }
}

