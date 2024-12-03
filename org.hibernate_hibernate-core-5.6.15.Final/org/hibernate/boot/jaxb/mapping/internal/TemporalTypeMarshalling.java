/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.TemporalType
 */
package org.hibernate.boot.jaxb.mapping.internal;

import javax.persistence.TemporalType;

public class TemporalTypeMarshalling {
    public static TemporalType fromXml(String name) {
        return TemporalType.valueOf((String)name);
    }

    public static String toXml(TemporalType temporalType) {
        return temporalType.name();
    }
}

