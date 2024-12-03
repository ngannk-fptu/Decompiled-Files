/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.hibernate.LockMode;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.function.ConditionalParenthesisFunction;
import org.hibernate.dialect.function.ConvertFunction;
import org.hibernate.dialect.function.NoArgSQLFunction;
import org.hibernate.dialect.function.NvlFunction;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.dialect.function.StandardJDBCEscapeFunction;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.dialect.function.VarArgsSQLFunction;
import org.hibernate.dialect.identity.Chache71IdentityColumnSupport;
import org.hibernate.dialect.identity.IdentityColumnSupport;
import org.hibernate.dialect.lock.LockingStrategy;
import org.hibernate.dialect.lock.OptimisticForceIncrementLockingStrategy;
import org.hibernate.dialect.lock.OptimisticLockingStrategy;
import org.hibernate.dialect.lock.PessimisticForceIncrementLockingStrategy;
import org.hibernate.dialect.lock.PessimisticReadUpdateLockingStrategy;
import org.hibernate.dialect.lock.PessimisticWriteUpdateLockingStrategy;
import org.hibernate.dialect.lock.SelectLockingStrategy;
import org.hibernate.dialect.lock.UpdateLockingStrategy;
import org.hibernate.dialect.pagination.LimitHandler;
import org.hibernate.dialect.pagination.TopLimitHandler;
import org.hibernate.exception.internal.CacheSQLExceptionConversionDelegate;
import org.hibernate.exception.spi.SQLExceptionConversionDelegate;
import org.hibernate.exception.spi.TemplatedViolatedConstraintNameExtracter;
import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;
import org.hibernate.hql.spi.id.IdTableSupportStandardImpl;
import org.hibernate.hql.spi.id.MultiTableBulkIdStrategy;
import org.hibernate.hql.spi.id.global.GlobalTemporaryTableBulkIdStrategy;
import org.hibernate.hql.spi.id.local.AfterUseAction;
import org.hibernate.persister.entity.Lockable;
import org.hibernate.sql.CacheJoinFragment;
import org.hibernate.sql.JoinFragment;
import org.hibernate.type.StandardBasicTypes;

