/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.LockModeType
 */
package org.hibernate.jpa.internal.util;

import javax.persistence.LockModeType;
import org.hibernate.LockMode;
import org.hibernate.internal.util.LockModeConverter;

public final class LockModeTypeHelper {
    private LockModeTypeHelper() {
    }

    public static LockModeType getLockModeType(LockMode lockMode) {
        return LockModeConverter.convertToLockModeType(lockMode);
    }

    public static LockMode getLockMode(LockModeType lockModeType) {
        return LockModeConverter.convertToLockMode(lockModeType);
    }

    public static LockMode interpretLockMode(Object value) {
        if (value == null) {
            return LockMode.NONE;
        }
        if (LockMode.class.isInstance(value)) {
            return (LockMode)((Object)value);
        }
        if (LockModeType.class.isInstance(value)) {
            return LockModeTypeHelper.getLockMode((LockModeType)value);
        }
        if (String.class.isInstance(value)) {
            return LockMode.fromExternalForm((String)value);
        }
        throw new IllegalArgumentException("Unknown lock mode source: '" + value + "'; can't convert from value of type " + value.getClass());
    }
}

