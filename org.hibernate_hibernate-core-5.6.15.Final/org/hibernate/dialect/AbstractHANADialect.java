/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.FilterReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Statement;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.hibernate.JDBCException;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.MappingException;
import org.hibernate.ScrollMode;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.model.TypeContributions;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.relational.SqlStringGenerationContext;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.function.AnsiTrimFunction;
import org.hibernate.dialect.function.NoArgSQLFunction;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.dialect.function.VarArgsSQLFunction;
import org.hibernate.dialect.identity.HANAIdentityColumnSupport;
import org.hibernate.dialect.identity.IdentityColumnSupport;
import org.hibernate.dialect.pagination.AbstractLimitHandler;
import org.hibernate.dialect.pagination.LimitHandler;
import org.hibernate.dialect.pagination.LimitHelper;
import org.hibernate.engine.config.spi.ConfigurationService;
import org.hibernate.engine.config.spi.StandardConverters;
import org.hibernate.engine.jdbc.BinaryStream;
import org.hibernate.engine.jdbc.BlobImplementer;
import org.hibernate.engine.jdbc.CharacterStream;
import org.hibernate.engine.jdbc.ClobImplementer;
import org.hibernate.engine.jdbc.NClobImplementer;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.engine.jdbc.env.spi.AnsiSqlKeywords;
import org.hibernate.engine.jdbc.env.spi.IdentifierCaseStrategy;
import org.hibernate.engine.jdbc.env.spi.IdentifierHelper;
import org.hibernate.engine.jdbc.env.spi.IdentifierHelperBuilder;
import org.hibernate.engine.jdbc.env.spi.NameQualifierSupport;
import org.hibernate.engine.spi.RowSelection;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.LockAcquisitionException;
import org.hibernate.exception.LockTimeoutException;
import org.hibernate.exception.SQLGrammarException;
import org.hibernate.exception.spi.SQLExceptionConversionDelegate;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.JdbcExceptionHelper;
import org.hibernate.mapping.Table;
import org.hibernate.procedure.internal.StandardCallableStatementSupport;
import org.hibernate.procedure.spi.CallableStatementSupport;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tool.schema.extract.internal.SequenceInformationExtractorHANADatabaseImpl;
import org.hibernate.tool.schema.extract.spi.SequenceInformationExtractor;
import org.hibernate.tool.schema.internal.StandardTableExporter;
import org.hibernate.tool.schema.spi.Exporter;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.descriptor.ValueBinder;
import org.hibernate.type.descriptor.ValueExtractor;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.DataHelper;
import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
import org.hibernate.type.descriptor.sql.BasicBinder;
import org.hibernate.type.descriptor.sql.BasicExtractor;
import org.hibernate.type.descriptor.sql.BitTypeDescriptor;
import org.hibernate.type.descriptor.sql.BlobTypeDescriptor;
import org.hibernate.type.descriptor.sql.BooleanTypeDescriptor;
import org.hibernate.type.descriptor.sql.CharTypeDescriptor;
import org.hibernate.type.descriptor.sql.ClobTypeDescriptor;
import org.hibernate.type.descriptor.sql.DecimalTypeDescriptor;
import org.hibernate.type.descriptor.sql.DoubleTypeDescriptor;
import org.hibernate.type.descriptor.sql.NCharTypeDescriptor;
import org.hibernate.type.descriptor.sql.NClobTypeDescriptor;
import org.hibernate.type.descriptor.sql.NVarcharTypeDescriptor;
import org.hibernate.type.descriptor.sql.SmallIntTypeDescriptor;
import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
import org.hibernate.type.descriptor.sql.VarcharTypeDescriptor;

