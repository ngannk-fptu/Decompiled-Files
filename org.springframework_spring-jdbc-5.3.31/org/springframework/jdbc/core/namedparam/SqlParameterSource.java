/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.jdbc.core.namedparam;

import org.springframework.lang.Nullable;

public interface SqlParameterSource {
    public static final int TYPE_UNKNOWN = Integer.MIN_VALUE;

    public boolean hasValue(String var1);

    @Nullable
    public Object getValue(String var1) throws IllegalArgumentException;

    default public int getSqlType(String paramName) {
        return Integer.MIN_VALUE;
    }

    @Nullable
    default public String getTypeName(String paramName) {
        return null;
    }

    @Nullable
    default public String[] getParameterNames() {
        return null;
    }
}

