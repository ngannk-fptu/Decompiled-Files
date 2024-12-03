/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 */
package net.java.ao.db;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.java.ao.Common;
import net.java.ao.DBParam;
import net.java.ao.DatabaseProvider;
import net.java.ao.DisposableDataSource;
import net.java.ao.EntityManager;
import net.java.ao.RawEntity;
import net.java.ao.schema.Case;
import net.java.ao.schema.IndexNameConverter;
import net.java.ao.schema.NameConverters;
import net.java.ao.schema.UniqueNameConverter;
import net.java.ao.schema.ddl.DDLField;
import net.java.ao.schema.ddl.DDLForeignKey;
import net.java.ao.schema.ddl.DDLIndex;
import net.java.ao.schema.ddl.DDLIndexField;
import net.java.ao.schema.ddl.DDLTable;
import net.java.ao.schema.ddl.SQLAction;
import net.java.ao.types.TypeInfo;
import net.java.ao.types.TypeManager;

public class PostgreSQLDatabaseProvider
extends DatabaseProvider {
    private static final int MAX_SEQUENCE_LENGTH = 64;
    private static final String SQL_STATE_UNDEFINED_FUNCTION = "42883";
    private static final Pattern PATTERN_QUOTE_ID = Pattern.compile("(\\*|\\d*?)");
    private static final Set<String> RESERVED_WORDS = ImmutableSet.of((Object)"ABS", (Object)"ABSOLUTE", (Object)"ACOS", (Object)"ACTION", (Object)"ADD", (Object)"ADMIN", (Object[])new String[]{"AFTER", "AGGREGATE", "ALIAS", "ALL", "ALLOCATE", "ALTER", "ANALYSE", "ANALYZE", "AND", "ANY", "ARE", "ARRAY", "ARRAY_AGG", "ARRAY_MAX_CARDINALITY", "AS", "ASC", "ASENSITIVE", "ASIN", "ASSERTION", "ASYMMETRIC", "AT", "ATAN", "ATOMIC", "AUTHORIZATION", "AVG", "BEFORE", "BEGIN", "BEGIN_FRAME", "BEGIN_PARTITION", "BETWEEN", "BIGINT", "BINARY", "BIT", "BIT_LENGTH", "BLOB", "BOOLEAN", "BOTH", "BREADTH", "BY", "CALL", "CALLED", "CARDINALITY", "CASCADE", "CASCADED", "CASE", "CAST", "CATALOG", "CEIL", "CEILING", "CHAR", "CHARACTER", "CHARACTER_LENGTH", "CHAR_LENGTH", "CHECK", "CLASS", "CLASSIFIER", "CLOB", "CLOSE", "COALESCE", "COLLATE", "COLLATION", "COLLECT", "COLUMN", "COMMIT", "COMPLETION", "CONDITION", "CONNECT", "CONNECTION", "CONSTRAINT", "CONSTRAINTS", "CONSTRUCTOR", "CONTAINS", "CONTINUE", "CONVERT", "COPY", "CORR", "CORRESPONDING", "COS", "COSH", "COUNT", "COVAR_POP", "COVAR_SAMP", "CREATE", "CROSS", "CUBE", "CUME_DIST", "CURRENT", "CURRENT_CATALOG", "CURRENT_DATE", "CURRENT_DEFAULT_TRANSFORM_GROUP", "CURRENT_PATH", "CURRENT_ROLE", "CURRENT_ROW", "CURRENT_SCHEMA", "CURRENT_TIME", "CURRENT_TIMESTAMP", "CURRENT_TRANSFORM_GROUP_FOR_TYPE", "CURRENT_USER", "CURRENT_DEFAULT_TRANSFORM_GROUP", "CURRENT_TRANSFORM_GROUP_FOR_TYPE", "CURSOR", "CYCLE", "DATA", "DATALINK", "DATE", "DAY", "DEALLOCATE", "DEC", "DECFLOAT", "DECIMAL", "DECLARE", "DEFAULT", "DEFERRABLE", "DEFERRED", "DEFINE", "DELETE", "DENSE_RANK", "DEPTH", "DEREF", "DESC", "DESCRIBE", "DESCRIPTOR", "DESTROY", "DESTRUCTOR", "DETERMINISTIC", "DIAGNOSTICS", "DICTIONARY", "DISCONNECT", "DISTINCT", "DLNEWCOPY", "DLPREVIOUSCOPY", "DLURLCOMPLETE", "DLURLCOMPLETEONLY", "DLURLCOMPLETEWRITE", "DLURLPATH", "DLURLPATHONLY", "DLURLPATHWRITE", "DLURLSCHEME", "DLURLSERVER", "DLVALUE", "DO", "DOMAIN", "DOUBLE", "DROP", "DYNAMIC", "EACH", "ELEMENT", "ELSE", "EMPTY", "END", "END-EXEC", "END_FRAME", "END_PARTITION", "EQUALS", "ESCAPE", "EVERY", "EXCEPT", "EXCEPTION", "EXEC", "EXECUTE", "EXISTS", "EXP", "EXTERNAL", "EXTRACT", "FALSE", "FETCH", "FILTER", "FIRST", "FIRST_VALUE", "FLOAT", "FLOOR", "FOR", "FOREIGN", "FOUND", "FRAME_ROW", "FREE", "FREEZE", "FROM", "FULL", "FUNCTION", "FUSION", "GENERAL", "GET", "GLOBAL", "GO", "GOTO", "GRANT", "GREATEST", "GROUP", "GROUPING", "GROUPS", "HAVING", "HOLD", "HOST", "HOUR", "IDENTITY", "IGNORE", "ILIKE", "IMMEDIATE", "IMPORT", "IN", "INDICATOR", "INITIAL", "INITIALIZE", "INITIALLY", "INNER", "INOUT", "INPUT", "INSENSITIVE", "INSERT", "INT", "INTEGER", "INTERSECT", "INTERSECTION", "INTERVAL", "INTO", "IS", "ISNULL", "ISOLATION", "ITERATE", "JOIN", "JSON_ARRAY", "JSON_ARRAYAGG", "JSON_EXISTS", "JSON_OBJECT", "JSON_OBJECTAGG", "JSON_QUERY", "JSON_TABLE", "JSON_TABLE_PRIMITIVE", "JSON_VALUE", "KEY", "LAG", "LANGUAGE", "LARGE", "LAST", "LAST_VALUE", "LATERAL", "LEAD", "LEADING", "LEAST", "LEFT", "LESS", "LEVEL", "LIKE", "LIKE_REGEX", "LIMIT", "LISTAGG", "LN", "LOCAL", "LOCALTIME", "LOCALTIMESTAMP", "LOCATOR", "LOG", "LOG10", "LOWER", "MAP", "MATCH", "MATCHES", "MATCH_NUMBER", "MATCH_RECOGNIZE", "MAX", "MEASURES", "MEMBER", "MERGE", "METHOD", "MIN", "MINUTE", "MOD", "MODIFIES", "MODIFY", "MODULE", "MONTH", "MULTISET", "NAMES", "NATIONAL", "NATURAL", "NCHAR", "NCLOB", "NEW", "NEXT", "NO", "NONE", "NORMALIZE", "NOT", "NOTNULL", "NTH_VALUE", "NTILE", "NULL", "NULLIF", "NUMERIC", "OBJECT", "OCCURRENCES_REGEX", "OCTET_LENGTH", "OF", "OFF", "OFFSET", "OLD", "OMIT", "ON", "ONE", "ONLY", "OPEN", "OPERATION", "OPTION", "OR", "ORDER", "ORDINALITY", "OUT", "OUTER", "OUTPUT", "OVER", "OVERLAPS", "OVERLAY", "PAD", "PARAMETER", "PARAMETERS", "PARTIAL", "PARTITION", "PATH", "PATTERN", "PER", "PERCENT", "PERCENTILE_CONT", "PERCENTILE_DISC", "PERCENT_RANK", "PERIOD", "PERMUTE", "PLACING", "PORTION", "POSITION", "POSITION_REGEX", "POSTFIX", "POWER", "PRECEDES", "PRECISION", "PREFIX", "PREORDER", "PREPARE", "PRESERVE", "PRIMARY", "PRIOR", "PRIVILEGES", "PROCEDURE", "PTF", "PUBLIC", "RANGE", "RANK", "READ", "READS", "REAL", "RECURSIVE", "REF", "REFERENCES", "REFERENCING", "REGR_AVGX", "REGR_AVGY", "REGR_COUNT", "REGR_INTERCEPT", "REGR_R2", "REGR_SLOPE", "REGR_SXX", "REGR_SXY", "REGR_SYY", "RELATIVE", "RELEASE", "RESTRICT", "RESULT", "RETURN", "RETURNING", "RETURNS", "REVOKE", "RIGHT", "ROLE", "ROLLBACK", "ROLLUP", "ROUTINE", "ROW", "ROWS", "ROW_NUMBER", "RUNNING", "SAVEPOINT", "SCHEMA", "SCOPE", "SCROLL", "SEARCH", "SECOND", "SECTION", "SEEK", "SELECT", "SENSITIVE", "SEQUENCE", "SESSION", "SESSION_USER", "SET", "SETOF", "SETS", "SHOW", "SIMILAR", "SIN", "SINH", "SIZE", "SKIP", "SMALLINT", "SOME", "SPACE", "SPECIFIC", "SPECIFICTYPE", "SQL", "SQLCODE", "SQLERROR", "SQLEXCEPTION", "SQLSTATE", "SQLWARNING", "SQRT", "START", "STATE", "STATEMENT", "STATIC", "STDDEV_POP", "STDDEV_SAMP", "STRUCTURE", "SUBMULTISET", "SUBSET", "SUBSTRING", "SUBSTRING_REGEX", "SUCCEEDS", "SUM", "SYMMETRIC", "SYSTEM", "SYSTEM_TIME", "SYSTEM_USER", "TABLE", "TABLESAMPLE", "TAN", "TANH", "TEMPORARY", "TERMINATE", "THAN", "THEN", "TIME", "TIMESTAMP", "TIMEZONE_HOUR", "TIMEZONE_MINUTE", "TO", "TRAILING", "TRANSACTION", "TRANSLATE", "TRANSLATE_REGEX", "TRANSLATION", "TREAT", "TRIGGER", "TRIM", "TRIM_ARRAY", "TRUE", "TRUNCATE", "UESCAPE", "UNDER", "UNION", "UNIQUE", "UNKNOWN", "UNMATCHED", "UNNEST", "UPDATE", "UPPER", "USAGE", "USER", "USING", "VALUE", "VALUES", "VALUE_OF", "VARBINARY", "VARCHAR", "VARIABLE", "VARIADIC", "VARYING", "VAR_POP", "VAR_SAMP", "VERBOSE", "VERSIONING", "VIEW", "WHEN", "WHENEVER", "WHERE", "WIDTH_BUCKET", "WINDOW", "WITH", "WITHIN", "WITHOUT", "WORK", "WRITE", "XML", "XMLAGG", "XMLATTRIBUTES", "XMLBINARY", "XMLCAST", "XMLCOMMENT", "XMLCONCAT", "XMLDOCUMENT", "XMLELEMENT", "XMLEXISTS", "XMLFOREST", "XMLITERATE", "XMLNAMESPACES", "XMLPARSE", "XMLPI", "XMLQUERY", "XMLSERIALIZE", "XMLTABLE", "XMLTEXT", "XMLVALIDATE", "YEAR", "ZONE"});

    public PostgreSQLDatabaseProvider(DisposableDataSource dataSource) {
        this(dataSource, "public");
    }

    public PostgreSQLDatabaseProvider(DisposableDataSource dataSource, String schema) {
        super(dataSource, schema, TypeManager.postgres());
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
                break;
            }
            case -7: {
                try {
                    return Byte.parseByte(value);
                }
                catch (Throwable t) {
                    try {
                        return Boolean.parseBoolean(value);
                    }
                    catch (Throwable t1) {
                        return null;
                    }
                }
            }
        }
        return super.parseValue(type, value);
    }

    @Override
    public ResultSet getTables(Connection conn) throws SQLException {
        return conn.getMetaData().getTables(null, this.getSchema(), null, new String[]{"TABLE"});
    }

    @Override
    protected String renderAutoIncrement() {
        return "";
    }

    @Override
    protected String renderFieldType(DDLField field) {
        if (field.getJdbcType() == 2) {
            field.setType(this.typeManager.getType(Integer.class));
        }
        if (field.isAutoIncrement()) {
            if (field.getJdbcType() == -5) {
                return "BIGSERIAL";
            }
            return "SERIAL";
        }
        return super.renderFieldType(field);
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
    protected String renderUnique(UniqueNameConverter uniqueNameConverter, DDLTable table, DDLField field) {
        return "CONSTRAINT " + uniqueNameConverter.getName(table.getName(), field.getName()) + " UNIQUE";
    }

    @Override
    public Object handleBlob(ResultSet res, Class<?> type, String field) throws SQLException {
        if (type.equals(InputStream.class)) {
            return res.getBinaryStream(field);
        }
        if (type.equals(byte[].class)) {
            return res.getBytes(field);
        }
        return null;
    }

    @Override
    protected Iterable<SQLAction> renderAlterTableChangeColumn(NameConverters nameConverters, DDLTable table, DDLField oldField, DDLField field) {
        StringBuilder str;
        UniqueNameConverter uniqueNameConverter = nameConverters.getUniqueNameConverter();
        ArrayList back = Lists.newArrayList();
        if (!field.isUnique() && oldField.isUnique()) {
            back.add(SQLAction.of(new StringBuilder().append("ALTER TABLE ").append(this.withSchema(table.getName())).append(" DROP CONSTRAINT ").append(uniqueNameConverter.getName(table.getName(), oldField.getName()))));
        }
        if (field.isUnique() && !oldField.isUnique()) {
            back.add(SQLAction.of(new StringBuilder().append("ALTER TABLE ").append(this.withSchema(table.getName())).append(" ADD CONSTRAINT ").append(uniqueNameConverter.getName(table.getName(), field.getName())).append(" UNIQUE (").append(this.processID(field.getName())).append(")")));
        }
        if (!field.getName().equalsIgnoreCase(oldField.getName())) {
            str = new StringBuilder();
            str.append("ALTER TABLE ").append(this.withSchema(table.getName())).append(" RENAME COLUMN ");
            str.append(this.processID(oldField.getName())).append(" TO ").append(this.processID(field.getName()));
            back.add(SQLAction.of(str));
        }
        if (!field.getType().equals(oldField.getType())) {
            str = new StringBuilder();
            str.append("ALTER TABLE ").append(this.withSchema(table.getName())).append(" ALTER COLUMN ");
            str.append(this.processID(field.getName())).append(" TYPE ");
            boolean autoIncrement = field.isAutoIncrement();
            field.setAutoIncrement(false);
            str.append(this.renderFieldType(field));
            back.add(SQLAction.of(str));
            field.setAutoIncrement(autoIncrement);
        }
        if (field.getDefaultValue() != null || oldField.getDefaultValue() != null) {
            if (field.getDefaultValue() == null && oldField.getDefaultValue() != null) {
                str = new StringBuilder();
                str.append("ALTER TABLE ").append(this.withSchema(table.getName())).append(" ALTER COLUMN ");
                str.append(this.processID(field.getName())).append(" DROP DEFAULT");
                back.add(SQLAction.of(str));
            } else if (!field.getDefaultValue().equals(oldField.getDefaultValue())) {
                str = new StringBuilder();
                str.append("ALTER TABLE ").append(this.withSchema(table.getName())).append(" ALTER COLUMN ");
                str.append(this.processID(field.getName())).append(" SET DEFAULT ").append(this.renderValue(field.getDefaultValue()));
                back.add(SQLAction.of(str));
            }
        }
        if (field.isNotNull() != oldField.isNotNull()) {
            if (field.isNotNull()) {
                str = new StringBuilder();
                str.append("ALTER TABLE ").append(this.withSchema(table.getName())).append(" ALTER COLUMN ");
                str.append(this.processID(field.getName())).append(" SET NOT NULL");
                back.add(SQLAction.of(str));
            } else {
                str = new StringBuilder();
                str.append("ALTER TABLE ").append(this.withSchema(table.getName())).append(" ALTER COLUMN ");
                str.append(this.processID(field.getName())).append(" DROP NOT NULL");
                back.add(SQLAction.of(str));
            }
        }
        if (back.isEmpty()) {
            System.err.println("WARNING: Unable to modify column '" + table.getName() + "' in place. Going to drop and re-create column.");
            System.err.println("WARNING: Data contained in column '" + table.getName() + "." + oldField.getName() + "' will be lost");
            Iterables.addAll((Collection)back, this.renderAlterTableDropColumn(nameConverters, table, oldField));
            Iterables.addAll((Collection)back, this.renderAlterTableAddColumn(nameConverters, table, field));
        }
        return ImmutableList.builder().addAll(this.renderDropAccessoriesForField(nameConverters, table, oldField)).addAll((Iterable)back).addAll(this.renderAccessoriesForField(nameConverters, table, field)).build();
    }

    @Override
    protected SQLAction renderAlterTableDropKey(DDLForeignKey key) {
        StringBuilder back = new StringBuilder("ALTER TABLE ");
        back.append(this.withSchema(key.getDomesticTable())).append(" DROP CONSTRAINT ").append(this.processID(key.getFKName()));
        return SQLAction.of(back);
    }

    @Override
    protected SQLAction renderCreateIndex(IndexNameConverter indexNameConverter, DDLIndex index) {
        String statement = "CREATE INDEX " + this.processID(index.getIndexName()) + " ON " + this.withSchema(index.getTable()) + Stream.of(index.getFields()).map(DDLIndexField::getFieldName).map(this::processID).collect(Collectors.joining(",", "(", ")"));
        return SQLAction.of(statement);
    }

    @Override
    protected SQLAction renderDropIndex(IndexNameConverter indexNameConverter, DDLIndex index) {
        String indexName = index.getIndexName();
        String tableName = index.getTable();
        if (this.hasIndex(tableName, indexName)) {
            return SQLAction.of(new StringBuilder("DROP INDEX ").append(this.withSchema(indexName)));
        }
        return null;
    }

    @Override
    public <T extends RawEntity<K>, K> K insertReturningKey(EntityManager manager, Connection conn, Class<T> entityType, Class<K> pkType, String pkField, boolean pkIdentity, String table, DBParam ... params) throws SQLException {
        Object back = null;
        for (DBParam param : params) {
            if (!param.getField().trim().equalsIgnoreCase(pkField)) continue;
            back = param.getValue();
            break;
        }
        if (back == null) {
            String sql = "SELECT NEXTVAL('" + this.withSchema(this.sequenceName(pkField, table)) + "')";
            try (PreparedStatement stmt = this.preparedStatement(conn, sql);
                 ResultSet res = stmt.executeQuery();){
                if (res.next()) {
                    back = this.typeManager.getType(pkType).getLogicalType().pullFromDatabase(null, res, pkType, 1);
                }
            }
            ArrayList<DBParam> newParams = new ArrayList<DBParam>(Arrays.asList(params));
            newParams.add(new DBParam(pkField, back));
            params = newParams.toArray(new DBParam[newParams.size()]);
        }
        super.insertReturningKey(manager, conn, entityType, pkType, pkField, pkIdentity, table, params);
        return (K)back;
    }

    private String sequenceName(String pkField, String table) {
        String suffix = "_" + pkField + "_seq";
        int tableLength = table.length();
        int theoreticalLength = tableLength + suffix.length();
        if (theoreticalLength > 64) {
            int extraCharacters = theoreticalLength - 64;
            return table.substring(0, tableLength - extraCharacters - 1) + suffix;
        }
        return table + suffix;
    }

    @Override
    protected <T extends RawEntity<K>, K> K executeInsertReturningKey(EntityManager manager, Connection conn, Class<T> entityType, Class<K> pkType, String pkField, String sql, DBParam ... params) throws SQLException {
        PreparedStatement stmt = this.preparedStatement(conn, sql);
        for (int i = 0; i < params.length; ++i) {
            Object value = params[i].getValue();
            if (value instanceof RawEntity) {
                value = Common.getPrimaryKeyValue((RawEntity)value);
            }
            if (value == null) {
                this.putNull(stmt, i + 1);
                continue;
            }
            TypeInfo<?> type = this.typeManager.getType(value.getClass());
            type.getLogicalType().putToDatabase(manager, stmt, i + 1, value, type.getJdbcWriteType());
        }
        stmt.executeUpdate();
        stmt.close();
        return null;
    }

    @Override
    protected Set<String> getReservedWords() {
        return RESERVED_WORDS;
    }

    @Override
    protected boolean shouldQuoteID(String id) {
        return !PATTERN_QUOTE_ID.matcher(id).matches();
    }

    @Override
    protected boolean shouldQuoteTableName(String tableName) {
        return this.getReservedWords().contains(Case.UPPER.apply(tableName));
    }

    @Override
    public void handleUpdateError(String sql, SQLException e) throws SQLException {
        if (e.getSQLState().equals(SQL_STATE_UNDEFINED_FUNCTION) && e.getMessage().contains("does not exist")) {
            this.logger.debug("Ignoring SQL exception for <" + sql + ">", (Throwable)e);
            return;
        }
        super.handleUpdateError(sql, e);
    }
}

