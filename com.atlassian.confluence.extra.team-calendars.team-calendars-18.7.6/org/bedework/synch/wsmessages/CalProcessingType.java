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

@XmlType(name="CalProcessingType")
@XmlEnum
public enum CalProcessingType {
    ALLOW,
    SYNCH,
    REMOVE,
    SPECIAL;


    public String value() {
        return this.name();
    }

    public static CalProcessingType fromValue(String v) {
        return CalProcessingType.valueOf(v);
    }
}

