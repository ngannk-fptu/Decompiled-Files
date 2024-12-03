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

@XmlType(name="LazyWithExtraEnum", namespace="http://www.hibernate.org/xsd/orm/hbm")
@XmlEnum
public enum JaxbHbmLazyWithExtraEnum {
    EXTRA("extra"),
    FALSE("false"),
    TRUE("true");

    private final String value;

    private JaxbHbmLazyWithExtraEnum(String v) {
        this.value = v;
    }

    public String value() {
        return this.value;
    }

    public static JaxbHbmLazyWithExtraEnum fromValue(String v) {
        for (JaxbHbmLazyWithExtraEnum c : JaxbHbmLazyWithExtraEnum.values()) {
            if (!c.value.equals(v)) continue;
            return c;
        }
        throw new IllegalArgumentException(v);
    }
}

