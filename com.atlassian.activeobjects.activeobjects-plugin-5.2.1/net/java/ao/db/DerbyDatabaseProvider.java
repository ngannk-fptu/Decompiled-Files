/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 */
package net.java.ao.db;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.java.ao.DatabaseProvider;
import net.java.ao.DisposableDataSource;
import net.java.ao.Query;
import net.java.ao.schema.IndexNameConverter;
import net.java.ao.schema.NameConverters;
import net.java.ao.schema.ddl.DDLField;
import net.java.ao.schema.ddl.DDLIndex;
import net.java.ao.schema.ddl.DDLTable;
import net.java.ao.schema.ddl.SQLAction;
import net.java.ao.types.TypeManager;

abstract class DerbyDatabaseProvider
extends DatabaseProvider {
    private static final Set<String> RESERVED_WORDS = ImmutableSet.of((Object)"ADD", (Object)"ALL", (Object)"ALLOCATE", (Object)"ALTER", (Object)"AND", (Object)"ANY", (Object[])new String[]{"ARE", "AS", "ASC", "ASSERTION", "AT", "AUTHORIZATION", "AVG", "BEGIN", "BETWEEN", "BIT", "BOOLEAN", "BOTH", "BY", "CALL", "CASCADE", "CASCADED", "CASE", "CAST", "CHAR", "CHARACTER", "CHECK", "CLOSE", "COLLATE", "COLLATION", "COLUMN", "COMMIT", "CONNECT", "CONNECTION", "CONSTRAINT", "CONSTRAINTS", "CONTINUE", "CONVERT", "CORRESPONDING", "COUNT", "CREATE", "CURRENT", "CURRENT_DATE", "CURRENT_TIME", "CURRENT_TIMESTAMP", "CURRENT_USER", "CURSOR", "DEALLOCATE", "DEC", "DECIMAL", "DECLARE", "DEFERRABLE", "DEFERRED", "DELETE", "DESC", "DESCRIBE", "DIAGNOSTICS", "DISCONNECT", "DISTINCT", "DOUBLE", "DROP", "ELSE", "END", "ENDEXEC", "ESCAPE", "EXCEPT", "EXCEPTION", "EXEC", "EXECUTE", "EXISTS", "EXPLAIN", "EXTERNAL", "FALSE", "FETCH", "FIRST", "FLOAT", "FOR", "FOREIGN", "FOUND", "FROM", "FULL", "FUNCTION", "GET", "GET_CURRENT_CONNECTION", "GLOBAL", "GO", "GOTO", "GRANT", "GROUP", "HAVING", "HOUR", "IDENTITY", "IMMEDIATE", "IN", "INDICATOR", "INITIALLY", "INNER", "INOUT", "INPUT", "INSENSITIVE", "INSERT", "INT", "INTEGER", "INTERSECT", "INTO", "IS", "ISOLATION", "JOIN", "KEY", "LAST", "LEFT", "LIKE", "LONGINT", "LOWER", "LTRIM", "MATCH", "MAX", "MIN", "MINUTE", "NATIONAL", "NATURAL", "NCHAR", "NVARCHAR", "NEXT", "NO", "NOT", "NULL", "NULLIF", "NUMERIC", "OF", "ON", "ONLY", "OPEN", "OPTION", "OR", "ORDER", "OUT", "OUTER", "OUTPUT", "OVERLAPS", "PAD", "PARTIAL", "PREPARE", "PRESERVE", "PRIMARY", "PRIOR", "PRIVILEGES", "PROCEDURE", "PUBLIC", "READ", "REAL", "REFERENCES", "RELATIVE", "RESTRICT", "REVOKE", "RIGHT", "ROLLBACK", "ROWS", "RTRIM", "SCHEMA", "SCROLL", "SECOND", "SELECT", "SESSION_USER", "SET", "SMALLINT", "SOME", "SPACE", "SQL", "SQLCODE", "SQLERROR", "SQLSTATE", "SUBSTR", "SUBSTRING", "SUM", "SYSTEM_USER", "TABLE", "TEMPORARY", "TIMEZONE_HOUR", "TIMEZONE_MINUTE", "TO", "TRAILING", "TRANSACTION", "TRANSLATE", "TRANSLATION", "TRUE", "UNION", "UNIQUE", "UNKNOWN", "UPDATE", "UPPER", "USER", "USING", "VALUES", "VARCHAR", "VARYING", "VIEW", "WHENEVER", "WHERE", "WITH", "WORK", "WRITE", "XML", "XMLEXISTS", "XMLPARSE", "XMLSERIALIZE", "YEAR"});

    DerbyDatabaseProvider(DisposableDataSource dataSource) {
        super(dataSource, null, TypeManager.derby());
    }

    @Override
    public String renderMetadataQuery(String tableName) {
        return "SELECT * FROM (SELECT * FROM " + this.withSchema(tableName) + ") WHERE ROWNUM <= 1";
    }

    @Override
    public void setQueryStatementProperties(Statement stmt, Query query) throws SQLException {
        int limit = query.getLimit();
        if (limit >= 0) {
            stmt.setFetchSize(limit);
            stmt.setMaxRows(limit);
        }
    }

    @Override
    public void setQueryResultSetProperties(ResultSet res, Query query) throws SQLException {
        if (query.getOffset() > 0) {
            res.absolute(query.getOffset());
        }
    }

    @Override
    public ResultSet getTables(Connection conn) throws SQLException {
        return conn.getMetaData().getTables("APP", this.getSchema(), null, new String[]{"TABLE"});
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
    protected void setPostConnectionProperties(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement();){
            stmt.executeUpdate("SET SCHEMA app");
        }
    }

    @Override
    protected String renderQueryLimit(Query query) {
        return "";
    }

    @Override
    protected String renderAutoIncrement() {
        return "GENERATED BY DEFAULT AS IDENTITY";
    }

    @Override
    public Object handleBlob(ResultSet res, Class<?> type, String field) throws SQLException {
        Blob blob = res.getBlob(field);
        if (type.equals(InputStream.class)) {
            return new ByteArrayInputStream(blob.getBytes(1L, (int)blob.length()));
        }
        if (type.equals(byte[].class)) {
            return blob.getBytes(1L, (int)blob.length());
        }
        return null;
    }

    @Override
    protected Iterable<SQLAction> renderAlterTableChangeColumn(NameConverters nameConverters, DDLTable table, DDLField oldField, DDLField field) {
        this.logger.warn("Derby doesn't support CHANGE TABLE statements!");
        this.logger.warn("Migration may not be entirely in sync as a result!");
        return ImmutableList.of();
    }

    @Override
    protected Iterable<SQLAction> renderAlterTableDropColumn(NameConverters nameConverters, DDLTable table, DDLField field) {
        System.err.println("WARNING: Derby doesn't support ALTER TABLE DROP COLUMN statements");
        return ImmutableList.of();
    }

    @Override
    protected SQLAction renderDropIndex(IndexNameConverter indexNameConverter, DDLIndex index) {
        return SQLAction.of("DROP INDEX " + this.processID(index.getIndexName()));
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

