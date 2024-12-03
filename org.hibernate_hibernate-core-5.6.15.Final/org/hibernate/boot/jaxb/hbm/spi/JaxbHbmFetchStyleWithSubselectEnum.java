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

@XmlType(name="FetchStyleWithSubselectEnum", namespace="http://www.hibernate.org/xsd/orm/hbm")
@XmlEnum
public enum JaxbHbmFetchStyleWithSubselectEnum {
    JOIN("join"),
    SELECT("select"),
    SUBSELECT("subselect");

    private final String value;

    private JaxbHbmFetchStyleWithSubselectEnum(String v) {
        this.value = v;
    }

    public String value() {
        return this.value;
    }

    public static JaxbHbmFetchStyleWithSubselectEnum fromValue(String v) {
        for (JaxbHbmFetchStyleWithSubselectEnum c : JaxbHbmFetchStyleWithSubselectEnum.values()) {
            if (!c.value.equals(v)) continue;
            return c;
        }
        throw new IllegalArgumentException(v);
    }
}

