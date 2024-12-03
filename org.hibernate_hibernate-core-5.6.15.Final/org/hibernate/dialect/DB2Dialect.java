/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import org.hibernate.JDBCException;
import org.hibernate.MappingException;
import org.hibernate.NullPrecedence;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.function.AvgWithArgumentCastFunction;
import org.hibernate.dialect.function.NoArgSQLFunction;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.dialect.function.VarArgsSQLFunction;
import org.hibernate.dialect.identity.DB2IdentityColumnSupport;
import org.hibernate.dialect.identity.IdentityColumnSupport;
import org.hibernate.dialect.pagination.AbstractLimitHandler;
import org.hibernate.dialect.pagination.LimitHandler;
import org.hibernate.dialect.pagination.LimitHelper;
import org.hibernate.dialect.unique.DB2UniqueDelegate;
import org.hibernate.dialect.unique.UniqueDelegate;
import org.hibernate.engine.spi.RowSelection;
import org.hibernate.exception.LockTimeoutException;
import org.hibernate.exception.spi.SQLExceptionConversionDelegate;
import org.hibernate.hql.spi.id.IdTableSupportStandardImpl;
import org.hibernate.hql.spi.id.MultiTableBulkIdStrategy;
import org.hibernate.hql.spi.id.local.AfterUseAction;
import org.hibernate.hql.spi.id.local.LocalTemporaryTableBulkIdStrategy;
import org.hibernate.internal.util.JdbcExceptionHelper;
import org.hibernate.tool.schema.extract.internal.SequenceInformationExtractorDB2DatabaseImpl;
import org.hibernate.tool.schema.extract.internal.SequenceInformationExtractorNoOpImpl;
import org.hibernate.tool.schema.extract.spi.SequenceInformationExtractor;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.descriptor.sql.DecimalTypeDescriptor;
import org.hibernate.type.descriptor.sql.SmallIntTypeDescriptor;
import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;

