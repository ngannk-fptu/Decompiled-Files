/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate;

import java.util.Locale;
import org.hibernate.MappingException;

public enum CacheMode {
    NORMAL(true, true),
    IGNORE(false, false),
    GET(false, true),
    PUT(true, false),
    REFRESH(true, false);

    private final boolean isPutEnabled;
    private final boolean isGetEnabled;

    private CacheMode(boolean isPutEnabled, boolean isGetEnabled) {
        this.isPutEnabled = isPutEnabled;
        this.isGetEnabled = isGetEnabled;
    }

    public boolean isGetEnabled() {
        return this.isGetEnabled;
    }

    public boolean isPutEnabled() {
        return this.isPutEnabled;
    }

    public static CacheMode interpretExternalSetting(String setting) {
        if (setting == null) {
            return null;
        }
        try {
            return CacheMode.valueOf(setting.toUpperCase(Locale.ROOT));
        }
        catch (IllegalArgumentException e) {
            throw new MappingException("Unknown Cache Mode: " + setting);
        }
    }
}

