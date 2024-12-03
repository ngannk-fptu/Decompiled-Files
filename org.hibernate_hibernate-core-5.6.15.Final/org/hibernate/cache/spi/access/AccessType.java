/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cache.spi.access;

import java.util.Locale;
import org.hibernate.cache.spi.access.UnknownAccessTypeException;

public enum AccessType {
    READ_ONLY("read-only"),
    READ_WRITE("read-write"),
    NONSTRICT_READ_WRITE("nonstrict-read-write"),
    TRANSACTIONAL("transactional");

    private final String externalName;

    private AccessType(String externalName) {
        this.externalName = externalName;
    }

    public String getExternalName() {
        return this.externalName;
    }

    public String toString() {
        return "AccessType[" + this.externalName + "]";
    }

    public static AccessType fromExternalName(String externalName) {
        if (externalName == null) {
            return null;
        }
        for (AccessType accessType : AccessType.values()) {
            if (!accessType.getExternalName().equals(externalName)) continue;
            return accessType;
        }
        try {
            return AccessType.valueOf(externalName.toUpperCase(Locale.ROOT));
        }
        catch (IllegalArgumentException e) {
            throw new UnknownAccessTypeException(externalName);
        }
    }
}

