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

@XmlType(name="web-param-mode")
@XmlEnum
public enum WebParamMode {
    IN,
    OUT,
    INOUT;


    public String value() {
        return this.name();
    }

    public static WebParamMode fromValue(String v) {
        return WebParamMode.valueOf(v);
    }
}

