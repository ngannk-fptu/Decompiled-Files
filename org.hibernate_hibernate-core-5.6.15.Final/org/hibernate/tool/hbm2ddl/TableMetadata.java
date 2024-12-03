/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.hbm2ddl;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.tool.hbm2ddl.ColumnMetadata;
import org.hibernate.tool.hbm2ddl.ForeignKeyMetadata;
import org.hibernate.tool.hbm2ddl.IndexMetadata;

@Deprecated
public class TableMetadata {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(TableMetadata.class);
    private final String catalog;
    private final String schema;
    private final String name;
    private final Map<String, ColumnMetadata> columns = new HashMap<String, ColumnMetadata>();
    private final Map<String, ForeignKeyMetadata> foreignKeys = new HashMap<String, ForeignKeyMetadata>();
    private final Map<String, IndexMetadata> indexes = new HashMap<String, IndexMetadata>();

    TableMetadata(ResultSet rs, DatabaseMetaData meta, boolean extras) throws SQLException {
        this.catalog = rs.getString("TABLE_CAT");
        this.schema = rs.getString("TABLE_SCHEM");
        this.name = rs.getString("TABLE_NAME");
        this.initColumns(meta);
        if (extras) {
            this.initForeignKeys(meta);
            this.initIndexes(meta);
        }
        String cat = this.catalog == null ? "" : this.catalog + '.';
        String schem = this.schema == null ? "" : this.schema + '.';
        LOG.tableFound(cat + schem + this.name);
        LOG.columns(this.columns.keySet());
        if (extras) {
            LOG.foreignKeys(this.foreignKeys.keySet());
            LOG.indexes(this.indexes.keySet());
        }
    }

    public String getName() {
        return this.name;
    }

    public String getCatalog() {
        return this.catalog;
    }

    public String getSchema() {
        return this.schema;
    }

    public String toString() {
        return "TableMetadata(" + this.name + ')';
    }

    public ColumnMetadata getColumnMetadata(String columnName) {
        return this.columns.get(columnName.toLowerCase(Locale.ROOT));
    }

    public ForeignKeyMetadata getForeignKeyMetadata(String keyName) {
        return this.foreignKeys.get(keyName.toLowerCase(Locale.ROOT));
    }

    public ForeignKeyMetadata getForeignKeyMetadata(ForeignKey fk) {
        for (ForeignKeyMetadata existingFk : this.foreignKeys.values()) {
            if (!existingFk.matches(fk)) continue;
            return existingFk;
        }
        return null;
    }

    public IndexMetadata getIndexMetadata(String indexName) {
        return this.indexes.get(indexName.toLowerCase(Locale.ROOT));
    }

    private void addForeignKey(ResultSet rs) throws SQLException {
        String fk = rs.getString("FK_NAME");
        if (fk == null) {
            return;
        }
        ForeignKeyMetadata info = this.getForeignKeyMetadata(fk);
        if (info == null) {
            info = new ForeignKeyMetadata(rs);
            this.foreignKeys.put(info.getName().toLowerCase(Locale.ROOT), info);
        }
        info.addReference(rs);
    }

    private void addIndex(ResultSet rs) throws SQLException {
        String index = rs.getString("INDEX_NAME");
        if (index == null) {
            return;
        }
        IndexMetadata info = this.getIndexMetadata(index);
        if (info == null) {
            info = new IndexMetadata(rs);
            this.indexes.put(info.getName().toLowerCase(Locale.ROOT), info);
        }
        info.addColumn(this.getColumnMetadata(rs.getString("COLUMN_NAME")));
    }

    public void addColumn(ResultSet rs) throws SQLException {
        String column = rs.getString("COLUMN_NAME");
        if (column == null) {
            return;
        }
        if (this.getColumnMetadata(column) == null) {
            ColumnMetadata info = new ColumnMetadata(rs);
            this.columns.put(info.getName().toLowerCase(Locale.ROOT), info);
        }
    }

    private void initForeignKeys(DatabaseMetaData meta) throws SQLException {
        try (ResultSet rs = null;){
            rs = meta.getImportedKeys(this.catalog, this.schema, this.name);
            while (rs.next()) {
                this.addForeignKey(rs);
            }
        }
    }

    private void initIndexes(DatabaseMetaData meta) throws SQLException {
        try (ResultSet rs = null;){
            rs = meta.getIndexInfo(this.catalog, this.schema, this.name, false, true);
            while (rs.next()) {
                if (rs.getShort("TYPE") == 0) continue;
                this.addIndex(rs);
            }
        }
    }

    private void initColumns(DatabaseMetaData meta) throws SQLException {
        try (ResultSet rs = null;){
            rs = meta.getColumns(this.catalog, this.schema, this.name, "%");
            while (rs.next()) {
                this.addColumn(rs);
            }
        }
    }
}

