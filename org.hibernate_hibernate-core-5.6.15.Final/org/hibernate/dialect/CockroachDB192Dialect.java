/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import org.hibernate.JDBCException;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.NullPrecedence;
import org.hibernate.PessimisticLockException;
import org.hibernate.boot.model.TypeContributions;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.function.NoArgSQLFunction;
import org.hibernate.dialect.function.PositionSubstringFunction;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.dialect.function.VarArgsSQLFunction;
import org.hibernate.dialect.identity.CockroachDB1920IdentityColumnSupport;
import org.hibernate.dialect.identity.IdentityColumnSupport;
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
import org.hibernate.hql.spi.id.MultiTableBulkIdStrategy;
import org.hibernate.hql.spi.id.inline.InlineIdsInClauseBulkIdStrategy;
import org.hibernate.internal.util.JdbcExceptionHelper;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.PostgresUUIDType;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
import org.hibernate.type.descriptor.sql.VarbinaryTypeDescriptor;
import org.hibernate.type.descriptor.sql.VarcharTypeDescriptor;

public class CockroachDB192Dialect
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
                    return this.extractUsingTemplate("failed to satisfy CHECK constraint ", "\"", sqle.getMessage());
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

    public CockroachDB192Dialect() {
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
        this.registerColumnType(2005, "text");
        this.registerColumnType(2004, "bytea");
        this.registerColumnType(2, "numeric($p, $s)");
        this.registerColumnType(1111, "uuid");
        this.registerColumnType(2000, "json");
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
        this.registerFunction("random", new NoArgSQLFunction("random", StandardBasicTypes.DOUBLE));
        this.registerFunction("round", new StandardSQLFunction("round"));
        this.registerFunction("trunc", new StandardSQLFunction("trunc"));
        this.registerFunction("ceil", new StandardSQLFunction("ceil"));
        this.registerFunction("floor", new StandardSQLFunction("floor"));
        this.registerFunction("chr", new StandardSQLFunction("chr", StandardBasicTypes.CHARACTER));
        this.registerFunction("lower", new StandardSQLFunction("lower"));
        this.registerFunction("upper", new StandardSQLFunction("upper"));
        this.registerFunction("substr", new StandardSQLFunction("substr", StandardBasicTypes.STRING));
        this.registerFunction("initcap", new StandardSQLFunction("initcap"));
        this.registerFunction("quote_ident", new StandardSQLFunction("quote_ident", StandardBasicTypes.STRING));
        this.registerFunction("quote_literal", new StandardSQLFunction("quote_literal", StandardBasicTypes.STRING));
        this.registerFunction("md5", new StandardSQLFunction("md5", StandardBasicTypes.STRING));
        this.registerFunction("ascii", new StandardSQLFunction("ascii", StandardBasicTypes.INTEGER));
        this.registerFunction("char_length", new StandardSQLFunction("char_length", StandardBasicTypes.LONG));
        this.registerFunction("bit_length", new StandardSQLFunction("bit_length", StandardBasicTypes.LONG));
        this.registerFunction("octet_length", new StandardSQLFunction("octet_length", StandardBasicTypes.LONG));
        this.registerFunction("age", new StandardSQLFunction("age"));
        this.registerFunction("current_date", new NoArgSQLFunction("current_date", StandardBasicTypes.DATE, false));
        this.registerFunction("current_timestamp", new NoArgSQLFunction("current_timestamp", StandardBasicTypes.TIMESTAMP, false));
        this.registerFunction("date_trunc", new StandardSQLFunction("date_trunc", StandardBasicTypes.TIMESTAMP));
        this.registerFunction("now", new NoArgSQLFunction("now", StandardBasicTypes.TIMESTAMP));
        this.registerFunction("current_user", new NoArgSQLFunction("current_user", StandardBasicTypes.STRING, false));
        this.registerFunction("session_user", new NoArgSQLFunction("session_user", StandardBasicTypes.STRING, false));
        this.registerFunction("user", new NoArgSQLFunction("user", StandardBasicTypes.STRING, false));
        this.registerFunction("current_database", new NoArgSQLFunction("current_database", StandardBasicTypes.STRING, true));
        this.registerFunction("current_schema", new NoArgSQLFunction("current_schema", StandardBasicTypes.STRING, true));
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
                descriptor = VarbinaryTypeDescriptor.INSTANCE;
                break;
            }
            case 2005: {
                descriptor = VarcharTypeDescriptor.INSTANCE;
                break;
            }
            default: {
                descriptor = super.getSqlTypeDescriptorOverride(sqlCode);
            }
        }
        return descriptor;
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
                return null;
            }
        };
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
        return false;
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
    public boolean supportsRowValueConstructorSyntax() {
        return true;
    }

    @Override
    public boolean qualifyIndexName() {
        return false;
    }

    @Override
    public boolean supportsNationalizedTypes() {
        return false;
    }

    @Override
    public boolean supportsJdbcConnectionLobCreation(DatabaseMetaData databaseMetaData) {
        return false;
    }

    @Override
    public boolean supportsSelectAliasInGroupByClause() {
        return true;
    }

    @Override
    public boolean supportsIfExistsBeforeTableName() {
        return true;
    }

    @Override
    public void contributeTypes(TypeContributions typeContributions, ServiceRegistry serviceRegistry) {
        super.contributeTypes(typeContributions, serviceRegistry);
        typeContributions.contributeType(PostgresUUIDType.INSTANCE);
    }

    @Override
    public String getDropSequenceString(String sequenceName) {
        return "drop sequence if exists " + sequenceName;
    }

    @Override
    public boolean supportsValuesList() {
        return true;
    }

    @Override
    public boolean supportsRowValueConstructorSyntaxInInList() {
        return true;
    }

    @Override
    public boolean supportsPartitionBy() {
        return true;
    }

    @Override
    public boolean supportsNonQueryWithCTE() {
        return true;
    }

    @Override
    public boolean supportsIfExistsAfterAlterTable() {
        return true;
    }

    @Override
    public String getForUpdateString() {
        return " for update";
    }

    @Override
    public String getWriteLockString(int timeout) {
        if (timeout == -2) {
            return this.getForUpdateSkipLockedString();
        }
        return super.getWriteLockString(timeout);
    }

    @Override
    public String getWriteLockString(String aliases, int timeout) {
        if (timeout == -2) {
            return this.getForUpdateSkipLockedString(aliases);
        }
        return super.getWriteLockString(aliases, timeout);
    }

    @Override
    public String getReadLockString(int timeout) {
        if (timeout == -2) {
            return " for share skip locked";
        }
        return super.getReadLockString(timeout);
    }

    @Override
    public String getReadLockString(String aliases, int timeout) {
        if (timeout == -2) {
            return String.format(" for share of %s skip locked", aliases);
        }
        return super.getReadLockString(aliases, timeout);
    }

    @Override
    public String getForUpdateSkipLockedString() {
        return " for update skip locked";
    }

    @Override
    public String getForUpdateSkipLockedString(String aliases) {
        return this.getForUpdateString() + " of " + aliases + " skip locked";
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
    public boolean supportsLockTimeouts() {
        return false;
    }

    @Override
    public boolean supportsSkipLocked() {
        return false;
    }

    @Override
    public boolean supportsNoWait() {
        return false;
    }

    @Override
    public IdentityColumnSupport getIdentityColumnSupport() {
        return new CockroachDB1920IdentityColumnSupport();
    }

    @Override
    public MultiTableBulkIdStrategy getDefaultMultiTableBulkIdStrategy() {
        return new InlineIdsInClauseBulkIdStrategy();
    }

    @Override
    public boolean doesReadCommittedCauseWritersToBlockReaders() {
        return true;
    }

    @Override
    public boolean canCreateSchema() {
        return false;
    }

    @Override
    public NameQualifierSupport getNameQualifierSupport() {
        return NameQualifierSupport.SCHEMA;
    }

    @Override
    public IdentifierHelper buildIdentifierHelper(IdentifierHelperBuilder builder, DatabaseMetaData dbMetaData) throws SQLException {
        if (dbMetaData == null) {
            builder.setUnquotedCaseStrategy(IdentifierCaseStrategy.LOWER);
            builder.setQuotedCaseStrategy(IdentifierCaseStrategy.MIXED);
        }
        return super.buildIdentifierHelper(builder, dbMetaData);
    }
}

