/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Iterables
 */
package net.java.ao.db;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.java.ao.Common;
import net.java.ao.DBParam;
import net.java.ao.DatabaseProvider;
import net.java.ao.DisposableDataSource;
import net.java.ao.EntityManager;
import net.java.ao.Query;
import net.java.ao.RawEntity;
import net.java.ao.schema.IndexNameConverter;
import net.java.ao.schema.NameConverters;
import net.java.ao.schema.TableNameConverter;
import net.java.ao.schema.UniqueNameConverter;
import net.java.ao.schema.ddl.DDLField;
import net.java.ao.schema.ddl.DDLForeignKey;
import net.java.ao.schema.ddl.DDLIndex;
import net.java.ao.schema.ddl.DDLTable;
import net.java.ao.schema.ddl.SQLAction;
import net.java.ao.sql.SqlUtils;
import net.java.ao.types.TypeInfo;
import net.java.ao.types.TypeManager;

public final class HSQLDatabaseProvider
extends DatabaseProvider {
    private static final Set<String> RESERVED_WORDS = ImmutableSet.of((Object)"ADD", (Object)"ALL", (Object)"ALLOCATE", (Object)"ALTER", (Object)"AND", (Object)"ANY", (Object[])new String[]{"ARE", "ARRAY", "AS", "ASENSITIVE", "ASYMMETRIC", "AT", "ATOMIC", "AUTHORIZATION", "BEGIN", "BIGINT", "BINARY", "BLOB", "BOOLEAN", "BY", "CALL", "CALLED", "CASCADED", "CASE", "CAST", "CHAR", "CHARACTER", "CHECK", "CLOB", "CLOSE", "COLLATE", "COLUMN", "COMMIT", "CONDIITON", "CONNECT", "CONSTRAINT", "CONTINUE", "CORRESPONDING", "CREATE", "CROSS", "COUNT", "CUBE", "CURRENT", "CURRENT_DATE", "CURRENT_DEFAULT_TRANSFORM_GROUP", "CURRENT_PATH", "CURRENT_ROLE", "CURRENT_TIME", "CURRENT_TIMESTAMP", "CURRENT_TRANSFORM_GROUP_FOR_TYPE", "CURRENT_USER", "CURSOR", "CYCLE", "DATE", "DAY", "DEALLOCATE", "DEC", "DECIMAL", "DECLARE", "DEFAULT", "DELETE", "DEREF", "DESCRIBE", "DETERMINISTIC", "DISCONNECT", "DISTINCT", "DO", "DOUBLE", "DAYOFWEEK", "DROP", "DYNAMIC", "EACH", "ELEMENT", "ELSE", "ELSEIF", "END", "ESCAPE", "EXCEPT", "EXEC", "EXECUTE", "EXISTS", "EXIT", "EXTERNAL", "FALSE", "FETCH", "FILTER", "FLOAT", "FOR", "FOREIGN", "FREE", "FROM", "FULL", "FUNCTION", "GET", "GLOBAL", "GRANT", "GROUP", "GROUPING", "HANDLER", "HAVING", "HEADER", "HOLD", "HOUR", "IDENTITY", "IF", "IMMEDIATE", "IN", "INDICATOR", "INNER", "INOUT", "INPUT", "INSENSITIVE", "INSERT", "INT", "INTEGER", "INTERSECT", "INTERVAL", "INTO", "IS", "ITERATE", "JOIN", "LANGUAGE", "LARGE", "LATERAL", "LEADING", "LEAVE", "LEFT", "LIKE", "LOCAL", "LOCALTIME", "LOCALTIMESTAMP", "LOOP", "MATCH", "MEMBER", "METHOD", "MINUTE", "MODIFIES", "MODULE", "MONTH", "MULTISET", "NATIONAL", "NAUTRAL", "NCHAR", "NCLOB", "NEW", "NEXT", "NO", "NONE", "NOT", "NULL", "NUMERIC", "OF", "OLD", "ON", "ONLY", "OPEN", "OR", "ORDER", "OUT", "OUTER", "OUTPUT", "OVER", "OVERLAPS", "PARAMETER", "PARTITION", "PRECISION", "PREPARE", "PRIMARY", "PROCEDURE", "RANGE", "READS", "REAL", "RECURSIVE", "REF", "REFERENCES", "REFERENCING", "RELEASE", "REPEAT", "RESIGNAL", "RESULT", "RETURN", "RETURNS", "REVOKE", "RIGHT", "ROLLBACK", "ROLLUP", "ROW", "ROWS", "SAVEPOINT", "SCOPE", "SCROLL", "SECOND", "SEARCH", "SELECT", "SENSITIVE", "SESSION_USER", "SET", "SIGNAL", "SIMILAR", "SMALLINT", "SOME", "SPECIFIC", "SPECIFICTYPE", "SQL", "SQLEXCEPTION", "SQLSTATE", "SQLWARNING", "START", "STATIC", "SUBMULTISET", "SYMMETRIC", "SYSTEM", "SYSTEM_USER", "TABLE", "TABLESAMPLE", "THEN", "TIME", "TIMESTAMP", "TIMEZONE_HOUR", "TIMEZONE_MINUTE", "TO", "TRAILING", "TRANSLATION", "TREAT", "TRIGGER", "TRUE", "UNDO", "UNION", "UNIQUE", "UNKNOWN", "UNNEST", "UNTIL", "UPDATE", "USER", "USING", "VALUE", "VALUES", "VARCHAR", "VARYING", "WHEN", "WHENEVER", "WHERE", "WHILE", "WINDOW", "WITH", "WITHIN", "WITHOUT", "YEAR", "MIN", "MAX", "POSITION"});

    public HSQLDatabaseProvider(DisposableDataSource dataSource) {
        this(dataSource, "PUBLIC");
    }

    public HSQLDatabaseProvider(DisposableDataSource dataSource, String schema) {
        super(dataSource, schema, TypeManager.hsql());
    }

    @Override
    public String renderMetadataQuery(String tableName) {
        return "SELECT LIMIT 0 1 * FROM " + this.withSchema(tableName);
    }

    @Override
    public <T extends RawEntity<K>, K> K insertReturningKey(EntityManager manager, Connection conn, Class<T> entityType, Class<K> pkType, String pkField, boolean pkIdentity, String table, DBParam ... params) throws SQLException {
        StringBuilder sql = new StringBuilder("INSERT INTO " + this.processID(table) + " (");
        for (DBParam param : params) {
            sql.append(this.processID(param.getField()));
            sql.append(',');
        }
        if (params.length > 0) {
            sql.setLength(sql.length() - 1);
        } else {
            sql.append(this.processID(pkField));
        }
        sql.append(") VALUES (");
        for (DBParam param : params) {
            sql.append("?,");
        }
        if (params.length > 0) {
            sql.setLength(sql.length() - 1);
        } else {
            sql.append("NULL");
        }
        sql.append(")");
        return this.executeInsertReturningKey(manager, conn, entityType, pkType, pkField, sql.toString(), params);
    }

    @Override
    protected synchronized <T extends RawEntity<K>, K> K executeInsertReturningKey(EntityManager manager, Connection conn, Class<T> entityType, Class<K> pkType, String pkField, String sql, DBParam ... params) throws SQLException {
        Object back = null;
        try (PreparedStatement stmt = this.preparedStatement(conn, sql);){
            for (int i = 0; i < params.length; ++i) {
                Object value = params[i].getValue();
                if (value instanceof RawEntity) {
                    value = Common.getPrimaryKeyValue((RawEntity)value);
                }
                if (params[i].getField().equalsIgnoreCase(pkField)) {
                    back = value;
                }
                if (value == null) {
                    this.putNull(stmt, i + 1);
                    continue;
                }
                TypeInfo<?> type = this.typeManager.getType(value.getClass());
                type.getLogicalType().putToDatabase(manager, stmt, i + 1, value, type.getJdbcWriteType());
            }
            stmt.executeUpdate();
        }
        if (back == null) {
            stmt = conn.prepareStatement("CALL IDENTITY()");
            var10_10 = null;
            try (ResultSet res = stmt.executeQuery();){
                if (res.next()) {
                    back = this.typeManager.getType(pkType).getLogicalType().pullFromDatabase(null, res, pkType, 1);
                }
            }
            catch (Throwable throwable) {
                var10_10 = throwable;
                throw throwable;
            }
            finally {
                if (stmt != null) {
                    if (var10_10 != null) {
                        try {
                            stmt.close();
                        }
                        catch (Throwable throwable) {
                            var10_10.addSuppressed(throwable);
                        }
                    } else {
                        stmt.close();
                    }
                }
            }
        }
        return (K)back;
    }

    @Override
    public <T extends RawEntity<K>, K> void insertBatch(EntityManager manager, Connection conn, Class<T> entityType, Class<K> pkType, String pkField, boolean pkIdentity, String table, List<Map<String, Object>> rows) throws SQLException {
        for (Map<String, Object> row : rows) {
            DBParam[] arrParams = (DBParam[])row.entrySet().stream().map(column -> new DBParam((String)column.getKey(), column.getValue())).toArray(DBParam[]::new);
            this.insertReturningKey(manager, conn, entityType, pkType, pkField, pkIdentity, table, arrParams);
        }
    }

    @Override
    public Object parseValue(int type, String value) {
        if (value == null || value.equals("") || value.equals("NULL")) {
            return null;
        }
        switch (type) {
            case 12: 
            case 91: 
            case 92: 
            case 93: {
                Matcher matcher = Pattern.compile("'(.*)'.*").matcher(value);
                if (!matcher.find()) break;
                value = matcher.group(1);
            }
        }
        return super.parseValue(type, value);
    }

    @Override
    public ResultSet getTables(Connection conn) throws SQLException {
        return conn.getMetaData().getTables(null, this.getSchema(), null, new String[]{"TABLE"});
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void dispose() {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = this.getConnection();
            stmt = conn.createStatement();
            stmt.executeUpdate("SHUTDOWN");
        }
        catch (SQLException sQLException) {
            SqlUtils.closeQuietly(stmt);
            SqlUtils.closeQuietly(conn);
        }
        catch (Throwable throwable) {
            SqlUtils.closeQuietly(stmt);
            SqlUtils.closeQuietly(conn);
            throw throwable;
        }
        SqlUtils.closeQuietly(stmt);
        SqlUtils.closeQuietly(conn);
        super.dispose();
    }

    @Override
    protected String renderQuerySelect(Query query, TableNameConverter converter, boolean count) {
        StringBuilder sql = new StringBuilder();
        String tableName = query.getTable();
        if (tableName == null) {
            tableName = converter.getName(query.getTableType());
        }
        switch (query.getType()) {
            case SELECT: {
                sql.append("SELECT ");
                int limit = query.getLimit();
                int offset = query.getOffset();
                if (limit >= 0 || offset > 0) {
                    sql.append("LIMIT ");
                    if (offset > 0) {
                        sql.append(offset);
                    } else {
                        sql.append(0);
                    }
                    sql.append(" ");
                    if (limit >= 0) {
                        sql.append(limit);
                    } else {
                        sql.append(0);
                    }
                    sql.append(" ");
                }
                if (query.isDistinct()) {
                    sql.append("DISTINCT ");
                }
                if (count) {
                    sql.append("COUNT(*)");
                } else {
                    sql.append(this.querySelectFields(query, converter));
                }
                sql.append(" FROM ").append(this.queryTableName(query, converter));
            }
        }
        return sql.toString();
    }

    @Override
    protected String renderQueryLimit(Query query) {
        return "";
    }

    @Override
    protected String renderAutoIncrement() {
        return "GENERATED BY DEFAULT AS IDENTITY (START WITH 1)";
    }

    @Override
    protected String getDateFormat() {
        return "yyyy-MM-dd HH:mm:ss.SSS";
    }

    @Override
    protected String renderUnique(UniqueNameConverter uniqueNameConverter, DDLTable table, DDLField field) {
        return "";
    }

    @Override
    protected String renderConstraintsForTable(UniqueNameConverter uniqueNameConverter, DDLTable table) {
        StringBuilder back = new StringBuilder(super.renderConstraintsForTable(uniqueNameConverter, table));
        for (DDLField field : table.getFields()) {
            if (!field.isUnique()) continue;
            back.append(" CONSTRAINT ").append(uniqueNameConverter.getName(table.getName(), field.getName())).append(" UNIQUE(").append(this.processID(field.getName())).append("),\n");
        }
        return back.toString();
    }

    @Override
    protected String renderValue(Object value) {
        if (value instanceof Boolean) {
            if (value.equals(true)) {
                return "TRUE";
            }
            return "FALSE";
        }
        return super.renderValue(value);
    }

    @Override
    protected Iterable<SQLAction> renderAlterTableAddColumn(NameConverters nameConverters, DDLTable table, DDLField field) {
        Iterable<SQLAction> back = super.renderAlterTableAddColumn(nameConverters, table, field);
        if (field.isUnique()) {
            return Iterables.concat(back, (Iterable)ImmutableList.of((Object)this.renderAddUniqueConstraint(nameConverters.getUniqueNameConverter(), table, field)));
        }
        return back;
    }

    @Override
    protected Iterable<SQLAction> renderAlterTableChangeColumn(NameConverters nameConverters, DDLTable table, DDLField oldField, DDLField field) {
        ImmutableList.Builder sql = ImmutableList.builder();
        Iterable<DDLForeignKey> foreignKeysForField = this.findForeignKeysForField(table, oldField);
        for (DDLForeignKey fk : foreignKeysForField) {
            sql.add((Object)this.renderAlterTableDropKey(fk));
        }
        sql.addAll(super.renderAlterTableChangeColumn(nameConverters, table, oldField, field));
        if (!field.isPrimaryKey()) {
            UniqueNameConverter uniqueNameConverter = nameConverters.getUniqueNameConverter();
            if (!oldField.isUnique() && field.isUnique()) {
                sql.add((Object)this.renderAddUniqueConstraint(uniqueNameConverter, table, field));
            }
            if (oldField.isUnique() && !field.isUnique()) {
                sql.add((Object)SQLAction.of(new StringBuilder().append("ALTER TABLE ").append(this.withSchema(table.getName())).append(" DROP CONSTRAINT ").append(uniqueNameConverter.getName(table.getName(), field.getName()))));
            }
            if (!field.isNotNull()) {
                sql.add((Object)SQLAction.of(new StringBuilder().append("ALTER TABLE ").append(this.withSchema(table.getName())).append(" ALTER COLUMN ").append(oldField.getName()).append(" SET NULL")));
            }
            if (field.isNotNull()) {
                sql.add((Object)SQLAction.of(new StringBuilder().append("ALTER TABLE ").append(this.withSchema(table.getName())).append(" ALTER COLUMN ").append(oldField.getName()).append(" SET NOT NULL")));
            }
            if (field.getDefaultValue() != null && !field.getDefaultValue().equals(oldField.getDefaultValue())) {
                sql.add((Object)SQLAction.of(new StringBuilder().append("ALTER TABLE ").append(this.withSchema(table.getName())).append(" ALTER COLUMN ").append(oldField.getName()).append(" SET DEFAULT ").append(this.renderValue(field.getDefaultValue()))));
            }
            if (field.getDefaultValue() == null && oldField.getDefaultValue() != null) {
                sql.add((Object)SQLAction.of(new StringBuilder().append("ALTER TABLE ").append(this.withSchema(table.getName())).append(" ALTER COLUMN ").append(oldField.getName()).append(" DROP DEFAULT")));
            }
        }
        for (DDLForeignKey fk : foreignKeysForField) {
            sql.add((Object)this.renderAlterTableAddKey(fk));
        }
        return sql.build();
    }

    private SQLAction renderAddUniqueConstraint(UniqueNameConverter uniqueNameConverter, DDLTable table, DDLField field) {
        return SQLAction.of(new StringBuilder().append("ALTER TABLE ").append(this.withSchema(table.getName())).append(" ADD CONSTRAINT ").append(uniqueNameConverter.getName(table.getName(), field.getName())).append(" UNIQUE (").append(this.processID(field.getName())).append(")"));
    }

    @Override
    protected SQLAction renderAlterTableChangeColumnStatement(NameConverters nameConverters, DDLTable table, DDLField oldField, DDLField field, DatabaseProvider.RenderFieldOptions options) {
        StringBuilder current = new StringBuilder();
        current.append("ALTER TABLE ").append(this.withSchema(table.getName())).append(" ALTER COLUMN ");
        current.append(this.renderField(nameConverters, table, field, options));
        return SQLAction.of(current);
    }

    @Override
    protected DatabaseProvider.RenderFieldOptions renderFieldOptionsInAlterColumn() {
        return new DatabaseProvider.RenderFieldOptions(true, false, false);
    }

    @Override
    protected SQLAction renderAlterTableDropKey(DDLForeignKey key) {
        StringBuilder back = new StringBuilder("ALTER TABLE ");
        back.append(this.withSchema(key.getDomesticTable())).append(" DROP CONSTRAINT ").append(this.processID(key.getFKName()));
        return SQLAction.of(back);
    }

    @Override
    protected SQLAction renderDropIndex(IndexNameConverter indexNameConverter, DDLIndex index) {
        String indexName = index.getIndexName();
        return SQLAction.of(new StringBuilder("DROP INDEX ").append(this.withSchema(indexName)).append(" IF EXISTS"));
    }

    @Override
    protected Set<String> getReservedWords() {
        return RESERVED_WORDS;
    }

    @Override
    public boolean isCaseSensitive() {
        return false;
    }
}

