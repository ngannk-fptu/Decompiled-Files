/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.jdbc.core;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.springframework.lang.Nullable;

public interface SqlTypeValue {
    public static final int TYPE_UNKNOWN = Integer.MIN_VALUE;

    public void setTypeValue(PreparedStatement var1, int var2, int var3, @Nullable String var4) throws SQLException;
}