public abstract class AbstractHANADialect
extends Dialect {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(AbstractHANADialect.class);
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
    private static final String MAX_LOB_PREFETCH_SIZE_PARAMETER_NAME = "hibernate.dialect.hana.max_lob_prefetch_size";
    private static final String USE_LEGACY_BOOLEAN_TYPE_PARAMETER_NAME = "hibernate.dialect.hana.use_legacy_boolean_type";
    private static final String USE_UNICODE_STRING_TYPES_PARAMETER_NAME = "hibernate.dialect.hana.use_unicode_string_types";
    private static final String TREAT_DOUBLE_TYPED_FIELDS_AS_DECIMAL_PARAMETER_NAME = "hibernate.dialect.hana.treat_double_typed_fields_as_decimal";
    private static final int MAX_LOB_PREFETCH_SIZE_DEFAULT_VALUE = 1024;
    private static final Boolean USE_LEGACY_BOOLEAN_TYPE_DEFAULT_VALUE = Boolean.FALSE;
    private static final Boolean TREAT_DOUBLE_TYPED_FIELDS_AS_DECIMAL_DEFAULT_VALUE = Boolean.FALSE;
    private HANANClobTypeDescriptor nClobTypeDescriptor = new HANANClobTypeDescriptor(1024);
    private HANABlobTypeDescriptor blobTypeDescriptor = new HANABlobTypeDescriptor(1024);
    private HANAClobTypeDescriptor clobTypeDescriptor;
    private boolean useLegacyBooleanType = USE_LEGACY_BOOLEAN_TYPE_DEFAULT_VALUE;
    private boolean useUnicodeStringTypes;
    private boolean treatDoubleTypedFieldsAsDecimal = TREAT_DOUBLE_TYPED_FIELDS_AS_DECIMAL_DEFAULT_VALUE;
    private final StandardTableExporter hanaTableExporter = new StandardTableExporter(this){

        @Override
        public String[] getSqlCreateStrings(Table table, Metadata metadata, SqlStringGenerationContext context) {
            String[] sqlCreateStrings = super.getSqlCreateStrings(table, metadata, context);
            return this.quoteTypeIfNecessary(table, sqlCreateStrings, AbstractHANADialect.this.getCreateTableString());
        }

        @Override
        public String[] getSqlDropStrings(Table table, Metadata metadata, SqlStringGenerationContext context) {
            String[] sqlDropStrings = super.getSqlDropStrings(table, metadata, context);
            return this.quoteTypeIfNecessary(table, sqlDropStrings, "drop table");
        }

        private String[] quoteTypeIfNecessary(Table table, String[] strings, String prefix) {
            if (table.getNameIdentifier() == null || table.getNameIdentifier().isQuoted() || !"type".equals(table.getNameIdentifier().getText().toLowerCase())) {
                return strings;
            }
            Pattern createTableTypePattern = Pattern.compile("(" + prefix + "\\s+)(" + table.getNameIdentifier().getText() + ")(.+)");
            Pattern commentOnTableTypePattern = Pattern.compile("(comment\\s+on\\s+table\\s+)(" + table.getNameIdentifier().getText() + ")(.+)");
            for (int i = 0; i < strings.length; ++i) {
                Matcher createTableTypeMatcher = createTableTypePattern.matcher(strings[i]);
                Matcher commentOnTableTypeMatcher = commentOnTableTypePattern.matcher(strings[i]);
                if (createTableTypeMatcher.matches()) {
                    strings[i] = createTableTypeMatcher.group(1) + "\"TYPE\"" + createTableTypeMatcher.group(3);
                }
                if (!commentOnTableTypeMatcher.matches()) continue;
                strings[i] = commentOnTableTypeMatcher.group(1) + "\"TYPE\"" + commentOnTableTypeMatcher.group(3);
            }
            return strings;
        }
    };

    public AbstractHANADialect() {
        this.useUnicodeStringTypes = this.useUnicodeStringTypesDefault();
        this.clobTypeDescriptor = new HANAClobTypeDescriptor(1024, this.useUnicodeStringTypesDefault());
        this.registerColumnType(3, "decimal($p, $s)");
        this.registerColumnType(2, "decimal($p, $s)");
        this.registerColumnType(8, "double");
        this.registerColumnType(-2, 5000L, "varbinary($l)");
        this.registerColumnType(-3, 5000L, "varbinary($l)");
        this.registerColumnType(-4, 5000L, "varbinary($l)");
        this.registerColumnType(-2, "blob");
        this.registerColumnType(-3, "blob");
        this.registerColumnType(-4, "blob");
        this.registerColumnType(1, "varchar(1)");
        this.registerColumnType(-15, "nvarchar(1)");
        this.registerColumnType(12, 5000L, "varchar($l)");
        this.registerColumnType(-1, 5000L, "varchar($l)");
        this.registerColumnType(-9, 5000L, "nvarchar($l)");
        this.registerColumnType(-16, 5000L, "nvarchar($l)");
        this.registerColumnType(-1, "clob");
        this.registerColumnType(12, "clob");
        this.registerColumnType(-16, "nclob");
        this.registerColumnType(-9, "nclob");
        this.registerColumnType(2005, "clob");
        this.registerColumnType(2011, "nclob");
        this.registerColumnType(16, "boolean");
        this.registerColumnType(-7, "smallint");
        this.registerColumnType(-6, "smallint");
        this.registerHibernateType(2011, StandardBasicTypes.MATERIALIZED_NCLOB.getName());
        this.registerHibernateType(2005, StandardBasicTypes.MATERIALIZED_CLOB.getName());
        this.registerHibernateType(2004, StandardBasicTypes.MATERIALIZED_BLOB.getName());
        this.registerHibernateType(-9, StandardBasicTypes.NSTRING.getName());
        this.registerFunction("to_date", new StandardSQLFunction("to_date", StandardBasicTypes.DATE));
        this.registerFunction("to_seconddate", new StandardSQLFunction("to_seconddate", StandardBasicTypes.TIMESTAMP));
        this.registerFunction("to_time", new StandardSQLFunction("to_time", StandardBasicTypes.TIME));
        this.registerFunction("to_timestamp", new StandardSQLFunction("to_timestamp", StandardBasicTypes.TIMESTAMP));
        this.registerFunction("current_date", new NoArgSQLFunction("current_date", StandardBasicTypes.DATE, false));
        this.registerFunction("current_time", new NoArgSQLFunction("current_time", StandardBasicTypes.TIME, false));
        this.registerFunction("current_timestamp", new NoArgSQLFunction("current_timestamp", StandardBasicTypes.TIMESTAMP, false));
        this.registerFunction("current_utcdate", new NoArgSQLFunction("current_utcdate", StandardBasicTypes.DATE, false));
        this.registerFunction("current_utctime", new NoArgSQLFunction("current_utctime", StandardBasicTypes.TIME, false));
        this.registerFunction("current_utctimestamp", new NoArgSQLFunction("current_utctimestamp", StandardBasicTypes.TIMESTAMP, false));
        this.registerFunction("add_days", new StandardSQLFunction("add_days"));
        this.registerFunction("add_months", new StandardSQLFunction("add_months"));
        this.registerFunction("add_seconds", new StandardSQLFunction("add_seconds"));
        this.registerFunction("add_years", new StandardSQLFunction("add_years"));
        this.registerFunction("dayname", new StandardSQLFunction("dayname", StandardBasicTypes.STRING));
        this.registerFunction("dayofmonth", new StandardSQLFunction("dayofmonth", StandardBasicTypes.INTEGER));
        this.registerFunction("dayofyear", new StandardSQLFunction("dayofyear", StandardBasicTypes.INTEGER));
        this.registerFunction("days_between", new StandardSQLFunction("days_between", StandardBasicTypes.INTEGER));
        this.registerFunction("hour", new StandardSQLFunction("hour", StandardBasicTypes.INTEGER));
        this.registerFunction("isoweek", new StandardSQLFunction("isoweek", StandardBasicTypes.STRING));
        this.registerFunction("last_day", new StandardSQLFunction("last_day", StandardBasicTypes.DATE));
        this.registerFunction("localtoutc", new StandardSQLFunction("localtoutc", StandardBasicTypes.TIMESTAMP));
        this.registerFunction("minute", new StandardSQLFunction("minute", StandardBasicTypes.INTEGER));
        this.registerFunction("month", new StandardSQLFunction("month", StandardBasicTypes.INTEGER));
        this.registerFunction("monthname", new StandardSQLFunction("monthname", StandardBasicTypes.STRING));
        this.registerFunction("next_day", new StandardSQLFunction("next_day", StandardBasicTypes.DATE));
        this.registerFunction("now", new NoArgSQLFunction("now", StandardBasicTypes.TIMESTAMP, true));
        this.registerFunction("quarter", new StandardSQLFunction("quarter", StandardBasicTypes.STRING));
        this.registerFunction("second", new StandardSQLFunction("second", StandardBasicTypes.INTEGER));
        this.registerFunction("seconds_between", new StandardSQLFunction("seconds_between", StandardBasicTypes.LONG));
        this.registerFunction("week", new StandardSQLFunction("week", StandardBasicTypes.INTEGER));
        this.registerFunction("weekday", new StandardSQLFunction("weekday", StandardBasicTypes.INTEGER));
        this.registerFunction("year", new StandardSQLFunction("year", StandardBasicTypes.INTEGER));
        this.registerFunction("utctolocal", new StandardSQLFunction("utctolocal", StandardBasicTypes.TIMESTAMP));
        this.registerFunction("to_bigint", new StandardSQLFunction("to_bigint", StandardBasicTypes.LONG));
        this.registerFunction("to_binary", new StandardSQLFunction("to_binary", StandardBasicTypes.BINARY));
        this.registerFunction("to_decimal", new StandardSQLFunction("to_decimal", StandardBasicTypes.BIG_DECIMAL));
        this.registerFunction("to_double", new StandardSQLFunction("to_double", StandardBasicTypes.DOUBLE));
        this.registerFunction("to_int", new StandardSQLFunction("to_int", StandardBasicTypes.INTEGER));
        this.registerFunction("to_integer", new StandardSQLFunction("to_integer", StandardBasicTypes.INTEGER));
        this.registerFunction("to_real", new StandardSQLFunction("to_real", StandardBasicTypes.FLOAT));
        this.registerFunction("to_smalldecimal", new StandardSQLFunction("to_smalldecimal", StandardBasicTypes.BIG_DECIMAL));
        this.registerFunction("to_smallint", new StandardSQLFunction("to_smallint", StandardBasicTypes.SHORT));
        this.registerFunction("to_tinyint", new StandardSQLFunction("to_tinyint", StandardBasicTypes.BYTE));
        this.registerFunction("abs", new StandardSQLFunction("abs"));
        this.registerFunction("acos", new StandardSQLFunction("acos", StandardBasicTypes.DOUBLE));
        this.registerFunction("asin", new StandardSQLFunction("asin", StandardBasicTypes.DOUBLE));
        this.registerFunction("atan2", new StandardSQLFunction("atan", StandardBasicTypes.DOUBLE));
        this.registerFunction("bin2hex", new StandardSQLFunction("bin2hex", StandardBasicTypes.STRING));
        this.registerFunction("bitand", new StandardSQLFunction("bitand", StandardBasicTypes.LONG));
        this.registerFunction("ceil", new StandardSQLFunction("ceil"));
        this.registerFunction("cos", new StandardSQLFunction("cos", StandardBasicTypes.DOUBLE));
        this.registerFunction("cosh", new StandardSQLFunction("cosh", StandardBasicTypes.DOUBLE));
        this.registerFunction("cot", new StandardSQLFunction("cos", StandardBasicTypes.DOUBLE));
        this.registerFunction("exp", new StandardSQLFunction("exp", StandardBasicTypes.DOUBLE));
        this.registerFunction("floor", new StandardSQLFunction("floor"));
        this.registerFunction("greatest", new StandardSQLFunction("greatest"));
        this.registerFunction("hex2bin", new StandardSQLFunction("hex2bin", StandardBasicTypes.BINARY));
        this.registerFunction("least", new StandardSQLFunction("least"));
        this.registerFunction("ln", new StandardSQLFunction("ln", StandardBasicTypes.DOUBLE));
        this.registerFunction("log", new StandardSQLFunction("ln", StandardBasicTypes.DOUBLE));
        this.registerFunction("power", new StandardSQLFunction("power"));
        this.registerFunction("round", new StandardSQLFunction("round"));
        this.registerFunction("mod", new StandardSQLFunction("mod", StandardBasicTypes.INTEGER));
        this.registerFunction("sign", new StandardSQLFunction("sign", StandardBasicTypes.INTEGER));
        this.registerFunction("sin", new StandardSQLFunction("sin", StandardBasicTypes.DOUBLE));
        this.registerFunction("sinh", new StandardSQLFunction("sinh", StandardBasicTypes.DOUBLE));
        this.registerFunction("sqrt", new StandardSQLFunction("sqrt", StandardBasicTypes.DOUBLE));
        this.registerFunction("tan", new StandardSQLFunction("tan", StandardBasicTypes.DOUBLE));
        this.registerFunction("tanh", new StandardSQLFunction("tanh", StandardBasicTypes.DOUBLE));
        this.registerFunction("uminus", new StandardSQLFunction("uminus"));
        this.registerFunction("to_alphanum", new StandardSQLFunction("to_alphanum", StandardBasicTypes.STRING));
        this.registerFunction("to_nvarchar", new StandardSQLFunction("to_nvarchar", StandardBasicTypes.STRING));
        this.registerFunction("to_varchar", new StandardSQLFunction("to_varchar", StandardBasicTypes.STRING));
        this.registerFunction("ascii", new StandardSQLFunction("ascii", StandardBasicTypes.INTEGER));
        this.registerFunction("char", new StandardSQLFunction("char", StandardBasicTypes.CHARACTER));
        this.registerFunction("concat", new VarArgsSQLFunction(StandardBasicTypes.STRING, "(", "||", ")"));
        this.registerFunction("lcase", new StandardSQLFunction("lcase", StandardBasicTypes.STRING));
        this.registerFunction("left", new StandardSQLFunction("left", StandardBasicTypes.STRING));
        this.registerFunction("length", new StandardSQLFunction("length", StandardBasicTypes.INTEGER));
        this.registerFunction("locate", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "locate(?2, ?1, ?3)"));
        this.registerFunction("lpad", new StandardSQLFunction("lpad", StandardBasicTypes.STRING));
        this.registerFunction("ltrim", new StandardSQLFunction("ltrim", StandardBasicTypes.STRING));
        this.registerFunction("nchar", new StandardSQLFunction("nchar", StandardBasicTypes.STRING));
        this.registerFunction("replace", new StandardSQLFunction("replace", StandardBasicTypes.STRING));
        this.registerFunction("right", new StandardSQLFunction("right", StandardBasicTypes.STRING));
        this.registerFunction("rpad", new StandardSQLFunction("rpad", StandardBasicTypes.STRING));
        this.registerFunction("rtrim", new StandardSQLFunction("rtrim", StandardBasicTypes.STRING));
        this.registerFunction("substr_after", new StandardSQLFunction("substr_after", StandardBasicTypes.STRING));
        this.registerFunction("substr_before", new StandardSQLFunction("substr_before", StandardBasicTypes.STRING));
        this.registerFunction("substring", new StandardSQLFunction("substring", StandardBasicTypes.STRING));
        this.registerFunction("trim", new AnsiTrimFunction());
        this.registerFunction("ucase", new StandardSQLFunction("ucase", StandardBasicTypes.STRING));
        this.registerFunction("unicode", new StandardSQLFunction("unicode", StandardBasicTypes.INTEGER));
        this.registerFunction("bit_length", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "length(to_binary(?1))*8"));
        this.registerFunction("to_blob", new StandardSQLFunction("to_blob", StandardBasicTypes.BLOB));
        this.registerFunction("to_clob", new StandardSQLFunction("to_clob", StandardBasicTypes.CLOB));
        this.registerFunction("to_nclob", new StandardSQLFunction("to_nclob", StandardBasicTypes.NCLOB));
        this.registerFunction("coalesce", new StandardSQLFunction("coalesce"));
        this.registerFunction("current_connection", new NoArgSQLFunction("current_connection", StandardBasicTypes.INTEGER, false));
        this.registerFunction("current_schema", new NoArgSQLFunction("current_schema", StandardBasicTypes.STRING, false));
        this.registerFunction("current_user", new NoArgSQLFunction("current_user", StandardBasicTypes.STRING, false));
        this.registerFunction("grouping_id", new VarArgsSQLFunction(StandardBasicTypes.INTEGER, "(", ",", ")"));
        this.registerFunction("ifnull", new StandardSQLFunction("ifnull"));
        this.registerFunction("map", new StandardSQLFunction("map"));
        this.registerFunction("nullif", new StandardSQLFunction("nullif"));
        this.registerFunction("session_context", new StandardSQLFunction("session_context"));
        this.registerFunction("session_user", new NoArgSQLFunction("session_user", StandardBasicTypes.STRING, false));
        this.registerFunction("sysuuid", new NoArgSQLFunction("sysuuid", StandardBasicTypes.STRING, false));
        this.registerHanaKeywords();
        this.getDefaultProperties().setProperty("hibernate.jdbc.lob.non_contextual_creation", "true");
        this.getDefaultProperties().setProperty("hibernate.jdbc.use_get_generated_keys", "false");
    }

    @Override
    public boolean bindLimitParametersInReverseOrder() {
        return true;
    }

    @Override
    public SQLExceptionConversionDelegate buildSQLExceptionConversionDelegate() {
        return new SQLExceptionConversionDelegate(){

            @Override
            public JDBCException convert(SQLException sqlException, String message, String sql) {
                int errorCode = JdbcExceptionHelper.extractErrorCode(sqlException);
                if (errorCode == 131) {
                    return new LockTimeoutException(message, sqlException, sql);
                }
                if (errorCode == 146) {
                    return new LockTimeoutException(message, sqlException, sql);
                }
                if (errorCode == 132) {
                    return new LockAcquisitionException(message, sqlException, sql);
                }
                if (errorCode == 133) {
                    return new LockAcquisitionException(message, sqlException, sql);
                }
                if (errorCode == 257 || errorCode >= 259 && errorCode <= 263) {
                    throw new SQLGrammarException(message, sqlException, sql);
                }
                if (errorCode == 287 || errorCode == 301 || errorCode == 461 || errorCode == 462) {
                    String constraintName = AbstractHANADialect.this.getViolatedConstraintNameExtracter().extractConstraintName(sqlException);
                    return new ConstraintViolationException(message, sqlException, sql, constraintName);
                }
                return null;
            }
        };
    }

    @Override
    public boolean forUpdateOfColumns() {
        return true;
    }

    @Override
    public String getAddColumnString() {
        return "add (";
    }

    @Override
    public String getAddColumnSuffixString() {
        return ")";
    }

    @Override
    public String getCascadeConstraintsString() {
        return " cascade";
    }

    @Override
    public String getCreateSequenceString(String sequenceName) {
        return "create sequence " + sequenceName;
    }

    @Override
    protected String getCreateSequenceString(String sequenceName, int initialValue, int incrementSize) throws MappingException {
        if (incrementSize == 0) {
            throw new MappingException("Unable to create the sequence [" + sequenceName + "]: the increment size must not be 0");
        }
        String createSequenceString = this.getCreateSequenceString(sequenceName) + " start with " + initialValue + " increment by " + incrementSize;
        if (incrementSize > 0) {
            if (initialValue < 1) {
                createSequenceString = createSequenceString + " minvalue " + initialValue;
            }
        } else if (initialValue > -1) {
            createSequenceString = createSequenceString + " maxvalue " + initialValue;
        }
        return createSequenceString;
    }

    @Override
    public String getCurrentTimestampSelectString() {
        return "select current_timestamp from sys.dummy";
    }

    @Override
    public String getDropSequenceString(String sequenceName) {
        return "drop sequence " + sequenceName;
    }

    @Override
    public String getForUpdateString(String aliases) {
        return this.getForUpdateString() + " of " + aliases;
    }

    @Override
    public String getForUpdateString(String aliases, LockOptions lockOptions) {
        LockMode lockMode = lockOptions.findGreatestLockMode();
        lockOptions.setLockMode(lockMode);
        if (aliases == null || aliases.isEmpty()) {
            return this.getForUpdateString(lockOptions);
        }
        return this.getForUpdateString(aliases, lockMode, lockOptions.getTimeOut());
    }

    private String getForUpdateString(String aliases, LockMode lockMode, int timeout) {
        switch (lockMode) {
            case UPGRADE: {
                return this.getForUpdateString(aliases);
            }
            case PESSIMISTIC_READ: {
                return this.getReadLockString(aliases, timeout);
            }
            case PESSIMISTIC_WRITE: {
                return this.getWriteLockString(aliases, timeout);
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
    public String getForUpdateNowaitString() {
        return this.getForUpdateString() + " nowait";
    }

    @Override
    public String getLimitString(String sql, boolean hasOffset) {
        return new StringBuilder(sql.length() + 20).append(sql).append(hasOffset ? " limit ? offset ?" : " limit ?").toString();
    }

    @Override
    public String getNotExpression(String expression) {
        return "not (" + expression + ")";
    }

    @Override
    public String getQuerySequencesString() {
        return "select * from sys.sequences";
    }

    @Override
    public SequenceInformationExtractor getSequenceInformationExtractor() {
        return SequenceInformationExtractorHANADatabaseImpl.INSTANCE;
    }

    @Override
    public String getSelectSequenceNextValString(String sequenceName) {
        return sequenceName + ".nextval";
    }

    @Override
    public String getSequenceNextValString(String sequenceName) {
        return "select " + this.getSelectSequenceNextValString(sequenceName) + " from sys.dummy";
    }

    @Override
    protected SqlTypeDescriptor getSqlTypeDescriptorOverride(int sqlCode) {
        switch (sqlCode) {
            case 2005: {
                return this.clobTypeDescriptor;
            }
            case 2011: {
                return this.nClobTypeDescriptor;
            }
            case 2004: {
                return this.blobTypeDescriptor;
            }
            case -6: {
                return SmallIntTypeDescriptor.INSTANCE;
            }
            case 16: {
                return this.useLegacyBooleanType ? BitTypeDescriptor.INSTANCE : BooleanTypeDescriptor.INSTANCE;
            }
            case 12: {
                return this.isUseUnicodeStringTypes() ? NVarcharTypeDescriptor.INSTANCE : VarcharTypeDescriptor.INSTANCE;
            }
            case 1: {
                return this.isUseUnicodeStringTypes() ? NCharTypeDescriptor.INSTANCE : CharTypeDescriptor.INSTANCE;
            }
            case 8: {
                return this.treatDoubleTypedFieldsAsDecimal ? DecimalTypeDescriptor.INSTANCE : DoubleTypeDescriptor.INSTANCE;
            }
        }
        return super.getSqlTypeDescriptorOverride(sqlCode);
    }

    @Override
    public boolean isCurrentTimestampSelectStringCallable() {
        return false;
    }

    protected void registerHanaKeywords() {
        this.registerKeyword("all");
        this.registerKeyword("alter");
        this.registerKeyword("as");
        this.registerKeyword("before");
        this.registerKeyword("begin");
        this.registerKeyword("both");
        this.registerKeyword("case");
        this.registerKeyword("char");
        this.registerKeyword("condition");
        this.registerKeyword("connect");
        this.registerKeyword("cross");
        this.registerKeyword("cube");
        this.registerKeyword("current_connection");
        this.registerKeyword("current_date");
        this.registerKeyword("current_schema");
        this.registerKeyword("current_time");
        this.registerKeyword("current_timestamp");
        this.registerKeyword("current_transaction_isolation_level");
        this.registerKeyword("current_user");
        this.registerKeyword("current_utcdate");
        this.registerKeyword("current_utctime");
        this.registerKeyword("current_utctimestamp");
        this.registerKeyword("currval");
        this.registerKeyword("cursor");
        this.registerKeyword("declare");
        this.registerKeyword("deferred");
        this.registerKeyword("distinct");
        this.registerKeyword("else");
        this.registerKeyword("elseif");
        this.registerKeyword("end");
        this.registerKeyword("except");
        this.registerKeyword("exception");
        this.registerKeyword("exec");
        this.registerKeyword("false");
        this.registerKeyword("for");
        this.registerKeyword("from");
        this.registerKeyword("full");
        this.registerKeyword("group");
        this.registerKeyword("having");
        this.registerKeyword("if");
        this.registerKeyword("in");
        this.registerKeyword("inner");
        this.registerKeyword("inout");
        this.registerKeyword("intersect");
        this.registerKeyword("into");
        this.registerKeyword("is");
        this.registerKeyword("join");
        this.registerKeyword("leading");
        this.registerKeyword("left");
        this.registerKeyword("limit");
        this.registerKeyword("loop");
        this.registerKeyword("minus");
        this.registerKeyword("natural");
        this.registerKeyword("nchar");
        this.registerKeyword("nextval");
        this.registerKeyword("null");
        this.registerKeyword("on");
        this.registerKeyword("order");
        this.registerKeyword("out");
        this.registerKeyword("prior");
        this.registerKeyword("return");
        this.registerKeyword("returns");
        this.registerKeyword("reverse");
        this.registerKeyword("right");
        this.registerKeyword("rollup");
        this.registerKeyword("rowid");
        this.registerKeyword("select");
        this.registerKeyword("session_user");
        this.registerKeyword("set");
        this.registerKeyword("sql");
        this.registerKeyword("start");
        this.registerKeyword("sysuuid");
        this.registerKeyword("tablesample");
        this.registerKeyword("top");
        this.registerKeyword("trailing");
        this.registerKeyword("true");
        this.registerKeyword("union");
        this.registerKeyword("unknown");
        this.registerKeyword("using");
        this.registerKeyword("utctimestamp");
        this.registerKeyword("values");
        this.registerKeyword("when");
        this.registerKeyword("where");
        this.registerKeyword("while");
        this.registerKeyword("with");
    }

    @Override
    public ScrollMode defaultScrollMode() {
        return ScrollMode.FORWARD_ONLY;
    }

    @Override
    public boolean supportsColumnCheck() {
        return false;
    }

    @Override
    public boolean supportsCurrentTimestampSelection() {
        return true;
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
    public boolean supportsExpectedLobUsagePattern() {
        return false;
    }

    @Override
    public boolean supportsUnboundedLobLocatorMaterialization() {
        return false;
    }

    @Override
    public boolean supportsLimit() {
        return true;
    }

    @Override
    public boolean supportsPooledSequences() {
        return true;
    }

    @Override
    public boolean supportsSequences() {
        return true;
    }

    @Override
    public boolean supportsTableCheck() {
        return true;
    }

    @Override
    public boolean supportsTupleDistinctCounts() {
        return true;
    }

    @Override
    public boolean supportsUnionAll() {
        return true;
    }

    @Override
    public boolean dropConstraints() {
        return false;
    }

    @Override
    public boolean supportsRowValueConstructorSyntax() {
        return true;
    }

    @Override
    public boolean supportsRowValueConstructorSyntaxInInList() {
        return true;
    }

    @Override
    public int getMaxAliasLength() {
        return 128;
    }

    @Override
    public LimitHandler getLimitHandler() {
        return LIMIT_HANDLER;
    }

    @Override
    public String getSelectGUIDString() {
        return "select sysuuid from sys.dummy";
    }

    @Override
    public NameQualifierSupport getNameQualifierSupport() {
        return NameQualifierSupport.SCHEMA;
    }

    @Override
    public IdentifierHelper buildIdentifierHelper(IdentifierHelperBuilder builder, DatabaseMetaData dbMetaData) throws SQLException {
        builder.applyIdentifierCasing(dbMetaData);
        builder.applyReservedWords(dbMetaData);
        builder.applyReservedWords(AnsiSqlKeywords.INSTANCE.sql2003());
        builder.applyReservedWords(this.getKeywords());
        builder.setNameQualifierSupport(this.getNameQualifierSupport());
        builder.setQuotedCaseStrategy(IdentifierCaseStrategy.MIXED);
        builder.setUnquotedCaseStrategy(IdentifierCaseStrategy.UPPER);
        final IdentifierHelper identifierHelper = builder.build();
        return new IdentifierHelper(){
            private final IdentifierHelper helper;
            {
                this.helper = identifierHelper;
            }

            @Override
            public String toMetaDataSchemaName(Identifier schemaIdentifier) {
                return this.helper.toMetaDataSchemaName(schemaIdentifier);
            }

            @Override
            public String toMetaDataObjectName(Identifier identifier) {
                return this.helper.toMetaDataObjectName(identifier);
            }

            @Override
            public String toMetaDataCatalogName(Identifier catalogIdentifier) {
                return this.helper.toMetaDataCatalogName(catalogIdentifier);
            }

            @Override
            public Identifier toIdentifier(String text) {
                return this.normalizeQuoting(Identifier.toIdentifier(text));
            }

            @Override
            public Identifier toIdentifier(String text, boolean quoted) {
                return this.normalizeQuoting(Identifier.toIdentifier(text, quoted));
            }

            @Override
            public Identifier normalizeQuoting(Identifier identifier) {
                Identifier normalizedIdentifier = this.helper.normalizeQuoting(identifier);
                if (normalizedIdentifier == null) {
                    return null;
                }
                if (!normalizedIdentifier.isQuoted() && !normalizedIdentifier.getText().matches("\\w+")) {
                    normalizedIdentifier = Identifier.quote(normalizedIdentifier);
                }
                return normalizedIdentifier;
            }

            @Override
            public boolean isReservedWord(String word) {
                return this.helper.isReservedWord(word);
            }

            @Override
            public Identifier applyGlobalQuoting(String text) {
                return this.helper.applyGlobalQuoting(text);
            }
        };
    }

    @Override
    public String getCurrentSchemaCommand() {
        return "select current_schema from sys.dummy";
    }

    @Override
    public String getForUpdateNowaitString(String aliases) {
        return this.getForUpdateString(aliases) + " nowait";
    }

    @Override
    public String getReadLockString(int timeout) {
        return this.getWriteLockString(timeout);
    }

    @Override
    public String getReadLockString(String aliases, int timeout) {
        return this.getWriteLockString(aliases, timeout);
    }

    @Override
    public String getWriteLockString(int timeout) {
        long timeoutInSeconds = this.getLockWaitTimeoutInSeconds(timeout);
        if (timeoutInSeconds > 0L) {
            return this.getForUpdateString() + " wait " + timeoutInSeconds;
        }
        if (timeoutInSeconds == 0L) {
            return this.getForUpdateNowaitString();
        }
        return this.getForUpdateString();
    }

    @Override
    public String getWriteLockString(String aliases, int timeout) {
        if (timeout > 0) {
            return this.getForUpdateString(aliases) + " wait " + this.getLockWaitTimeoutInSeconds(timeout);
        }
        if (timeout == 0) {
            return this.getForUpdateNowaitString(aliases);
        }
        return this.getForUpdateString(aliases);
    }

    private long getLockWaitTimeoutInSeconds(int timeoutInMilliseconds) {
        Duration duration = Duration.ofMillis(timeoutInMilliseconds);
        long timeoutInSeconds = duration.getSeconds();
        if (duration.getNano() != 0) {
            LOG.info("Changing the query timeout from " + timeoutInMilliseconds + " ms to " + timeoutInSeconds + " s, because HANA requires the timeout in seconds");
        }
        return timeoutInSeconds;
    }

    @Override
    public String getQueryHintString(String query, List<String> hints) {
        return query + " with hint (" + String.join((CharSequence)",", hints) + ")";
    }

    @Override
    public String getTableComment(String comment) {
        return "comment '" + comment + "'";
    }

    @Override
    public String getColumnComment(String comment) {
        return "comment '" + comment + "'";
    }

    @Override
    public boolean supportsCommentOn() {
        return true;
    }

    @Override
    public boolean supportsPartitionBy() {
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void contributeTypes(TypeContributions typeContributions, ServiceRegistry serviceRegistry) {
        super.contributeTypes(typeContributions, serviceRegistry);
        ConnectionProvider connectionProvider = serviceRegistry.getService(ConnectionProvider.class);
        int maxLobPrefetchSizeDefault = 1024;
        if (connectionProvider != null) {
            Connection conn = null;
            try {
                conn = connectionProvider.getConnection();
                try (Statement statement = conn.createStatement();
                     ResultSet rs = statement.executeQuery("SELECT TOP 1 VALUE, MAP(LAYER_NAME, 'DEFAULT', 1, 'SYSTEM', 2, 'DATABASE', 3, 4) AS LAYER FROM SYS.M_INIFILE_CONTENTS WHERE FILE_NAME='indexserver.ini' AND SECTION='session' AND KEY='max_lob_prefetch_size' ORDER BY LAYER DESC");){
                    if (rs.next()) {
                        maxLobPrefetchSizeDefault = rs.getInt(1);
                    }
                }
            }
            catch (Exception e) {
                LOG.debug("An error occurred while trying to determine the value of the HANA parameter indexserver.ini / session / max_lob_prefetch_size. Using the default value " + maxLobPrefetchSizeDefault, e);
            }
            finally {
                if (conn != null) {
                    try {
                        connectionProvider.closeConnection(conn);
                    }
                    catch (SQLException e) {}
                }
            }
        }
        ConfigurationService configurationService = serviceRegistry.getService(ConfigurationService.class);
        int maxLobPrefetchSize = configurationService.getSetting(MAX_LOB_PREFETCH_SIZE_PARAMETER_NAME, new ConfigurationService.Converter<Integer>(){

            @Override
            public Integer convert(Object value) {
                return Integer.valueOf(value.toString());
            }
        }, Integer.valueOf(maxLobPrefetchSizeDefault));
        if (this.nClobTypeDescriptor.getMaxLobPrefetchSize() != maxLobPrefetchSize) {
            this.nClobTypeDescriptor = new HANANClobTypeDescriptor(maxLobPrefetchSize);
        }
        if (this.blobTypeDescriptor.getMaxLobPrefetchSize() != maxLobPrefetchSize) {
            this.blobTypeDescriptor = new HANABlobTypeDescriptor(maxLobPrefetchSize);
        }
        if (this.supportsAsciiStringTypes()) {
            this.useUnicodeStringTypes = configurationService.getSetting(USE_UNICODE_STRING_TYPES_PARAMETER_NAME, StandardConverters.BOOLEAN, this.useUnicodeStringTypesDefault());
            if (this.isUseUnicodeStringTypes()) {
                this.registerColumnType(1, "nvarchar(1)");
                this.registerColumnType(12, 5000L, "nvarchar($l)");
                this.registerColumnType(-1, 5000L, "nvarchar($l)");
                this.registerColumnType(-1, "nclob");
                this.registerColumnType(12, "nclob");
                this.registerColumnType(2005, "nclob");
            }
            if (this.clobTypeDescriptor.getMaxLobPrefetchSize() != maxLobPrefetchSize || this.clobTypeDescriptor.isUseUnicodeStringTypes() != this.isUseUnicodeStringTypes()) {
                this.clobTypeDescriptor = new HANAClobTypeDescriptor(maxLobPrefetchSize, this.isUseUnicodeStringTypes());
            }
        }
        this.useLegacyBooleanType = configurationService.getSetting(USE_LEGACY_BOOLEAN_TYPE_PARAMETER_NAME, StandardConverters.BOOLEAN, USE_LEGACY_BOOLEAN_TYPE_DEFAULT_VALUE);
        if (this.useLegacyBooleanType) {
            this.registerColumnType(16, "tinyint");
        }
        this.treatDoubleTypedFieldsAsDecimal = configurationService.getSetting(TREAT_DOUBLE_TYPED_FIELDS_AS_DECIMAL_PARAMETER_NAME, StandardConverters.BOOLEAN, TREAT_DOUBLE_TYPED_FIELDS_AS_DECIMAL_DEFAULT_VALUE);
        if (this.treatDoubleTypedFieldsAsDecimal) {
            this.registerHibernateType(8, StandardBasicTypes.BIG_DECIMAL.getName());
        }
    }

    public SqlTypeDescriptor getBlobTypeDescriptor() {
        return this.blobTypeDescriptor;
    }

    @Override
    public String toBooleanValueString(boolean bool) {
        if (this.useLegacyBooleanType) {
            return bool ? "1" : "0";
        }
        return bool ? "true" : "false";
    }

    @Override
    public IdentityColumnSupport getIdentityColumnSupport() {
        return new HANAIdentityColumnSupport();
    }

    @Override
    public Exporter<Table> getTableExporter() {
        return this.hanaTableExporter;
    }

    @Override
    public CallableStatementSupport getCallableStatementSupport() {
        return StandardCallableStatementSupport.REF_CURSOR_INSTANCE;
    }

    @Override
    public int registerResultSetOutParameter(CallableStatement statement, int position) throws SQLException {
        return position;
    }

    @Override
    public int registerResultSetOutParameter(CallableStatement statement, String name) throws SQLException {
        return 0;
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
    public boolean supportsNoColumnsInsert() {
        return false;
    }

    public boolean isUseUnicodeStringTypes() {
        return this.useUnicodeStringTypes;
    }

    protected abstract boolean supportsAsciiStringTypes();

    protected abstract Boolean useUnicodeStringTypesDefault();

    public static class HANABlobTypeDescriptor
    implements SqlTypeDescriptor {
        private static final long serialVersionUID = 5874441715643764323L;
        final int maxLobPrefetchSize;
        final HANAStreamBlobTypeDescriptor hanaStreamBlobTypeDescriptor;

        public HANABlobTypeDescriptor(int maxLobPrefetchSize) {
            this.maxLobPrefetchSize = maxLobPrefetchSize;
            this.hanaStreamBlobTypeDescriptor = new HANAStreamBlobTypeDescriptor(maxLobPrefetchSize);
        }

        @Override
        public int getSqlType() {
            return 2004;
        }

        @Override
        public boolean canBeRemapped() {
            return true;
        }

        @Override
        public <X> ValueExtractor<X> getExtractor(final JavaTypeDescriptor<X> javaTypeDescriptor) {
            return new BasicExtractor<X>(javaTypeDescriptor, this){

                @Override
                protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
                    Blob rsBlob = rs.getBlob(name);
                    if (rsBlob == null || rsBlob.length() < (long)maxLobPrefetchSize) {
                        return javaTypeDescriptor.wrap(rsBlob, options);
                    }
                    MaterializedBlob blob = new MaterializedBlob(DataHelper.extractBytes(rsBlob.getBinaryStream()));
                    return javaTypeDescriptor.wrap(blob, options);
                }

                @Override
                protected X doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
                    return javaTypeDescriptor.wrap(statement.getBlob(index), options);
                }

                @Override
                protected X doExtract(CallableStatement statement, String name, WrapperOptions options) throws SQLException {
                    return javaTypeDescriptor.wrap(statement.getBlob(name), options);
                }
            };
        }

        public <X> BasicBinder<X> getBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
            return new BasicBinder<X>(javaTypeDescriptor, this){

                @Override
                protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
                    SqlTypeDescriptor descriptor = BlobTypeDescriptor.BLOB_BINDING;
                    if (byte[].class.isInstance(value)) {
                        descriptor = BlobTypeDescriptor.PRIMITIVE_ARRAY_BINDING;
                    } else if (options.useStreamForLobBinding()) {
                        descriptor = hanaStreamBlobTypeDescriptor;
                    }
                    descriptor.getBinder(javaTypeDescriptor).bind(st, value, index, options);
                }

                @Override
                protected void doBind(CallableStatement st, X value, String name, WrapperOptions options) throws SQLException {
                    SqlTypeDescriptor descriptor = BlobTypeDescriptor.BLOB_BINDING;
                    if (byte[].class.isInstance(value)) {
                        descriptor = BlobTypeDescriptor.PRIMITIVE_ARRAY_BINDING;
                    } else if (options.useStreamForLobBinding()) {
                        descriptor = hanaStreamBlobTypeDescriptor;
                    }
                    descriptor.getBinder(javaTypeDescriptor).bind(st, value, name, options);
                }
            };
        }

        public int getMaxLobPrefetchSize() {
            return this.maxLobPrefetchSize;
        }
    }

    private static class HANANClobTypeDescriptor
    extends NClobTypeDescriptor {
        private static final long serialVersionUID = 5651116091681647859L;
        final int maxLobPrefetchSize;

        public HANANClobTypeDescriptor(int maxLobPrefetchSize) {
            this.maxLobPrefetchSize = maxLobPrefetchSize;
        }

        @Override
        public <X> BasicBinder<X> getNClobBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
            return new BasicBinder<X>(javaTypeDescriptor, this){

                @Override
                protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
                    CharacterStream characterStream = javaTypeDescriptor.unwrap(value, CharacterStream.class, options);
                    if (value instanceof NClobImplementer) {
                        try (CloseSuppressingReader r = new CloseSuppressingReader(characterStream.asReader());){
                            st.setCharacterStream(index, (Reader)r, characterStream.getLength());
                        }
                        catch (IOException iOException) {}
                    } else {
                        st.setCharacterStream(index, characterStream.asReader(), characterStream.getLength());
                    }
                }

                @Override
                protected void doBind(CallableStatement st, X value, String name, WrapperOptions options) throws SQLException {
                    CharacterStream characterStream = javaTypeDescriptor.unwrap(value, CharacterStream.class, options);
                    if (value instanceof NClobImplementer) {
                        try (CloseSuppressingReader r = new CloseSuppressingReader(characterStream.asReader());){
                            st.setCharacterStream(name, (Reader)r, characterStream.getLength());
                        }
                        catch (IOException iOException) {}
                    } else {
                        st.setCharacterStream(name, characterStream.asReader(), characterStream.getLength());
                    }
                }
            };
        }

        @Override
        public <X> ValueExtractor<X> getExtractor(final JavaTypeDescriptor<X> javaTypeDescriptor) {
            return new BasicExtractor<X>(javaTypeDescriptor, this){

                @Override
                protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
                    NClob rsNClob = rs.getNClob(name);
                    if (rsNClob == null || rsNClob.length() < (long)maxLobPrefetchSize) {
                        return javaTypeDescriptor.wrap(rsNClob, options);
                    }
                    MaterializedNClob nClob = new MaterializedNClob(DataHelper.extractString(rsNClob));
                    return javaTypeDescriptor.wrap(nClob, options);
                }

                @Override
                protected X doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
                    return javaTypeDescriptor.wrap(statement.getNClob(index), options);
                }

                @Override
                protected X doExtract(CallableStatement statement, String name, WrapperOptions options) throws SQLException {
                    return javaTypeDescriptor.wrap(statement.getNClob(name), options);
                }
            };
        }

        public int getMaxLobPrefetchSize() {
            return this.maxLobPrefetchSize;
        }
    }

    private static class HANAClobTypeDescriptor
    extends ClobTypeDescriptor {
        private static final long serialVersionUID = -379042275442752102L;
        final int maxLobPrefetchSize;
        final boolean useUnicodeStringTypes;

        public HANAClobTypeDescriptor(int maxLobPrefetchSize, boolean useUnicodeStringTypes) {
            this.maxLobPrefetchSize = maxLobPrefetchSize;
            this.useUnicodeStringTypes = useUnicodeStringTypes;
        }

        @Override
        public <X> BasicBinder<X> getClobBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
            return new BasicBinder<X>(javaTypeDescriptor, this){

                @Override
                protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
                    CharacterStream characterStream = javaTypeDescriptor.unwrap(value, CharacterStream.class, options);
                    if (value instanceof ClobImplementer) {
                        try (CloseSuppressingReader r = new CloseSuppressingReader(characterStream.asReader());){
                            st.setCharacterStream(index, (Reader)r, characterStream.getLength());
                        }
                        catch (IOException iOException) {}
                    } else {
                        st.setCharacterStream(index, characterStream.asReader(), characterStream.getLength());
                    }
                }

                @Override
                protected void doBind(CallableStatement st, X value, String name, WrapperOptions options) throws SQLException {
                    CharacterStream characterStream = javaTypeDescriptor.unwrap(value, CharacterStream.class, options);
                    if (value instanceof ClobImplementer) {
                        try (CloseSuppressingReader r = new CloseSuppressingReader(characterStream.asReader());){
                            st.setCharacterStream(name, (Reader)r, characterStream.getLength());
                        }
                        catch (IOException iOException) {}
                    } else {
                        st.setCharacterStream(name, characterStream.asReader(), characterStream.getLength());
                    }
                }
            };
        }

        @Override
        public <X> ValueExtractor<X> getExtractor(final JavaTypeDescriptor<X> javaTypeDescriptor) {
            return new BasicExtractor<X>(javaTypeDescriptor, this){

                @Override
                protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
                    Clob rsClob = useUnicodeStringTypes ? rs.getNClob(name) : rs.getClob(name);
                    if (rsClob == null || rsClob.length() < (long)maxLobPrefetchSize) {
                        return javaTypeDescriptor.wrap(rsClob, options);
                    }
                    MaterializedNClob clob = new MaterializedNClob(DataHelper.extractString(rsClob));
                    return javaTypeDescriptor.wrap(clob, options);
                }

                @Override
                protected X doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
                    return javaTypeDescriptor.wrap(statement.getClob(index), options);
                }

                @Override
                protected X doExtract(CallableStatement statement, String name, WrapperOptions options) throws SQLException {
                    return javaTypeDescriptor.wrap(statement.getClob(name), options);
                }
            };
        }

        public int getMaxLobPrefetchSize() {
            return this.maxLobPrefetchSize;
        }

        public boolean isUseUnicodeStringTypes() {
            return this.useUnicodeStringTypes;
        }
    }

    private static class HANAStreamBlobTypeDescriptor
    implements SqlTypeDescriptor {
        private static final long serialVersionUID = -2476600722093442047L;
        final int maxLobPrefetchSize;

        public HANAStreamBlobTypeDescriptor(int maxLobPrefetchSize) {
            this.maxLobPrefetchSize = maxLobPrefetchSize;
        }

        @Override
        public int getSqlType() {
            return 2004;
        }

        @Override
        public boolean canBeRemapped() {
            return true;
        }

        @Override
        public <X> ValueBinder<X> getBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
            return new BasicBinder<X>(javaTypeDescriptor, this){

                @Override
                protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
                    BinaryStream binaryStream = javaTypeDescriptor.unwrap(value, BinaryStream.class, options);
                    if (value instanceof BlobImplementer) {
                        try (CloseSuppressingInputStream is = new CloseSuppressingInputStream(binaryStream.getInputStream());){
                            st.setBinaryStream(index, (InputStream)is, binaryStream.getLength());
                        }
                        catch (IOException iOException) {}
                    } else {
                        st.setBinaryStream(index, binaryStream.getInputStream(), binaryStream.getLength());
                    }
                }

                @Override
                protected void doBind(CallableStatement st, X value, String name, WrapperOptions options) throws SQLException {
                    BinaryStream binaryStream = javaTypeDescriptor.unwrap(value, BinaryStream.class, options);
                    if (value instanceof BlobImplementer) {
                        try (CloseSuppressingInputStream is = new CloseSuppressingInputStream(binaryStream.getInputStream());){
                            st.setBinaryStream(name, (InputStream)is, binaryStream.getLength());
                        }
                        catch (IOException iOException) {}
                    } else {
                        st.setBinaryStream(name, binaryStream.getInputStream(), binaryStream.getLength());
                    }
                }
            };
        }

        @Override
        public <X> ValueExtractor<X> getExtractor(final JavaTypeDescriptor<X> javaTypeDescriptor) {
            return new BasicExtractor<X>(javaTypeDescriptor, this){

                @Override
                protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
                    Blob rsBlob = rs.getBlob(name);
                    if (rsBlob == null || rsBlob.length() < (long)maxLobPrefetchSize) {
                        return javaTypeDescriptor.wrap(rsBlob, options);
                    }
                    MaterializedBlob blob = new MaterializedBlob(DataHelper.extractBytes(rsBlob.getBinaryStream()));
                    return javaTypeDescriptor.wrap(blob, options);
                }

                @Override
                protected X doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
                    return javaTypeDescriptor.wrap(statement.getBlob(index), options);
                }

                @Override
                protected X doExtract(CallableStatement statement, String name, WrapperOptions options) throws SQLException {
                    return javaTypeDescriptor.wrap(statement.getBlob(name), options);
                }
            };
        }
    }

    private static class MaterializedNClob
    implements NClob {
        private String data = null;

        public MaterializedNClob(String data) {
            this.data = data;
        }

        @Override
        public void truncate(long len) throws SQLException {
            this.data = "";
        }

        @Override
        public int setString(long pos, String str, int offset, int len) throws SQLException {
            this.data = this.data.substring(0, (int)(pos - 1L)) + str.substring(offset, offset + len) + this.data.substring((int)(pos - 1L + (long)len));
            return len;
        }

        @Override
        public int setString(long pos, String str) throws SQLException {
            this.data = this.data.substring(0, (int)(pos - 1L)) + str + this.data.substring((int)(pos - 1L + (long)str.length()));
            return str.length();
        }

        @Override
        public Writer setCharacterStream(long pos) throws SQLException {
            throw new SQLFeatureNotSupportedException();
        }

        @Override
        public OutputStream setAsciiStream(long pos) throws SQLException {
            throw new SQLFeatureNotSupportedException();
        }

        @Override
        public long position(Clob searchstr, long start) throws SQLException {
            return this.data.indexOf(DataHelper.extractString(searchstr), (int)(start - 1L));
        }

        @Override
        public long position(String searchstr, long start) throws SQLException {
            return this.data.indexOf(searchstr, (int)(start - 1L));
        }

        @Override
        public long length() throws SQLException {
            return this.data.length();
        }

        @Override
        public String getSubString(long pos, int length) throws SQLException {
            return this.data.substring((int)(pos - 1L), (int)(pos - 1L + (long)length));
        }

        @Override
        public Reader getCharacterStream(long pos, long length) throws SQLException {
            return new StringReader(this.data.substring((int)(pos - 1L), (int)(pos - 1L + length)));
        }

        @Override
        public Reader getCharacterStream() throws SQLException {
            return new StringReader(this.data);
        }

        @Override
        public InputStream getAsciiStream() throws SQLException {
            return new ByteArrayInputStream(this.data.getBytes(StandardCharsets.ISO_8859_1));
        }

        @Override
        public void free() throws SQLException {
            this.data = null;
        }
    }

    private static class MaterializedBlob
    implements Blob {
        private byte[] bytes = null;

        public MaterializedBlob(byte[] bytes) {
            this.setBytes(bytes);
        }

        @Override
        public long length() throws SQLException {
            return this.getBytes().length;
        }

        @Override
        public byte[] getBytes(long pos, int length) throws SQLException {
            return Arrays.copyOfRange(this.bytes, (int)(pos - 1L), (int)(pos - 1L + (long)length));
        }

        @Override
        public InputStream getBinaryStream() throws SQLException {
            return new ByteArrayInputStream(this.getBytes());
        }

        @Override
        public long position(byte[] pattern, long start) throws SQLException {
            throw new SQLFeatureNotSupportedException();
        }

        @Override
        public long position(Blob pattern, long start) throws SQLException {
            throw new SQLFeatureNotSupportedException();
        }

        @Override
        public int setBytes(long pos, byte[] bytes) throws SQLException {
            int bytesSet = 0;
            if ((long)this.bytes.length < pos - 1L + (long)bytes.length) {
                this.bytes = Arrays.copyOf(this.bytes, (int)(pos - 1L + (long)bytes.length));
            }
            int i = 0;
            while (i < bytes.length && i < this.bytes.length) {
                this.bytes[(int)((long)i + pos - 1L)] = bytes[i];
                ++i;
                ++bytesSet;
            }
            return bytesSet;
        }

        @Override
        public int setBytes(long pos, byte[] bytes, int offset, int len) throws SQLException {
            int bytesSet = 0;
            if ((long)this.bytes.length < pos - 1L + (long)len) {
                this.bytes = Arrays.copyOf(this.bytes, (int)(pos - 1L + (long)len));
            }
            int i = offset;
            while (i < len && i < this.bytes.length) {
                this.bytes[(int)((long)i + pos - 1L)] = bytes[i];
                ++i;
                ++bytesSet;
            }
            return bytesSet;
        }

        @Override
        public OutputStream setBinaryStream(long pos) throws SQLException {
            return new ByteArrayOutputStream(){
                {
                    this.buf = this.getBytes();
                }
            };
        }

        @Override
        public void truncate(long len) throws SQLException {
            this.setBytes(Arrays.copyOf(this.getBytes(), (int)len));
        }

        @Override
        public void free() throws SQLException {
            this.setBytes(null);
        }

        @Override
        public InputStream getBinaryStream(long pos, long length) throws SQLException {
            return new ByteArrayInputStream(this.getBytes(), (int)(pos - 1L), (int)length);
        }

        byte[] getBytes() {
            return this.bytes;
        }

        void setBytes(byte[] bytes) {
            this.bytes = bytes;
        }
    }

    private static class CloseSuppressingInputStream
    extends FilterInputStream {
        protected CloseSuppressingInputStream(InputStream in) {
            super(in);
        }

        @Override
        public void close() {
        }
    }

    private static class CloseSuppressingReader
    extends FilterReader {
        protected CloseSuppressingReader(Reader in) {
            super(in);
        }

        @Override
        public void close() {
        }
    }
}

