/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.hibernate.JDBCException;
import org.hibernate.QueryTimeoutException;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.OracleTypesHelper;
import org.hibernate.dialect.function.NoArgSQLFunction;
import org.hibernate.dialect.function.NvlFunction;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.dialect.function.VarArgsSQLFunction;
import org.hibernate.dialect.pagination.AbstractLimitHandler;
import org.hibernate.dialect.pagination.LimitHandler;
import org.hibernate.dialect.pagination.LimitHelper;
import org.hibernate.engine.spi.QueryParameters;
import org.hibernate.engine.spi.RowSelection;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.LockAcquisitionException;
import org.hibernate.exception.LockTimeoutException;
import org.hibernate.exception.spi.SQLExceptionConversionDelegate;
import org.hibernate.exception.spi.TemplatedViolatedConstraintNameExtracter;
import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;
import org.hibernate.hql.spi.id.IdTableSupportStandardImpl;
import org.hibernate.hql.spi.id.MultiTableBulkIdStrategy;
import org.hibernate.hql.spi.id.global.GlobalTemporaryTableBulkIdStrategy;
import org.hibernate.hql.spi.id.local.AfterUseAction;
import org.hibernate.internal.util.JdbcExceptionHelper;
import org.hibernate.procedure.internal.StandardCallableStatementSupport;
import org.hibernate.procedure.spi.CallableStatementSupport;
import org.hibernate.sql.CaseFragment;
import org.hibernate.sql.DecodeCaseFragment;
import org.hibernate.sql.JoinFragment;
import org.hibernate.sql.OracleJoinFragment;
import org.hibernate.tool.schema.extract.internal.SequenceInformationExtractorOracleDatabaseImpl;
import org.hibernate.tool.schema.extract.spi.SequenceInformationExtractor;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.descriptor.sql.BitTypeDescriptor;
import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;

