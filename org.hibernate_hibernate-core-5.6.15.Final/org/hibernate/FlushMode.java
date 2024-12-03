/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate;

import org.hibernate.jpa.internal.util.FlushModeTypeHelper;

public enum FlushMode {
    MANUAL(0),
    COMMIT(5),
    AUTO(10),
    ALWAYS(20);

    private final int level;

    private FlushMode(int level) {
        this.level = level;
    }

    public boolean lessThan(FlushMode other) {
        return this.level < other.level;
    }

    @Deprecated
    public static boolean isManualFlushMode(FlushMode mode) {
        return FlushMode.MANUAL.level == mode.level;
    }

    public static FlushMode interpretExternalSetting(String externalName) {
        return FlushModeTypeHelper.interpretExternalSetting(externalName);
    }
}

