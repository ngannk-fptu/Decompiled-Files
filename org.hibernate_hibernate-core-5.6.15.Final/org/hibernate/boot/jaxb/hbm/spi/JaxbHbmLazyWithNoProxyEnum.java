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

@XmlType(name="LazyWithNoProxyEnum", namespace="http://www.hibernate.org/xsd/orm/hbm")
@XmlEnum
public enum JaxbHbmLazyWithNoProxyEnum {
    FALSE("false"),
    NO_PROXY("no-proxy"),
    PROXY("proxy");

    private final String value;

    private JaxbHbmLazyWithNoProxyEnum(String v) {
        this.value = v;
    }

    public String value() {
        return this.value;
    }

    public static JaxbHbmLazyWithNoProxyEnum fromValue(String v) {
        for (JaxbHbmLazyWithNoProxyEnum c : JaxbHbmLazyWithNoProxyEnum.values()) {
            if (!c.value.equals(v)) continue;
            return c;
        }
        throw new IllegalArgumentException(v);
    }
}

