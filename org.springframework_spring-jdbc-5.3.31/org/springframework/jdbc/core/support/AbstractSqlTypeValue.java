/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.jdbc.core.support;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.springframework.jdbc.core.SqlTypeValue;
import org.springframework.lang.Nullable;

public abstract class AbstractSqlTypeValue
implements SqlTypeValue {
    @Override
    public final void setTypeValue(PreparedStatement ps, int paramIndex, int sqlType, @Nullable String typeName) throws SQLException {
        Object value = this.createTypeValue(ps.getConnection(), sqlType, typeName);
        if (sqlType == Integer.MIN_VALUE) {
            ps.setObject(paramIndex, value);
        } else {
            ps.setObject(paramIndex, value, sqlType);
        }
    }

    protected abstract Object createTypeValue(Connection var1, int var2, @Nullable String var3) throws SQLException;
}

