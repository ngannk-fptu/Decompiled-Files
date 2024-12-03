/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.LockModeType
 */
package org.hibernate.internal.util;

import javax.persistence.LockModeType;
import org.hibernate.AssertionFailure;
import org.hibernate.LockMode;

public final class LockModeConverter {
    private LockModeConverter() {
    }

    public static LockModeType convertToLockModeType(LockMode lockMode) {
        if (lockMode == LockMode.NONE) {
            return LockModeType.NONE;
        }
        if (lockMode == LockMode.OPTIMISTIC || lockMode == LockMode.READ) {
            return LockModeType.OPTIMISTIC;
        }
        if (lockMode == LockMode.OPTIMISTIC_FORCE_INCREMENT || lockMode == LockMode.WRITE) {
            return LockModeType.OPTIMISTIC_FORCE_INCREMENT;
        }
        if (lockMode == LockMode.PESSIMISTIC_READ) {
            return LockModeType.PESSIMISTIC_READ;
        }
        if (lockMode == LockMode.PESSIMISTIC_WRITE || lockMode == LockMode.UPGRADE || lockMode == LockMode.UPGRADE_NOWAIT || lockMode == LockMode.UPGRADE_SKIPLOCKED) {
            return LockModeType.PESSIMISTIC_WRITE;
        }
        if (lockMode == LockMode.PESSIMISTIC_FORCE_INCREMENT || lockMode == LockMode.FORCE) {
            return LockModeType.PESSIMISTIC_FORCE_INCREMENT;
        }
        throw new AssertionFailure("unhandled lock mode " + (Object)((Object)lockMode));
    }

    public static LockMode convertToLockMode(LockModeType lockMode) {
        switch (lockMode) {
            case READ: 
            case OPTIMISTIC: {
                return LockMode.OPTIMISTIC;
            }
            case OPTIMISTIC_FORCE_INCREMENT: 
            case WRITE: {
                return LockMode.OPTIMISTIC_FORCE_INCREMENT;
            }
            case PESSIMISTIC_READ: {
                return LockMode.PESSIMISTIC_READ;
            }
            case PESSIMISTIC_WRITE: {
                return LockMode.PESSIMISTIC_WRITE;
            }
            case PESSIMISTIC_FORCE_INCREMENT: {
                return LockMode.PESSIMISTIC_FORCE_INCREMENT;
            }
            case NONE: {
                return LockMode.NONE;
            }
        }
        throw new AssertionFailure("Unknown LockModeType: " + lockMode);
    }
}

