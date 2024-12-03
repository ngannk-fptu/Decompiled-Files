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

@XmlType(name="CalscaleValueType")
@XmlEnum
public enum CalscaleValueType {
    GREGORIAN;


    public String value() {
        return this.name();
    }

    public static CalscaleValueType fromValue(String v) {
        return CalscaleValueType.valueOf(v);
    }
}

