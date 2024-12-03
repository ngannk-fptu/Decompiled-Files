/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect;

import java.sql.CallableStatement;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.hibernate.JDBCException;
import org.hibernate.NullPrecedence;
import org.hibernate.PessimisticLockException;
import org.hibernate.boot.TempTableDdlTransactionHandling;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.InnoDBStorageEngine;
import org.hibernate.dialect.MyISAMStorageEngine;
import org.hibernate.dialect.MySQLStorageEngine;
import org.hibernate.dialect.function.NoArgSQLFunction;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.dialect.identity.IdentityColumnSupport;
import org.hibernate.dialect.identity.MySQLIdentityColumnSupport;
import org.hibernate.dialect.pagination.AbstractLimitHandler;
import org.hibernate.dialect.pagination.LimitHandler;
import org.hibernate.dialect.pagination.LimitHelper;
import org.hibernate.dialect.unique.MySQLUniqueDelegate;
import org.hibernate.dialect.unique.UniqueDelegate;
import org.hibernate.engine.jdbc.env.spi.IdentifierCaseStrategy;
import org.hibernate.engine.jdbc.env.spi.IdentifierHelper;
import org.hibernate.engine.jdbc.env.spi.IdentifierHelperBuilder;
import org.hibernate.engine.jdbc.env.spi.NameQualifierSupport;
import org.hibernate.engine.spi.RowSelection;
import org.hibernate.exception.LockAcquisitionException;
import org.hibernate.exception.LockTimeoutException;
import org.hibernate.exception.spi.SQLExceptionConversionDelegate;
import org.hibernate.hql.spi.id.IdTableSupportStandardImpl;
import org.hibernate.hql.spi.id.MultiTableBulkIdStrategy;
import org.hibernate.hql.spi.id.local.AfterUseAction;
import org.hibernate.hql.spi.id.local.LocalTemporaryTableBulkIdStrategy;
import org.hibernate.internal.util.JdbcExceptionHelper;
import org.hibernate.type.StandardBasicTypes;

