/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.db.HibernateConfig
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Joiner
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 *  org.apache.commons.lang3.StringUtils
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.upgrade.dml;

import com.atlassian.config.db.HibernateConfig;
import com.atlassian.confluence.upgrade.dml.DmlCommand;
import com.atlassian.confluence.upgrade.dml.DmlStatement;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.Nullable;

public class SelectWithOffsetLimitCommand
implements DmlCommand {
    private static final Pattern ALIASED_COLUMN_PATTERN = Pattern.compile("\\s+as\\s+(\\S+)$", 2);
    private static final Pattern QUALIFIED_COLUMN_PATTERN = Pattern.compile("^[^\\.\\s]+\\.(.+)");
    private final HibernateConfig hibernateConfig;
    private final @Nullable String insertStatement;
    private final Iterable<String> selectColumns;
    private final String fromClause;
    private final @Nullable String whereClause;
    private final @Nullable String groupByClause;
    private final @Nullable String havingClause;
    private final String orderByClause;
    private final long offset;
    private final @Nullable Long limit;
    private final Object[] arguments;

    private SelectWithOffsetLimitCommand(HibernateConfig hibernateConfig, @Nullable String insertStatement, Iterable<String> selectColumns, String fromClause, @Nullable String whereClause, @Nullable String groupByClause, @Nullable String havingClause, String orderByClause, long offset, @Nullable Long limit, Object[] arguments) {
        this.hibernateConfig = hibernateConfig;
        this.insertStatement = insertStatement;
        this.selectColumns = selectColumns;
        this.fromClause = fromClause;
        this.whereClause = whereClause;
        this.groupByClause = groupByClause;
        this.havingClause = havingClause;
        this.orderByClause = orderByClause;
        this.offset = offset;
        this.limit = limit;
        this.arguments = arguments;
    }

    @VisibleForTesting
    @Nullable String getInsertStatement() {
        return this.insertStatement;
    }

    @VisibleForTesting
    Iterable<String> getSelectColumns() {
        return this.selectColumns;
    }

    @VisibleForTesting
    String getFromClause() {
        return this.fromClause;
    }

    @VisibleForTesting
    @Nullable String getWhereClause() {
        return this.whereClause;
    }

    @VisibleForTesting
    @Nullable String getGroupByClause() {
        return this.groupByClause;
    }

    @VisibleForTesting
    @Nullable String getHavingClause() {
        return this.havingClause;
    }

    @VisibleForTesting
    String getOrderByClause() {
        return this.orderByClause;
    }

    @VisibleForTesting
    long getOffset() {
        return this.offset;
    }

    @VisibleForTesting
    @Nullable Long getLimit() {
        return this.limit;
    }

    @VisibleForTesting
    Object[] getArguments() {
        return this.arguments;
    }

    @Override
    public DmlStatement getStatement() {
        if (this.hibernateConfig.isSqlServer()) {
            return new DmlStatement(this.getSqlServerStyleStatement(), this.arguments);
        }
        if (this.hibernateConfig.isOracle()) {
            return new DmlStatement(this.getOracleStyleStatement(), this.arguments);
        }
        if (this.hibernateConfig.isMySql() || this.hibernateConfig.isPostgreSql() || this.hibernateConfig.isHSQL() || this.hibernateConfig.isH2()) {
            return new DmlStatement(this.getOffsetLimitStyleStatement(), this.arguments);
        }
        throw new UnsupportedOperationException("Unsupported database dialect");
    }

    private String getSelectSqlWithoutLimit() {
        String sql = "select " + this.getCommaSeparatedSelectColumns() + "\nfrom " + this.fromClause + "\n";
        if (StringUtils.isNotEmpty((CharSequence)this.whereClause)) {
            sql = sql + "where " + this.whereClause + "\n";
        }
        if (StringUtils.isNotEmpty((CharSequence)this.groupByClause)) {
            sql = sql + "group by " + this.groupByClause + "\n";
        }
        if (StringUtils.isNotEmpty((CharSequence)this.havingClause)) {
            sql = sql + "having " + this.havingClause + "\n";
        }
        sql = sql + "order by " + this.orderByClause;
        return sql;
    }

    private String getInsertSelectSqlWithoutLimit() {
        Object sql = "";
        if (StringUtils.isNotEmpty((CharSequence)this.insertStatement)) {
            sql = (String)sql + "insert into " + this.insertStatement + "\n";
        }
        sql = (String)sql + this.getSelectSqlWithoutLimit();
        return sql;
    }

    private String getOffsetLimitStyleStatement() {
        String sql = this.getInsertSelectSqlWithoutLimit() + "\n";
        if (this.limit != null && this.offset != 0L) {
            sql = sql + "limit " + this.limit + " offset " + this.offset;
        }
        if (this.limit != null && this.offset == 0L) {
            sql = sql + "limit " + this.limit;
        }
        if (this.limit == null && this.offset != 0L) {
            sql = sql + "offset " + this.offset;
        }
        return sql.trim();
    }

    private String getSqlServerStyleStatement() {
        if (this.offset == 0L && this.limit == null) {
            return this.getInsertSelectSqlWithoutLimit();
        }
        String sql = "with limitedcte as (\nselect " + this.getCommaSeparatedSelectColumns() + ", row_number() over (order by " + this.orderByClause + ") as row_num\n";
        sql = sql + "from " + this.fromClause + "\n";
        if (StringUtils.isNotEmpty((CharSequence)this.whereClause)) {
            sql = sql + "where " + this.whereClause + "\n";
        }
        if (StringUtils.isNotEmpty((CharSequence)this.groupByClause)) {
            sql = sql + "group by " + this.groupByClause + "\n";
        }
        if (StringUtils.isNotEmpty((CharSequence)this.havingClause)) {
            sql = sql + "having " + this.havingClause + "\n";
        }
        sql = sql + ")\n";
        if (StringUtils.isNotEmpty((CharSequence)this.insertStatement)) {
            sql = sql + "insert into " + this.insertStatement + "\n";
        }
        sql = sql + "select " + this.getCommaSeparatedSelectColumnsForOuterSelect() + " from limitedcte\n";
        sql = this.offset != 0L && this.limit != null ? sql + "where row_num between " + SelectWithOffsetLimitCommand.getMinRowNumber(this.offset) + " and " + SelectWithOffsetLimitCommand.getMaxRowNumber(this.offset, this.limit) : (this.offset != 0L ? sql + "where row_num >= " + SelectWithOffsetLimitCommand.getMinRowNumber(this.offset) : sql + "where row_num <= " + SelectWithOffsetLimitCommand.getMaxRowNumber(this.offset, this.limit));
        return sql;
    }

    private String getOracleStyleStatement() {
        if (this.offset == 0L && this.limit == null) {
            return this.getInsertSelectSqlWithoutLimit();
        }
        Object sql = "";
        if (StringUtils.isNotEmpty((CharSequence)this.insertStatement)) {
            sql = (String)sql + "insert into " + this.insertStatement + "\n";
        }
        sql = (String)sql + "select " + this.getCommaSeparatedSelectColumnsForOuterSelect() + " from (\n";
        sql = (String)sql + "select b.*, rownum b_rownum from (\n";
        sql = (String)sql + this.getSelectSqlWithoutLimit() + "\n";
        sql = (String)sql + ") b\n";
        if (this.limit != null) {
            sql = (String)sql + "where rownum <= " + SelectWithOffsetLimitCommand.getMaxRowNumber(this.offset, this.limit) + "\n";
        }
        sql = (String)sql + ") a\n";
        sql = (String)sql + "where b_rownum >= " + SelectWithOffsetLimitCommand.getMinRowNumber(this.offset);
        return sql;
    }

    private static long getMaxRowNumber(long offset, long limit) {
        return offset + limit;
    }

    private static long getMinRowNumber(long offset) {
        return offset + 1L;
    }

    private String getCommaSeparatedSelectColumns() {
        return Joiner.on((String)", ").join(this.selectColumns);
    }

    private String getCommaSeparatedSelectColumnsForOuterSelect() {
        return Joiner.on((String)", ").join(Iterables.transform(this.selectColumns, input -> {
            Matcher aliasedColumnMatcher = ALIASED_COLUMN_PATTERN.matcher((CharSequence)input);
            if (aliasedColumnMatcher.find()) {
                return aliasedColumnMatcher.group(1);
            }
            Matcher qualifiedColumnMatcher = QUALIFIED_COLUMN_PATTERN.matcher((CharSequence)input);
            if (qualifiedColumnMatcher.find()) {
                return qualifiedColumnMatcher.group(1);
            }
            return input;
        }));
    }

    public static class Builder {
        private static final ImmutableList<String> DEFAULT_SELECT_COLUMNS = ImmutableList.of((Object)"*");
        private final HibernateConfig hibernateConfig;
        private String insertStatement = null;
        private Iterable<String> selectColumns = DEFAULT_SELECT_COLUMNS;
        private String fromClause = null;
        private String whereClause = null;
        private String groupByClause;
        private String havingClause = null;
        private String orderByClause = null;
        private long offset = 0L;
        private Long limit = null;
        private Object[] arguments = new Object[0];

        public Builder(HibernateConfig hibernateConfig) {
            this.hibernateConfig = hibernateConfig;
        }

        public Builder insertInto(String insertStatement) {
            this.insertStatement = insertStatement;
            return this;
        }

        public Builder select(String ... selectColumns) {
            if (selectColumns == null || selectColumns.length == 0) {
                this.selectColumns = DEFAULT_SELECT_COLUMNS;
                return this;
            }
            this.selectColumns = ImmutableList.copyOf((Object[])selectColumns);
            return this;
        }

        public Builder select(Iterable<String> selectColumns) {
            if (selectColumns == null || !selectColumns.iterator().hasNext()) {
                this.selectColumns = DEFAULT_SELECT_COLUMNS;
                return this;
            }
            this.selectColumns = selectColumns;
            return this;
        }

        public Builder from(String fromClause) {
            this.validateFromClause(fromClause);
            this.fromClause = fromClause;
            return this;
        }

        public Builder where(String whereClause) {
            this.whereClause = whereClause;
            return this;
        }

        public Builder groupBy(String groupByClause) {
            this.groupByClause = groupByClause;
            return this;
        }

        public Builder having(String havingClause) {
            this.havingClause = havingClause;
            return this;
        }

        public Builder orderBy(String orderByClause) {
            this.validateOrderByClause(orderByClause);
            this.orderByClause = orderByClause;
            return this;
        }

        public Builder offset(long offset) {
            this.offset = offset;
            return this;
        }

        public Builder limit(long limit) {
            this.limit = limit;
            return this;
        }

        public Builder arguments(Object ... arguments) {
            if (arguments == null) {
                this.arguments = new Object[0];
            }
            this.arguments = arguments;
            return this;
        }

        public SelectWithOffsetLimitCommand build() {
            this.validateFromClause(this.fromClause);
            this.validateOrderByClause(this.orderByClause);
            return new SelectWithOffsetLimitCommand(this.hibernateConfig, this.insertStatement, this.selectColumns, this.fromClause, this.whereClause, this.groupByClause, this.havingClause, this.orderByClause, this.offset, this.limit, this.arguments);
        }

        private void validateFromClause(String fromClause) {
            if (StringUtils.isBlank((CharSequence)fromClause)) {
                throw new IllegalArgumentException("Cannot have select statement with empty from clause");
            }
        }

        private void validateOrderByClause(String orderByClause) {
            if (StringUtils.isBlank((CharSequence)orderByClause)) {
                throw new IllegalArgumentException("Cannot have select statement with empty order by clause because SQL Server and Oracle require it.");
            }
        }
    }
}

