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

@XmlType(name="LazyEnum", namespace="http://www.hibernate.org/xsd/orm/hbm")
@XmlEnum
public enum JaxbHbmLazyEnum {
    FALSE("false"),
    PROXY("proxy");

    private final String value;

    private JaxbHbmLazyEnum(String v) {
        this.value = v;
    }

    public String value() {
        return this.value;
    }

    public static JaxbHbmLazyEnum fromValue(String v) {
        for (JaxbHbmLazyEnum c : JaxbHbmLazyEnum.values()) {
            if (!c.value.equals(v)) continue;
            return c;
        }
        throw new IllegalArgumentException(v);
    }
}

