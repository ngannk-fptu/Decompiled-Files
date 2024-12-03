/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.internal.upgrade.constraint.dedup;

import com.atlassian.confluence.internal.upgrade.constraint.dedup.DuplicateRowHolder;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class DuplicateRowHolderFactory {
    private final String idColumn;
    private final List<String> columns;

    public DuplicateRowHolderFactory(String idColumn, List<String> columns) {
        this.idColumn = Objects.requireNonNull(idColumn);
        this.columns = new ArrayList<String>(columns);
    }

    public DuplicateRowHolder make(ResultSet rs) throws SQLException {
        Object id = rs.getObject(this.idColumn);
        HashMap<String, Object> values = new HashMap<String, Object>();
        for (String column : this.columns) {
            values.put(column, rs.getObject(column));
        }
        return new DuplicateRowHolder(id, values);
    }
}

