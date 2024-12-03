/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.dialect;

import java.sql.SQLException;
import java.util.List;
import org.hibernate.JDBCException;
import org.hibernate.PessimisticLockException;
import org.hibernate.boot.TempTableDdlTransactionHandling;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.function.AvgWithArgumentCastFunction;
import org.hibernate.dialect.function.NoArgSQLFunction;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.dialect.function.VarArgsSQLFunction;
import org.hibernate.dialect.hint.IndexQueryHintHandler;
import org.hibernate.dialect.identity.H2FinalTableIdentityColumnSupport;
import org.hibernate.dialect.identity.H2IdentityColumnSupport;
import org.hibernate.dialect.identity.IdentityColumnSupport;
import org.hibernate.dialect.pagination.AbstractLimitHandler;
import org.hibernate.dialect.pagination.LimitHandler;
import org.hibernate.dialect.pagination.LimitHelper;
import org.hibernate.engine.spi.RowSelection;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.LockAcquisitionException;
import org.hibernate.exception.spi.SQLExceptionConversionDelegate;
import org.hibernate.exception.spi.TemplatedViolatedConstraintNameExtracter;
import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;
import org.hibernate.hql.spi.id.IdTableSupportStandardImpl;
import org.hibernate.hql.spi.id.MultiTableBulkIdStrategy;
import org.hibernate.hql.spi.id.local.AfterUseAction;
import org.hibernate.hql.spi.id.local.LocalTemporaryTableBulkIdStrategy;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.JdbcExceptionHelper;
import org.hibernate.internal.util.ReflectHelper;
import org.hibernate.tool.schema.extract.internal.SequenceInformationExtractorH2DatabaseImpl;
import org.hibernate.tool.schema.extract.internal.SequenceInformationExtractorLegacyImpl;
import org.hibernate.tool.schema.extract.internal.SequenceInformationExtractorNoOpImpl;
import org.hibernate.tool.schema.extract.spi.SequenceInformationExtractor;
import org.hibernate.type.StandardBasicTypes;
import org.jboss.logging.Logger;

