/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public interface ColumnAliasExtractor {
    public static final ColumnAliasExtractor COLUMN_LABEL_EXTRACTOR = new ColumnAliasExtractor(){

        @Override
        public String extractColumnAlias(ResultSetMetaData metaData, int position) throws SQLException {
            return metaData.getColumnLabel(position);
        }
    };
    public static final ColumnAliasExtractor COLUMN_NAME_EXTRACTOR = new ColumnAliasExtractor(){

        @Override
        public String extractColumnAlias(ResultSetMetaData metaData, int position) throws SQLException {
            return metaData.getColumnName(position);
        }
    };

    public String extractColumnAlias(ResultSetMetaData var1, int var2) throws SQLException;
}