public class MySQLDialect
extends Dialect {
    private static final Pattern ESCAPE_PATTERN = Pattern.compile("\\", 16);
    public static final String ESCAPE_PATTERN_REPLACEMENT = Matcher.quoteReplacement("\\\\");
    private final UniqueDelegate uniqueDelegate;
    private final MySQLStorageEngine storageEngine;
    private static final LimitHandler LIMIT_HANDLER = new AbstractLimitHandler(){

        @Override
        public String processSql(String sql, RowSelection selection) {
            boolean hasOffset = LimitHelper.hasFirstRow(selection);
            return sql + (hasOffset ? " limit ?, ?" : " limit ?");
        }

        @Override
        public boolean supportsLimit() {
            return true;
        }
    };

    public MySQLDialect() {
        String storageEngine = Environment.getProperties().getProperty("hibernate.dialect.storage_engine");
        if (storageEngine == null) {
            this.storageEngine = this.getDefaultMySQLStorageEngine();
        } else if ("innodb".equals(storageEngine.toLowerCase())) {
            this.storageEngine = InnoDBStorageEngine.INSTANCE;
        } else if ("myisam".equals(storageEngine.toLowerCase())) {
            this.storageEngine = MyISAMStorageEngine.INSTANCE;
        } else {
            throw new UnsupportedOperationException("The storage engine '" + storageEngine + "' is not supported!");
        }
        this.registerColumnType(-7, "bit");
        this.registerColumnType(-5, "bigint");
        this.registerColumnType(5, "smallint");
        this.registerColumnType(-6, "tinyint");
        this.registerColumnType(4, "integer");
        this.registerColumnType(1, "char(1)");
        this.registerColumnType(6, "float");
        this.registerColumnType(8, "double precision");
        this.registerColumnType(16, "bit");
        this.registerColumnType(91, "date");
        this.registerColumnType(92, "time");
        this.registerColumnType(93, "datetime");
        this.registerColumnType(-3, "longblob");
        this.registerColumnType(-3, 0xFFFFFFL, "mediumblob");
        this.registerColumnType(-3, 65535L, "blob");
        this.registerColumnType(-3, 255L, "tinyblob");
        this.registerColumnType(-2, "binary($l)");
        this.registerColumnType(-4, "longblob");
        this.registerColumnType(-4, 0xFFFFFFL, "mediumblob");
        this.registerColumnType(2, "decimal($p,$s)");
        this.registerColumnType(2004, "longblob");
        this.registerColumnType(2005, "longtext");
        this.registerColumnType(2011, "longtext");
        this.registerVarcharTypes();
        this.registerFunction("ascii", new StandardSQLFunction("ascii", StandardBasicTypes.INTEGER));
        this.registerFunction("bin", new StandardSQLFunction("bin", StandardBasicTypes.STRING));
        this.registerFunction("char_length", new StandardSQLFunction("char_length", StandardBasicTypes.LONG));
        this.registerFunction("character_length", new StandardSQLFunction("character_length", StandardBasicTypes.LONG));
        this.registerFunction("lcase", new StandardSQLFunction("lcase"));
        this.registerFunction("lower", new StandardSQLFunction("lower"));
        this.registerFunction("ltrim", new StandardSQLFunction("ltrim"));
        this.registerFunction("ord", new StandardSQLFunction("ord", StandardBasicTypes.INTEGER));
        this.registerFunction("quote", new StandardSQLFunction("quote"));
        this.registerFunction("reverse", new StandardSQLFunction("reverse"));
        this.registerFunction("rtrim", new StandardSQLFunction("rtrim"));
        this.registerFunction("soundex", new StandardSQLFunction("soundex"));
        this.registerFunction("space", new StandardSQLFunction("space", StandardBasicTypes.STRING));
        this.registerFunction("ucase", new StandardSQLFunction("ucase"));
        this.registerFunction("upper", new StandardSQLFunction("upper"));
        this.registerFunction("unhex", new StandardSQLFunction("unhex", StandardBasicTypes.STRING));
        this.registerFunction("abs", new StandardSQLFunction("abs"));
        this.registerFunction("sign", new StandardSQLFunction("sign", StandardBasicTypes.INTEGER));
        this.registerFunction("acos", new StandardSQLFunction("acos", StandardBasicTypes.DOUBLE));
        this.registerFunction("asin", new StandardSQLFunction("asin", StandardBasicTypes.DOUBLE));
        this.registerFunction("atan", new StandardSQLFunction("atan", StandardBasicTypes.DOUBLE));
        this.registerFunction("cos", new StandardSQLFunction("cos", StandardBasicTypes.DOUBLE));
        this.registerFunction("cot", new StandardSQLFunction("cot", StandardBasicTypes.DOUBLE));
        this.registerFunction("crc32", new StandardSQLFunction("crc32", StandardBasicTypes.LONG));
        this.registerFunction("exp", new StandardSQLFunction("exp", StandardBasicTypes.DOUBLE));
        this.registerFunction("ln", new StandardSQLFunction("ln", StandardBasicTypes.DOUBLE));
        this.registerFunction("log", new StandardSQLFunction("log", StandardBasicTypes.DOUBLE));
        this.registerFunction("log2", new StandardSQLFunction("log2", StandardBasicTypes.DOUBLE));
        this.registerFunction("log10", new StandardSQLFunction("log10", StandardBasicTypes.DOUBLE));
        this.registerFunction("pi", new NoArgSQLFunction("pi", StandardBasicTypes.DOUBLE));
        this.registerFunction("rand", new NoArgSQLFunction("rand", StandardBasicTypes.DOUBLE));
        this.registerFunction("sin", new StandardSQLFunction("sin", StandardBasicTypes.DOUBLE));
        this.registerFunction("sqrt", new StandardSQLFunction("sqrt", StandardBasicTypes.DOUBLE));
        this.registerFunction("stddev", new StandardSQLFunction("std", StandardBasicTypes.DOUBLE));
        this.registerFunction("tan", new StandardSQLFunction("tan", StandardBasicTypes.DOUBLE));
        this.registerFunction("radians", new StandardSQLFunction("radians", StandardBasicTypes.DOUBLE));
        this.registerFunction("degrees", new StandardSQLFunction("degrees", StandardBasicTypes.DOUBLE));
        this.registerFunction("ceiling", new StandardSQLFunction("ceiling", StandardBasicTypes.INTEGER));
        this.registerFunction("ceil", new StandardSQLFunction("ceil", StandardBasicTypes.INTEGER));
        this.registerFunction("floor", new StandardSQLFunction("floor", StandardBasicTypes.INTEGER));
        this.registerFunction("round", new StandardSQLFunction("round"));
        this.registerFunction("datediff", new StandardSQLFunction("datediff", StandardBasicTypes.INTEGER));
        this.registerFunction("timediff", new StandardSQLFunction("timediff", StandardBasicTypes.TIME));
        this.registerFunction("date_format", new StandardSQLFunction("date_format", StandardBasicTypes.STRING));
        this.registerFunction("curdate", new NoArgSQLFunction("curdate", StandardBasicTypes.DATE));
        this.registerFunction("curtime", new NoArgSQLFunction("curtime", StandardBasicTypes.TIME));
        this.registerFunction("current_date", new NoArgSQLFunction("current_date", StandardBasicTypes.DATE, false));
        this.registerFunction("current_time", new NoArgSQLFunction("current_time", StandardBasicTypes.TIME, false));
        this.registerFunction("current_timestamp", new NoArgSQLFunction("current_timestamp", StandardBasicTypes.TIMESTAMP, false));
        this.registerFunction("date", new StandardSQLFunction("date", StandardBasicTypes.DATE));
        this.registerFunction("day", new StandardSQLFunction("day", StandardBasicTypes.INTEGER));
        this.registerFunction("dayofmonth", new StandardSQLFunction("dayofmonth", StandardBasicTypes.INTEGER));
        this.registerFunction("dayname", new StandardSQLFunction("dayname", StandardBasicTypes.STRING));
        this.registerFunction("dayofweek", new StandardSQLFunction("dayofweek", StandardBasicTypes.INTEGER));
        this.registerFunction("dayofyear", new StandardSQLFunction("dayofyear", StandardBasicTypes.INTEGER));
        this.registerFunction("from_days", new StandardSQLFunction("from_days", StandardBasicTypes.DATE));
        this.registerFunction("from_unixtime", new StandardSQLFunction("from_unixtime", StandardBasicTypes.TIMESTAMP));
        this.registerFunction("hour", new StandardSQLFunction("hour", StandardBasicTypes.INTEGER));
        this.registerFunction("last_day", new StandardSQLFunction("last_day", StandardBasicTypes.DATE));
        this.registerFunction("localtime", new NoArgSQLFunction("localtime", StandardBasicTypes.TIMESTAMP));
        this.registerFunction("localtimestamp", new NoArgSQLFunction("localtimestamp", StandardBasicTypes.TIMESTAMP));
        this.registerFunction("microseconds", new StandardSQLFunction("microseconds", StandardBasicTypes.INTEGER));
        this.registerFunction("minute", new StandardSQLFunction("minute", StandardBasicTypes.INTEGER));
        this.registerFunction("month", new StandardSQLFunction("month", StandardBasicTypes.INTEGER));
        this.registerFunction("monthname", new StandardSQLFunction("monthname", StandardBasicTypes.STRING));
        this.registerFunction("now", new NoArgSQLFunction("now", StandardBasicTypes.TIMESTAMP));
        this.registerFunction("quarter", new StandardSQLFunction("quarter", StandardBasicTypes.INTEGER));
        this.registerFunction("second", new StandardSQLFunction("second", StandardBasicTypes.INTEGER));
        this.registerFunction("sec_to_time", new StandardSQLFunction("sec_to_time", StandardBasicTypes.TIME));
        this.registerFunction("sysdate", new NoArgSQLFunction("sysdate", StandardBasicTypes.TIMESTAMP));
        this.registerFunction("time", new StandardSQLFunction("time", StandardBasicTypes.STRING));
        this.registerFunction("timestamp", new StandardSQLFunction("timestamp", StandardBasicTypes.TIMESTAMP));
        this.registerFunction("time_to_sec", new StandardSQLFunction("time_to_sec", StandardBasicTypes.INTEGER));
        this.registerFunction("to_days", new StandardSQLFunction("to_days", StandardBasicTypes.LONG));
        this.registerFunction("unix_timestamp", new StandardSQLFunction("unix_timestamp", StandardBasicTypes.LONG));
        this.registerFunction("utc_date", new NoArgSQLFunction("utc_date", StandardBasicTypes.STRING));
        this.registerFunction("utc_time", new NoArgSQLFunction("utc_time", StandardBasicTypes.STRING));
        this.registerFunction("utc_timestamp", new NoArgSQLFunction("utc_timestamp", StandardBasicTypes.STRING));
        this.registerFunction("week", new StandardSQLFunction("week", StandardBasicTypes.INTEGER));
        this.registerFunction("weekday", new StandardSQLFunction("weekday", StandardBasicTypes.INTEGER));
        this.registerFunction("weekofyear", new StandardSQLFunction("weekofyear", StandardBasicTypes.INTEGER));
        this.registerFunction("year", new StandardSQLFunction("year", StandardBasicTypes.INTEGER));
        this.registerFunction("yearweek", new StandardSQLFunction("yearweek", StandardBasicTypes.INTEGER));
        this.registerFunction("hex", new StandardSQLFunction("hex", StandardBasicTypes.STRING));
        this.registerFunction("oct", new StandardSQLFunction("oct", StandardBasicTypes.STRING));
        this.registerFunction("octet_length", new StandardSQLFunction("octet_length", StandardBasicTypes.LONG));
        this.registerFunction("bit_length", new StandardSQLFunction("bit_length", StandardBasicTypes.LONG));
        this.registerFunction("bit_count", new StandardSQLFunction("bit_count", StandardBasicTypes.LONG));
        this.registerFunction("encrypt", new StandardSQLFunction("encrypt", StandardBasicTypes.STRING));
        this.registerFunction("md5", new StandardSQLFunction("md5", StandardBasicTypes.STRING));
        this.registerFunction("sha1", new StandardSQLFunction("sha1", StandardBasicTypes.STRING));
        this.registerFunction("sha", new StandardSQLFunction("sha", StandardBasicTypes.STRING));
        this.registerFunction("concat", new StandardSQLFunction("concat", StandardBasicTypes.STRING));
        this.getDefaultProperties().setProperty("hibernate.max_fetch_depth", "2");
        this.getDefaultProperties().setProperty("hibernate.jdbc.batch_size", "15");
        this.uniqueDelegate = new MySQLUniqueDelegate(this);
    }

    protected void registerVarcharTypes() {
        this.registerColumnType(12, "longtext");
        this.registerColumnType(12, 255L, "varchar($l)");
        this.registerColumnType(-1, "longtext");
    }

    @Override
    public String getAddColumnString() {
        return "add column";
    }

    @Override
    public boolean qualifyIndexName() {
        return false;
    }

    @Override
    public String getAddForeignKeyConstraintString(String constraintName, String[] foreignKey, String referencedTable, String[] primaryKey, boolean referencesPrimaryKey) {
        String cols = String.join((CharSequence)", ", foreignKey);
        String referencedCols = String.join((CharSequence)", ", primaryKey);
        return String.format(" add constraint %s foreign key (%s) references %s (%s)", constraintName, cols, referencedTable, referencedCols);
    }

    @Override
    public boolean supportsLimit() {
        return true;
    }

    @Override
    public String getDropForeignKeyString() {
        return " drop foreign key ";
    }

    @Override
    public LimitHandler getLimitHandler() {
        return LIMIT_HANDLER;
    }

    @Override
    public String getLimitString(String sql, boolean hasOffset) {
        return sql + (hasOffset ? " limit ?, ?" : " limit ?");
    }

    @Override
    public char closeQuote() {
        return '`';
    }

    @Override
    public char openQuote() {
        return '`';
    }

    @Override
    public boolean canCreateCatalog() {
        return true;
    }

    @Override
    public String[] getCreateCatalogCommand(String catalogName) {
        return new String[]{"create database " + catalogName};
    }

    @Override
    public String[] getDropCatalogCommand(String catalogName) {
        return new String[]{"drop database " + catalogName};
    }

    @Override
    public boolean canCreateSchema() {
        return false;
    }

    @Override
    public String[] getCreateSchemaCommand(String schemaName) {
        throw new UnsupportedOperationException("MySQL does not support dropping creating/dropping schemas in the JDBC sense");
    }

    @Override
    public String[] getDropSchemaCommand(String schemaName) {
        throw new UnsupportedOperationException("MySQL does not support dropping creating/dropping schemas in the JDBC sense");
    }

    @Override
    public boolean supportsIfExistsBeforeTableName() {
        return true;
    }

    @Override
    public String getSelectGUIDString() {
        return "select uuid()";
    }

    @Override
    public String getTableComment(String comment) {
        return " comment='" + comment + "'";
    }

    @Override
    public String getColumnComment(String comment) {
        return " comment '" + comment + "'";
    }

    @Override
    public MultiTableBulkIdStrategy getDefaultMultiTableBulkIdStrategy() {
        return new LocalTemporaryTableBulkIdStrategy(new IdTableSupportStandardImpl(){

            @Override
            public String getCreateIdTableCommand() {
                return "create temporary table if not exists";
            }

            @Override
            public String getDropIdTableCommand() {
                return "drop temporary table";
            }
        }, AfterUseAction.DROP, TempTableDdlTransactionHandling.NONE);
    }

    @Override
    public String getCastTypeName(int code) {
        switch (code) {
            case 16: {
                return "char";
            }
            case -5: 
            case 4: 
            case 5: {
                return this.smallIntegerCastTarget();
            }
            case 6: 
            case 7: {
                return this.floatingPointNumberCastTarget();
            }
            case 2: {
                return this.fixedPointNumberCastTarget();
            }
            case 12: {
                return "char";
            }
            case -3: {
                return "binary";
            }
        }
        return super.getCastTypeName(code);
    }

    protected String smallIntegerCastTarget() {
        return "signed";
    }

    protected String floatingPointNumberCastTarget() {
        return this.fixedPointNumberCastTarget();
    }

    protected String fixedPointNumberCastTarget() {
        return "decimal(19,2)";
    }

    @Override
    public boolean supportsCurrentTimestampSelection() {
        return true;
    }

    @Override
    public boolean isCurrentTimestampSelectStringCallable() {
        return false;
    }

    @Override
    public String getCurrentTimestampSelectString() {
        return "select now()";
    }

    @Override
    public int registerResultSetOutParameter(CallableStatement statement, int col) throws SQLException {
        return col;
    }

    @Override
    public ResultSet getResultSet(CallableStatement ps) throws SQLException {
        boolean isResultSet = ps.execute();
        while (!isResultSet && ps.getUpdateCount() != -1) {
            isResultSet = ps.getMoreResults();
        }
        return ps.getResultSet();
    }

    @Override
    public UniqueDelegate getUniqueDelegate() {
        return this.uniqueDelegate;
    }

    @Override
    public boolean supportsRowValueConstructorSyntax() {
        return true;
    }

    @Override
    public boolean supportsRowValueConstructorSyntaxInSet() {
        return false;
    }

    @Override
    public String renderOrderByElement(String expression, String collation, String order, NullPrecedence nulls) {
        StringBuilder orderByElement = new StringBuilder();
        if (nulls != NullPrecedence.NONE) {
            orderByElement.append("case when ").append(expression).append(" is null then ");
            if (nulls == NullPrecedence.FIRST) {
                orderByElement.append("0 else 1");
            } else {
                orderByElement.append("1 else 0");
            }
            orderByElement.append(" end, ");
        }
        orderByElement.append(super.renderOrderByElement(expression, collation, order, NullPrecedence.NONE));
        return orderByElement.toString();
    }

    @Override
    public String getForUpdateString() {
        return " for update";
    }

    @Override
    public String getWriteLockString(int timeout) {
        return " for update";
    }

    @Override
    public String getReadLockString(int timeout) {
        return " lock in share mode";
    }

    @Override
    public boolean supportsEmptyInList() {
        return false;
    }

    @Override
    public boolean areStringComparisonsCaseInsensitive() {
        return true;
    }

    @Override
    public boolean supportsLobValueChangePropogation() {
        return false;
    }

    @Override
    public boolean supportsSubqueryOnMutatingTable() {
        return false;
    }

    @Override
    public boolean supportsLockTimeouts() {
        return false;
    }

    @Override
    public SQLExceptionConversionDelegate buildSQLExceptionConversionDelegate() {
        return new SQLExceptionConversionDelegate(){

            @Override
            public JDBCException convert(SQLException sqlException, String message, String sql) {
                switch (sqlException.getErrorCode()) {
                    case 1205: 
                    case 3572: {
                        return new PessimisticLockException(message, sqlException, sql);
                    }
                    case 1206: 
                    case 1207: {
                        return new LockAcquisitionException(message, sqlException, sql);
                    }
                }
                String sqlState = JdbcExceptionHelper.extractSqlState(sqlException);
                if ("41000".equals(sqlState)) {
                    return new LockTimeoutException(message, sqlException, sql);
                }
                if ("40001".equals(sqlState)) {
                    return new LockAcquisitionException(message, sqlException, sql);
                }
                return null;
            }
        };
    }

    @Override
    public NameQualifierSupport getNameQualifierSupport() {
        return NameQualifierSupport.CATALOG;
    }

    @Override
    public IdentifierHelper buildIdentifierHelper(IdentifierHelperBuilder builder, DatabaseMetaData dbMetaData) throws SQLException {
        if (dbMetaData == null) {
            builder.setUnquotedCaseStrategy(IdentifierCaseStrategy.MIXED);
            builder.setQuotedCaseStrategy(IdentifierCaseStrategy.MIXED);
        }
        return super.buildIdentifierHelper(builder, dbMetaData);
    }

    @Override
    public String getNotExpression(String expression) {
        return "not (" + expression + ")";
    }

    @Override
    public IdentityColumnSupport getIdentityColumnSupport() {
        return new MySQLIdentityColumnSupport();
    }

    @Override
    public boolean isJdbcLogWarningsEnabledByDefault() {
        return false;
    }

    @Override
    public boolean supportsCascadeDelete() {
        return this.storageEngine.supportsCascadeDelete();
    }

    @Override
    public String getTableTypeString() {
        return this.storageEngine.getTableTypeString(this.getEngineKeyword());
    }

    protected String getEngineKeyword() {
        return "type";
    }

    @Override
    public boolean hasSelfReferentialForeignKeyBug() {
        return this.storageEngine.hasSelfReferentialForeignKeyBug();
    }

    @Override
    public boolean dropConstraints() {
        return this.storageEngine.dropConstraints();
    }

    protected MySQLStorageEngine getDefaultMySQLStorageEngine() {
        return MyISAMStorageEngine.INSTANCE;
    }

    @Override
    protected String escapeLiteral(String literal) {
        return ESCAPE_PATTERN.matcher(super.escapeLiteral(literal)).replaceAll(ESCAPE_PATTERN_REPLACEMENT);
    }

    @Override
    public boolean supportsSelectAliasInGroupByClause() {
        return true;
    }
}

