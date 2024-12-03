/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.jaxb.hbm.internal;

import org.hibernate.cache.spi.access.AccessType;

public class CacheAccessTypeConverter {
    public static AccessType fromXml(String name) {
        return AccessType.fromExternalName(name);
    }

    public static String toXml(AccessType accessType) {
        return accessType.getExternalName();
    }
}

