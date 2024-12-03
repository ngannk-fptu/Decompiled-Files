/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.jdbc.core.namedparam;

import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.lang.Nullable;

public class EmptySqlParameterSource
implements SqlParameterSource {
    public static final EmptySqlParameterSource INSTANCE = new EmptySqlParameterSource();

    @Override
    public boolean hasValue(String paramName) {
        return false;
    }

    @Override
    @Nullable
    public Object getValue(String paramName) throws IllegalArgumentException {
        throw new IllegalArgumentException("This SqlParameterSource is empty");
    }

    @Override
    public int getSqlType(String paramName) {
        return Integer.MIN_VALUE;
    }

    @Override
    @Nullable
    public String getTypeName(String paramName) {
        return null;
    }

    @Override
    @Nullable
    public String[] getParameterNames() {
        return null;
    }
}