public class DB2Dialect
extends Dialect {
    private static final int BIND_PARAMETERS_NUMBER_LIMIT = Short.MAX_VALUE;
    private static final AbstractLimitHandler LIMIT_HANDLER = new AbstractLimitHandler(){

        @Override
        public String processSql(String sql, RowSelection selection) {
            if (LimitHelper.hasFirstRow(selection)) {
                return "select * from ( select inner2_.*, rownumber() over(order by order of inner2_) as rownumber_ from ( " + sql + " fetch first " + this.getMaxOrLimit(selection) + " rows only ) as inner2_ ) as inner1_ where rownumber_ > " + selection.getFirstRow() + " order by rownumber_";
            }
            return sql + " fetch first " + this.getMaxOrLimit(selection) + " rows only";
        }

        @Override
        public boolean supportsLimit() {
            return true;
        }

        @Override
        public boolean useMaxForLimit() {
            return true;
        }

        @Override
        public boolean supportsVariableLimit() {
            return false;
        }
    };
    private final UniqueDelegate uniqueDelegate;

    public DB2Dialect() {
        this.registerColumnType(-7, "smallint");
        this.registerColumnType(-5, "bigint");
        this.registerColumnType(5, "smallint");
        this.registerColumnType(-6, "smallint");
        this.registerColumnType(4, "integer");
        this.registerColumnType(1, "char(1)");
        this.registerColumnType(12, "varchar($l)");
        this.registerColumnType(6, "float");
        this.registerColumnType(8, "double");
        this.registerColumnType(91, "date");
        this.registerColumnType(92, "time");
        this.registerColumnType(93, "timestamp");
        this.registerColumnType(-3, "varchar($l) for bit data");
        this.registerColumnType(2, "decimal($p,$s)");
        this.registerColumnType(3, "decimal($p,$s)");
        this.registerColumnType(2004, "blob($l)");
        this.registerColumnType(2005, "clob($l)");
        this.registerColumnType(-1, "long varchar");
        this.registerColumnType(-4, "long varchar for bit data");
        this.registerColumnType(-2, "varchar($l) for bit data");
        this.registerColumnType(-2, 254L, "char($l) for bit data");
        this.registerColumnType(16, "smallint");
        this.registerFunction("avg", new AvgWithArgumentCastFunction("double"));
        this.registerFunction("abs", new StandardSQLFunction("abs"));
        this.registerFunction("absval", new StandardSQLFunction("absval"));
        this.registerFunction("sign", new StandardSQLFunction("sign", StandardBasicTypes.INTEGER));
        this.registerFunction("ceiling", new StandardSQLFunction("ceiling"));
        this.registerFunction("ceil", new StandardSQLFunction("ceil"));
        this.registerFunction("floor", new StandardSQLFunction("floor"));
        this.registerFunction("round", new StandardSQLFunction("round"));
        this.registerFunction("acos", new StandardSQLFunction("acos", StandardBasicTypes.DOUBLE));
        this.registerFunction("asin", new StandardSQLFunction("asin", StandardBasicTypes.DOUBLE));
        this.registerFunction("atan", new StandardSQLFunction("atan", StandardBasicTypes.DOUBLE));
        this.registerFunction("cos", new StandardSQLFunction("cos", StandardBasicTypes.DOUBLE));
        this.registerFunction("cot", new StandardSQLFunction("cot", StandardBasicTypes.DOUBLE));
        this.registerFunction("degrees", new StandardSQLFunction("degrees", StandardBasicTypes.DOUBLE));
        this.registerFunction("exp", new StandardSQLFunction("exp", StandardBasicTypes.DOUBLE));
        this.registerFunction("float", new StandardSQLFunction("float", StandardBasicTypes.DOUBLE));
        this.registerFunction("hex", new StandardSQLFunction("hex", StandardBasicTypes.STRING));
        this.registerFunction("ln", new StandardSQLFunction("ln", StandardBasicTypes.DOUBLE));
        this.registerFunction("log", new StandardSQLFunction("log", StandardBasicTypes.DOUBLE));
        this.registerFunction("log10", new StandardSQLFunction("log10", StandardBasicTypes.DOUBLE));
        this.registerFunction("radians", new StandardSQLFunction("radians", StandardBasicTypes.DOUBLE));
        this.registerFunction("rand", new NoArgSQLFunction("rand", StandardBasicTypes.DOUBLE));
        this.registerFunction("sin", new StandardSQLFunction("sin", StandardBasicTypes.DOUBLE));
        this.registerFunction("soundex", new StandardSQLFunction("soundex", StandardBasicTypes.STRING));
        this.registerFunction("sqrt", new StandardSQLFunction("sqrt", StandardBasicTypes.DOUBLE));
        this.registerFunction("stddev", new StandardSQLFunction("stddev", StandardBasicTypes.DOUBLE));
        this.registerFunction("tan", new StandardSQLFunction("tan", StandardBasicTypes.DOUBLE));
        this.registerFunction("variance", new StandardSQLFunction("variance", StandardBasicTypes.DOUBLE));
        this.registerFunction("julian_day", new StandardSQLFunction("julian_day", StandardBasicTypes.INTEGER));
        this.registerFunction("microsecond", new StandardSQLFunction("microsecond", StandardBasicTypes.INTEGER));
        this.registerFunction("midnight_seconds", new StandardSQLFunction("midnight_seconds", StandardBasicTypes.INTEGER));
        this.registerFunction("minute", new StandardSQLFunction("minute", StandardBasicTypes.INTEGER));
        this.registerFunction("month", new StandardSQLFunction("month", StandardBasicTypes.INTEGER));
        this.registerFunction("monthname", new StandardSQLFunction("monthname", StandardBasicTypes.STRING));
        this.registerFunction("quarter", new StandardSQLFunction("quarter", StandardBasicTypes.INTEGER));
        this.registerFunction("hour", new StandardSQLFunction("hour", StandardBasicTypes.INTEGER));
        this.registerFunction("second", new StandardSQLFunction("second", StandardBasicTypes.INTEGER));
        this.registerFunction("current_date", new NoArgSQLFunction("current date", StandardBasicTypes.DATE, false));
        this.registerFunction("date", new StandardSQLFunction("date", StandardBasicTypes.DATE));
        this.registerFunction("day", new StandardSQLFunction("day", StandardBasicTypes.INTEGER));
        this.registerFunction("dayname", new StandardSQLFunction("dayname", StandardBasicTypes.STRING));
        this.registerFunction("dayofweek", new StandardSQLFunction("dayofweek", StandardBasicTypes.INTEGER));
        this.registerFunction("dayofweek_iso", new StandardSQLFunction("dayofweek_iso", StandardBasicTypes.INTEGER));
        this.registerFunction("dayofyear", new StandardSQLFunction("dayofyear", StandardBasicTypes.INTEGER));
        this.registerFunction("days", new StandardSQLFunction("days", StandardBasicTypes.LONG));
        this.registerFunction("current_time", new NoArgSQLFunction("current time", StandardBasicTypes.TIME, false));
        this.registerFunction("time", new StandardSQLFunction("time", StandardBasicTypes.TIME));
        this.registerFunction("current_timestamp", new NoArgSQLFunction("current timestamp", StandardBasicTypes.TIMESTAMP, false));
        this.registerFunction("timestamp", new StandardSQLFunction("timestamp", StandardBasicTypes.TIMESTAMP));
        this.registerFunction("timestamp_iso", new StandardSQLFunction("timestamp_iso", StandardBasicTypes.TIMESTAMP));
        this.registerFunction("week", new StandardSQLFunction("week", StandardBasicTypes.INTEGER));
        this.registerFunction("week_iso", new StandardSQLFunction("week_iso", StandardBasicTypes.INTEGER));
        this.registerFunction("year", new StandardSQLFunction("year", StandardBasicTypes.INTEGER));
        this.registerFunction("double", new StandardSQLFunction("double", StandardBasicTypes.DOUBLE));
        this.registerFunction("varchar", new StandardSQLFunction("varchar", StandardBasicTypes.STRING));
        this.registerFunction("real", new StandardSQLFunction("real", StandardBasicTypes.FLOAT));
        this.registerFunction("bigint", new StandardSQLFunction("bigint", StandardBasicTypes.LONG));
        this.registerFunction("char", new StandardSQLFunction("char", StandardBasicTypes.CHARACTER));
        this.registerFunction("integer", new StandardSQLFunction("integer", StandardBasicTypes.INTEGER));
        this.registerFunction("smallint", new StandardSQLFunction("smallint", StandardBasicTypes.SHORT));
        this.registerFunction("digits", new StandardSQLFunction("digits", StandardBasicTypes.STRING));
        this.registerFunction("chr", new StandardSQLFunction("chr", StandardBasicTypes.CHARACTER));
        this.registerFunction("upper", new StandardSQLFunction("upper"));
        this.registerFunction("lower", new StandardSQLFunction("lower"));
        this.registerFunction("ucase", new StandardSQLFunction("ucase"));
        this.registerFunction("lcase", new StandardSQLFunction("lcase"));
        this.registerFunction("ltrim", new StandardSQLFunction("ltrim"));
        this.registerFunction("rtrim", new StandardSQLFunction("rtrim"));
        this.registerFunction("substr", new StandardSQLFunction("substr", StandardBasicTypes.STRING));
        this.registerFunction("posstr", new StandardSQLFunction("posstr", StandardBasicTypes.INTEGER));
        this.registerFunction("substring", new StandardSQLFunction("substr", StandardBasicTypes.STRING));
        this.registerFunction("bit_length", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "length(?1)*8"));
        this.registerFunction("trim", new SQLFunctionTemplate(StandardBasicTypes.STRING, "trim(?1 ?2 ?3 ?4)"));
        this.registerFunction("concat", new VarArgsSQLFunction(StandardBasicTypes.STRING, "", "||", ""));
        this.registerFunction("str", new SQLFunctionTemplate(StandardBasicTypes.STRING, "rtrim(char(?1))"));
        this.registerKeyword("current");
        this.registerKeyword("date");
        this.registerKeyword("time");
        this.registerKeyword("timestamp");
        this.registerKeyword("fetch");
        this.registerKeyword("first");
        this.registerKeyword("rows");
        this.registerKeyword("only");
        this.getDefaultProperties().setProperty("hibernate.jdbc.batch_size", "0");
        this.uniqueDelegate = new DB2UniqueDelegate(this);
    }

    @Override
    public String getLowercaseFunction() {
        return "lcase";
    }

    @Override
    public String getAddColumnString() {
        return "add column";
    }

    @Override
    public boolean dropConstraints() {
        return false;
    }

    @Override
    public String[] getDropSchemaCommand(String schemaName) {
        return new String[]{"drop schema " + schemaName + " restrict"};
    }

    @Override
    public String getSequenceNextValString(String sequenceName) {
        return "values nextval for " + sequenceName;
    }

    @Override
    public String getSelectSequenceNextValString(String sequenceName) throws MappingException {
        return "next value for " + sequenceName;
    }

    @Override
    public String getCreateSequenceString(String sequenceName) {
        return "create sequence " + sequenceName;
    }

    @Override
    public String getDropSequenceString(String sequenceName) {
        return "drop sequence " + sequenceName + " restrict";
    }

    @Override
    public boolean supportsSequences() {
        return true;
    }

    @Override
    public boolean supportsPooledSequences() {
        return true;
    }

    @Override
    public String getQuerySequencesString() {
        return "select * from syscat.sequences";
    }

    @Override
    public SequenceInformationExtractor getSequenceInformationExtractor() {
        if (this.getQuerySequencesString() == null) {
            return SequenceInformationExtractorNoOpImpl.INSTANCE;
        }
        return SequenceInformationExtractorDB2DatabaseImpl.INSTANCE;
    }

    @Override
    public boolean supportsLimit() {
        return true;
    }

    @Override
    public boolean supportsVariableLimit() {
        return false;
    }

    @Override
    public String getLimitString(String sql, int offset, int limit) {
        if (offset == 0) {
            return sql + " fetch first " + limit + " rows only";
        }
        return "select * from ( select inner2_.*, rownumber() over(order by order of inner2_) as rownumber_ from ( " + sql + " fetch first " + limit + " rows only ) as inner2_ ) as inner1_ where rownumber_ > " + offset + " order by rownumber_";
    }

    @Override
    public int convertToFirstRowValue(int zeroBasedFirstResult) {
        return zeroBasedFirstResult;
    }

    @Override
    public String getForUpdateString() {
        return " for read only with rs use and keep update locks";
    }

    @Override
    public boolean useMaxForLimit() {
        return true;
    }

    @Override
    public boolean supportsOuterJoinForUpdate() {
        return false;
    }

    @Override
    public boolean supportsExistsInSelect() {
        return false;
    }

    @Override
    public boolean supportsLockTimeouts() {
        return false;
    }

    @Override
    public String getSelectClauseNullString(int sqlType) {
        String literal;
        switch (sqlType) {
            case 1: 
            case 12: {
                literal = "'x'";
                break;
            }
            case 91: {
                literal = "'2000-1-1'";
                break;
            }
            case 93: {
                literal = "'2000-1-1 00:00:00'";
                break;
            }
            case 92: {
                literal = "'00:00:00'";
                break;
            }
            default: {
                literal = "0";
            }
        }
        return "nullif(" + literal + ", " + literal + ')';
    }

    @Override
    public boolean supportsUnionAll() {
        return true;
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
    public boolean supportsCommentOn() {
        return true;
    }

    @Override
    public MultiTableBulkIdStrategy getDefaultMultiTableBulkIdStrategy() {
        return new LocalTemporaryTableBulkIdStrategy(new IdTableSupportStandardImpl(){

            @Override
            public String generateIdTableName(String baseName) {
                return "session." + super.generateIdTableName(baseName);
            }

            @Override
            public String getCreateIdTableCommand() {
                return "declare global temporary table";
            }

            @Override
            public String getCreateIdTableStatementOptions() {
                return "not logged";
            }
        }, AfterUseAction.DROP, null);
    }

    @Override
    public boolean supportsCurrentTimestampSelection() {
        return true;
    }

    @Override
    public String getCurrentTimestampSelectString() {
        return "values current timestamp";
    }

    @Override
    public boolean isCurrentTimestampSelectStringCallable() {
        return false;
    }

    @Override
    public boolean supportsParametersInInsertSelect() {
        return true;
    }

    @Override
    public boolean requiresCastingOfParametersInSelectClause() {
        return true;
    }

    @Override
    public boolean supportsResultSetPositionQueryMethodsOnForwardOnlyCursor() {
        return false;
    }

    @Override
    public String getCrossJoinSeparator() {
        return ", ";
    }

    @Override
    public boolean supportsEmptyInList() {
        return false;
    }

    @Override
    public boolean supportsLobValueChangePropogation() {
        return false;
    }

    @Override
    public boolean doesReadCommittedCauseWritersToBlockReaders() {
        return true;
    }

    @Override
    public boolean supportsTupleDistinctCounts() {
        return false;
    }

    @Override
    protected SqlTypeDescriptor getSqlTypeDescriptorOverride(int sqlCode) {
        if (sqlCode == 16) {
            return SmallIntTypeDescriptor.INSTANCE;
        }
        if (sqlCode == 2) {
            return DecimalTypeDescriptor.INSTANCE;
        }
        return super.getSqlTypeDescriptorOverride(sqlCode);
    }

    @Override
    public SQLExceptionConversionDelegate buildSQLExceptionConversionDelegate() {
        return new SQLExceptionConversionDelegate(){

            @Override
            public JDBCException convert(SQLException sqlException, String message, String sql) {
                String sqlState = JdbcExceptionHelper.extractSqlState(sqlException);
                int errorCode = JdbcExceptionHelper.extractErrorCode(sqlException);
                if (-952 == errorCode && "57014".equals(sqlState)) {
                    throw new LockTimeoutException(message, sqlException, sql);
                }
                return null;
            }
        };
    }

    @Override
    public UniqueDelegate getUniqueDelegate() {
        return this.uniqueDelegate;
    }

    @Override
    public String getNotExpression(String expression) {
        return "not (" + expression + ")";
    }

    @Override
    public LimitHandler getLimitHandler() {
        return LIMIT_HANDLER;
    }

    @Override
    public String renderOrderByElement(String expression, String collation, String order, NullPrecedence nullPrecedence) {
        if (nullPrecedence == null || nullPrecedence == NullPrecedence.NONE) {
            return super.renderOrderByElement(expression, collation, order, NullPrecedence.NONE);
        }
        if (nullPrecedence == NullPrecedence.FIRST && "desc".equalsIgnoreCase(order) || nullPrecedence == NullPrecedence.LAST && "asc".equalsIgnoreCase(order)) {
            return super.renderOrderByElement(expression, collation, order, NullPrecedence.NONE);
        }
        return String.format(Locale.ENGLISH, "case when %s is null then %s else %s end, %s %s", expression, nullPrecedence == NullPrecedence.FIRST ? "0" : "1", nullPrecedence == NullPrecedence.FIRST ? "1" : "0", expression, order == null ? "asc" : order);
    }

    @Override
    public IdentityColumnSupport getIdentityColumnSupport() {
        return new DB2IdentityColumnSupport();
    }

    @Override
    public boolean supportsPartitionBy() {
        return true;
    }

    @Override
    public int getInExpressionCountLimit() {
        return Short.MAX_VALUE;
    }
}