public class Cache71Dialect
extends Dialect {
    private LimitHandler limitHandler;
    public static final ViolatedConstraintNameExtracter EXTRACTER = new TemplatedViolatedConstraintNameExtracter(){

        @Override
        protected String doExtractConstraintName(SQLException sqle) throws NumberFormatException {
            return this.extractUsingTemplate("constraint (", ") violated", sqle.getMessage());
        }
    };

    public Cache71Dialect() {
        this.commonRegistration();
        this.register71Functions();
        this.limitHandler = new TopLimitHandler(true, true);
    }

    protected final void commonRegistration() {
        this.registerColumnType(-2, "varbinary($1)");
        this.registerColumnType(-5, "BigInt");
        this.registerColumnType(-7, "bit");
        this.registerColumnType(1, "char(1)");
        this.registerColumnType(91, "date");
        this.registerColumnType(3, "decimal");
        this.registerColumnType(8, "double");
        this.registerColumnType(6, "float");
        this.registerColumnType(4, "integer");
        this.registerColumnType(-4, "longvarbinary");
        this.registerColumnType(-1, "longvarchar");
        this.registerColumnType(2, "numeric($p,$s)");
        this.registerColumnType(7, "real");
        this.registerColumnType(5, "smallint");
        this.registerColumnType(93, "timestamp");
        this.registerColumnType(92, "time");
        this.registerColumnType(-6, "tinyint");
        this.registerColumnType(-3, "longvarbinary");
        this.registerColumnType(12, "varchar($l)");
        this.registerColumnType(2004, "longvarbinary");
        this.registerColumnType(2005, "longvarchar");
        this.getDefaultProperties().setProperty("hibernate.jdbc.use_streams_for_binary", "false");
        this.getDefaultProperties().setProperty("hibernate.jdbc.batch_size", "15");
        this.getDefaultProperties().setProperty("hibernate.use_sql_comments", "false");
        this.registerFunction("abs", new StandardSQLFunction("abs"));
        this.registerFunction("acos", new StandardJDBCEscapeFunction("acos", StandardBasicTypes.DOUBLE));
        this.registerFunction("%alphaup", new StandardSQLFunction("%alphaup", StandardBasicTypes.STRING));
        this.registerFunction("ascii", new StandardSQLFunction("ascii", StandardBasicTypes.STRING));
        this.registerFunction("asin", new StandardJDBCEscapeFunction("asin", StandardBasicTypes.DOUBLE));
        this.registerFunction("atan", new StandardJDBCEscapeFunction("atan", StandardBasicTypes.DOUBLE));
        this.registerFunction("bit_length", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "($length(?1)*8)"));
        this.registerFunction("ceiling", new StandardSQLFunction("ceiling", StandardBasicTypes.INTEGER));
        this.registerFunction("char", new StandardJDBCEscapeFunction("char", StandardBasicTypes.CHARACTER));
        this.registerFunction("character_length", new StandardSQLFunction("character_length", StandardBasicTypes.INTEGER));
        this.registerFunction("char_length", new StandardSQLFunction("char_length", StandardBasicTypes.INTEGER));
        this.registerFunction("cos", new StandardJDBCEscapeFunction("cos", StandardBasicTypes.DOUBLE));
        this.registerFunction("cot", new StandardJDBCEscapeFunction("cot", StandardBasicTypes.DOUBLE));
        this.registerFunction("coalesce", new VarArgsSQLFunction("coalesce(", ",", ")"));
        this.registerFunction("concat", new VarArgsSQLFunction(StandardBasicTypes.STRING, "", "||", ""));
        this.registerFunction("convert", new ConvertFunction());
        this.registerFunction("curdate", new StandardJDBCEscapeFunction("curdate", StandardBasicTypes.DATE));
        this.registerFunction("current_date", new NoArgSQLFunction("current_date", StandardBasicTypes.DATE, false));
        this.registerFunction("current_time", new NoArgSQLFunction("current_time", StandardBasicTypes.TIME, false));
        this.registerFunction("current_timestamp", new ConditionalParenthesisFunction("current_timestamp", StandardBasicTypes.TIMESTAMP));
        this.registerFunction("curtime", new StandardJDBCEscapeFunction("curtime", StandardBasicTypes.TIME));
        this.registerFunction("database", new StandardJDBCEscapeFunction("database", StandardBasicTypes.STRING));
        this.registerFunction("dateadd", new VarArgsSQLFunction(StandardBasicTypes.TIMESTAMP, "dateadd(", ",", ")"));
        this.registerFunction("datediff", new VarArgsSQLFunction(StandardBasicTypes.INTEGER, "datediff(", ",", ")"));
        this.registerFunction("datename", new VarArgsSQLFunction(StandardBasicTypes.STRING, "datename(", ",", ")"));
        this.registerFunction("datepart", new VarArgsSQLFunction(StandardBasicTypes.INTEGER, "datepart(", ",", ")"));
        this.registerFunction("day", new StandardSQLFunction("day", StandardBasicTypes.INTEGER));
        this.registerFunction("dayname", new StandardJDBCEscapeFunction("dayname", StandardBasicTypes.STRING));
        this.registerFunction("dayofmonth", new StandardJDBCEscapeFunction("dayofmonth", StandardBasicTypes.INTEGER));
        this.registerFunction("dayofweek", new StandardJDBCEscapeFunction("dayofweek", StandardBasicTypes.INTEGER));
        this.registerFunction("dayofyear", new StandardJDBCEscapeFunction("dayofyear", StandardBasicTypes.INTEGER));
        this.registerFunction("%exact", new StandardSQLFunction("%exact", StandardBasicTypes.STRING));
        this.registerFunction("exp", new StandardJDBCEscapeFunction("exp", StandardBasicTypes.DOUBLE));
        this.registerFunction("%external", new StandardSQLFunction("%external", StandardBasicTypes.STRING));
        this.registerFunction("$extract", new VarArgsSQLFunction(StandardBasicTypes.INTEGER, "$extract(", ",", ")"));
        this.registerFunction("$find", new VarArgsSQLFunction(StandardBasicTypes.INTEGER, "$find(", ",", ")"));
        this.registerFunction("floor", new StandardSQLFunction("floor", StandardBasicTypes.INTEGER));
        this.registerFunction("getdate", new StandardSQLFunction("getdate", StandardBasicTypes.TIMESTAMP));
        this.registerFunction("hour", new StandardJDBCEscapeFunction("hour", StandardBasicTypes.INTEGER));
        this.registerFunction("ifnull", new VarArgsSQLFunction("ifnull(", ",", ")"));
        this.registerFunction("%internal", new StandardSQLFunction("%internal"));
        this.registerFunction("isnull", new VarArgsSQLFunction("isnull(", ",", ")"));
        this.registerFunction("isnumeric", new StandardSQLFunction("isnumeric", StandardBasicTypes.INTEGER));
        this.registerFunction("lcase", new StandardJDBCEscapeFunction("lcase", StandardBasicTypes.STRING));
        this.registerFunction("left", new StandardJDBCEscapeFunction("left", StandardBasicTypes.STRING));
        this.registerFunction("len", new StandardSQLFunction("len", StandardBasicTypes.INTEGER));
        this.registerFunction("$length", new VarArgsSQLFunction("$length(", ",", ")"));
        this.registerFunction("$list", new VarArgsSQLFunction("$list(", ",", ")"));
        this.registerFunction("$listdata", new VarArgsSQLFunction("$listdata(", ",", ")"));
        this.registerFunction("$listfind", new VarArgsSQLFunction("$listfind(", ",", ")"));
        this.registerFunction("$listget", new VarArgsSQLFunction("$listget(", ",", ")"));
        this.registerFunction("$listlength", new StandardSQLFunction("$listlength", StandardBasicTypes.INTEGER));
        this.registerFunction("locate", new StandardSQLFunction("$FIND", StandardBasicTypes.INTEGER));
        this.registerFunction("log", new StandardJDBCEscapeFunction("log", StandardBasicTypes.DOUBLE));
        this.registerFunction("log10", new StandardJDBCEscapeFunction("log", StandardBasicTypes.DOUBLE));
        this.registerFunction("lower", new StandardSQLFunction("lower"));
        this.registerFunction("ltrim", new StandardSQLFunction("ltrim"));
        this.registerFunction("minute", new StandardJDBCEscapeFunction("minute", StandardBasicTypes.INTEGER));
        this.registerFunction("mod", new StandardJDBCEscapeFunction("mod", StandardBasicTypes.DOUBLE));
        this.registerFunction("month", new StandardJDBCEscapeFunction("month", StandardBasicTypes.INTEGER));
        this.registerFunction("monthname", new StandardJDBCEscapeFunction("monthname", StandardBasicTypes.STRING));
        this.registerFunction("now", new StandardJDBCEscapeFunction("monthname", StandardBasicTypes.TIMESTAMP));
        this.registerFunction("nullif", new VarArgsSQLFunction("nullif(", ",", ")"));
        this.registerFunction("nvl", new NvlFunction());
        this.registerFunction("%odbcin", new StandardSQLFunction("%odbcin"));
        this.registerFunction("%odbcout", new StandardSQLFunction("%odbcin"));
        this.registerFunction("%pattern", new VarArgsSQLFunction(StandardBasicTypes.STRING, "", "%pattern", ""));
        this.registerFunction("pi", new StandardJDBCEscapeFunction("pi", StandardBasicTypes.DOUBLE));
        this.registerFunction("$piece", new VarArgsSQLFunction(StandardBasicTypes.STRING, "$piece(", ",", ")"));
        this.registerFunction("position", new VarArgsSQLFunction(StandardBasicTypes.INTEGER, "position(", " in ", ")"));
        this.registerFunction("power", new VarArgsSQLFunction(StandardBasicTypes.STRING, "power(", ",", ")"));
        this.registerFunction("quarter", new StandardJDBCEscapeFunction("quarter", StandardBasicTypes.INTEGER));
        this.registerFunction("repeat", new VarArgsSQLFunction(StandardBasicTypes.STRING, "repeat(", ",", ")"));
        this.registerFunction("replicate", new VarArgsSQLFunction(StandardBasicTypes.STRING, "replicate(", ",", ")"));
        this.registerFunction("right", new StandardJDBCEscapeFunction("right", StandardBasicTypes.STRING));
        this.registerFunction("round", new VarArgsSQLFunction(StandardBasicTypes.FLOAT, "round(", ",", ")"));
        this.registerFunction("rtrim", new StandardSQLFunction("rtrim", StandardBasicTypes.STRING));
        this.registerFunction("second", new StandardJDBCEscapeFunction("second", StandardBasicTypes.INTEGER));
        this.registerFunction("sign", new StandardSQLFunction("sign", StandardBasicTypes.INTEGER));
        this.registerFunction("sin", new StandardJDBCEscapeFunction("sin", StandardBasicTypes.DOUBLE));
        this.registerFunction("space", new StandardSQLFunction("space", StandardBasicTypes.STRING));
        this.registerFunction("%sqlstring", new VarArgsSQLFunction(StandardBasicTypes.STRING, "%sqlstring(", ",", ")"));
        this.registerFunction("%sqlupper", new VarArgsSQLFunction(StandardBasicTypes.STRING, "%sqlupper(", ",", ")"));
        this.registerFunction("sqrt", new StandardJDBCEscapeFunction("SQRT", StandardBasicTypes.DOUBLE));
        this.registerFunction("%startswith", new VarArgsSQLFunction(StandardBasicTypes.STRING, "", "%startswith", ""));
        this.registerFunction("str", new SQLFunctionTemplate(StandardBasicTypes.STRING, "cast(?1 as char varying)"));
        this.registerFunction("string", new VarArgsSQLFunction(StandardBasicTypes.STRING, "string(", ",", ")"));
        this.registerFunction("%string", new VarArgsSQLFunction(StandardBasicTypes.STRING, "%string(", ",", ")"));
        this.registerFunction("substr", new VarArgsSQLFunction(StandardBasicTypes.STRING, "substr(", ",", ")"));
        this.registerFunction("substring", new VarArgsSQLFunction(StandardBasicTypes.STRING, "substring(", ",", ")"));
        this.registerFunction("sysdate", new NoArgSQLFunction("sysdate", StandardBasicTypes.TIMESTAMP, false));
        this.registerFunction("tan", new StandardJDBCEscapeFunction("tan", StandardBasicTypes.DOUBLE));
        this.registerFunction("timestampadd", new StandardJDBCEscapeFunction("timestampadd", StandardBasicTypes.DOUBLE));
        this.registerFunction("timestampdiff", new StandardJDBCEscapeFunction("timestampdiff", StandardBasicTypes.DOUBLE));
        this.registerFunction("tochar", new VarArgsSQLFunction(StandardBasicTypes.STRING, "tochar(", ",", ")"));
        this.registerFunction("to_char", new VarArgsSQLFunction(StandardBasicTypes.STRING, "to_char(", ",", ")"));
        this.registerFunction("todate", new VarArgsSQLFunction(StandardBasicTypes.STRING, "todate(", ",", ")"));
        this.registerFunction("to_date", new VarArgsSQLFunction(StandardBasicTypes.STRING, "todate(", ",", ")"));
        this.registerFunction("tonumber", new StandardSQLFunction("tonumber"));
        this.registerFunction("to_number", new StandardSQLFunction("tonumber"));
        this.registerFunction("truncate", new StandardJDBCEscapeFunction("truncate", StandardBasicTypes.STRING));
        this.registerFunction("ucase", new StandardJDBCEscapeFunction("ucase", StandardBasicTypes.STRING));
        this.registerFunction("upper", new StandardSQLFunction("upper"));
        this.registerFunction("%upper", new StandardSQLFunction("%upper"));
        this.registerFunction("user", new StandardJDBCEscapeFunction("user", StandardBasicTypes.STRING));
        this.registerFunction("week", new StandardJDBCEscapeFunction("user", StandardBasicTypes.INTEGER));
        this.registerFunction("xmlconcat", new VarArgsSQLFunction(StandardBasicTypes.STRING, "xmlconcat(", ",", ")"));
        this.registerFunction("xmlelement", new VarArgsSQLFunction(StandardBasicTypes.STRING, "xmlelement(", ",", ")"));
        this.registerFunction("year", new StandardJDBCEscapeFunction("year", StandardBasicTypes.INTEGER));
    }

    protected final void register71Functions() {
        this.registerFunction("str", new VarArgsSQLFunction(StandardBasicTypes.STRING, "str(", ",", ")"));
    }

    @Override
    public boolean hasAlterTable() {
        return true;
    }

    @Override
    public boolean qualifyIndexName() {
        return false;
    }

    @Override
    public String getAddForeignKeyConstraintString(String constraintName, String[] foreignKey, String referencedTable, String[] primaryKey, boolean referencesPrimaryKey) {
        return new StringBuilder(300).append(" ADD CONSTRAINT ").append(constraintName).append(" FOREIGN KEY ").append(constraintName).append(" (").append(String.join((CharSequence)", ", foreignKey)).append(") REFERENCES ").append(referencedTable).append(" (").append(String.join((CharSequence)", ", primaryKey)).append(") ").toString();
    }

    public boolean supportsCheck() {
        return false;
    }

    @Override
    public String getAddColumnString() {
        return " add column";
    }

    @Override
    public String getCascadeConstraintsString() {
        return "";
    }

    @Override
    public boolean dropConstraints() {
        return true;
    }

    @Override
    public boolean supportsCascadeDelete() {
        return true;
    }

    @Override
    public boolean hasSelfReferentialForeignKeyBug() {
        return true;
    }

    @Override
    public MultiTableBulkIdStrategy getDefaultMultiTableBulkIdStrategy() {
        return new GlobalTemporaryTableBulkIdStrategy(new IdTableSupportStandardImpl(){

            @Override
            public String generateIdTableName(String baseName) {
                String name = super.generateIdTableName(baseName);
                return name.length() > 25 ? name.substring(1, 25) : name;
            }

            @Override
            public String getCreateIdTableCommand() {
                return "create global temporary table";
            }
        }, AfterUseAction.DROP);
    }

    @Override
    public String getNativeIdentifierGeneratorStrategy() {
        return "identity";
    }

    @Override
    public IdentityColumnSupport getIdentityColumnSupport() {
        return new Chache71IdentityColumnSupport();
    }

    @Override
    public boolean supportsSequences() {
        return false;
    }

    @Override
    public boolean supportsOuterJoinForUpdate() {
        return false;
    }

    @Override
    public LockingStrategy getLockingStrategy(Lockable lockable, LockMode lockMode) {
        if (lockMode == LockMode.PESSIMISTIC_FORCE_INCREMENT) {
            return new PessimisticForceIncrementLockingStrategy(lockable, lockMode);
        }
        if (lockMode == LockMode.PESSIMISTIC_WRITE) {
            return new PessimisticWriteUpdateLockingStrategy(lockable, lockMode);
        }
        if (lockMode == LockMode.PESSIMISTIC_READ) {
            return new PessimisticReadUpdateLockingStrategy(lockable, lockMode);
        }
        if (lockMode == LockMode.OPTIMISTIC) {
            return new OptimisticLockingStrategy(lockable, lockMode);
        }
        if (lockMode == LockMode.OPTIMISTIC_FORCE_INCREMENT) {
            return new OptimisticForceIncrementLockingStrategy(lockable, lockMode);
        }
        if (lockMode.greaterThan(LockMode.READ)) {
            return new UpdateLockingStrategy(lockable, lockMode);
        }
        return new SelectLockingStrategy(lockable, lockMode);
    }

    @Override
    public LimitHandler getLimitHandler() {
        if (this.isLegacyLimitHandlerBehaviorEnabled()) {
            return super.getLimitHandler();
        }
        return this.limitHandler;
    }

    @Override
    public boolean supportsLimit() {
        return true;
    }

    @Override
    public boolean supportsLimitOffset() {
        return false;
    }

    @Override
    public boolean supportsVariableLimit() {
        return true;
    }

    @Override
    public boolean bindLimitParametersFirst() {
        return true;
    }

    @Override
    public boolean useMaxForLimit() {
        return true;
    }

    @Override
    public String getLimitString(String sql, boolean hasOffset) {
        if (hasOffset) {
            throw new UnsupportedOperationException("query result offset is not supported");
        }
        int insertionPoint = sql.startsWith("select distinct") ? 15 : 6;
        return new StringBuilder(sql.length() + 8).append(sql).insert(insertionPoint, " TOP ? ").toString();
    }

    @Override
    public int registerResultSetOutParameter(CallableStatement statement, int col) throws SQLException {
        return col;
    }

    @Override
    public ResultSet getResultSet(CallableStatement ps) throws SQLException {
        ps.execute();
        return (ResultSet)ps.getObject(1);
    }

    @Override
    public String getLowercaseFunction() {
        return "lower";
    }

    @Override
    public String getNullColumnString() {
        return " null";
    }

    @Override
    public JoinFragment createOuterJoinFragment() {
        return new CacheJoinFragment();
    }

    @Override
    public String getNoColumnsInsertString() {
        return " default values";
    }

    @Override
    public SQLExceptionConversionDelegate buildSQLExceptionConversionDelegate() {
        return new CacheSQLExceptionConversionDelegate(this);
    }

    @Override
    public ViolatedConstraintNameExtracter getViolatedConstraintNameExtracter() {
        return EXTRACTER;
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
    public boolean supportsResultSetPositionQueryMethodsOnForwardOnlyCursor() {
        return false;
    }
}

