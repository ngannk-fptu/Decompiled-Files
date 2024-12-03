/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect;

import java.sql.CallableStatement;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import org.hibernate.JDBCException;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.PessimisticLockException;
import org.hibernate.QueryTimeoutException;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.function.NoArgSQLFunction;
import org.hibernate.dialect.function.PositionSubstringFunction;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.dialect.function.VarArgsSQLFunction;
import org.hibernate.dialect.identity.IdentityColumnSupport;
import org.hibernate.dialect.identity.PostgreSQL81IdentityColumnSupport;
import org.hibernate.dialect.pagination.AbstractLimitHandler;
import org.hibernate.dialect.pagination.LimitHandler;
import org.hibernate.dialect.pagination.LimitHelper;
import org.hibernate.engine.jdbc.env.spi.IdentifierCaseStrategy;
import org.hibernate.engine.jdbc.env.spi.IdentifierHelper;
import org.hibernate.engine.jdbc.env.spi.IdentifierHelperBuilder;
import org.hibernate.engine.jdbc.env.spi.NameQualifierSupport;
import org.hibernate.engine.spi.RowSelection;
import org.hibernate.exception.LockAcquisitionException;
import org.hibernate.exception.spi.SQLExceptionConversionDelegate;
import org.hibernate.exception.spi.TemplatedViolatedConstraintNameExtracter;
import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;
import org.hibernate.hql.spi.id.IdTableSupportStandardImpl;
import org.hibernate.hql.spi.id.MultiTableBulkIdStrategy;
import org.hibernate.hql.spi.id.local.AfterUseAction;
import org.hibernate.hql.spi.id.local.LocalTemporaryTableBulkIdStrategy;
import org.hibernate.internal.util.JdbcExceptionHelper;
import org.hibernate.procedure.internal.PostgresCallableStatementSupport;
import org.hibernate.procedure.spi.CallableStatementSupport;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.descriptor.sql.BlobTypeDescriptor;
import org.hibernate.type.descriptor.sql.ClobTypeDescriptor;
import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;

