/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine;

public enum OptimisticLockStyle {
    NONE(-1),
    VERSION(0),
    DIRTY(1),
    ALL(2);

    private final int oldCode;

    private OptimisticLockStyle(int oldCode) {
        this.oldCode = oldCode;
    }

    public int getOldCode() {
        return this.oldCode;
    }

    public boolean isAllOrDirty() {
        return this.isAll() || this.isDirty();
    }

    public boolean isAll() {
        return this == ALL;
    }

    public boolean isDirty() {
        return this == DIRTY;
    }

    public boolean isVersion() {
        return this == VERSION;
    }

    public boolean isNone() {
        return this == NONE;
    }

    public static OptimisticLockStyle interpretOldCode(int oldCode) {
        switch (oldCode) {
            case -1: {
                return NONE;
            }
            case 0: {
                return VERSION;
            }
            case 1: {
                return DIRTY;
            }
            case 2: {
                return ALL;
            }
        }
        throw new IllegalArgumentException("Illegal legacy optimistic lock style code :" + oldCode);
    }
}

