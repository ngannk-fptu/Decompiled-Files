/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.CharMatcher
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 */
package com.querydsl.sql;

import com.google.common.base.CharMatcher;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.querydsl.core.JoinType;
import com.querydsl.core.QueryException;
import com.querydsl.core.QueryFlag;
import com.querydsl.core.QueryMetadata;
import com.querydsl.core.QueryModifiers;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Operator;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.Templates;
import com.querydsl.sql.Keywords;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.SQLOps;
import com.querydsl.sql.SQLSerializer;
import com.querydsl.sql.SchemaAndTable;
import com.querydsl.sql.dml.SQLInsertBatch;
import com.querydsl.sql.types.Type;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SQLTemplates
extends Templates {
    protected static final Expression<?> FOR_SHARE = ExpressionUtils.operation(Object.class, (Operator)SQLOps.FOR_SHARE, ImmutableList.of());
    protected static final Expression<?> FOR_UPDATE = ExpressionUtils.operation(Object.class, (Operator)SQLOps.FOR_UPDATE, ImmutableList.of());
    protected static final Expression<?> NO_WAIT = ExpressionUtils.operation(Object.class, (Operator)SQLOps.NO_WAIT, ImmutableList.of());
    protected static final int TIME_WITH_TIMEZONE = 2013;
    protected static final int TIMESTAMP_WITH_TIMEZONE = 2014;
    public static final Expression<?> RECURSIVE = ExpressionUtils.template(Object.class, "", new Object[0]);
    public static final SQLTemplates DEFAULT = new SQLTemplates("\"", '\\', false);
    protected static final Set<? extends Operator> OTHER_LIKE_CASES = Sets.immutableEnumSet((Enum)Ops.ENDS_WITH, (Enum[])new Ops[]{Ops.ENDS_WITH_IC, Ops.LIKE_IC, Ops.LIKE_ESCAPE_IC, Ops.STARTS_WITH, Ops.STARTS_WITH_IC, Ops.STRING_CONTAINS, Ops.STRING_CONTAINS_IC});
    private static final CharMatcher NON_UNDERSCORE_ALPHA_NUMERIC = CharMatcher.is((char)'_').or(CharMatcher.inRange((char)'a', (char)'z').or(CharMatcher.inRange((char)'A', (char)'Z'))).or(CharMatcher.inRange((char)'0', (char)'9')).negate().precomputed();
    private static final CharMatcher NON_UNDERSCORE_ALPHA = CharMatcher.is((char)'_').or(CharMatcher.inRange((char)'a', (char)'z').or(CharMatcher.inRange((char)'A', (char)'Z'))).negate().precomputed();
    private final Set<String> reservedWords;
    private final Map<String, Integer> typeNameToCode = Maps.newHashMap();
    private final Map<Integer, String> codeToTypeName = Maps.newHashMap();
    private final Map<SchemaAndTable, SchemaAndTable> tableOverrides = Maps.newHashMap();
    private final List<Type<?>> customTypes = Lists.newArrayList();
    private final String quoteStr;
    private final boolean useQuotes;
    private boolean printSchema;
    private String createTable = "create table ";
    private String asc = " asc";
    private String autoIncrement = " auto_increment";
    private String columnAlias = " ";
    private String count = "count ";
    private String countStar = "count(*)";
    private String crossJoin = ", ";
    private String delete = "delete ";
    private String desc = " desc";
    private String distinctCountEnd = ")";
    private String distinctCountStart = "count(distinct ";
    private String dummyTable = "dual";
    private String from = "\nfrom ";
    private String fullJoin = "\nfull join ";
    private String groupBy = "\ngroup by ";
    private String having = "\nhaving ";
    private String innerJoin = "\ninner join ";
    private String insertInto = "insert into ";
    private String join = "\njoin ";
    private String key = "key";
    private String leftJoin = "\nleft join ";
    private String rightJoin = "\nright join ";
    private String limitTemplate = "\nlimit {0}";
    private String mergeInto = "merge into ";
    private boolean nativeMerge;
    private String notNull = " not null";
    private String offsetTemplate = "\noffset {0}";
    private String on = "\non ";
    private String orderBy = "\norder by ";
    private String select = "select ";
    private String selectDistinct = "select distinct ";
    private String set = "set ";
    private String tableAlias = " ";
    private String update = "update ";
    private String values = "\nvalues ";
    private String defaultValues = "\nvalues ()";
    private String where = "\nwhere ";
    private String with = "with ";
    private String withRecursive = "with recursive ";
    private String createIndex = "create index ";
    private String createUniqueIndex = "create unique index ";
    private String nullsFirst = " nulls first";
    private String nullsLast = " nulls last";
    private boolean parameterMetadataAvailable = true;
    private boolean batchCountViaGetUpdateCount = false;
    private boolean unionsWrapped = true;
    private boolean functionJoinsWrapped = false;
    private boolean limitRequired = false;
    private boolean countDistinctMultipleColumns = false;
    private boolean countViaAnalytics = false;
    private boolean wrapSelectParameters = false;
    private boolean arraysSupported = true;
    private boolean forShareSupported = false;
    private boolean batchToBulkSupported = true;
    private int listMaxSize = 0;
    private boolean supportsUnquotedReservedWordsAsIdentifier = false;
    private int maxLimit = Integer.MAX_VALUE;
    private QueryFlag forShareFlag = new QueryFlag(QueryFlag.Position.END, FOR_SHARE);
    private QueryFlag forUpdateFlag = new QueryFlag(QueryFlag.Position.END, FOR_UPDATE);
    private QueryFlag noWaitFlag = new QueryFlag(QueryFlag.Position.END, NO_WAIT);

    @Deprecated
    protected SQLTemplates(String quoteStr, char escape, boolean useQuotes) {
        this(Keywords.DEFAULT, quoteStr, escape, useQuotes);
    }

    protected SQLTemplates(Set<String> reservedKeywords, String quoteStr, char escape, boolean useQuotes) {
        super(escape);
        this.reservedWords = reservedKeywords;
        this.quoteStr = quoteStr;
        this.useQuotes = useQuotes;
        this.add(SQLOps.ALL, "{0}.*");
        this.add(SQLOps.WITH_ALIAS, "{0} as {1}", 0);
        this.add(SQLOps.WITH_COLUMNS, "{0} {1}", 0);
        this.add(SQLOps.FOR_UPDATE, "\nfor update");
        this.add(SQLOps.FOR_SHARE, "\nfor share");
        this.add(SQLOps.NO_WAIT, " nowait");
        this.add(SQLOps.QUALIFY, "\nqualify {0}");
        this.add(Ops.AND, "{0} and {1}");
        this.add(Ops.NOT, "not {0}", 80);
        this.add(Ops.OR, "{0} or {1}");
        this.add(Ops.MathOps.RANDOM, "rand()");
        this.add(Ops.MathOps.RANDOM2, "rand({0})");
        this.add(Ops.MathOps.CEIL, "ceiling({0})");
        this.add(Ops.MathOps.POWER, "power({0},{1})");
        this.add(Ops.MOD, "mod({0},{1})", -1);
        this.add(Ops.DateTimeOps.CURRENT_DATE, "current_date");
        this.add(Ops.DateTimeOps.CURRENT_TIME, "current_time");
        this.add(Ops.DateTimeOps.CURRENT_TIMESTAMP, "current_timestamp");
        this.add(Ops.DateTimeOps.MILLISECOND, "0");
        this.add(Ops.DateTimeOps.SECOND, "extract(second from {0})");
        this.add(Ops.DateTimeOps.MINUTE, "extract(minute from {0})");
        this.add(Ops.DateTimeOps.HOUR, "extract(hour from {0})");
        this.add(Ops.DateTimeOps.WEEK, "extract(week from {0})");
        this.add(Ops.DateTimeOps.MONTH, "extract(month from {0})");
        this.add(Ops.DateTimeOps.YEAR, "extract(year from {0})");
        this.add(Ops.DateTimeOps.YEAR_MONTH, "extract(year from {0}) * 100 + extract(month from {0})", 40);
        this.add(Ops.DateTimeOps.YEAR_WEEK, "extract(year from {0}) * 100 + extract(week from {0})", 40);
        this.add(Ops.DateTimeOps.DAY_OF_WEEK, "extract(day_of_week from {0})");
        this.add(Ops.DateTimeOps.DAY_OF_MONTH, "extract(day from {0})");
        this.add(Ops.DateTimeOps.DAY_OF_YEAR, "extract(day_of_year from {0})");
        this.add(Ops.DateTimeOps.ADD_YEARS, "dateadd('year',{1},{0})");
        this.add(Ops.DateTimeOps.ADD_MONTHS, "dateadd('month',{1},{0})");
        this.add(Ops.DateTimeOps.ADD_WEEKS, "dateadd('week',{1},{0})");
        this.add(Ops.DateTimeOps.ADD_DAYS, "dateadd('day',{1},{0})");
        this.add(Ops.DateTimeOps.ADD_HOURS, "dateadd('hour',{1},{0})");
        this.add(Ops.DateTimeOps.ADD_MINUTES, "dateadd('minute',{1},{0})");
        this.add(Ops.DateTimeOps.ADD_SECONDS, "dateadd('second',{1},{0})");
        this.add(Ops.DateTimeOps.DIFF_YEARS, "datediff('year',{0},{1})");
        this.add(Ops.DateTimeOps.DIFF_MONTHS, "datediff('month',{0},{1})");
        this.add(Ops.DateTimeOps.DIFF_WEEKS, "datediff('week',{0},{1})");
        this.add(Ops.DateTimeOps.DIFF_DAYS, "datediff('day',{0},{1})");
        this.add(Ops.DateTimeOps.DIFF_HOURS, "datediff('hour',{0},{1})");
        this.add(Ops.DateTimeOps.DIFF_MINUTES, "datediff('minute',{0},{1})");
        this.add(Ops.DateTimeOps.DIFF_SECONDS, "datediff('second',{0},{1})");
        this.add(Ops.DateTimeOps.TRUNC_YEAR, "date_trunc('year',{0})");
        this.add(Ops.DateTimeOps.TRUNC_MONTH, "date_trunc('month',{0})");
        this.add(Ops.DateTimeOps.TRUNC_WEEK, "date_trunc('week',{0})");
        this.add(Ops.DateTimeOps.TRUNC_DAY, "date_trunc('day',{0})");
        this.add(Ops.DateTimeOps.TRUNC_HOUR, "date_trunc('hour',{0})");
        this.add(Ops.DateTimeOps.TRUNC_MINUTE, "date_trunc('minute',{0})");
        this.add(Ops.DateTimeOps.TRUNC_SECOND, "date_trunc('second',{0})");
        this.add(Ops.CONCAT, "{0} || {1}", 40);
        this.add(Ops.MATCHES, "{0} regexp {1}", 50);
        this.add(Ops.CHAR_AT, "cast(substr({0},{1+'1's},1) as char)");
        this.add(Ops.EQ_IGNORE_CASE, "{0l} = {1l}");
        this.add(Ops.INDEX_OF, "locate({1},{0})-1", 40);
        this.add(Ops.INDEX_OF_2ARGS, "locate({1},{0},{2+'1's})-1", 40);
        this.add(Ops.STRING_IS_EMPTY, "length({0}) = 0");
        this.add(Ops.SUBSTR_1ARG, "substr({0},{1s}+1)", 40);
        this.add(Ops.SUBSTR_2ARGS, "substr({0},{1+'1's},{2-1s})", 40);
        this.add(Ops.StringOps.LOCATE, "locate({0},{1})");
        this.add(Ops.StringOps.LOCATE2, "locate({0},{1},{2})");
        this.add(Ops.LIKE, "{0} like {1} escape '" + escape + "'", 50);
        this.add(Ops.ENDS_WITH, "{0} like {%1} escape '" + escape + "'", 50);
        this.add(Ops.ENDS_WITH_IC, "{0l} like {%%1} escape '" + escape + "'", 50);
        this.add(Ops.STARTS_WITH, "{0} like {1%} escape '" + escape + "'", 50);
        this.add(Ops.STARTS_WITH_IC, "{0l} like {1%%} escape '" + escape + "'", 50);
        this.add(Ops.STRING_CONTAINS, "{0} like {%1%} escape '" + escape + "'", 50);
        this.add(Ops.STRING_CONTAINS_IC, "{0l} like {%%1%%} escape '" + escape + "'", 50);
        this.add(SQLOps.CAST, "cast({0} as {1s})");
        this.add(SQLOps.UNION, "{0}\nunion\n{1}", 111);
        this.add(SQLOps.UNION_ALL, "{0}\nunion all\n{1}", 111);
        this.add(SQLOps.NEXTVAL, "nextval('{0s}')");
        this.add(SQLOps.CORR, "corr({0},{1})");
        this.add(SQLOps.COVARPOP, "covar_pop({0},{1})");
        this.add(SQLOps.COVARSAMP, "covar_samp({0},{1})");
        this.add(SQLOps.CUMEDIST, "cume_dist()");
        this.add(SQLOps.CUMEDIST2, "cume_dist({0})");
        this.add(SQLOps.DENSERANK, "dense_rank()");
        this.add(SQLOps.DENSERANK2, "dense_rank({0})");
        this.add(SQLOps.FIRSTVALUE, "first_value({0})");
        this.add(SQLOps.LAG, "lag({0})");
        this.add(SQLOps.LASTVALUE, "last_value({0})");
        this.add(SQLOps.LEAD, "lead({0})");
        this.add(SQLOps.LISTAGG, "listagg({0},'{1s}')");
        this.add(SQLOps.NTHVALUE, "nth_value({0}, {1})");
        this.add(SQLOps.NTILE, "ntile({0})");
        this.add(SQLOps.PERCENTILECONT, "percentile_cont({0})");
        this.add(SQLOps.PERCENTILEDISC, "percentile_disc({0})");
        this.add(SQLOps.PERCENTRANK, "percent_rank()");
        this.add(SQLOps.PERCENTRANK2, "percent_rank({0})");
        this.add(SQLOps.RANK, "rank()");
        this.add(SQLOps.RANK2, "rank({0})");
        this.add(SQLOps.RATIOTOREPORT, "ratio_to_report({0})");
        this.add(SQLOps.REGR_SLOPE, "regr_slope({0}, {1})");
        this.add(SQLOps.REGR_INTERCEPT, "regr_intercept({0}, {1})");
        this.add(SQLOps.REGR_COUNT, "regr_count({0}, {1})");
        this.add(SQLOps.REGR_R2, "regr_r2({0}, {1})");
        this.add(SQLOps.REGR_AVGX, "regr_avgx({0}, {1})");
        this.add(SQLOps.REGR_AVGY, "regr_avgy({0}, {1})");
        this.add(SQLOps.REGR_SXX, "regr_sxx({0}, {1})");
        this.add(SQLOps.REGR_SYY, "regr_syy({0}, {1})");
        this.add(SQLOps.REGR_SXY, "regr_sxy({0}, {1})");
        this.add(SQLOps.ROWNUMBER, "row_number()");
        this.add(SQLOps.STDDEV, "stddev({0})");
        this.add(SQLOps.STDDEVPOP, "stddev_pop({0})");
        this.add(SQLOps.STDDEVSAMP, "stddev_samp({0})");
        this.add(SQLOps.STDDEV_DISTINCT, "stddev(distinct {0})");
        this.add(SQLOps.VARIANCE, "variance({0})");
        this.add(SQLOps.VARPOP, "var_pop({0})");
        this.add(SQLOps.VARSAMP, "var_samp({0})");
        this.add(SQLOps.GROUP_CONCAT, "group_concat({0})");
        this.add(SQLOps.GROUP_CONCAT2, "group_concat({0} separator {1})");
        this.add(Ops.AggOps.BOOLEAN_ANY, "some({0})");
        this.add(Ops.AggOps.BOOLEAN_ALL, "every({0})");
        this.add(SQLOps.SET_LITERAL, "{0} = {1}");
        this.add(SQLOps.SET_PATH, "{0} = values({1})");
        this.addTypeNameToCode("null", 0);
        this.addTypeNameToCode("char", 1);
        this.addTypeNameToCode("datalink", 70);
        this.addTypeNameToCode("numeric", 2);
        this.addTypeNameToCode("decimal", 3);
        this.addTypeNameToCode("integer", 4);
        this.addTypeNameToCode("smallint", 5);
        this.addTypeNameToCode("float", 6);
        this.addTypeNameToCode("real", 7);
        this.addTypeNameToCode("double", 8);
        this.addTypeNameToCode("varchar", 12);
        this.addTypeNameToCode("longnvarchar", -16);
        this.addTypeNameToCode("nchar", -15);
        this.addTypeNameToCode("boolean", 16);
        this.addTypeNameToCode("nvarchar", -9);
        this.addTypeNameToCode("rowid", -8);
        this.addTypeNameToCode("timestamp", 93);
        this.addTypeNameToCode("timestamp", 2014);
        this.addTypeNameToCode("bit", -7);
        this.addTypeNameToCode("time", 92);
        this.addTypeNameToCode("time", 2013);
        this.addTypeNameToCode("tinyint", -6);
        this.addTypeNameToCode("other", 1111);
        this.addTypeNameToCode("bigint", -5);
        this.addTypeNameToCode("longvarbinary", -4);
        this.addTypeNameToCode("varbinary", -3);
        this.addTypeNameToCode("date", 91);
        this.addTypeNameToCode("binary", -2);
        this.addTypeNameToCode("longvarchar", -1);
        this.addTypeNameToCode("struct", 2002);
        this.addTypeNameToCode("array", 2003);
        this.addTypeNameToCode("java_object", 2000);
        this.addTypeNameToCode("distinct", 2001);
        this.addTypeNameToCode("ref", 2006);
        this.addTypeNameToCode("blob", 2004);
        this.addTypeNameToCode("clob", 2005);
        this.addTypeNameToCode("nclob", 2011);
        this.addTypeNameToCode("sqlxml", 2009);
    }

    public String serialize(String literal, int jdbcType) {
        switch (jdbcType) {
            case 93: 
            case 2014: {
                return "(timestamp '" + literal + "')";
            }
            case 91: {
                return "(date '" + literal + "')";
            }
            case 92: 
            case 2013: {
                return "(time '" + literal + "')";
            }
            case -16: 
            case -15: 
            case -9: 
            case -1: 
            case 1: 
            case 12: 
            case 2005: 
            case 2011: {
                return "'" + this.escapeLiteral(literal) + "'";
            }
            case -7: 
            case -6: 
            case -5: 
            case 0: 
            case 2: 
            case 3: 
            case 4: 
            case 5: 
            case 6: 
            case 8: 
            case 16: {
                return literal;
            }
        }
        return literal;
    }

    public String escapeLiteral(String str) {
        StringBuilder builder = new StringBuilder();
        for (char ch : str.toCharArray()) {
            if (ch == '\'') {
                builder.append("''");
                continue;
            }
            builder.append(ch);
        }
        return builder.toString();
    }

    protected void addTypeNameToCode(String type, int code, boolean override) {
        if (!this.typeNameToCode.containsKey(type)) {
            this.typeNameToCode.put(type, code);
        }
        if (override || !this.codeToTypeName.containsKey(code)) {
            this.codeToTypeName.put(code, type);
        }
    }

    protected void addTypeNameToCode(String type, int code) {
        this.addTypeNameToCode(type, code, false);
    }

    protected void addTableOverride(SchemaAndTable from, SchemaAndTable to) {
        this.tableOverrides.put(from, to);
    }

    public final List<Type<?>> getCustomTypes() {
        return this.customTypes;
    }

    public final String getAsc() {
        return this.asc;
    }

    public final String getAutoIncrement() {
        return this.autoIncrement;
    }

    public final String getColumnAlias() {
        return this.columnAlias;
    }

    public final String getCount() {
        return this.count;
    }

    public final String getCountStar() {
        return this.countStar;
    }

    public final String getCrossJoin() {
        return this.crossJoin;
    }

    public final String getDelete() {
        return this.delete;
    }

    public final String getDesc() {
        return this.desc;
    }

    public final String getDistinctCountEnd() {
        return this.distinctCountEnd;
    }

    public final String getDistinctCountStart() {
        return this.distinctCountStart;
    }

    public final String getDummyTable() {
        return this.dummyTable;
    }

    public final String getFrom() {
        return this.from;
    }

    public final String getFullJoin() {
        return this.fullJoin;
    }

    public final String getGroupBy() {
        return this.groupBy;
    }

    public final String getHaving() {
        return this.having;
    }

    public final String getInnerJoin() {
        return this.innerJoin;
    }

    public final String getInsertInto() {
        return this.insertInto;
    }

    public final String getJoin() {
        return this.join;
    }

    public final String getJoinSymbol(JoinType joinType) {
        switch (joinType) {
            case JOIN: {
                return this.join;
            }
            case INNERJOIN: {
                return this.innerJoin;
            }
            case FULLJOIN: {
                return this.fullJoin;
            }
            case LEFTJOIN: {
                return this.leftJoin;
            }
            case RIGHTJOIN: {
                return this.rightJoin;
            }
        }
        return this.crossJoin;
    }

    public final String getKey() {
        return this.key;
    }

    public final String getLeftJoin() {
        return this.leftJoin;
    }

    public final String getRightJoin() {
        return this.rightJoin;
    }

    public final String getLimitTemplate() {
        return this.limitTemplate;
    }

    public final String getMergeInto() {
        return this.mergeInto;
    }

    public final String getNotNull() {
        return this.notNull;
    }

    public final String getOffsetTemplate() {
        return this.offsetTemplate;
    }

    public final String getOn() {
        return this.on;
    }

    public final String getOrderBy() {
        return this.orderBy;
    }

    public final String getSelect() {
        return this.select;
    }

    public final String getSelectDistinct() {
        return this.selectDistinct;
    }

    public final String getSet() {
        return this.set;
    }

    public final String getTableAlias() {
        return this.tableAlias;
    }

    public final Map<SchemaAndTable, SchemaAndTable> getTableOverrides() {
        return this.tableOverrides;
    }

    public String getTypeNameForCode(int code) {
        return this.codeToTypeName.get(code);
    }

    public String getCastTypeNameForCode(int code) {
        return this.getTypeNameForCode(code);
    }

    public Integer getCodeForTypeName(String type) {
        return this.typeNameToCode.get(type);
    }

    public final String getUpdate() {
        return this.update;
    }

    public final String getValues() {
        return this.values;
    }

    public final String getDefaultValues() {
        return this.defaultValues;
    }

    public final String getWhere() {
        return this.where;
    }

    public final boolean isNativeMerge() {
        return this.nativeMerge;
    }

    public final boolean isSupportsAlias() {
        return true;
    }

    public final String getCreateIndex() {
        return this.createIndex;
    }

    public final String getCreateUniqueIndex() {
        return this.createUniqueIndex;
    }

    public final String getCreateTable() {
        return this.createTable;
    }

    public final String getWith() {
        return this.with;
    }

    public final String getWithRecursive() {
        return this.withRecursive;
    }

    public final boolean isCountDistinctMultipleColumns() {
        return this.countDistinctMultipleColumns;
    }

    public final boolean isPrintSchema() {
        return this.printSchema;
    }

    public final boolean isParameterMetadataAvailable() {
        return this.parameterMetadataAvailable;
    }

    public final boolean isBatchCountViaGetUpdateCount() {
        return this.batchCountViaGetUpdateCount;
    }

    public final boolean isUseQuotes() {
        return this.useQuotes;
    }

    public final boolean isUnionsWrapped() {
        return this.unionsWrapped;
    }

    public boolean isForShareSupported() {
        return this.forShareSupported;
    }

    public final boolean isFunctionJoinsWrapped() {
        return this.functionJoinsWrapped;
    }

    public final boolean isLimitRequired() {
        return this.limitRequired;
    }

    public final String getNullsFirst() {
        return this.nullsFirst;
    }

    public final String getNullsLast() {
        return this.nullsLast;
    }

    public final boolean isCountViaAnalytics() {
        return this.countViaAnalytics;
    }

    public final boolean isWrapSelectParameters() {
        return this.wrapSelectParameters;
    }

    public final boolean isArraysSupported() {
        return this.arraysSupported;
    }

    public final int getListMaxSize() {
        return this.listMaxSize;
    }

    public final boolean isSupportsUnquotedReservedWordsAsIdentifier() {
        return this.supportsUnquotedReservedWordsAsIdentifier;
    }

    public final boolean isBatchToBulkSupported() {
        return this.batchToBulkSupported;
    }

    public final QueryFlag getForShareFlag() {
        return this.forShareFlag;
    }

    public final QueryFlag getForUpdateFlag() {
        return this.forUpdateFlag;
    }

    public final QueryFlag getNoWaitFlag() {
        return this.noWaitFlag;
    }

    protected void newLineToSingleSpace() {
        for (Class cl : Arrays.asList(this.getClass(), SQLTemplates.class)) {
            for (Field field : cl.getDeclaredFields()) {
                try {
                    if (!field.getType().equals(String.class)) continue;
                    field.setAccessible(true);
                    Object val = field.get(this);
                    if (val == null) continue;
                    field.set(this, val.toString().replace('\n', ' '));
                }
                catch (IllegalAccessException e) {
                    throw new QueryException(e.getMessage(), e);
                }
            }
        }
    }

    public final String quoteIdentifier(String identifier) {
        return this.quoteIdentifier(identifier, false);
    }

    public final String quoteIdentifier(String identifier, boolean precededByDot) {
        if (this.useQuotes || this.requiresQuotes(identifier, precededByDot)) {
            return this.quoteStr + identifier + this.quoteStr;
        }
        return identifier;
    }

    protected boolean requiresQuotes(String identifier, boolean precededByDot) {
        if (NON_UNDERSCORE_ALPHA_NUMERIC.matchesAnyOf((CharSequence)identifier)) {
            return true;
        }
        if (NON_UNDERSCORE_ALPHA.matches(identifier.charAt(0))) {
            return true;
        }
        if (precededByDot && this.supportsUnquotedReservedWordsAsIdentifier) {
            return false;
        }
        return this.isReservedWord(identifier);
    }

    private boolean isReservedWord(String identifier) {
        return this.reservedWords.contains(identifier.toUpperCase());
    }

    public void serialize(QueryMetadata metadata, boolean forCountRow, SQLSerializer context) {
        context.serializeForQuery(metadata, forCountRow);
        if (!metadata.getFlags().isEmpty()) {
            context.serialize(QueryFlag.Position.END, metadata.getFlags());
        }
    }

    public void serializeDelete(QueryMetadata metadata, RelationalPath<?> entity, SQLSerializer context) {
        context.serializeForDelete(metadata, entity);
        if (metadata.getModifiers().isRestricting()) {
            this.serializeModifiers(metadata, context);
        }
        if (!metadata.getFlags().isEmpty()) {
            context.serialize(QueryFlag.Position.END, metadata.getFlags());
        }
    }

    public void serializeInsert(QueryMetadata metadata, RelationalPath<?> entity, List<Path<?>> columns, List<Expression<?>> values, SubQueryExpression<?> subQuery, SQLSerializer context) {
        context.serializeForInsert(metadata, entity, columns, values, subQuery);
        if (!metadata.getFlags().isEmpty()) {
            context.serialize(QueryFlag.Position.END, metadata.getFlags());
        }
    }

    public void serializeInsert(QueryMetadata metadata, RelationalPath<?> entity, List<SQLInsertBatch> batches, SQLSerializer context) {
        context.serializeForInsert(metadata, entity, batches);
        if (!metadata.getFlags().isEmpty()) {
            context.serialize(QueryFlag.Position.END, metadata.getFlags());
        }
    }

    public void serializeMerge(QueryMetadata metadata, RelationalPath<?> entity, List<Path<?>> keys, List<Path<?>> columns, List<Expression<?>> values, SubQueryExpression<?> subQuery, SQLSerializer context) {
        context.serializeForMerge(metadata, entity, keys, columns, values, subQuery);
        if (!metadata.getFlags().isEmpty()) {
            context.serialize(QueryFlag.Position.END, metadata.getFlags());
        }
    }

    public void serializeUpdate(QueryMetadata metadata, RelationalPath<?> entity, Map<Path<?>, Expression<?>> updates, SQLSerializer context) {
        context.serializeForUpdate(metadata, entity, updates);
        if (metadata.getModifiers().isRestricting()) {
            this.serializeModifiers(metadata, context);
        }
        if (!metadata.getFlags().isEmpty()) {
            context.serialize(QueryFlag.Position.END, metadata.getFlags());
        }
    }

    protected void serializeModifiers(QueryMetadata metadata, SQLSerializer context) {
        QueryModifiers mod = metadata.getModifiers();
        if (mod.getLimit() != null) {
            context.handle(this.limitTemplate, mod.getLimit());
        } else if (this.limitRequired) {
            context.handle(this.limitTemplate, this.maxLimit);
        }
        if (mod.getOffset() != null) {
            context.handle(this.offsetTemplate, mod.getOffset());
        }
    }

    protected void addCustomType(Type<?> type) {
        this.customTypes.add(type);
    }

    protected void setAsc(String asc) {
        this.asc = asc;
    }

    protected void setAutoIncrement(String autoIncrement) {
        this.autoIncrement = autoIncrement;
    }

    protected void setColumnAlias(String columnAlias) {
        this.columnAlias = columnAlias;
    }

    protected void setCount(String count) {
        this.count = count;
    }

    protected void setCountStar(String countStar) {
        this.countStar = countStar;
    }

    protected void setCrossJoin(String crossJoin) {
        this.crossJoin = crossJoin;
    }

    protected void setDelete(String delete) {
        this.delete = delete;
    }

    protected void setDesc(String desc) {
        this.desc = desc;
    }

    protected void setDistinctCountEnd(String distinctCountEnd) {
        this.distinctCountEnd = distinctCountEnd;
    }

    protected void setDistinctCountStart(String distinctCountStart) {
        this.distinctCountStart = distinctCountStart;
    }

    protected void setDummyTable(String dummyTable) {
        this.dummyTable = dummyTable;
    }

    protected void setForShareSupported(boolean forShareSupported) {
        this.forShareSupported = forShareSupported;
    }

    protected void setFrom(String from) {
        this.from = from;
    }

    protected void setFullJoin(String fullJoin) {
        this.fullJoin = fullJoin;
    }

    protected void setGroupBy(String groupBy) {
        this.groupBy = groupBy;
    }

    protected void setHaving(String having) {
        this.having = having;
    }

    protected void setInnerJoin(String innerJoin) {
        this.innerJoin = innerJoin;
    }

    protected void setInsertInto(String insertInto) {
        this.insertInto = insertInto;
    }

    protected void setJoin(String join) {
        this.join = join;
    }

    protected void setKey(String key) {
        this.key = key;
    }

    protected void setLeftJoin(String leftJoin) {
        this.leftJoin = leftJoin;
    }

    protected void setRightJoin(String rightJoin) {
        this.rightJoin = rightJoin;
    }

    protected void setMergeInto(String mergeInto) {
        this.mergeInto = mergeInto;
    }

    protected void setNativeMerge(boolean nativeMerge) {
        this.nativeMerge = nativeMerge;
    }

    protected void setNotNull(String notNull) {
        this.notNull = notNull;
    }

    protected void setOffsetTemplate(String offsetTemplate) {
        this.offsetTemplate = offsetTemplate;
    }

    protected void setOn(String on) {
        this.on = on;
    }

    protected void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    protected void setSelect(String select) {
        this.select = select;
    }

    protected void setSelectDistinct(String selectDistinct) {
        this.selectDistinct = selectDistinct;
    }

    protected void setSet(String set) {
        this.set = set;
    }

    protected void setTableAlias(String tableAlias) {
        this.tableAlias = tableAlias;
    }

    protected void setUpdate(String update) {
        this.update = update;
    }

    protected void setValues(String values) {
        this.values = values;
    }

    protected void setDefaultValues(String defaultValues) {
        this.defaultValues = defaultValues;
    }

    protected void setWhere(String where) {
        this.where = where;
    }

    protected void setWith(String with) {
        this.with = with;
    }

    protected void setWithRecursive(String withRecursive) {
        this.withRecursive = withRecursive;
    }

    protected void setCreateIndex(String createIndex) {
        this.createIndex = createIndex;
    }

    protected void setCreateUniqueIndex(String createUniqueIndex) {
        this.createUniqueIndex = createUniqueIndex;
    }

    protected void setCreateTable(String createTable) {
        this.createTable = createTable;
    }

    protected void setPrintSchema(boolean printSchema) {
        this.printSchema = printSchema;
    }

    protected void setParameterMetadataAvailable(boolean parameterMetadataAvailable) {
        this.parameterMetadataAvailable = parameterMetadataAvailable;
    }

    protected void setBatchCountViaGetUpdateCount(boolean batchCountViaGetUpdateCount) {
        this.batchCountViaGetUpdateCount = batchCountViaGetUpdateCount;
    }

    protected void setUnionsWrapped(boolean unionsWrapped) {
        this.unionsWrapped = unionsWrapped;
    }

    protected void setFunctionJoinsWrapped(boolean functionJoinsWrapped) {
        this.functionJoinsWrapped = functionJoinsWrapped;
    }

    protected void setNullsFirst(String nullsFirst) {
        this.nullsFirst = nullsFirst;
    }

    protected void setNullsLast(String nullsLast) {
        this.nullsLast = nullsLast;
    }

    protected void setLimitRequired(boolean limitRequired) {
        this.limitRequired = limitRequired;
    }

    protected void setCountDistinctMultipleColumns(boolean countDistinctMultipleColumns) {
        this.countDistinctMultipleColumns = countDistinctMultipleColumns;
    }

    protected void setCountViaAnalytics(boolean countViaAnalytics) {
        this.countViaAnalytics = countViaAnalytics;
    }

    protected void setWrapSelectParameters(boolean b) {
        this.wrapSelectParameters = b;
    }

    protected void setArraysSupported(boolean b) {
        this.arraysSupported = b;
    }

    protected void setListMaxSize(int i) {
        this.listMaxSize = i;
    }

    protected void setSupportsUnquotedReservedWordsAsIdentifier(boolean b) {
        this.supportsUnquotedReservedWordsAsIdentifier = b;
    }

    protected void setMaxLimit(int i) {
        this.maxLimit = i;
    }

    protected void setBatchToBulkSupported(boolean b) {
        this.batchToBulkSupported = b;
    }

    protected void setForShareFlag(QueryFlag flag) {
        this.forShareFlag = flag;
    }

    protected void setForUpdateFlag(QueryFlag flag) {
        this.forUpdateFlag = flag;
    }

    protected void setNoWaitFlag(QueryFlag flag) {
        this.noWaitFlag = flag;
    }

    public static abstract class Builder {
        protected boolean printSchema;
        protected boolean quote;
        protected boolean newLineToSingleSpace;
        protected char escape = (char)92;

        public Builder printSchema() {
            this.printSchema = true;
            return this;
        }

        public Builder quote() {
            this.quote = true;
            return this;
        }

        public Builder newLineToSingleSpace() {
            this.newLineToSingleSpace = true;
            return this;
        }

        public Builder escape(char ch) {
            this.escape = ch;
            return this;
        }

        protected abstract SQLTemplates build(char var1, boolean var2);

        public SQLTemplates build() {
            SQLTemplates templates = this.build(this.escape, this.quote);
            if (this.newLineToSingleSpace) {
                templates.newLineToSingleSpace();
            }
            templates.setPrintSchema(this.printSchema);
            return templates;
        }
    }
}

