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

@XmlType(name="WeekdayRecurType")
@XmlEnum
public enum WeekdayRecurType {
    SU,
    MO,
    TU,
    WE,
    TH,
    FR,
    SA;


    public String value() {
        return this.name();
    }

    public static WeekdayRecurType fromValue(String v) {
        return WeekdayRecurType.valueOf(v);
    }
}

