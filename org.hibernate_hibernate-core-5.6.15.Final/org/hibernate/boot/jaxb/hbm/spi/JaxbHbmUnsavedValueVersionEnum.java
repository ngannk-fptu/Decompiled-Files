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

@XmlType(name="UnsavedValueVersionEnum", namespace="http://www.hibernate.org/xsd/orm/hbm")
@XmlEnum
public enum JaxbHbmUnsavedValueVersionEnum {
    NEGATIVE("negative"),
    NULL("null"),
    UNDEFINED("undefined");

    private final String value;

    private JaxbHbmUnsavedValueVersionEnum(String v) {
        this.value = v;
    }

    public String value() {
        return this.value;
    }

    public static JaxbHbmUnsavedValueVersionEnum fromValue(String v) {
        for (JaxbHbmUnsavedValueVersionEnum c : JaxbHbmUnsavedValueVersionEnum.values()) {
            if (!c.value.equals(v)) continue;
            return c;
        }
        throw new IllegalArgumentException(v);
    }
}

