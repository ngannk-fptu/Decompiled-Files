/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.hbm2ddl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.ForeignKey;

@Deprecated
public class ForeignKeyMetadata {
    private final String name;
    private final String refTable;
    private final Map references = new HashMap();

    ForeignKeyMetadata(ResultSet rs) throws SQLException {
        this.name = rs.getString("FK_NAME");
        this.refTable = rs.getString("PKTABLE_NAME");
    }

    public String getName() {
        return this.name;
    }

    public String getReferencedTableName() {
        return this.refTable;
    }

    void addReference(ResultSet rs) throws SQLException {
        this.references.put(rs.getString("FKCOLUMN_NAME").toLowerCase(Locale.ROOT), rs.getString("PKCOLUMN_NAME"));
    }

    private boolean hasReference(Column column, Column ref) {
        String refName = (String)this.references.get(column.getName().toLowerCase(Locale.ROOT));
        return ref.getName().equalsIgnoreCase(refName);
    }

    public boolean matches(ForeignKey fk) {
        if (this.refTable.equalsIgnoreCase(fk.getReferencedTable().getName()) && fk.getColumnSpan() == this.references.size()) {
            List<Column> fkRefs = fk.isReferenceToPrimaryKey() ? fk.getReferencedTable().getPrimaryKey().getColumns() : fk.getReferencedColumns();
            for (int i = 0; i < fk.getColumnSpan(); ++i) {
                Column ref;
                Column column = fk.getColumn(i);
                if (this.hasReference(column, ref = fkRefs.get(i))) continue;
                return false;
            }
            return true;
        }
        return false;
    }

    public String toString() {
        return "ForeignKeyMetadata(" + this.name + ')';
    }
}

