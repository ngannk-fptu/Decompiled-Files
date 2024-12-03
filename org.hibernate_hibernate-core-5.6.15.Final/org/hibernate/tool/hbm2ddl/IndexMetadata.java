/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.hbm2ddl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.tool.hbm2ddl.ColumnMetadata;

@Deprecated
public class IndexMetadata {
    private final String name;
    private final List columns = new ArrayList();

    IndexMetadata(ResultSet rs) throws SQLException {
        this.name = rs.getString("INDEX_NAME");
    }

    public String getName() {
        return this.name;
    }

    void addColumn(ColumnMetadata column) {
        if (column != null) {
            this.columns.add(column);
        }
    }

    public ColumnMetadata[] getColumns() {
        return this.columns.toArray(new ColumnMetadata[0]);
    }

    public String toString() {
        return "IndexMatadata(" + this.name + ')';
    }
}

