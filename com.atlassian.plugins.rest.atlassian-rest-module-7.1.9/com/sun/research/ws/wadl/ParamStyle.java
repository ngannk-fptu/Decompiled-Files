/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlEnum
 *  javax.xml.bind.annotation.XmlType
 */
package com.sun.research.ws.wadl;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name="ParamStyle")
@XmlEnum
public enum ParamStyle {
    PLAIN("plain"),
    QUERY("query"),
    MATRIX("matrix"),
    HEADER("header"),
    TEMPLATE("template");

    private final String value;

    private ParamStyle(String v) {
        this.value = v;
    }

    public String value() {
        return this.value;
    }

    public static ParamStyle fromValue(String v) {
        for (ParamStyle c : ParamStyle.values()) {
            if (!c.value.equals(v)) continue;
            return c;
        }
        throw new IllegalArgumentException(v);
    }
}

