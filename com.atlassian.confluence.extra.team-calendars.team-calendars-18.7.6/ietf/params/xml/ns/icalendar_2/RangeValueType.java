/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlEnum
 *  javax.xml.bind.annotation.XmlType
 */
package ietf.params.xml.ns.icalendar_2;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name="RangeValueType")
@XmlEnum
public enum RangeValueType {
    THISANDFUTURE;


    public String value() {
        return this.name();
    }

    public static RangeValueType fromValue(String v) {
        return RangeValueType.valueOf(v);
    }
}