public class Oracle8iDialect
extends Dialect {
    private static final Pattern DISTINCT_KEYWORD_PATTERN = Pattern.compile("\\bdistinct\\b");
    private static final Pattern GROUP_BY_KEYWORD_PATTERN = Pattern.compile("\\bgroup\\sby\\b");
    private static final Pattern ORDER_BY_KEYWORD_PATTERN = Pattern.compile("\\border\\sby\\b");
    private static final Pattern UNION_KEYWORD_PATTERN = Pattern.compile("\\bunion\\b");
    private static final Pattern SQL_STATEMENT_TYPE_PATTERN = Pattern.compile("^(?:\\/\\*.*?\\*\\/)?\\s*(select|insert|update|delete)\\s+.*?", 2);
    private static final AbstractLimitHandler LIMIT_HANDLER = new AbstractLimitHandler(){

        @Override
        public String processSql(String sql, RowSelection selection) {
            boolean hasOffset = LimitHelper.hasFirstRow(selection);
            sql = sql.trim();
            boolean isForUpdate = false;
            if (sql.toLowerCase(Locale.ROOT).endsWith(" for update")) {
                sql = sql.substring(0, sql.length() - 11);
                isForUpdate = true;
            }
            StringBuilder pagingSelect = new StringBuilder(sql.length() + 100);
            if (hasOffset) {
                pagingSelect.append("select * from ( select row_.*, rownum rownum_ from ( ");
            } else {
                pagingSelect.append("select * from ( ");
            }
            pagingSelect.append(sql);
            if (hasOffset) {
                pagingSelect.append(" ) row_ ) where rownum_ <= ? and rownum_ > ?");
            } else {
                pagingSelect.append(" ) where rownum <= ?");
            }
            if (isForUpdate) {
                pagingSelect.append(" for update");
            }
            return pagingSelect.toString();
        }

        @Override
        public boolean supportsLimit() {
            return true;
        }

        @Override
        public boolean bindLimitParametersInReverseOrder() {
            return true;
        }

        @Override
        public boolean useMaxForLimit() {
            return true;
        }
    };
    private static final int PARAM_LIST_SIZE_LIMIT = 1000;
    private static final ViolatedConstraintNameExtracter EXTRACTER = new TemplatedViolatedConstraintNameExtracter(){

        @Override
        protected String doExtractConstraintName(SQLException sqle) throws NumberFormatException {
            int errorCode = JdbcExceptionHelper.extractErrorCode(sqle);
            if (errorCode == 1 || errorCode == 2291 || errorCode == 2292) {
                return this.extractUsingTemplate("(", ")", sqle.getMessage());
            }
            if (errorCode == 1400) {
                return null;
            }
            return null;
        }
    };

    public Oracle8iDialect() {
        this.registerCharacterTypeMappings();
        this.registerNumericTypeMappings();
        this.registerDateTimeTypeMappings();
        this.registerLargeObjectTypeMappings();
        this.registerReverseHibernateTypeMappings();
        this.registerFunctions();
        this.registerDefaultProperties();
    }

    protected void registerCharacterTypeMappings() {
        this.registerColumnType(1, "char(1)");
        this.registerColumnType(12, 4000L, "varchar2($l)");
        this.registerColumnType(12, "long");
    }

    protected void registerNumericTypeMappings() {
        this.registerColumnType(-7, "number(1,0)");
        this.registerColumnType(-5, "number(19,0)");
        this.registerColumnType(5, "number(5,0)");
        this.registerColumnType(-6, "number(3,0)");
        this.registerColumnType(4, "number(10,0)");
        this.registerColumnType(6, "float");
        this.registerColumnType(8, "double precision");
        this.registerColumnType(2, "number($p,$s)");
        this.registerColumnType(3, "number($p,$s)");
        this.registerColumnType(16, "number(1,0)");
    }

    protected void registerDateTimeTypeMappings() {
        this.registerColumnType(91, "date");
        this.registerColumnType(92, "date");
        this.registerColumnType(93, "date");
    }

    protected void registerLargeObjectTypeMappings() {
        this.registerColumnType(-2, 2000L, "raw($l)");
        this.registerColumnType(-2, "long raw");
        this.registerColumnType(-3, 2000L, "raw($l)");
        this.registerColumnType(-3, "long raw");
        this.registerColumnType(2004, "blob");
        this.registerColumnType(2005, "clob");
        this.registerColumnType(-1, "long");
        this.registerColumnType(-4, "long raw");
    }

    protected void registerReverseHibernateTypeMappings() {
    }

    protected void registerFunctions() {
        this.registerFunction("abs", new StandardSQLFunction("abs"));
        this.registerFunction("sign", new StandardSQLFunction("sign", StandardBasicTypes.INTEGER));
        this.registerFunction("acos", new StandardSQLFunction("acos", StandardBasicTypes.DOUBLE));
        this.registerFunction("asin", new StandardSQLFunction("asin", StandardBasicTypes.DOUBLE));
        this.registerFunction("atan", new StandardSQLFunction("atan", StandardBasicTypes.DOUBLE));
        this.registerFunction("bitand", new StandardSQLFunction("bitand"));
        this.registerFunction("cos", new StandardSQLFunction("cos", StandardBasicTypes.DOUBLE));
        this.registerFunction("cosh", new StandardSQLFunction("cosh", StandardBasicTypes.DOUBLE));
        this.registerFunction("exp", new StandardSQLFunction("exp", StandardBasicTypes.DOUBLE));
        this.registerFunction("ln", new StandardSQLFunction("ln", StandardBasicTypes.DOUBLE));
        this.registerFunction("sin", new StandardSQLFunction("sin", StandardBasicTypes.DOUBLE));
        this.registerFunction("sinh", new StandardSQLFunction("sinh", StandardBasicTypes.DOUBLE));
        this.registerFunction("stddev", new StandardSQLFunction("stddev", StandardBasicTypes.DOUBLE));
        this.registerFunction("sqrt", new StandardSQLFunction("sqrt", StandardBasicTypes.DOUBLE));
        this.registerFunction("tan", new StandardSQLFunction("tan", StandardBasicTypes.DOUBLE));
        this.registerFunction("tanh", new StandardSQLFunction("tanh", StandardBasicTypes.DOUBLE));
        this.registerFunction("variance", new StandardSQLFunction("variance", StandardBasicTypes.DOUBLE));
        this.registerFunction("round", new StandardSQLFunction("round"));
        this.registerFunction("trunc", new StandardSQLFunction("trunc"));
        this.registerFunction("ceil", new StandardSQLFunction("ceil"));
        this.registerFunction("floor", new StandardSQLFunction("floor"));
        this.registerFunction("chr", new StandardSQLFunction("chr", StandardBasicTypes.CHARACTER));
        this.registerFunction("initcap", new StandardSQLFunction("initcap"));
        this.registerFunction("lower", new StandardSQLFunction("lower"));
        this.registerFunction("ltrim", new StandardSQLFunction("ltrim"));
        this.registerFunction("rtrim", new StandardSQLFunction("rtrim"));
        this.registerFunction("soundex", new StandardSQLFunction("soundex"));
        this.registerFunction("upper", new StandardSQLFunction("upper"));
        this.registerFunction("ascii", new StandardSQLFunction("ascii", StandardBasicTypes.INTEGER));
        this.registerFunction("to_char", new StandardSQLFunction("to_char", StandardBasicTypes.STRING));
        this.registerFunction("to_date", new StandardSQLFunction("to_date", StandardBasicTypes.TIMESTAMP));
        this.registerFunction("current_date", new NoArgSQLFunction("current_date", StandardBasicTypes.DATE, false));
        this.registerFunction("current_time", new NoArgSQLFunction("current_timestamp", StandardBasicTypes.TIME, false));
        this.registerFunction("current_timestamp", new NoArgSQLFunction("current_timestamp", StandardBasicTypes.TIMESTAMP, false));
        this.registerFunction("last_day", new StandardSQLFunction("last_day", StandardBasicTypes.DATE));
        this.registerFunction("sysdate", new NoArgSQLFunction("sysdate", StandardBasicTypes.DATE, false));
        this.registerFunction("systimestamp", new NoArgSQLFunction("systimestamp", StandardBasicTypes.TIMESTAMP, false));
        this.registerFunction("uid", new NoArgSQLFunction("uid", StandardBasicTypes.INTEGER, false));
        this.registerFunction("user", new NoArgSQLFunction("user", StandardBasicTypes.STRING, false));
        this.registerFunction("rowid", new NoArgSQLFunction("rowid", StandardBasicTypes.LONG, false));
        this.registerFunction("rownum", new NoArgSQLFunction("rownum", StandardBasicTypes.LONG, false));
        this.registerFunction("concat", new VarArgsSQLFunction(StandardBasicTypes.STRING, "", "||", ""));
        this.registerFunction("instr", new StandardSQLFunction("instr", StandardBasicTypes.INTEGER));
        this.registerFunction("instrb", new StandardSQLFunction("instrb", StandardBasicTypes.INTEGER));
        this.registerFunction("lpad", new StandardSQLFunction("lpad", StandardBasicTypes.STRING));
        this.registerFunction("replace", new StandardSQLFunction("replace", StandardBasicTypes.STRING));
        this.registerFunction("rpad", new StandardSQLFunction("rpad", StandardBasicTypes.STRING));
        this.registerFunction("substr", new StandardSQLFunction("substr", StandardBasicTypes.STRING));
        this.registerFunction("substrb", new StandardSQLFunction("substrb", StandardBasicTypes.STRING));
        this.registerFunction("translate", new StandardSQLFunction("translate", StandardBasicTypes.STRING));
        this.registerFunction("substring", new StandardSQLFunction("substr", StandardBasicTypes.STRING));
        this.registerFunction("locate", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "instr(?2,?1)"));
        this.registerFunction("bit_length", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "vsize(?1)*8"));
        this.registerFunction("coalesce", new NvlFunction());
        this.registerFunction("atan2", new StandardSQLFunction("atan2", StandardBasicTypes.FLOAT));
        this.registerFunction("log", new StandardSQLFunction("log", StandardBasicTypes.INTEGER));
        this.registerFunction("mod", new StandardSQLFunction("mod", StandardBasicTypes.INTEGER));
        this.registerFunction("nvl", new StandardSQLFunction("nvl"));
        this.registerFunction("nvl2", new StandardSQLFunction("nvl2"));
        this.registerFunction("power", new StandardSQLFunction("power", StandardBasicTypes.FLOAT));
        this.registerFunction("add_months", new StandardSQLFunction("add_months", StandardBasicTypes.DATE));
        this.registerFunction("months_between", new StandardSQLFunction("months_between", StandardBasicTypes.FLOAT));
        this.registerFunction("next_day", new StandardSQLFunction("next_day", StandardBasicTypes.DATE));
        this.registerFunction("str", new StandardSQLFunction("to_char", StandardBasicTypes.STRING));
    }

    protected void registerDefaultProperties() {
        this.getDefaultProperties().setProperty("hibernate.jdbc.use_streams_for_binary", "true");
        this.getDefaultProperties().setProperty("hibernate.jdbc.batch_size", "15");
        this.getDefaultProperties().setProperty("hibernate.jdbc.use_get_generated_keys", "false");
        this.getDefaultProperties().setProperty("hibernate.jdbc.batch_versioned_data", "false");
    }

    @Override
    protected SqlTypeDescriptor getSqlTypeDescriptorOverride(int sqlCode) {
        return sqlCode == 16 ? BitTypeDescriptor.INSTANCE : super.getSqlTypeDescriptorOverride(sqlCode);
    }

    @Override
    public JoinFragment createOuterJoinFragment() {
        return new OracleJoinFragment();
    }

    @Override
    public String getCrossJoinSeparator() {
        return ", ";
    }

    @Override
    public CaseFragment createCaseFragment() {
        return new DecodeCaseFragment();
    }

    @Override
    public LimitHandler getLimitHandler() {
        return LIMIT_HANDLER;
    }

    @Override
    public String getLimitString(String sql, boolean hasOffset) {
        sql = sql.trim();
        boolean isForUpdate = false;
        if (sql.toLowerCase(Locale.ROOT).endsWith(" for update")) {
            sql = sql.substring(0, sql.length() - 11);
            isForUpdate = true;
        }
        StringBuilder pagingSelect = new StringBuilder(sql.length() + 100);
        if (hasOffset) {
            pagingSelect.append("select * from ( select row_.*, rownum rownum_ from ( ");
        } else {
            pagingSelect.append("select * from ( ");
        }
        pagingSelect.append(sql);
        if (hasOffset) {
            pagingSelect.append(" ) row_ ) where rownum_ <= ? and rownum_ > ?");
        } else {
            pagingSelect.append(" ) where rownum <= ?");
        }
        if (isForUpdate) {
            pagingSelect.append(" for update");
        }
        return pagingSelect.toString();
    }

    public String getBasicSelectClauseNullString(int sqlType) {
        return super.getSelectClauseNullString(sqlType);
    }

    @Override
    public String getSelectClauseNullString(int sqlType) {
        switch (sqlType) {
            case 1: 
            case 12: {
                return "to_char(null)";
            }
            case 91: 
            case 92: 
            case 93: {
                return "to_date(null)";
            }
        }
        return "to_number(null)";
    }

    @Override
    public String getCurrentTimestampSelectString() {
        return "select sysdate from dual";
    }

    @Override
    public String getCurrentTimestampSQLFunctionName() {
        return "sysdate";
    }

    @Override
    public String getAddColumnString() {
        return "add";
    }

    @Override
    public String getSequenceNextValString(String sequenceName) {
        return "select " + this.getSelectSequenceNextValString(sequenceName) + " from dual";
    }

    @Override
    public String getSelectSequenceNextValString(String sequenceName) {
        return sequenceName + ".nextval";
    }

    @Override
    public String getCreateSequenceString(String sequenceName) {
        return "create sequence " + sequenceName;
    }

    @Override
    protected String getCreateSequenceString(String sequenceName, int initialValue, int incrementSize) {
        if (initialValue < 0 && incrementSize > 0) {
            return String.format("%s minvalue %d start with %d increment by %d", this.getCreateSequenceString(sequenceName), initialValue, initialValue, incrementSize);
        }
        if (initialValue > 0 && incrementSize < 0) {
            return String.format("%s maxvalue %d start with %d increment by %d", this.getCreateSequenceString(sequenceName), initialValue, initialValue, incrementSize);
        }
        return String.format("%s start with %d increment by  %d", this.getCreateSequenceString(sequenceName), initialValue, incrementSize);
    }

    @Override
    public String getDropSequenceString(String sequenceName) {
        return "drop sequence " + sequenceName;
    }

    @Override
    public String getCascadeConstraintsString() {
        return " cascade constraints";
    }

    @Override
    public boolean dropConstraints() {
        return false;
    }

    @Override
    public String getForUpdateNowaitString() {
        return " for update nowait";
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
    public boolean supportsLimit() {
        return true;
    }

    @Override
    public String getForUpdateString(String aliases) {
        return this.getForUpdateString() + " of " + aliases;
    }

    @Override
    public String getForUpdateNowaitString(String aliases) {
        return this.getForUpdateString() + " of " + aliases + " nowait";
    }

    @Override
    public boolean bindLimitParametersInReverseOrder() {
        return true;
    }

    @Override
    public boolean useMaxForLimit() {
        return true;
    }

    @Override
    public boolean forUpdateOfColumns() {
        return true;
    }

    @Override
    public String getQuerySequencesString() {
        return "select * from all_sequences";
    }

    @Override
    public SequenceInformationExtractor getSequenceInformationExtractor() {
        return SequenceInformationExtractorOracleDatabaseImpl.INSTANCE;
    }

    @Override
    public String getSelectGUIDString() {
        return "select rawtohex(sys_guid()) from dual";
    }

    @Override
    public ViolatedConstraintNameExtracter getViolatedConstraintNameExtracter() {
        return EXTRACTER;
    }

    @Override
    public SQLExceptionConversionDelegate buildSQLExceptionConversionDelegate() {
        return new SQLExceptionConversionDelegate(){

            @Override
            public JDBCException convert(SQLException sqlException, String message, String sql) {
                int errorCode = JdbcExceptionHelper.extractErrorCode(sqlException);
                if (errorCode == 30006) {
                    throw new LockTimeoutException(message, sqlException, sql);
                }
                if (errorCode == 54) {
                    throw new LockTimeoutException(message, sqlException, sql);
                }
                if (4021 == errorCode) {
                    throw new LockTimeoutException(message, sqlException, sql);
                }
                if (60 == errorCode) {
                    return new LockAcquisitionException(message, sqlException, sql);
                }
                if (4020 == errorCode) {
                    return new LockAcquisitionException(message, sqlException, sql);
                }
                if (1013 == errorCode) {
                    throw new QueryTimeoutException(message, sqlException, sql);
                }
                if (1407 == errorCode) {
                    String constraintName = Oracle8iDialect.this.getViolatedConstraintNameExtracter().extractConstraintName(sqlException);
                    return new ConstraintViolationException(message, sqlException, sql, constraintName);
                }
                return null;
            }
        };
    }

    @Override
    public int registerResultSetOutParameter(CallableStatement statement, int col) throws SQLException {
        statement.registerOutParameter(col, OracleTypesHelper.INSTANCE.getOracleCursorTypeSqlType());
        return ++col;
    }

    @Override
    public ResultSet getResultSet(CallableStatement ps) throws SQLException {
        ps.execute();
        return (ResultSet)ps.getObject(1);
    }

    @Override
    public boolean supportsUnionAll() {
        return true;
    }

    @Override
    public boolean supportsCommentOn() {
        return true;
    }

    @Override
    public MultiTableBulkIdStrategy getDefaultMultiTableBulkIdStrategy() {
        return new GlobalTemporaryTableBulkIdStrategy(new IdTableSupportStandardImpl(){

            @Override
            public String generateIdTableName(String baseName) {
                String name = super.generateIdTableName(baseName);
                return name.length() > 30 ? name.substring(0, 30) : name;
            }

            @Override
            public String getCreateIdTableCommand() {
                return "create global temporary table";
            }

            @Override
            public String getCreateIdTableStatementOptions() {
                return "on commit delete rows";
            }
        }, AfterUseAction.CLEAN);
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
    public boolean supportsEmptyInList() {
        return false;
    }

    @Override
    public boolean supportsExistsInSelect() {
        return false;
    }

    @Override
    public int getInExpressionCountLimit() {
        return 1000;
    }

    @Override
    public boolean forceLobAsLastValue() {
        return true;
    }

    @Override
    public boolean useFollowOnLocking(QueryParameters parameters) {
        if (parameters != null) {
            String lowerCaseSQL = parameters.getFilteredSQL().toLowerCase();
            return DISTINCT_KEYWORD_PATTERN.matcher(lowerCaseSQL).find() || GROUP_BY_KEYWORD_PATTERN.matcher(lowerCaseSQL).find() || UNION_KEYWORD_PATTERN.matcher(lowerCaseSQL).find() || parameters.hasRowSelection() && (ORDER_BY_KEYWORD_PATTERN.matcher(lowerCaseSQL).find() || parameters.getRowSelection().getFirstRow() != null);
        }
        return true;
    }

    @Override
    public String getNotExpression(String expression) {
        return "not (" + expression + ")";
    }

    @Override
    public String getQueryHintString(String sql, String hints) {
        String statementType = this.statementType(sql);
        int pos = sql.indexOf(statementType);
        if (pos > -1) {
            StringBuilder buffer = new StringBuilder(sql.length() + hints.length() + 8);
            if (pos > 0) {
                buffer.append(sql.substring(0, pos));
            }
            buffer.append(statementType).append(" /*+ ").append(hints).append(" */").append(sql.substring(pos + statementType.length()));
            sql = buffer.toString();
        }
        return sql;
    }

    @Override
    public int getMaxAliasLength() {
        return 20;
    }

    @Override
    public CallableStatementSupport getCallableStatementSupport() {
        return StandardCallableStatementSupport.REF_CURSOR_INSTANCE;
    }

    @Override
    public boolean canCreateSchema() {
        return false;
    }

    @Override
    public String getCurrentSchemaCommand() {
        return "select sys_context('USERENV', 'CURRENT_SCHEMA') from dual";
    }

    @Override
    public boolean supportsPartitionBy() {
        return true;
    }

    protected String statementType(String sql) {
        Matcher matcher = SQL_STATEMENT_TYPE_PATTERN.matcher(sql);
        if (matcher.matches() && matcher.groupCount() == 1) {
            return matcher.group(1);
        }
        throw new IllegalArgumentException("Can't determine SQL statement type for statement: " + sql);
    }

    @Override
    public boolean supportsNoWait() {
        return true;
    }
}

