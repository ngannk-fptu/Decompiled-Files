/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.AccessType
 */
package org.hibernate.boot.jaxb.mapping.internal;

import javax.persistence.AccessType;

public class AccessTypeMarshalling {
    public static AccessType fromXml(String name) {
        return AccessType.valueOf((String)name);
    }

    public static String toXml(AccessType accessType) {
        return accessType.name();
    }
}

