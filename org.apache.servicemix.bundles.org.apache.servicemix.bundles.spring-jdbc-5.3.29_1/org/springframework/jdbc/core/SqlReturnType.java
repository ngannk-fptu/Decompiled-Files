/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.jdbc.core;

import java.sql.CallableStatement;
import java.sql.SQLException;
import org.springframework.lang.Nullable;

public interface SqlReturnType {
    public static final int TYPE_UNKNOWN = Integer.MIN_VALUE;

    public Object getTypeValue(CallableStatement var1, int var2, int var3, @Nullable String var4) throws SQLException;
}

