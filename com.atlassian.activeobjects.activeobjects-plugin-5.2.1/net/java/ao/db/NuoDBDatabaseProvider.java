/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.ImmutableSet
 */
package net.java.ao.db;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.java.ao.ActiveObjectsException;
import net.java.ao.Common;
import net.java.ao.DatabaseProvider;
import net.java.ao.DisposableDataSource;
import net.java.ao.schema.IndexNameConverter;
import net.java.ao.schema.NameConverters;
import net.java.ao.schema.UniqueNameConverter;
import net.java.ao.schema.ddl.DDLField;
import net.java.ao.schema.ddl.DDLForeignKey;
import net.java.ao.schema.ddl.DDLIndex;
import net.java.ao.schema.ddl.DDLIndexField;
import net.java.ao.schema.ddl.DDLTable;
import net.java.ao.schema.ddl.SQLAction;
import net.java.ao.types.TypeManager;

public class NuoDBDatabaseProvider
extends DatabaseProvider {
    public static final Set<String> RESERVED_WORDS = ImmutableSet.of((Object)"ACCESS", (Object)"ACCOUNT", (Object)"ACTIVATE", (Object)"ADD", (Object)"ADMIN", (Object)"ADVISE", (Object[])new String[]{"AFTER", "ALL", "ALL_ROWS", "ALLOCATE", "ALTER", "ANALYZE", "AND", "ANY", "ARCHIVE", "ARCHIVELOG", "ARRAY", "AS", "ASC", "AT", "AUDIT", "AUTHENTICATED", "AUTHORIZATION", "AUTOEXTEND", "AUTOMATIC", "BACKUP", "BECOME", "BEFORE", "BEGIN", "BETWEEN", "BFILE", "BITMAP", "BLOB", "BLOCK", "BODY", "BY", "CACHE", "CACHE_INSTANCES", "CANCEL", "CASCADE", "CAST", "CFILE", "CHAINED", "CHANGE", "CHAR", "CHAR_CS", "CHARACTER", "CHECK", "CHECKPOINT", "CHOOSE", "CHUNK", "CLEAR", "CLOB", "CLONE", "CLOSE", "CLOSE_CACHED_OPEN_CURSORS", "CLUSTER", "COALESCE", "COLUMN", "COLUMNS", "COMMENT", "COMMIT", "COMMITTED", "COMPATIBILITY", "COMPILE", "COMPLETE", "COMPOSITE_LIMIT", "COMPRESS", "COMPUTE", "CONNECT", "CONNECT_TIME", "CONSTRAINT", "CONSTRAINTS", "CONTENTS", "CONTINUE", "CONTROLFILE", "CONVERT", "COST", "CPU_PER_CALL", "CPU_PER_SESSION", "CREATE", "CURRENT", "CURRENT_SCHEMA", "CURREN_USER", "CURSOR", "CYCLE", "DANGLING", "DATABASE", "DATAFILE", "DATAFILES", "DATAOBJNO", "DATE", "DBA", "DBHIGH", "DBLOW", "DBMAC", "DEALLOCATE", "DEBUG", "DEC", "DECIMAL", "DECLARE", "DEFAULT", "DEFERRABLE", "DEFERRED", "DEGREE", "DELETE", "DEREF", "DESC", "DIRECTORY", "DISABLE", "DISCONNECT", "DISMOUNT", "DISTINCT", "DISTRIBUTED", "DML", "DOUBLE", "DROP", "DUMP", "EACH", "ELSE", "ENABLE", "END", "ENFORCE", "ENTRY", "ESCAPE", "EXCEPT", "EXCEPTIONS", "EXCHANGE", "EXCLUDING", "EXCLUSIVE", "EXECUTE", "EXISTS", "EXPIRE", "EXPLAIN", "EXTENT", "EXTENTS", "EXTERNALLY", "FAILED_LOGIN_ATTEMPTS", "FALSE", "FAST", "FILE", "FIRST_ROWS", "FLAGGER", "FLOAT", "FLOB", "FLUSH", "FOR", "FORCE", "FOREIGN", "FREELIST", "FREELISTS", "FROM", "FULL", "FUNCTION", "GLOBAL", "GLOBALLY", "GLOBAL_NAME", "GRANT", "GROUP", "GROUPS", "HASH", "HASHKEYS", "HAVING", "HEADER", "HEAP", "IDENTIFIED", "IDGENERATORS", "IDLE_TIME", "IF", "IMMEDIATE", "IN", "INCLUDING", "INCREMENT", "INDEX", "INDEXED", "INDEXES", "INDICATOR", "IND_PARTITION", "INITIAL", "INITIALLY", "INITRANS", "INSERT", "INSTANCE", "INSTANCES", "INSTEAD", "INT", "INTEGER", "INTERMEDIATE", "INTERSECT", "INTO", "IS", "ISOLATION", "ISOLATION_LEVEL", "KEEP", "KEY", "KILL", "LABEL", "LAYER", "LESS", "LEVEL", "LIBRARY", "LIKE", "LIMIT", "LINK", "LIST", "LOB", "LOCAL", "LOCK", "LOCKED", "LOG", "LOGFILE", "LOGGING", "LOGICAL_READS_PER_CALL", "LOGICAL_READS_PER_SESSION", "LONG", "MANAGE", "MASTER", "MAX", "MAXARCHLOGS", "MAXDATAFILES", "MAXEXTENTS", "MAXINSTANCES", "MAXLOGFILES", "MAXLOGHISTORY", "MAXLOGMEMBERS", "MAXSIZE", "MAXTRANS", "MAXVALUE", "MIN", "MEMBER", "MINIMUM", "MINEXTENTS", "MINUS", "MINVALUE", "MLSLABEL", "MLS_LABEL_FORMAT", "MODE", "MODIFY", "MOUNT", "MOVE", "MTS_DISPATCHERS", "MULTISET", "NATIONAL", "NCHAR", "NCHAR_CS", "NCLOB", "NEEDED", "NESTED", "NETWORK", "NEW", "NEXT", "NOARCHIVELOG", "NOAUDIT", "NOCACHE", "NOCOMPRESS", "NOCYCLE", "NOFORCE", "NOLOGGING", "NOMAXVALUE", "NOMINVALUE", "NONE", "NOORDER", "NOOVERRIDE", "NOPARALLEL", "NOPARALLEL", "NOREVERSE", "NORMAL", "NOSORT", "NOT", "NOTHING", "NOWAIT", "NULL", "NUMBER", "NUMERIC", "NVARCHAR2", "OBJECT", "OBJNO", "OBJNO_REUSE", "OF", "OFF", "OFFLINE", "OID", "OIDINDEX", "OLD", "ON", "ONLINE", "ONLY", "OPCODE", "OPEN", "OPTIMAL", "OPTIMIZER_GOAL", "OPTION", "OR", "ORDER", "ORGANIZATION", "OSLABEL", "OVERFLOW", "OWN", "PACKAGE", "PARALLEL", "PARTITION", "PASSWORD", "PASSWORD_GRACE_TIME", "PASSWORD_LIFE_TIME", "PASSWORD_LOCK_TIME", "PASSWORD_REUSE_MAX", "PASSWORD_REUSE_TIME", "PASSWORD_VERIFY_FUNCTION", "PCTFREE", "PCTINCREASE", "PCTTHRESHOLD", "PCTUSED", "PCTVERSION", "PERCENT", "PERMANENT", "PLAN", "PLSQL_DEBUG", "POST_TRANSACTION", "PRECISION", "PRESERVE", "PRIMARY", "PRIOR", "PRIVATE", "PRIVATE_SGA", "PRIVILEGE", "PRIVILEGES", "PROCEDURE", "PROFILE", "PUBLIC", "PURGE", "QUEUE", "QUOTA", "RANGE", "RAW", "RBA", "READ", "READUP", "REAL", "REBUILD", "RECOVER", "RECOVERABLE", "RECOVERY", "REF", "REFERENCES", "REFERENCING", "REFRESH", "RENAME", "REPLACE", "RESET", "RESETLOGS", "RESIZE", "RESOURCE", "RESTRICTED", "RETURN", "RETURNING", "REUSE", "REVERSE", "REVOKE", "ROLE", "ROLES", "ROLLBACK", "ROW", "ROWID", "ROWNUM", "ROWS", "RULE", "SAMPLE", "SAVEPOINT", "SB4", "SCAN_INSTANCES", "SCHEMA", "SCN", "SCOPE", "SD_ALL", "SD_INHIBIT", "SD_SHOW", "SEGMENT", "SEG_BLOCK", "SEG_FILE", "SELECT", "SEQUENCE", "SERIALIZABLE", "SESSION", "SESSION_CACHED_CURSORS", "SESSIONS_PER_USER", "SET", "SHARE", "SHARED", "SHARED_POOL", "SHRINK", "SIZE", "SKIP", "SKIP_UNUSABLE_INDEXES", "SMALLINT", "SNAPSHOT", "SOME", "SORT", "SPECIFICATION", "SPLIT", "SQL_TRACE", "STANDBY", "START", "STATEMENT_ID", "STATISTICS", "STOP", "STORAGE", "STORE", "STRUCTURE", "SUCCESSFUL", "SWITCH", "SYS_OP_ENFORCE_NOT_NULL$", "SYS_OP_NTCIMG$", "SYNONYM", "SYSDATE", "SYSDBA", "SYSOPER", "SYSTEM", "TABLE", "TABLES", "TABLESPACE", "TABLESPACE_NO", "TABNO", "TEMPORARY", "THAN", "THE", "THEN", "THREAD", "TIMESTAMP", "TIME", "TO", "TOPLEVEL", "TRACE", "TRACING", "TRANSACTION", "TRANSITIONAL", "TRIGGER", "TRIGGERS", "TRUE", "TRUNCATE", "TX", "TYPE", "UB2", "UBA", "UID", "UNARCHIVED", "UNDO", "UNION", "UNIQUE", "UNLIMITED", "UNLOCK", "UNRECOVERABLE", "UNTIL", "UNUSABLE", "UNUSED", "UPDATABLE", "UPDATE", "USAGE", "USE", "USER", "USING", "VALIDATE", "VALIDATION", "VALUE", "VALUES", "VARCHAR", "VARCHAR2", "VARYING", "VIEW", "WHEN", "WHENEVER", "WHERE", "WITH", "WITHOUT", "WORK", "WRITE", "WRITEDOWN", "WRITEUP", "XID", "YEAR", "ZONE"});

    public NuoDBDatabaseProvider(DisposableDataSource dataSource) {
        this(dataSource, null);
    }

    public NuoDBDatabaseProvider(DisposableDataSource dataSource, String schema) {
        super(dataSource, schema, TypeManager.nuodb());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ResultSet getTables(Connection conn) throws SQLException {
        int transactionIsolation = conn.getTransactionIsolation();
        try {
            conn.setTransactionIsolation(2);
            ResultSet resultSet = conn.getMetaData().getTables(null, this.getSchema(), null, new String[]{"TABLE"});
            return resultSet;
        }
        finally {
            conn.setTransactionIsolation(transactionIsolation);
        }
    }

    @Override
    protected String renderAutoIncrement() {
        return "GENERATED BY DEFAULT AS IDENTITY";
    }

    @Override
    protected DatabaseProvider.RenderFieldOptions renderFieldOptionsInAlterColumn() {
        return new DatabaseProvider.RenderFieldOptions(false, false, true, true);
    }

    @Override
    protected Iterable<SQLAction> renderAlterTableChangeColumn(NameConverters nameConverters, DDLTable table, DDLField oldField, DDLField field) {
        ImmutableList.Builder back = ImmutableList.builder();
        back.addAll(this.renderDropAccessoriesForField(nameConverters, table, oldField));
        boolean needsAutoIncrement = field.isAutoIncrement();
        field.setAutoIncrement(false);
        if (oldField.isPrimaryKey() && oldField.getJdbcType() != field.getJdbcType()) {
            back.add((Object)this.findAndRenderDropUniqueIndex(nameConverters, table, field, true));
        }
        back.add((Object)this.renderAlterTableChangeColumnStatement(nameConverters, table, oldField, field, new DatabaseProvider.RenderFieldOptions(false, true, true)));
        if (oldField.getDefaultValue() != null && field.getDefaultValue() == null) {
            back.add((Object)SQLAction.of("ALTER TABLE " + this.withSchema(table.getName()) + " ALTER " + field.getName() + " DROP DEFAULT"));
        }
        if (oldField.isNotNull() && !field.isNotNull()) {
            back.add((Object)SQLAction.of("ALTER TABLE " + this.withSchema(table.getName()) + " ALTER " + field.getName() + " NULL"));
        }
        if (oldField.isUnique() && !field.isUnique()) {
            back.add((Object)this.findAndRenderDropUniqueIndex(nameConverters, table, field, false));
        }
        if (!oldField.isUnique() && field.isUnique()) {
            back.add((Object)this.renderUniqueIndex(nameConverters.getIndexNameConverter(), table.getName(), field.getName()));
        }
        if (field.isPrimaryKey() && oldField.getJdbcType() != field.getJdbcType()) {
            back.add((Object)SQLAction.of("ALTER TABLE " + this.withSchema(table.getName()) + " ADD PRIMARY KEY (" + field.getName() + ")"));
        }
        back.addAll(this.renderAccessoriesForField(nameConverters, table, field));
        return back.build();
    }

    @Override
    protected Iterable<SQLAction> renderDropAccessoriesForField(NameConverters nameConverters, DDLTable table, DDLField field) {
        return Stream.of(table.getIndexes()).filter(index -> index.containsFieldWithNameIgnoreCase(field.getName())).map(index -> this.renderDropIndex(nameConverters.getIndexNameConverter(), (DDLIndex)index)).collect(Collectors.toList());
    }

    @Override
    protected Iterable<SQLAction> renderAccessoriesForField(NameConverters nameConverters, DDLTable table, DDLField field) {
        return Stream.of(table.getIndexes()).filter(index -> index.containsFieldWithNameIgnoreCase(field.getName())).map(index -> this.renderCreateIndex(nameConverters.getIndexNameConverter(), (DDLIndex)index)).collect(Collectors.toList());
    }

    private SQLAction findAndRenderDropUniqueIndex(NameConverters nameConverters, DDLTable table, DDLField field, boolean isPrimary) {
        String indexName = this.findUniqueIndex(table.getName(), field.getName(), isPrimary);
        if (indexName == null || indexName.isEmpty()) {
            this.logger.error("Unable to find unique index for field {} in table {}", (Object)field.getName(), (Object)table.getName());
        }
        DDLIndex index = DDLIndex.builder().field(DDLIndexField.builder().fieldName(field.getName()).build()).table(table.getName()).indexName(indexName).build();
        SQLAction renderDropIndex = this.renderDropIndex(nameConverters.getIndexNameConverter(), index);
        return renderDropIndex;
    }

    private String findUniqueIndex(String table, String field, boolean returnPrimary) {
        Connection connection = null;
        try {
            connection = this.getConnection();
            ResultSet indexes = this.getIndexes(connection, table);
            while (indexes.next()) {
                if (!field.equalsIgnoreCase(indexes.getString("COLUMN_NAME"))) continue;
                String name = indexes.getString("INDEX_NAME");
                if (returnPrimary != name.contains("PRIMARY")) continue;
                String string = indexes.getString("INDEX_NAME");
                return string;
            }
            String string = null;
            return string;
        }
        catch (SQLException e) {
            throw new ActiveObjectsException(e);
        }
        finally {
            Common.closeQuietly(connection);
        }
    }

    @Override
    protected SQLAction renderCreateIndex(IndexNameConverter indexNameConverter, DDLIndex index) {
        String statement = "CREATE INDEX " + index.getIndexName() + " ON " + this.withSchema(index.getTable()) + Stream.of(index.getFields()).map(DDLIndexField::getFieldName).map(this::processID).collect(Collectors.joining(",", "(", ")"));
        return SQLAction.of(statement);
    }

    private SQLAction renderUniqueIndex(IndexNameConverter indexNameConverter, String table, String field) {
        return SQLAction.of("CREATE UNIQUE INDEX " + indexNameConverter.getName(this.shorten(table), this.shorten(field)) + " ON " + this.withSchema(table) + "(" + this.processID(field) + ")");
    }

    @Override
    protected SQLAction renderDropIndex(IndexNameConverter indexNameConverter, DDLIndex index) {
        String indexName = index.getIndexName();
        String tableName = index.getTable();
        if (this.hasIndex(tableName, indexName)) {
            return SQLAction.of("DROP INDEX " + this.withSchema("\"" + indexName + "\""));
        }
        return null;
    }

    @Override
    protected String renderConstraintsForTable(UniqueNameConverter uniqueNameConverter, DDLTable table) {
        StringBuilder back = new StringBuilder();
        for (DDLForeignKey key : table.getForeignKeys()) {
            back.append("    ").append(this.renderForeignKey(key));
            if (!this.disableForeignKey(key)) {
                back.append(",");
            }
            back.append("\n");
        }
        return back.toString();
    }

    @Override
    protected String renderForeignKey(DDLForeignKey key) {
        StringBuilder back = new StringBuilder();
        if (this.disableForeignKey(key)) {
            back.append("/* DISABLE ");
        }
        back.append("FOREIGN KEY (").append(this.processID(key.getField())).append(") REFERENCES ");
        back.append(this.withSchema(key.getTable())).append('(').append(this.processID(key.getForeignField())).append(")");
        if (this.disableForeignKey(key)) {
            back.append(" */");
        }
        return back.toString();
    }

    private boolean disableForeignKey(DDLForeignKey key) {
        return key.getTable().equals(key.getDomesticTable());
    }

    @Override
    protected SQLAction renderAlterTableDropKey(DDLForeignKey key) {
        StringBuilder sql = new StringBuilder();
        sql.append("ALTER TABLE ").append(this.withSchema(key.getDomesticTable()));
        sql.append(" DROP FOREIGN KEY (").append(this.processID(key.getField())).append(") REFERENCES ");
        sql.append(this.withSchema(key.getTable()));
        return SQLAction.of(sql);
    }

    @Override
    protected void loadQuoteString() {
        this.quoteRef.set("`");
    }

    @Override
    public Object parseValue(int type, String value) {
        if (value != null) {
            Matcher matcher = Pattern.compile("'(.*)'.*").matcher(value);
            if (matcher.find()) {
                value = matcher.group(1);
            }
            if (value.isEmpty()) {
                value = null;
            }
        }
        return super.parseValue(type, value);
    }

    @Override
    protected Set<String> getReservedWords() {
        return RESERVED_WORDS;
    }

    @Override
    public void putNull(PreparedStatement stmt, int index) throws SQLException {
        stmt.setNull(index, 1111);
    }
}

