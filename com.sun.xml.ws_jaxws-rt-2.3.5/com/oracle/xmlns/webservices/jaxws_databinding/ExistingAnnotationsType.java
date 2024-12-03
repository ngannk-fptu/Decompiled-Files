/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlEnum
 *  javax.xml.bind.annotation.XmlType
 */
package com.oracle.xmlns.webservices.jaxws_databinding;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name="existing-annotations-type")
@XmlEnum
public enum ExistingAnnotationsType {
    MERGE("merge"),
    IGNORE("ignore");

    private final String value;

    private ExistingAnnotationsType(String v) {
        this.value = v;
    }

    public String value() {
        return this.value;
    }

    public static ExistingAnnotationsType fromValue(String v) {
        for (ExistingAnnotationsType c : ExistingAnnotationsType.values()) {
            if (!c.value.equals(v)) continue;
            return c;
        }
        throw new IllegalArgumentException(v);
    }
}

