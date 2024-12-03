/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate;

public enum LockMode {
    NONE(0, "none"),
    READ(5, "read"),
    UPGRADE(10, "upgrade"),
    UPGRADE_NOWAIT(10, "upgrade-nowait"),
    UPGRADE_SKIPLOCKED(10, "upgrade-skiplocked"),
    WRITE(10, "write"),
    FORCE(15, "force"),
    OPTIMISTIC(6, "optimistic"),
    OPTIMISTIC_FORCE_INCREMENT(7, "optimistic_force_increment"),
    PESSIMISTIC_READ(12, "pessimistic_read"),
    PESSIMISTIC_WRITE(13, "pessimistic_write"),
    PESSIMISTIC_FORCE_INCREMENT(17, "pessimistic_force_increment");

    private final int level;
    private final String externalForm;

    private LockMode(int level, String externalForm) {
        this.level = level;
        this.externalForm = externalForm;
    }

    public boolean greaterThan(LockMode mode) {
        return this.level > mode.level;
    }

    public boolean lessThan(LockMode mode) {
        return this.level < mode.level;
    }

    public String toExternalForm() {
        return this.externalForm;
    }

    public static LockMode fromExternalForm(String externalForm) {
        if (externalForm == null) {
            return NONE;
        }
        for (LockMode lockMode : LockMode.values()) {
            if (!lockMode.externalForm.equals(externalForm)) continue;
            return lockMode;
        }
        for (LockMode lockMode : LockMode.values()) {
            if (!lockMode.externalForm.equalsIgnoreCase(externalForm)) continue;
            return lockMode;
        }
        throw new IllegalArgumentException("Unable to interpret LockMode reference from incoming external form : " + externalForm);
    }
}

