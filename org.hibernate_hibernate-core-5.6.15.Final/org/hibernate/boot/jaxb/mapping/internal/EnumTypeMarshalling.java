/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.EnumType
 */
package org.hibernate.boot.jaxb.mapping.internal;

import javax.persistence.EnumType;

public class EnumTypeMarshalling {
    public static EnumType fromXml(String name) {
        return EnumType.valueOf((String)name);
    }

    public static String toXml(EnumType enumType) {
        return enumType.name();
    }
}

