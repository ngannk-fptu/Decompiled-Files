/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.InheritanceType
 */
package org.hibernate.boot.jaxb.mapping.internal;

import javax.persistence.InheritanceType;

public class InheritanceTypeMarshalling {
    public static InheritanceType fromXml(String name) {
        return InheritanceType.valueOf((String)name);
    }

    public static String toXml(InheritanceType accessType) {
        return accessType.name();
    }
}