public class H2Dialect
extends Dialect {
    private static final CoreMessageLogger LOG = (CoreMessageLogger)Logger.getMessageLogger(CoreMessageLogger.class, (String)H2Dialect.class.getName());
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
    private final boolean supportsTuplesInSubqueries;
    private final boolean hasOddDstBehavior;
    private final boolean isVersion2;
    private final String querySequenceString;
    private final SequenceInformationExtractor sequenceInformationExtractor;
    private static final ViolatedConstraintNameExtracter EXTRACTER = new TemplatedViolatedConstraintNameExtracter(){

        @Override
        protected String doExtractConstraintName(SQLException sqle) throws NumberFormatException {
            String constraintName = null;
            if (sqle.getSQLState().startsWith("23")) {
                String message = sqle.getMessage();
                int idx = message.indexOf("violation: ");
                if (idx > 0) {
                    constraintName = message.substring(idx + "violation: ".length());
                }
                if (sqle.getSQLState().equals("23506")) {
                    constraintName = constraintName.substring(1, constraintName.indexOf(":"));
                }
            }
            return constraintName;
        }
    };

    public H2Dialect() {
        int buildId = Integer.MIN_VALUE;
        boolean supportsTuplesInSubqueries = false;
        boolean hasOddDstBehavior = false;
        boolean isVersion2 = false;
        try {
            Class h2ConstantsClass = ReflectHelper.classForName("org.h2.engine.Constants");
            int majorVersion = (Integer)h2ConstantsClass.getDeclaredField("VERSION_MAJOR").get(null);
            int minorVersion = (Integer)h2ConstantsClass.getDeclaredField("VERSION_MINOR").get(null);
            buildId = (Integer)h2ConstantsClass.getDeclaredField("BUILD_ID").get(null);
            if (majorVersion <= 1 && minorVersion <= 2 && buildId < 139) {
                LOG.unsupportedMultiTableBulkHqlJpaql(majorVersion, minorVersion, buildId);
            }
            supportsTuplesInSubqueries = majorVersion > 1 || minorVersion > 4 || buildId >= 198;
            hasOddDstBehavior = majorVersion > 1 || minorVersion > 4 || buildId >= 200;
            isVersion2 = majorVersion > 1;
        }
        catch (Exception e) {
            LOG.undeterminedH2Version();
        }
        if (buildId >= 32) {
            this.sequenceInformationExtractor = buildId >= 201 ? SequenceInformationExtractorLegacyImpl.INSTANCE : SequenceInformationExtractorH2DatabaseImpl.INSTANCE;
            this.querySequenceString = "select * from INFORMATION_SCHEMA.SEQUENCES";
        } else {
            this.sequenceInformationExtractor = SequenceInformationExtractorNoOpImpl.INSTANCE;
            this.querySequenceString = null;
        }
        this.supportsTuplesInSubqueries = supportsTuplesInSubqueries;
        this.hasOddDstBehavior = hasOddDstBehavior;
        this.isVersion2 = isVersion2;
        this.registerColumnType(16, "boolean");
        this.registerColumnType(-5, "bigint");
        this.registerColumnType(-2, "binary");
        this.registerColumnType(-7, "boolean");
        this.registerColumnType(1, "char($l)");
        this.registerColumnType(91, "date");
        this.registerColumnType(3, "decimal($p,$s)");
        this.registerColumnType(2, buildId >= 201 ? "numeric($p,$s)" : "decimal($p,$s)");
        this.registerColumnType(8, "double");
        this.registerColumnType(6, "float");
        this.registerColumnType(4, "integer");
        this.registerColumnType(-4, "longvarbinary");
        this.registerColumnType(-1, String.format("varchar(%d)", Integer.MAX_VALUE));
        this.registerColumnType(7, "real");
        this.registerColumnType(5, "smallint");
        this.registerColumnType(-6, "tinyint");
        this.registerColumnType(92, "time");
        this.registerColumnType(93, "timestamp");
        this.registerColumnType(12, "varchar($l)");
        this.registerColumnType(-3, buildId >= 201 ? "varbinary($l)" : "binary($l)");
        this.registerColumnType(2004, "blob");
        this.registerColumnType(2005, "clob");
        if (isVersion2) {
            this.registerColumnType(-1, "character varying");
            this.registerColumnType(-2, "binary($l)");
            this.registerFunction("str", new SQLFunctionTemplate(StandardBasicTypes.STRING, "cast(?1 as character varying)"));
        }
        this.registerFunction("avg", new AvgWithArgumentCastFunction("double"));
        this.registerFunction("acos", new StandardSQLFunction("acos", StandardBasicTypes.DOUBLE));
        this.registerFunction("asin", new StandardSQLFunction("asin", StandardBasicTypes.DOUBLE));
        this.registerFunction("atan", new StandardSQLFunction("atan", StandardBasicTypes.DOUBLE));
        this.registerFunction("atan2", new StandardSQLFunction("atan2", StandardBasicTypes.DOUBLE));
        this.registerFunction("bitand", new StandardSQLFunction("bitand", StandardBasicTypes.INTEGER));
        this.registerFunction("bitor", new StandardSQLFunction("bitor", StandardBasicTypes.INTEGER));
        this.registerFunction("bitxor", new StandardSQLFunction("bitxor", StandardBasicTypes.INTEGER));
        this.registerFunction("ceiling", new StandardSQLFunction("ceiling", StandardBasicTypes.DOUBLE));
        this.registerFunction("cos", new StandardSQLFunction("cos", StandardBasicTypes.DOUBLE));
        this.registerFunction("compress", new StandardSQLFunction("compress", StandardBasicTypes.BINARY));
        this.registerFunction("cot", new StandardSQLFunction("cot", StandardBasicTypes.DOUBLE));
        this.registerFunction("decrypt", new StandardSQLFunction("decrypt", StandardBasicTypes.BINARY));
        this.registerFunction("degrees", new StandardSQLFunction("degrees", StandardBasicTypes.DOUBLE));
        this.registerFunction("encrypt", new StandardSQLFunction("encrypt", StandardBasicTypes.BINARY));
        this.registerFunction("exp", new StandardSQLFunction("exp", StandardBasicTypes.DOUBLE));
        this.registerFunction("expand", new StandardSQLFunction("compress", StandardBasicTypes.BINARY));
        this.registerFunction("floor", new StandardSQLFunction("floor", StandardBasicTypes.DOUBLE));
        this.registerFunction("hash", new StandardSQLFunction("hash", StandardBasicTypes.BINARY));
        this.registerFunction("log", new StandardSQLFunction("log", StandardBasicTypes.DOUBLE));
        this.registerFunction("log10", new StandardSQLFunction("log10", StandardBasicTypes.DOUBLE));
        this.registerFunction("pi", new NoArgSQLFunction("pi", StandardBasicTypes.DOUBLE));
        this.registerFunction("power", new StandardSQLFunction("power", StandardBasicTypes.DOUBLE));
        this.registerFunction("radians", new StandardSQLFunction("radians", StandardBasicTypes.DOUBLE));
        this.registerFunction("rand", new NoArgSQLFunction("rand", StandardBasicTypes.DOUBLE));
        this.registerFunction("round", new StandardSQLFunction("round", StandardBasicTypes.DOUBLE));
        this.registerFunction("roundmagic", new StandardSQLFunction("roundmagic", StandardBasicTypes.DOUBLE));
        this.registerFunction("sign", new StandardSQLFunction("sign", StandardBasicTypes.INTEGER));
        this.registerFunction("sin", new StandardSQLFunction("sin", StandardBasicTypes.DOUBLE));
        this.registerFunction("tan", new StandardSQLFunction("tan", StandardBasicTypes.DOUBLE));
        this.registerFunction("truncate", new StandardSQLFunction("truncate", StandardBasicTypes.DOUBLE));
        this.registerFunction("ascii", new StandardSQLFunction("ascii", StandardBasicTypes.INTEGER));
        this.registerFunction("char", new StandardSQLFunction("char", StandardBasicTypes.CHARACTER));
        this.registerFunction("concat", new VarArgsSQLFunction(StandardBasicTypes.STRING, "(", "||", ")"));
        this.registerFunction("difference", new StandardSQLFunction("difference", StandardBasicTypes.INTEGER));
        this.registerFunction("hextoraw", new StandardSQLFunction("hextoraw", StandardBasicTypes.STRING));
        this.registerFunction("insert", new StandardSQLFunction("lower", StandardBasicTypes.STRING));
        this.registerFunction("left", new StandardSQLFunction("left", StandardBasicTypes.STRING));
        this.registerFunction("lcase", new StandardSQLFunction("lcase", StandardBasicTypes.STRING));
        this.registerFunction("ltrim", new StandardSQLFunction("ltrim", StandardBasicTypes.STRING));
        this.registerFunction("octet_length", new StandardSQLFunction("octet_length", StandardBasicTypes.INTEGER));
        this.registerFunction("position", new StandardSQLFunction("position", StandardBasicTypes.INTEGER));
        this.registerFunction("rawtohex", new StandardSQLFunction("rawtohex", StandardBasicTypes.STRING));
        this.registerFunction("repeat", new StandardSQLFunction("repeat", StandardBasicTypes.STRING));
        this.registerFunction("replace", new StandardSQLFunction("replace", StandardBasicTypes.STRING));
        this.registerFunction("right", new StandardSQLFunction("right", StandardBasicTypes.STRING));
        this.registerFunction("rtrim", new StandardSQLFunction("rtrim", StandardBasicTypes.STRING));
        this.registerFunction("soundex", new StandardSQLFunction("soundex", StandardBasicTypes.STRING));
        this.registerFunction("space", new StandardSQLFunction("space", StandardBasicTypes.STRING));
        this.registerFunction("stringencode", new StandardSQLFunction("stringencode", StandardBasicTypes.STRING));
        this.registerFunction("stringdecode", new StandardSQLFunction("stringdecode", StandardBasicTypes.STRING));
        this.registerFunction("stringtoutf8", new StandardSQLFunction("stringtoutf8", StandardBasicTypes.BINARY));
        this.registerFunction("ucase", new StandardSQLFunction("ucase", StandardBasicTypes.STRING));
        this.registerFunction("utf8tostring", new StandardSQLFunction("utf8tostring", StandardBasicTypes.STRING));
        this.registerFunction("curdate", new NoArgSQLFunction("curdate", StandardBasicTypes.DATE));
        this.registerFunction("curtime", new NoArgSQLFunction("curtime", StandardBasicTypes.TIME));
        this.registerFunction("curtimestamp", new NoArgSQLFunction("curtimestamp", StandardBasicTypes.TIME));
        this.registerFunction("current_date", new NoArgSQLFunction("current_date", StandardBasicTypes.DATE));
        this.registerFunction("current_time", new NoArgSQLFunction(buildId >= 200 ? "localtime" : "current_time", StandardBasicTypes.TIME));
        this.registerFunction("current_timestamp", new NoArgSQLFunction(buildId >= 200 ? "localtimestamp" : "current_timestamp", StandardBasicTypes.TIMESTAMP));
        this.registerFunction("datediff", new StandardSQLFunction("datediff", StandardBasicTypes.INTEGER));
        this.registerFunction("dayname", new StandardSQLFunction("dayname", StandardBasicTypes.STRING));
        this.registerFunction("dayofmonth", new StandardSQLFunction("dayofmonth", StandardBasicTypes.INTEGER));
        this.registerFunction("dayofweek", new StandardSQLFunction("dayofweek", StandardBasicTypes.INTEGER));
        this.registerFunction("dayofyear", new StandardSQLFunction("dayofyear", StandardBasicTypes.INTEGER));
        this.registerFunction("monthname", new StandardSQLFunction("monthname", StandardBasicTypes.STRING));
        this.registerFunction("now", new NoArgSQLFunction("now", StandardBasicTypes.TIMESTAMP));
        this.registerFunction("quarter", new StandardSQLFunction("quarter", StandardBasicTypes.INTEGER));
        this.registerFunction("week", new StandardSQLFunction("week", StandardBasicTypes.INTEGER));
        this.registerFunction("database", new NoArgSQLFunction("database", StandardBasicTypes.STRING));
        this.registerFunction("user", new NoArgSQLFunction("user", StandardBasicTypes.STRING));
        this.getDefaultProperties().setProperty("hibernate.jdbc.batch_size", "15");
        this.getDefaultProperties().setProperty("hibernate.jdbc.lob.non_contextual_creation", "true");
    }

    public boolean hasOddDstBehavior() {
        return this.hasOddDstBehavior;
    }

    public boolean isVersion2() {
        return this.isVersion2;
    }

    @Override
    public String getAddColumnString() {
        return "add column";
    }

    @Override
    public String getForUpdateString() {
        return " for update";
    }

    @Override
    public String toBooleanValueString(boolean bool) {
        return String.valueOf(bool);
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
    public boolean bindLimitParametersFirst() {
        return false;
    }

    @Override
    public boolean supportsIfExistsBeforeConstraintName() {
        return true;
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
    public String getCreateSequenceString(String sequenceName) {
        return "create sequence " + sequenceName;
    }

    @Override
    public String getDropSequenceString(String sequenceName) {
        return "drop sequence if exists " + sequenceName;
    }

    @Override
    public String getSelectSequenceNextValString(String sequenceName) {
        return "next value for " + sequenceName;
    }

    @Override
    public String getSequenceNextValString(String sequenceName) {
        return "call next value for " + sequenceName;
    }

    @Override
    public String getQuerySequencesString() {
        return this.querySequenceString;
    }

    @Override
    public SequenceInformationExtractor getSequenceInformationExtractor() {
        return this.sequenceInformationExtractor;
    }

    @Override
    public ViolatedConstraintNameExtracter getViolatedConstraintNameExtracter() {
        return EXTRACTER;
    }

    @Override
    public SQLExceptionConversionDelegate buildSQLExceptionConversionDelegate() {
        SQLExceptionConversionDelegate delegate = super.buildSQLExceptionConversionDelegate();
        if (delegate == null) {
            delegate = new SQLExceptionConversionDelegate(){

                @Override
                public JDBCException convert(SQLException sqlException, String message, String sql) {
                    int errorCode = JdbcExceptionHelper.extractErrorCode(sqlException);
                    if (40001 == errorCode) {
                        return new LockAcquisitionException(message, sqlException, sql);
                    }
                    if (50200 == errorCode) {
                        return new PessimisticLockException(message, sqlException, sql);
                    }
                    if (90006 == errorCode) {
                        String constraintName = H2Dialect.this.getViolatedConstraintNameExtracter().extractConstraintName(sqlException);
                        return new ConstraintViolationException(message, sqlException, sql, constraintName);
                    }
                    return null;
                }
            };
        }
        return delegate;
    }

    @Override
    public MultiTableBulkIdStrategy getDefaultMultiTableBulkIdStrategy() {
        return new LocalTemporaryTableBulkIdStrategy(new IdTableSupportStandardImpl(){

            @Override
            public String getCreateIdTableCommand() {
                return "create cached local temporary table if not exists";
            }

            @Override
            public String getCreateIdTableStatementOptions() {
                return "on commit drop transactional";
            }
        }, AfterUseAction.CLEAN, TempTableDdlTransactionHandling.NONE);
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
        return "call current_timestamp()";
    }

    @Override
    public boolean supportsUnionAll() {
        return true;
    }

    @Override
    public void augmentPhysicalTableTypes(List<String> tableTypesList) {
        if (this.isVersion2) {
            tableTypesList.add("BASE TABLE");
        }
    }

    @Override
    public boolean supportsLobValueChangePropogation() {
        return false;
    }

    @Override
    public boolean requiresParensForTupleDistinctCounts() {
        return true;
    }

    @Override
    public boolean doesReadCommittedCauseWritersToBlockReaders() {
        return true;
    }

    @Override
    public boolean supportsTuplesInSubqueries() {
        return this.supportsTuplesInSubqueries;
    }

    @Override
    public boolean dropConstraints() {
        return false;
    }

    @Override
    public String getCascadeConstraintsString() {
        return " CASCADE ";
    }

    @Override
    public boolean supportsIfExistsAfterTableName() {
        return false;
    }

    @Override
    public boolean supportsIfExistsBeforeTableName() {
        return true;
    }

    @Override
    public IdentityColumnSupport getIdentityColumnSupport() {
        return this.isVersion2 ? H2FinalTableIdentityColumnSupport.INSTANCE : H2IdentityColumnSupport.INSTANCE;
    }

    @Override
    public String getQueryHintString(String query, String hints) {
        return IndexQueryHintHandler.INSTANCE.addQueryHints(query, hints);
    }

    @Override
    public boolean supportsSelectAliasInGroupByClause() {
        return true;
    }
}

