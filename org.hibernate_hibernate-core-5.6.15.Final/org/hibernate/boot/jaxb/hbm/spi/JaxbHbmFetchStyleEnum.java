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

@XmlType(name="FetchStyleEnum", namespace="http://www.hibernate.org/xsd/orm/hbm")
@XmlEnum
public enum JaxbHbmFetchStyleEnum {
    JOIN("join"),
    SELECT("select");

    private final String value;

    private JaxbHbmFetchStyleEnum(String v) {
        this.value = v;
    }

    public String value() {
        return this.value;
    }

    public static JaxbHbmFetchStyleEnum fromValue(String v) {
        for (JaxbHbmFetchStyleEnum c : JaxbHbmFetchStyleEnum.values()) {
            if (!c.value.equals(v)) continue;
            return c;
        }
        throw new IllegalArgumentException(v);
    }
}

