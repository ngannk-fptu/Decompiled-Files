/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.plugin.servlet.filter;

import java.util.Locale;
import javax.annotation.Nonnull;

public enum FilterLocation {
    AFTER_ENCODING,
    BEFORE_LOGIN,
    BEFORE_DECORATION,
    BEFORE_DISPATCH;


    @Nonnull
    public static FilterLocation parse(String value) {
        if (value != null) {
            return FilterLocation.valueOf(value.toUpperCase(Locale.ENGLISH).replace('-', '_'));
        }
        throw new IllegalArgumentException("Invalid filter location: null");
    }
}

