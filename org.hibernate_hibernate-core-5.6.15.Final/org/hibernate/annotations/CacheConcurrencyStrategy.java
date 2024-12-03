/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.annotations;

import org.hibernate.cache.spi.access.AccessType;

public enum CacheConcurrencyStrategy {
    NONE(null),
    READ_ONLY(AccessType.READ_ONLY),
    NONSTRICT_READ_WRITE(AccessType.NONSTRICT_READ_WRITE),
    READ_WRITE(AccessType.READ_WRITE),
    TRANSACTIONAL(AccessType.TRANSACTIONAL);

    private final AccessType accessType;

    private CacheConcurrencyStrategy(AccessType accessType) {
        this.accessType = accessType;
    }

    public AccessType toAccessType() {
        return this.accessType;
    }

    public static CacheConcurrencyStrategy fromAccessType(AccessType accessType) {
        if (null == accessType) {
            return NONE;
        }
        switch (accessType) {
            case READ_ONLY: {
                return READ_ONLY;
            }
            case READ_WRITE: {
                return READ_WRITE;
            }
            case NONSTRICT_READ_WRITE: {
                return NONSTRICT_READ_WRITE;
            }
            case TRANSACTIONAL: {
                return TRANSACTIONAL;
            }
        }
        return NONE;
    }

    public static CacheConcurrencyStrategy parse(String name) {
        if (READ_ONLY.isMatch(name)) {
            return READ_ONLY;
        }
        if (READ_WRITE.isMatch(name)) {
            return READ_WRITE;
        }
        if (NONSTRICT_READ_WRITE.isMatch(name)) {
            return NONSTRICT_READ_WRITE;
        }
        if (TRANSACTIONAL.isMatch(name)) {
            return TRANSACTIONAL;
        }
        if (NONE.isMatch(name)) {
            return NONE;
        }
        return null;
    }

    private boolean isMatch(String name) {
        return this.accessType != null && this.accessType.getExternalName().equalsIgnoreCase(name) || this.name().equalsIgnoreCase(name);
    }
}

