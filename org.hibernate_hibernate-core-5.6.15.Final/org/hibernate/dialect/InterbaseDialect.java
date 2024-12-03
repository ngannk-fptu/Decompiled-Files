/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect;

import java.util.Locale;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.function.NoArgSQLFunction;
import org.hibernate.dialect.function.VarArgsSQLFunction;
import org.hibernate.dialect.pagination.AbstractLimitHandler;
import org.hibernate.dialect.pagination.LimitHandler;
import org.hibernate.dialect.pagination.LimitHelper;
import org.hibernate.engine.spi.RowSelection;
import org.hibernate.tool.schema.extract.internal.SequenceNameExtractorImpl;
import org.hibernate.tool.schema.extract.spi.SequenceInformationExtractor;
import org.hibernate.type.StandardBasicTypes;

public class InterbaseDialect
extends Dialect {
    private static final AbstractLimitHandler LIMIT_HANDLER = new AbstractLimitHandler(){

        @Override
        public String processSql(String sql, RowSelection selection) {
            boolean hasOffset = LimitHelper.hasFirstRow(selection);
            return hasOffset ? sql + " rows ? to ?" : sql + " rows ?";
        }

        @Override
        public boolean supportsLimit() {
            return true;
        }
    };

    public InterbaseDialect() {
        this.registerColumnType(-7, "smallint");
        this.registerColumnType(-5, "numeric(18,0)");
        this.registerColumnType(5, "smallint");
        this.registerColumnType(-6, "smallint");
        this.registerColumnType(4, "integer");
        this.registerColumnType(1, "char(1)");
        this.registerColumnType(12, "varchar($l)");
        this.registerColumnType(6, "float");
        this.registerColumnType(8, "double precision");
        this.registerColumnType(91, "date");
        this.registerColumnType(92, "time");
        this.registerColumnType(93, "timestamp");
        this.registerColumnType(-3, "blob");
        this.registerColumnType(2, "numeric($p,$s)");
        this.registerColumnType(2004, "blob");
        this.registerColumnType(2005, "blob sub_type 1");
        this.registerColumnType(16, "smallint");
        this.registerFunction("concat", new VarArgsSQLFunction(StandardBasicTypes.STRING, "(", "||", ")"));
        this.registerFunction("current_date", new NoArgSQLFunction("current_date", StandardBasicTypes.DATE, false));
        this.getDefaultProperties().setProperty("hibernate.jdbc.batch_size", "0");
    }

    @Override
    public String getAddColumnString() {
        return "add";
    }

    @Override
    public String getSequenceNextValString(String sequenceName) {
        return "select " + this.getSelectSequenceNextValString(sequenceName) + " from rdb$database";
    }

    @Override
    public String getSelectSequenceNextValString(String sequenceName) {
        return "gen_id( " + sequenceName + ", 1 )";
    }

    @Override
    public String getCreateSequenceString(String sequenceName) {
        return "create generator " + sequenceName;
    }

    @Override
    public String getDropSequenceString(String sequenceName) {
        return "delete from rdb$generators where rdb$generator_name = '" + sequenceName.toUpperCase(Locale.ROOT) + "'";
    }

    @Override
    public String getQuerySequencesString() {
        return "select rdb$generator_name from rdb$generators";
    }

    @Override
    public SequenceInformationExtractor getSequenceInformationExtractor() {
        return SequenceNameExtractorImpl.INSTANCE;
    }

    @Override
    public String getForUpdateString() {
        return " with lock";
    }

    @Override
    public String getForUpdateString(String aliases) {
        return " for update of " + aliases + " with lock";
    }

    @Override
    public boolean supportsSequences() {
        return true;
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
        return hasOffset ? sql + " rows ? to ?" : sql + " rows ?";
    }

    @Override
    public boolean bindLimitParametersFirst() {
        return false;
    }

    @Override
    public boolean bindLimitParametersInReverseOrder() {
        return false;
    }

    @Override
    public String getCurrentTimestampSelectString() {
        return "{?= call current_timestamp }";
    }

    @Override
    public boolean isCurrentTimestampSelectStringCallable() {
        return true;
    }
}

