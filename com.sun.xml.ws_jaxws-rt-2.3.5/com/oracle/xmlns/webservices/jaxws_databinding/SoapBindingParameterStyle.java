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

@XmlType(name="soap-binding-parameter-style")
@XmlEnum
public enum SoapBindingParameterStyle {
    BARE,
    WRAPPED;


    public String value() {
        return this.name();
    }

    public static SoapBindingParameterStyle fromValue(String v) {
        return SoapBindingParameterStyle.valueOf(v);
    }
}

