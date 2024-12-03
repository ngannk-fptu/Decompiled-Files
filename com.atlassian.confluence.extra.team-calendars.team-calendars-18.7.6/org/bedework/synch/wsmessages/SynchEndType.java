/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlEnum
 *  javax.xml.bind.annotation.XmlType
 */
package org.bedework.synch.wsmessages;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name="SynchEndType")
@XmlEnum
public enum SynchEndType {
    NONE("none"),
    A("A"),
    B("B");

    private final String value;

    private SynchEndType(String v) {
        this.value = v;
    }

    public String value() {
        return this.value;
    }

    public static SynchEndType fromValue(String v) {
        for (SynchEndType c : SynchEndType.values()) {
            if (!c.value.equals(v)) continue;
            return c;
        }
        throw new IllegalArgumentException(v);
    }
}

