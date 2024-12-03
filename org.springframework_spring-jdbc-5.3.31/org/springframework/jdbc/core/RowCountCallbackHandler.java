/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.jdbc.core;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.lang.Nullable;

public class RowCountCallbackHandler
implements RowCallbackHandler {
    private int rowCount;
    private int columnCount;
    @Nullable
    private int[] columnTypes;
    @Nullable
    private String[] columnNames;

    @Override
    public final void processRow(ResultSet rs) throws SQLException {
        if (this.rowCount == 0) {
            ResultSetMetaData rsmd = rs.getMetaData();
            this.columnCount = rsmd.getColumnCount();
            this.columnTypes = new int[this.columnCount];
            this.columnNames = new String[this.columnCount];
            for (int i = 0; i < this.columnCount; ++i) {
                this.columnTypes[i] = rsmd.getColumnType(i + 1);
                this.columnNames[i] = JdbcUtils.lookupColumnName(rsmd, i + 1);
            }
        }
        this.processRow(rs, this.rowCount++);
    }

    protected void processRow(ResultSet rs, int rowNum) throws SQLException {
    }

    @Nullable
    public final int[] getColumnTypes() {
        return this.columnTypes;
    }

    @Nullable
    public final String[] getColumnNames() {
        return this.columnNames;
    }

    public final int getRowCount() {
        return this.rowCount;
    }

    public final int getColumnCount() {
        return this.columnCount;
    }
}

