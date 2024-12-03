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

@XmlType(name="SynchDirectionType")
@XmlEnum
public enum SynchDirectionType {
    B_TO_A("BToA"),
    A_TO_B("AToB"),
    BOTH_WAYS("bothWays");

    private final String value;

    private SynchDirectionType(String v) {
        this.value = v;
    }

    public String value() {
        return this.value;
    }

    public static SynchDirectionType fromValue(String v) {
        for (SynchDirectionType c : SynchDirectionType.values()) {
            if (!c.value.equals(v)) continue;
            return c;
        }
        throw new IllegalArgumentException(v);
    }
}

