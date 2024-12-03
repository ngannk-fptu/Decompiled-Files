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
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.java.ao.DatabaseProvider;
import net.java.ao.DisposableDataSource;
import net.java.ao.Query;
import net.java.ao.db.FileSystemUtils;
import net.java.ao.schema.IndexNameConverter;
import net.java.ao.schema.NameConverters;
import net.java.ao.schema.UniqueNameConverter;
import net.java.ao.schema.ddl.DDLField;
import net.java.ao.schema.ddl.DDLIndex;
import net.java.ao.schema.ddl.DDLIndexField;
import net.java.ao.schema.ddl.DDLTable;
import net.java.ao.schema.ddl.SQLAction;
import net.java.ao.types.TypeManager;
import net.java.ao.types.TypeQualifiers;

public class MySQLDatabaseProvider
extends DatabaseProvider {
    public static final String TRUNCATE_TO_255_CHARACTERS_FUNCTION = "(255)";
    private static final Set<String> RESERVED_WORDS = ImmutableSet.of((Object)"ACCESSIBLE", (Object)"ADD", (Object)"ALL", (Object)"ALTER", (Object)"ANALYZE", (Object)"AND", (Object[])new String[]{"AS", "ASC", "ASENSITIVE", "BEFORE", "BETWEEN", "BIGINT", "BINARY", "BLOB", "BOTH", "BY", "CALL", "CASCADE", "CASE", "CHANGE", "CHAR", "CHARACTER", "CHECK", "COLLATE", "COLUMN", "COLUMNS", "CONDITION", "CONNECTION", "CONSTRAINT", "CONTINUE", "CONVERT", "CREATE", "CROSS", "CUBE", "CUME_DIST", "CURRENT_DATE", "CURRENT_TIME", "CURRENT_TIMESTAMP", "CURRENT_USER", "CURSOR", "DATABASE", "DATABASES", "DAY_HOUR", "DAY_MICROSECOND", "DAY_MINUTE", "DAY_SECOND", "DEC", "DECIMAL", "DECLARE", "DEFAULT", "DELAYED", "DELETE", "DENSE_RANK", "DESC", "DESCRIBE", "DETERMINISTIC", "DISTINCT", "DISTINCTROW", "DIV", "DOUBLE", "DROP", "DUAL", "EACH", "ELSE", "ELSEIF", "EMPTY", "ENCLOSED", "ESCAPED", "EXCEPT", "EXISTS", "EXIT", "EXPLAIN", "FALSE", "FETCH", "FIELDS", "FIRST_VALUE", "FLOAT", "FLOAT4", "FLOAT8", "FOR", "FORCE", "FOREIGN", "FROM", "FULLTEXT", "FUNCTION", "GENERATED", "GET", "GOTO", "GRANT", "GROUP", "GROUPING", "GROUPS", "HAVING", "HIGH_PRIORITY", "HOUR_MICROSECOND", "HOUR_MINUTE", "HOUR_SECOND", "IF", "IGNORE", "IN", "INDEX", "INFILE", "INNER", "INOUT", "INSENSITIVE", "INSERT", "INT", "INT1", "INT2", "INT3", "INT4", "INT8", "INTEGER", "INTERSECT", "INTERVAL", "INTO", "IO_AFTER_GTIDS", "IO_BEFORE_GTIDS", "IS", "ITERATE", "JOIN", "JSON_TABLE", "KEY", "KEYS", "KILL", "LABEL", "LAG", "LAST_VALUE", "LATERAL", "LEAD", "LEADING", "LEAVE", "LEFT", "LIKE", "LIMIT", "LINEAR", "LINES", "LOAD", "LOCALTIME", "LOCALTIMESTAMP", "LOCK", "LONG", "LONGBLOB", "LONGTEXT", "LOOP", "LOW_PRIORITY", "MASTER_BIND", "MASTER_SSL_VERIFY_SERVER_CERT", "MATCH", "MAXVALUE", "MEDIUMBLOB", "MEDIUMINT", "MEDIUMTEXT", "MIDDLEINT", "MINUTE_MICROSECOND", "MINUTE_SECOND", "MOD", "MODIFIES", "NATURAL", "NOT", "NO_WRITE_TO_BINLOG", "NTH_VALUE", "NTILE", "NULL", "NUMERIC", "OF", "ON", "OPTIMIZE", "OPTIMIZER_COST", "OPTION", "OPTIONALLY", "OR", "ORDER", "OUT", "OUTER", "OUTFILE", "OVER", "PARTITION", "PERCENT_RANK", "PRECISION", "PRIMARY", "PRIVILEGES", "PROCEDURE", "PURGE", "RANGE", "RANK", "READ", "READ_WRITE", "READS", "REAL", "RECURSIVE", "REFERENCES", "REGEXP", "RELEASE", "RENAME", "REPEAT", "REPLACE", "REQUIRE", "RESIGNAL", "RESTRICT", "RETURN", "REVOKE", "RIGHT", "RLIKE", "ROW", "ROWS", "ROW_NUMBER", "SCHEMA", "SCHEMAS", "SECOND_MICROSECOND", "SELECT", "SENSITIVE", "SEPARATOR", "SET", "SHOW", "SIGNAL", "SMALLINT", "SONAME", "SPATIAL", "SPECIFIC", "SQL", "SQLEXCEPTION", "SQLSTATE", "SQLWARNING", "SQL_BIG_RESULT", "SQL_CALC_FOUND_ROWS", "SQL_SMALL_RESULT", "SSL", "STARTING", "STORED", "STRAIGHT_JOIN", "SYSTEM", "TABLE", "TABLES", "TERMINATED", "THEN", "TINYBLOB", "TINYINT", "TINYTEXT", "TO", "TRAILING", "TRIGGER", "TRUE", "UNDO", "UNION", "UNIQUE", "UNLOCK", "UNSIGNED", "UPDATE", "UPGRADE", "USAGE", "USE", "USING", "UTC_DATE", "UTC_TIME", "UTC_TIMESTAMP", "VALUES", "VARBINARY", "VARCHAR", "VARCHARACTER", "VARYING", "VIRTUAL", "WHEN", "WHERE", "WHILE", "WINDOW", "WITH", "WRITE", "XOR", "YEAR_MONTH", "ZEROFILL"});

    public MySQLDatabaseProvider(DisposableDataSource dataSource) {
        super(dataSource, null, TypeManager.mysql());
    }

    @Override
    protected String renderAutoIncrement() {
        return "AUTO_INCREMENT";
    }

    @Override
    protected String renderAppend() {
        return "ENGINE=InnoDB";
    }

    @Override
    protected String renderUnique(UniqueNameConverter uniqueNameConverter, DDLTable table, DDLField field) {
        return "";
    }

    @Override
    protected String renderQueryLimit(Query query) {
        StringBuilder sql = new StringBuilder();
        int offset = query.getOffset();
        int limit = query.getLimit();
        if (offset > 0) {
            sql.append(" LIMIT ");
            sql.append(offset);
            sql.append(", ");
            if (limit >= 0) {
                sql.append(limit);
            } else {
                sql.append("18446744073709551615");
            }
        } else if (limit >= 0) {
            sql.append(" LIMIT ");
            sql.append(limit);
        }
        return sql.toString();
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
    protected Iterable<SQLAction> renderAlterTableAddColumn(NameConverters nameConverters, DDLTable table, DDLField field) {
        Iterable<SQLAction> back = super.renderAlterTableAddColumn(nameConverters, table, field);
        if (field.isUnique()) {
            return Iterables.concat(back, (Iterable)ImmutableList.of((Object)this.alterAddUniqueConstraint(nameConverters, table, field)));
        }
        return back;
    }

    private SQLAction alterAddUniqueConstraint(NameConverters nameConverters, DDLTable table, DDLField field) {
        return SQLAction.of(new StringBuilder().append("ALTER TABLE ").append(this.withSchema(table.getName())).append(" ADD CONSTRAINT ").append(nameConverters.getUniqueNameConverter().getName(table.getName(), field.getName())).append(" UNIQUE (").append(this.processID(field.getName())).append(")"));
    }

    @Override
    protected Iterable<SQLAction> renderAlterTableChangeColumn(NameConverters nameConverters, DDLTable table, DDLField oldField, DDLField field) {
        ImmutableList.Builder back = ImmutableList.builder();
        back.addAll(this.renderDropAccessoriesForField(nameConverters, table, oldField));
        back.add((Object)this.renderAlterTableChangeColumnStatement(nameConverters, table, oldField, field, this.renderFieldOptionsInAlterColumn()));
        if (oldField.isUnique() && !field.isUnique()) {
            back.add((Object)SQLAction.of(new StringBuilder().append("ALTER TABLE ").append(this.withSchema(table.getName())).append(" DROP INDEX ").append(nameConverters.getUniqueNameConverter().getName(table.getName(), field.getName()))));
        }
        if (!oldField.isUnique() && field.isUnique()) {
            back.add((Object)this.alterAddUniqueConstraint(nameConverters, table, field));
        }
        back.addAll(this.renderAccessoriesForField(nameConverters, table, field));
        return back.build();
    }

    @Override
    protected SQLAction renderCreateIndex(IndexNameConverter indexNameConverter, DDLIndex index) {
        String statement = "CREATE INDEX " + this.processID(index.getIndexName()) + " ON " + this.processID(index.getTable()) + Stream.of(index.getFields()).map(this::appendLength).collect(Collectors.joining(",", "(", ")"));
        return SQLAction.of(statement);
    }

    private String appendLength(DDLIndexField indexField) {
        TypeQualifiers qualifiers = indexField.getType().getQualifiers();
        if (qualifiers.hasStringLength() && qualifiers.getStringLength() > 255) {
            return this.processID(indexField.getFieldName()) + TRUNCATE_TO_255_CHARACTERS_FUNCTION;
        }
        return this.processID(indexField.getFieldName());
    }

    @Override
    public SQLAction renderCreateCompositeIndex(String tableName, String indexName, List<String> fields) {
        StringBuilder statement = new StringBuilder();
        statement.append("CREATE INDEX " + this.processID(indexName));
        statement.append(" ON " + this.processID(tableName));
        statement.append(" (");
        boolean needDelimiter = false;
        for (String field : fields) {
            if (needDelimiter) {
                statement.append(",");
            }
            statement.append(this.processID(field));
            needDelimiter = true;
        }
        statement.append(")");
        return SQLAction.of(statement);
    }

    @Override
    public void putNull(PreparedStatement stmt, int index) throws SQLException {
        stmt.setString(index, null);
    }

    @Override
    protected Set<String> getReservedWords() {
        return RESERVED_WORDS;
    }

    @Override
    public boolean isCaseSensitive() {
        return FileSystemUtils.isCaseSensitive();
    }
}

