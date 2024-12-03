/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlEnum
 *  javax.xml.bind.annotation.XmlType
 */
package org.apache.xml.security.configuration;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name="inOutAttrType", namespace="http://www.xmlsecurity.org/NS/configuration")
@XmlEnum
public enum InOutAttrType {
    IN,
    OUT;


    public String value() {
        return this.name();
    }

    public static InOutAttrType fromValue(String v) {
        return InOutAttrType.valueOf(v);
    }
}

