/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Locale;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.dialect.AbstractTransactSQLDialect;
import org.hibernate.dialect.function.AnsiTrimEmulationFunction;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.dialect.identity.IdentityColumnSupport;
import org.hibernate.dialect.identity.SQLServerIdentityColumnSupport;
import org.hibernate.dialect.pagination.LegacyLimitHandler;
import org.hibernate.dialect.pagination.LimitHandler;
import org.hibernate.dialect.pagination.TopLimitHandler;
import org.hibernate.engine.jdbc.env.spi.IdentifierCaseStrategy;
import org.hibernate.engine.jdbc.env.spi.IdentifierHelper;
import org.hibernate.engine.jdbc.env.spi.IdentifierHelperBuilder;
import org.hibernate.engine.jdbc.env.spi.NameQualifierSupport;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.descriptor.sql.SmallIntTypeDescriptor;
import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;

public class SQLServerDialect
extends AbstractTransactSQLDialect {
    private static final int PARAM_LIST_SIZE_LIMIT = 2100;
    private final LimitHandler limitHandler;

    public SQLServerDialect() {
        this.registerColumnType(-3, "image");
        this.registerColumnType(-3, 8000L, "varbinary($l)");
        this.registerColumnType(-4, "image");
        this.registerColumnType(-1, "text");
        this.registerColumnType(16, "bit");
        this.registerFunction("second", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "datepart(second, ?1)"));
        this.registerFunction("minute", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "datepart(minute, ?1)"));
        this.registerFunction("hour", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "datepart(hour, ?1)"));
        this.registerFunction("locate", new StandardSQLFunction("charindex", StandardBasicTypes.INTEGER));
        this.registerFunction("extract", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "datepart(?1, ?3)"));
        this.registerFunction("mod", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "?1 % ?2"));
        this.registerFunction("bit_length", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "datalength(?1) * 8"));
        this.registerFunction("trim", new AnsiTrimEmulationFunction());
        this.registerKeyword("top");
        this.registerKeyword("key");
        this.limitHandler = new TopLimitHandler(false, false);
    }

    @Override
    public String getNoColumnsInsertString() {
        return "default values";
    }

    static int getAfterSelectInsertPoint(String sql) {
        int selectIndex = sql.toLowerCase(Locale.ROOT).indexOf("select");
        int selectDistinctIndex = sql.toLowerCase(Locale.ROOT).indexOf("select distinct");
        return selectIndex + (selectDistinctIndex == selectIndex ? 15 : 6);
    }

    @Override
    public String getLimitString(String querySelect, int offset, int limit) {
        if (offset > 0) {
            throw new UnsupportedOperationException("query result offset is not supported");
        }
        return new StringBuilder(querySelect.length() + 8).append(querySelect).insert(SQLServerDialect.getAfterSelectInsertPoint(querySelect), " top " + limit).toString();
    }

    @Override
    public LimitHandler getLimitHandler() {
        if (this.isLegacyLimitHandlerBehaviorEnabled()) {
            return new LegacyLimitHandler(this);
        }
        return this.getDefaultLimitHandler();
    }

    protected LimitHandler getDefaultLimitHandler() {
        return this.limitHandler;
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
    public IdentifierHelper buildIdentifierHelper(IdentifierHelperBuilder builder, DatabaseMetaData dbMetaData) throws SQLException {
        if (dbMetaData == null) {
            builder.setUnquotedCaseStrategy(IdentifierCaseStrategy.MIXED);
            builder.setQuotedCaseStrategy(IdentifierCaseStrategy.MIXED);
        }
        return super.buildIdentifierHelper(builder, dbMetaData);
    }

    @Override
    public boolean supportsLimitOffset() {
        return false;
    }

    @Override
    public boolean supportsVariableLimit() {
        return false;
    }

    @Override
    public char closeQuote() {
        return ']';
    }

    @Override
    public String getCurrentSchemaCommand() {
        return "select schema_name()";
    }

    @Override
    public char openQuote() {
        return '[';
    }

    @Override
    public String appendLockHint(LockOptions lockOptions, String tableName) {
        LockMode mode = lockOptions.getLockMode();
        switch (mode) {
            case UPGRADE: 
            case UPGRADE_NOWAIT: 
            case PESSIMISTIC_WRITE: 
            case WRITE: {
                return tableName + " with (updlock, rowlock)";
            }
            case PESSIMISTIC_READ: {
                return tableName + " with (holdlock, rowlock)";
            }
            case UPGRADE_SKIPLOCKED: {
                return tableName + " with (updlock, rowlock, readpast)";
            }
        }
        return tableName;
    }

    @Override
    public String getCurrentTimestampSelectString() {
        return "select current_timestamp";
    }

    @Override
    public boolean areStringComparisonsCaseInsensitive() {
        return true;
    }

    @Override
    public boolean supportsResultSetPositionQueryMethodsOnForwardOnlyCursor() {
        return false;
    }

    @Override
    public boolean supportsCircularCascadeDeleteConstraints() {
        return false;
    }

    @Override
    public boolean supportsLobValueChangePropogation() {
        return false;
    }

    @Override
    public boolean doesReadCommittedCauseWritersToBlockReaders() {
        return false;
    }

    @Override
    public boolean doesRepeatableReadCauseReadersToBlockWriters() {
        return false;
    }

    @Override
    protected SqlTypeDescriptor getSqlTypeDescriptorOverride(int sqlCode) {
        return sqlCode == -6 ? SmallIntTypeDescriptor.INSTANCE : super.getSqlTypeDescriptorOverride(sqlCode);
    }

    @Override
    public int getInExpressionCountLimit() {
        return 2100;
    }

    @Override
    public IdentityColumnSupport getIdentityColumnSupport() {
        return new SQLServerIdentityColumnSupport();
    }

    @Override
    public String getCreateTemporaryTableColumnAnnotation(int sqlTypeCode) {
        switch (sqlTypeCode) {
            case -16: 
            case -15: 
            case -9: 
            case -1: 
            case 1: 
            case 12: {
                return "collate database_default";
            }
        }
        return "";
    }

    @Override
    public NameQualifierSupport getNameQualifierSupport() {
        return NameQualifierSupport.BOTH;
    }
}

