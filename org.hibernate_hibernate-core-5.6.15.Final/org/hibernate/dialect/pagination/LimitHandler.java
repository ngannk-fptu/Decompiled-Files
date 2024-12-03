/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect.pagination;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.hibernate.engine.spi.QueryParameters;
import org.hibernate.engine.spi.RowSelection;

public interface LimitHandler {
    public boolean supportsLimit();

    public boolean supportsLimitOffset();

    public String processSql(String var1, RowSelection var2);

    default public String processSql(String sql, QueryParameters queryParameters) {
        return this.processSql(sql, queryParameters.getRowSelection());
    }

    public int bindLimitParametersAtStartOfQuery(RowSelection var1, PreparedStatement var2, int var3) throws SQLException;

    public int bindLimitParametersAtEndOfQuery(RowSelection var1, PreparedStatement var2, int var3) throws SQLException;

    public void setMaxRows(RowSelection var1, PreparedStatement var2) throws SQLException;
}

