/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlEnum
 *  javax.xml.bind.annotation.XmlType
 */
package org.oasis_open.docs.ws_calendar.ns.soap;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name="StatusType")
@XmlEnum
public enum StatusType {
    OK("OK"),
    NOT_FOUND("Not Found"),
    WARNING("Warning"),
    NO_ACCESS("No Access"),
    SERVICE_STOPPED("Service Stopped"),
    ERROR("Error");

    private final String value;

    private StatusType(String v) {
        this.value = v;
    }

    public String value() {
        return this.value;
    }

    public static StatusType fromValue(String v) {
        for (StatusType c : StatusType.values()) {
            if (!c.value.equals(v)) continue;
            return c;
        }
        throw new IllegalArgumentException(v);
    }
}

