/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.jdbc.core;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.jdbc.core.StatementCreatorUtils;
import org.springframework.lang.Nullable;

@Deprecated
public abstract class BatchUpdateUtils {
    public static int[] executeBatchUpdate(String sql, final List<Object[]> batchArgs, final int[] columnTypes, JdbcOperations jdbcOperations) {
        if (batchArgs.isEmpty()) {
            return new int[0];
        }
        return jdbcOperations.batchUpdate(sql, new BatchPreparedStatementSetter(){

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Object[] values = (Object[])batchArgs.get(i);
                BatchUpdateUtils.setStatementParameters(values, ps, columnTypes);
            }

            @Override
            public int getBatchSize() {
                return batchArgs.size();
            }
        });
    }

    protected static void setStatementParameters(Object[] values, PreparedStatement ps, @Nullable int[] columnTypes) throws SQLException {
        int colIndex = 0;
        for (Object value : values) {
            ++colIndex;
            if (value instanceof SqlParameterValue) {
                SqlParameterValue paramValue = (SqlParameterValue)value;
                StatementCreatorUtils.setParameterValue(ps, colIndex, paramValue, paramValue.getValue());
                continue;
            }
            int colType = columnTypes == null || columnTypes.length < colIndex ? Integer.MIN_VALUE : columnTypes[colIndex - 1];
            StatementCreatorUtils.setParameterValue(ps, colIndex, colType, value);
        }
    }
}

