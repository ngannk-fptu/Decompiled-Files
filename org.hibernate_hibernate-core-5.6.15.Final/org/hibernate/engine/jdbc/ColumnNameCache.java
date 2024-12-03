/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

@Deprecated
public final class ColumnNameCache {
    private static final float LOAD_FACTOR = 0.75f;
    private final ConcurrentHashMap<String, Integer> columnNameToIndexCache;

    public ColumnNameCache(int columnCount) {
        this.columnNameToIndexCache = new ConcurrentHashMap(columnCount + (int)((float)columnCount * 0.75f) + 1, 0.75f);
    }

    public Integer getIndexForColumnName(String columnName, ResultSet rs) throws SQLException {
        Integer cached = this.columnNameToIndexCache.get(columnName);
        if (cached != null) {
            return cached;
        }
        Integer index = rs.findColumn(columnName);
        this.columnNameToIndexCache.put(columnName, index);
        return index;
    }
}

