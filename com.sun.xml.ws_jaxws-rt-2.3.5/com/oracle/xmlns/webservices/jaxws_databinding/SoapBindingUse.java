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

@XmlType(name="soap-binding-use")
@XmlEnum
public enum SoapBindingUse {
    LITERAL,
    ENCODED;


    public String value() {
        return this.name();
    }

    public static SoapBindingUse fromValue(String v) {
        return SoapBindingUse.valueOf(v);
    }
}