public class PostgreSQL81Dialect
extends Dialect {
    private static final AbstractLimitHandler LIMIT_HANDLER = new AbstractLimitHandler(){

        @Override
        public String processSql(String sql, RowSelection selection) {
            boolean hasOffset = LimitHelper.hasFirstRow(selection);
            return sql + (hasOffset ? " limit ? offset ?" : " limit ?");
        }

        @Override
        public boolean supportsLimit() {
            return true;
        }

        @Override
        public boolean bindLimitParametersInReverseOrder() {
            return true;
        }
    };
    private static final ViolatedConstraintNameExtracter EXTRACTER = new TemplatedViolatedConstraintNameExtracter(){

        @Override
        protected String doExtractConstraintName(SQLException sqle) throws NumberFormatException {
            int sqlState = Integer.parseInt(JdbcExceptionHelper.extractSqlState(sqle));
            switch (sqlState) {
                case 23514: {
                    return this.extractUsingTemplate("violates check constraint \"", "\"", sqle.getMessage());
                }
                case 23505: {
                    return this.extractUsingTemplate("violates unique constraint \"", "\"", sqle.getMessage());
                }
                case 23503: {
                    return this.extractUsingTemplate("violates foreign key constraint \"", "\"", sqle.getMessage());
                }
                case 23502: {
                    return this.extractUsingTemplate("null value in column \"", "\" violates not-null constraint", sqle.getMessage());
                }
                case 23001: {
                    return null;
                }
            }
            return null;
        }
    };

    @Override
    public IdentifierHelper buildIdentifierHelper(IdentifierHelperBuilder builder, DatabaseMetaData dbMetaData) throws SQLException {
        if (dbMetaData == null) {
            builder.setUnquotedCaseStrategy(IdentifierCaseStrategy.LOWER);
            builder.setQuotedCaseStrategy(IdentifierCaseStrategy.MIXED);
        }
        return super.buildIdentifierHelper(builder, dbMetaData);
    }

    public PostgreSQL81Dialect() {
        this.registerColumnType(-7, "bool");
        this.registerColumnType(-5, "int8");
        this.registerColumnType(5, "int2");
        this.registerColumnType(-6, "int2");
        this.registerColumnType(4, "int4");
        this.registerColumnType(1, "char(1)");
        this.registerColumnType(12, "varchar($l)");
        this.registerColumnType(6, "float4");
        this.registerColumnType(8, "float8");
        this.registerColumnType(91, "date");
        this.registerColumnType(92, "time");
        this.registerColumnType(93, "timestamp");
        this.registerColumnType(-3, "bytea");
        this.registerColumnType(-2, "bytea");
        this.registerColumnType(-1, "text");
        this.registerColumnType(-4, "bytea");
        this.registerColumnType(2005, "oid");
        this.registerColumnType(2004, "oid");
        this.registerColumnType(2, "numeric($p, $s)");
        this.registerColumnType(1111, "uuid");
        this.registerFunction("abs", new StandardSQLFunction("abs"));
        this.registerFunction("sign", new StandardSQLFunction("sign", StandardBasicTypes.INTEGER));
        this.registerFunction("acos", new StandardSQLFunction("acos", StandardBasicTypes.DOUBLE));
        this.registerFunction("asin", new StandardSQLFunction("asin", StandardBasicTypes.DOUBLE));
        this.registerFunction("atan", new StandardSQLFunction("atan", StandardBasicTypes.DOUBLE));
        this.registerFunction("cos", new StandardSQLFunction("cos", StandardBasicTypes.DOUBLE));
        this.registerFunction("cot", new StandardSQLFunction("cot", StandardBasicTypes.DOUBLE));
        this.registerFunction("exp", new StandardSQLFunction("exp", StandardBasicTypes.DOUBLE));
        this.registerFunction("ln", new StandardSQLFunction("ln", StandardBasicTypes.DOUBLE));
        this.registerFunction("log", new StandardSQLFunction("log", StandardBasicTypes.DOUBLE));
        this.registerFunction("sin", new StandardSQLFunction("sin", StandardBasicTypes.DOUBLE));
        this.registerFunction("sqrt", new StandardSQLFunction("sqrt", StandardBasicTypes.DOUBLE));
        this.registerFunction("cbrt", new StandardSQLFunction("cbrt", StandardBasicTypes.DOUBLE));
        this.registerFunction("tan", new StandardSQLFunction("tan", StandardBasicTypes.DOUBLE));
        this.registerFunction("radians", new StandardSQLFunction("radians", StandardBasicTypes.DOUBLE));
        this.registerFunction("degrees", new StandardSQLFunction("degrees", StandardBasicTypes.DOUBLE));
        this.registerFunction("stddev", new StandardSQLFunction("stddev", StandardBasicTypes.DOUBLE));
        this.registerFunction("variance", new StandardSQLFunction("variance", StandardBasicTypes.DOUBLE));
        this.registerFunction("random", new NoArgSQLFunction("random", StandardBasicTypes.DOUBLE));
        this.registerFunction("rand", new NoArgSQLFunction("random", StandardBasicTypes.DOUBLE));
        this.registerFunction("round", new StandardSQLFunction("round"));
        this.registerFunction("trunc", new StandardSQLFunction("trunc"));
        this.registerFunction("ceil", new StandardSQLFunction("ceil"));
        this.registerFunction("floor", new StandardSQLFunction("floor"));
        this.registerFunction("chr", new StandardSQLFunction("chr", StandardBasicTypes.CHARACTER));
        this.registerFunction("lower", new StandardSQLFunction("lower"));
        this.registerFunction("upper", new StandardSQLFunction("upper"));
        this.registerFunction("substr", new StandardSQLFunction("substr", StandardBasicTypes.STRING));
        this.registerFunction("initcap", new StandardSQLFunction("initcap"));
        this.registerFunction("to_ascii", new StandardSQLFunction("to_ascii"));
        this.registerFunction("quote_ident", new StandardSQLFunction("quote_ident", StandardBasicTypes.STRING));
        this.registerFunction("quote_literal", new StandardSQLFunction("quote_literal", StandardBasicTypes.STRING));
        this.registerFunction("md5", new StandardSQLFunction("md5", StandardBasicTypes.STRING));
        this.registerFunction("ascii", new StandardSQLFunction("ascii", StandardBasicTypes.INTEGER));
        this.registerFunction("char_length", new StandardSQLFunction("char_length", StandardBasicTypes.LONG));
        this.registerFunction("bit_length", new StandardSQLFunction("bit_length", StandardBasicTypes.LONG));
        this.registerFunction("octet_length", new StandardSQLFunction("octet_length", StandardBasicTypes.LONG));
        this.registerFunction("age", new StandardSQLFunction("age"));
        this.registerFunction("current_date", new NoArgSQLFunction("current_date", StandardBasicTypes.DATE, false));
        this.registerFunction("current_time", new NoArgSQLFunction("current_time", StandardBasicTypes.TIME, false));
        this.registerFunction("current_timestamp", new NoArgSQLFunction("current_timestamp", StandardBasicTypes.TIMESTAMP, false));
        this.registerFunction("date_trunc", new StandardSQLFunction("date_trunc", StandardBasicTypes.TIMESTAMP));
        this.registerFunction("localtime", new NoArgSQLFunction("localtime", StandardBasicTypes.TIME, false));
        this.registerFunction("localtimestamp", new NoArgSQLFunction("localtimestamp", StandardBasicTypes.TIMESTAMP, false));
        this.registerFunction("now", new NoArgSQLFunction("now", StandardBasicTypes.TIMESTAMP));
        this.registerFunction("timeofday", new NoArgSQLFunction("timeofday", StandardBasicTypes.STRING));
        this.registerFunction("current_user", new NoArgSQLFunction("current_user", StandardBasicTypes.STRING, false));
        this.registerFunction("session_user", new NoArgSQLFunction("session_user", StandardBasicTypes.STRING, false));
        this.registerFunction("user", new NoArgSQLFunction("user", StandardBasicTypes.STRING, false));
        this.registerFunction("current_database", new NoArgSQLFunction("current_database", StandardBasicTypes.STRING, true));
        this.registerFunction("current_schema", new NoArgSQLFunction("current_schema", StandardBasicTypes.STRING, true));
        this.registerFunction("to_char", new StandardSQLFunction("to_char", StandardBasicTypes.STRING));
        this.registerFunction("to_date", new StandardSQLFunction("to_date", StandardBasicTypes.DATE));
        this.registerFunction("to_timestamp", new StandardSQLFunction("to_timestamp", StandardBasicTypes.TIMESTAMP));
        this.registerFunction("to_number", new StandardSQLFunction("to_number", StandardBasicTypes.BIG_DECIMAL));
        this.registerFunction("concat", new VarArgsSQLFunction(StandardBasicTypes.STRING, "(", "||", ")"));
        this.registerFunction("locate", new PositionSubstringFunction());
        this.registerFunction("str", new SQLFunctionTemplate(StandardBasicTypes.STRING, "cast(?1 as varchar)"));
        this.getDefaultProperties().setProperty("hibernate.jdbc.batch_size", "15");
        this.getDefaultProperties().setProperty("hibernate.jdbc.lob.non_contextual_creation", "true");
    }

    @Override
    public SqlTypeDescriptor getSqlTypeDescriptorOverride(int sqlCode) {
        SqlTypeDescriptor descriptor;
        switch (sqlCode) {
            case 2004: {
                descriptor = BlobTypeDescriptor.BLOB_BINDING;
                break;
            }
            case 2005: {
                descriptor = ClobTypeDescriptor.CLOB_BINDING;
                break;
            }
            default: {
                descriptor = super.getSqlTypeDescriptorOverride(sqlCode);
            }
        }
        return descriptor;
    }

    @Override
    public NameQualifierSupport getNameQualifierSupport() {
        return NameQualifierSupport.SCHEMA;
    }

    @Override
    public String getCurrentSchemaCommand() {
        return "select current_schema()";
    }

    @Override
    public String getAddColumnString() {
        return "add column";
    }

    @Override
    public String getSequenceNextValString(String sequenceName) {
        return "select " + this.getSelectSequenceNextValString(sequenceName);
    }

    @Override
    public String getSelectSequenceNextValString(String sequenceName) {
        return "nextval ('" + sequenceName + "')";
    }

    @Override
    public String getCreateSequenceString(String sequenceName) {
        return "create sequence " + sequenceName;
    }

    @Override
    public String getDropSequenceString(String sequenceName) {
        return "drop sequence " + sequenceName;
    }

    @Override
    public String getCascadeConstraintsString() {
        return " cascade";
    }

    @Override
    public boolean dropConstraints() {
        return true;
    }

    @Override
    public boolean supportsSequences() {
        return true;
    }

    @Override
    public String getQuerySequencesString() {
        return "select * from information_schema.sequences";
    }

    @Override
    public LimitHandler getLimitHandler() {
        return LIMIT_HANDLER;
    }

    @Override
    public boolean supportsLimit() {
        return true;
    }

    @Override
    public String getLimitString(String sql, boolean hasOffset) {
        return sql + (hasOffset ? " limit ? offset ?" : " limit ?");
    }

    @Override
    public boolean bindLimitParametersInReverseOrder() {
        return true;
    }

    @Override
    public String getForUpdateString(String aliases) {
        return this.getForUpdateString() + " of " + aliases;
    }

    @Override
    public String getForUpdateString(String aliases, LockOptions lockOptions) {
        LockMode lockMode;
        if (aliases != null && aliases.isEmpty()) {
            lockMode = lockOptions.getLockMode();
            Iterator<Map.Entry<String, LockMode>> itr = lockOptions.getAliasLockIterator();
            while (itr.hasNext()) {
                Map.Entry<String, LockMode> entry = itr.next();
                LockMode lm = entry.getValue();
                if (!lm.greaterThan(lockMode)) continue;
                aliases = entry.getKey();
            }
        }
        if ((lockMode = lockOptions.getAliasSpecificLockMode(aliases)) == null) {
            lockMode = lockOptions.getLockMode();
        }
        switch (lockMode) {
            case UPGRADE: {
                return this.getForUpdateString(aliases);
            }
            case PESSIMISTIC_READ: {
                return this.getReadLockString(aliases, lockOptions.getTimeOut());
            }
            case PESSIMISTIC_WRITE: {
                return this.getWriteLockString(aliases, lockOptions.getTimeOut());
            }
            case UPGRADE_NOWAIT: 
            case FORCE: 
            case PESSIMISTIC_FORCE_INCREMENT: {
                return this.getForUpdateNowaitString(aliases);
            }
            case UPGRADE_SKIPLOCKED: {
                return this.getForUpdateSkipLockedString(aliases);
            }
        }
        return "";
    }

    @Override
    public String getNoColumnsInsertString() {
        return "default values";
    }

    @Override
    public String getCaseInsensitiveLike() {
        return "ilike";
    }

    @Override
    public boolean supportsCaseInsensitiveLike() {
        return true;
    }

    @Override
    public String getNativeIdentifierGeneratorStrategy() {
        return "sequence";
    }

    @Override
    public boolean supportsOuterJoinForUpdate() {
        return false;
    }

    @Override
    public boolean useInputStreamToInsertBlob() {
        return false;
    }

    @Override
    public boolean supportsUnionAll() {
        return true;
    }

    @Override
    public String getSelectClauseNullString(int sqlType) {
        String typeName = this.getTypeName(sqlType, 1L, 1, 0);
        int loc = typeName.indexOf(40);
        if (loc > -1) {
            typeName = typeName.substring(0, loc);
        }
        return "null::" + typeName;
    }

    @Override
    public boolean supportsCommentOn() {
        return true;
    }

    @Override
    public MultiTableBulkIdStrategy getDefaultMultiTableBulkIdStrategy() {
        return new LocalTemporaryTableBulkIdStrategy(new IdTableSupportStandardImpl(){

            @Override
            public String getCreateIdTableCommand() {
                return "create temporary table";
            }

            @Override
            public String getCreateIdTableStatementOptions() {
                return "on commit drop";
            }
        }, AfterUseAction.CLEAN, null);
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
    public boolean requiresParensForTupleDistinctCounts() {
        return true;
    }

    @Override
    public String toBooleanValueString(boolean bool) {
        return bool ? "true" : "false";
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
                String sqlState = JdbcExceptionHelper.extractSqlState(sqlException);
                if ("40P01".equals(sqlState)) {
                    return new LockAcquisitionException(message, sqlException, sql);
                }
                if ("55P03".equals(sqlState)) {
                    return new PessimisticLockException(message, sqlException, sql);
                }
                if ("57014".equals(sqlState)) {
                    return new QueryTimeoutException(message, sqlException, sql);
                }
                return null;
            }
        };
    }

    @Override
    public int registerResultSetOutParameter(CallableStatement statement, int col) throws SQLException {
        statement.registerOutParameter(col++, 1111);
        return col;
    }

    @Override
    public ResultSet getResultSet(CallableStatement ps) throws SQLException {
        ps.execute();
        return (ResultSet)ps.getObject(1);
    }

    @Override
    public boolean supportsPooledSequences() {
        return true;
    }

    @Override
    protected String getCreateSequenceString(String sequenceName, int initialValue, int incrementSize) {
        if (initialValue < 0 && incrementSize > 0) {
            return String.format("%s minvalue %d start %d increment %d", this.getCreateSequenceString(sequenceName), initialValue, initialValue, incrementSize);
        }
        if (initialValue > 0 && incrementSize < 0) {
            return String.format("%s maxvalue %d start %d increment %d", this.getCreateSequenceString(sequenceName), initialValue, initialValue, incrementSize);
        }
        return String.format("%s start %d increment %d", this.getCreateSequenceString(sequenceName), initialValue, incrementSize);
    }

    @Override
    public boolean supportsEmptyInList() {
        return false;
    }

    @Override
    public boolean supportsExpectedLobUsagePattern() {
        return true;
    }

    @Override
    public boolean supportsLobValueChangePropogation() {
        return false;
    }

    @Override
    public boolean supportsUnboundedLobLocatorMaterialization() {
        return false;
    }

    @Override
    public String getForUpdateString() {
        return " for update";
    }

    @Override
    public String getWriteLockString(int timeout) {
        if (timeout == 0) {
            return " for update nowait";
        }
        return " for update";
    }

    @Override
    public String getWriteLockString(String aliases, int timeout) {
        if (timeout == 0) {
            return String.format(" for update of %s nowait", aliases);
        }
        return " for update of " + aliases;
    }

    @Override
    public String getReadLockString(int timeout) {
        if (timeout == 0) {
            return " for share nowait";
        }
        return " for share";
    }

    @Override
    public String getReadLockString(String aliases, int timeout) {
        if (timeout == 0) {
            return String.format(" for share of %s nowait", aliases);
        }
        return " for share of " + aliases;
    }

    @Override
    public boolean supportsRowValueConstructorSyntax() {
        return true;
    }

    @Override
    public String getForUpdateNowaitString() {
        return this.getForUpdateString() + " nowait ";
    }

    @Override
    public String getForUpdateNowaitString(String aliases) {
        return this.getForUpdateString(aliases) + " nowait ";
    }

    @Override
    public CallableStatementSupport getCallableStatementSupport() {
        return PostgresCallableStatementSupport.INSTANCE;
    }

    @Override
    public ResultSet getResultSet(CallableStatement statement, int position) throws SQLException {
        if (position != 1) {
            throw new UnsupportedOperationException("PostgreSQL only supports REF_CURSOR parameters as the first parameter");
        }
        return (ResultSet)statement.getObject(1);
    }

    @Override
    public ResultSet getResultSet(CallableStatement statement, String name) throws SQLException {
        throw new UnsupportedOperationException("PostgreSQL only supports accessing REF_CURSOR parameters by position");
    }

    @Override
    public boolean qualifyIndexName() {
        return false;
    }

    @Override
    public IdentityColumnSupport getIdentityColumnSupport() {
        return new PostgreSQL81IdentityColumnSupport();
    }

    @Override
    public boolean supportsNationalizedTypes() {
        return false;
    }

    @Override
    public boolean supportsNoWait() {
        return true;
    }

    @Override
    public boolean supportsJdbcConnectionLobCreation(DatabaseMetaData databaseMetaData) {
        return false;
    }

    @Override
    public boolean supportsSelectAliasInGroupByClause() {
        return true;
    }
}

