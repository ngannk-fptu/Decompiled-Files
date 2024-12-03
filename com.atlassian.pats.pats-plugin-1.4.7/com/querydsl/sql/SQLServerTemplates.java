/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package com.querydsl.sql;

import com.google.common.collect.ImmutableList;
import com.querydsl.core.QueryFlag;
import com.querydsl.core.QueryMetadata;
import com.querydsl.core.QueryModifiers;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Operator;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.sql.Keywords;
import com.querydsl.sql.SQLOps;
import com.querydsl.sql.SQLSerializer;
import com.querydsl.sql.SQLTemplates;
import java.util.Set;

public class SQLServerTemplates
extends SQLTemplates {
    protected static final Expression<?> WITH_REPEATABLE_READ = ExpressionUtils.operation(Object.class, (Operator)SQLOps.WITH_REPEATABLE_READ, ImmutableList.of());
    public static final SQLServerTemplates DEFAULT = new SQLServerTemplates();
    private String topTemplate = "top {0s} ";

    public static SQLTemplates.Builder builder() {
        return new SQLTemplates.Builder(){

            @Override
            protected SQLTemplates build(char escape, boolean quote) {
                return new SQLServerTemplates(escape, quote);
            }
        };
    }

    public SQLServerTemplates() {
        this('\\', false);
    }

    public SQLServerTemplates(boolean quote) {
        this('\\', quote);
    }

    public SQLServerTemplates(char escape, boolean quote) {
        this(Keywords.DEFAULT, escape, quote);
    }

    protected SQLServerTemplates(Set<String> keywords, char escape, boolean quote) {
        super(keywords, "\"", escape, quote);
        this.setDummyTable("");
        this.setNullsFirst(null);
        this.setNullsLast(null);
        this.setDefaultValues("\ndefault values");
        this.setArraysSupported(false);
        this.setForUpdateFlag(new QueryFlag(QueryFlag.Position.BEFORE_FILTERS, FOR_UPDATE));
        this.setForShareSupported(true);
        this.setForShareFlag(new QueryFlag(QueryFlag.Position.BEFORE_FILTERS, WITH_REPEATABLE_READ));
        this.setPrecedence(40, Ops.NEGATE);
        this.setPrecedence(50, Ops.EQ, Ops.EQ_IGNORE_CASE, Ops.NE);
        this.setPrecedence(110, Ops.BETWEEN, Ops.IN, Ops.NOT_IN, Ops.LIKE, Ops.LIKE_ESCAPE);
        this.setPrecedence(110, OTHER_LIKE_CASES);
        this.setPrecedence(111, Ops.LIST, Ops.SET, Ops.SINGLETON);
        this.add(SQLOps.WITH_REPEATABLE_READ, "\nwith (repeatableread)");
        this.add(Ops.CONCAT, "{0} + {1}");
        this.add(Ops.CHAR_AT, "cast(substring({0},{1+'1'},1) as char)");
        this.add(Ops.INDEX_OF, "charindex({1},{0})-1", 40);
        this.add(Ops.INDEX_OF_2ARGS, "charindex({1},{0},{2})-1", 40);
        this.add(Ops.MATCHES, "{0} like {1}", 110);
        this.add(Ops.STRING_IS_EMPTY, "len({0}) = 0", 50);
        this.add(Ops.STRING_LENGTH, "len({0})");
        this.add(Ops.SUBSTR_1ARG, "substring({0},{1+'1'},255)");
        this.add(Ops.SUBSTR_2ARGS, "substring({0},{1+'1'},{2-1s})");
        this.add(Ops.TRIM, "ltrim(rtrim({0}))");
        this.add(SQLOps.FOR_UPDATE, "\nwith (updlock)");
        this.add(Ops.StringOps.LOCATE, "charindex({0},{1})");
        this.add(Ops.StringOps.LOCATE2, "charindex({0},{1},{2})");
        this.add(Ops.StringOps.LPAD, "right(replicate(' ', {1}) + left({0}, {1}), {1})");
        this.add(Ops.StringOps.LPAD2, "right(replicate({2}, {1}) + left({0}, {1}), {1})");
        this.add(Ops.StringOps.RPAD, "left(left({0}, {1}) + replicate(' ', {1}), {1})");
        this.add(Ops.StringOps.RPAD2, "left(left({0}, {1}) + replicate({2}, {1}), {1})");
        this.add(SQLOps.NEXTVAL, "{0s}.nextval");
        this.add(Ops.MOD, "{0} % {1}", 30);
        this.add(Ops.MathOps.COSH, "(exp({0}) + exp({0*'-1'})) / 2");
        this.add(Ops.MathOps.COTH, "(exp({0*'2'}) + 1) / (exp({0*'2'}) - 1)");
        this.add(Ops.MathOps.LN, "log({0})");
        this.add(Ops.MathOps.LOG, "log({0}, {1})");
        this.add(Ops.MathOps.POWER, "power({0}, {1})");
        this.add(Ops.MathOps.ROUND, "round({0}, 0)");
        this.add(Ops.MathOps.SINH, "(exp({0}) - exp({0*'-1'})) / 2");
        this.add(Ops.MathOps.TANH, "(exp({0*'2'}) - 1) / (exp({0*'2'}) + 1)");
        this.add(Ops.DateTimeOps.YEAR, "datepart(year, {0})");
        this.add(Ops.DateTimeOps.MONTH, "datepart(month, {0})");
        this.add(Ops.DateTimeOps.WEEK, "datepart(week, {0})");
        this.add(Ops.DateTimeOps.DAY_OF_MONTH, "datepart(day, {0})");
        this.add(Ops.DateTimeOps.DAY_OF_WEEK, "datepart(weekday, {0})");
        this.add(Ops.DateTimeOps.DAY_OF_YEAR, "datepart(dayofyear, {0})");
        this.add(Ops.DateTimeOps.HOUR, "datepart(hour, {0})");
        this.add(Ops.DateTimeOps.MINUTE, "datepart(minute, {0})");
        this.add(Ops.DateTimeOps.SECOND, "datepart(second, {0})");
        this.add(Ops.DateTimeOps.MILLISECOND, "datepart(millisecond, {0})");
        this.add(Ops.DateTimeOps.YEAR_MONTH, "(datepart(year, {0}) * 100 + datepart(month, {0}))");
        this.add(Ops.DateTimeOps.YEAR_WEEK, "(datepart(year, {0}) * 100 + datepart(isowk, {0}))");
        this.add(Ops.DateTimeOps.ADD_YEARS, "dateadd(year, {1s}, {0})");
        this.add(Ops.DateTimeOps.ADD_MONTHS, "dateadd(month, {1s}, {0})");
        this.add(Ops.DateTimeOps.ADD_WEEKS, "dateadd(week, {1s}, {0})");
        this.add(Ops.DateTimeOps.ADD_DAYS, "dateadd(day, {1s}, {0})");
        this.add(Ops.DateTimeOps.ADD_HOURS, "dateadd(hour, {1s}, {0})");
        this.add(Ops.DateTimeOps.ADD_MINUTES, "dateadd(minute, {1s}, {0})");
        this.add(Ops.DateTimeOps.ADD_SECONDS, "dateadd(second, {1s}, {0})");
        this.add(Ops.DateTimeOps.DIFF_YEARS, "datediff(year,{0},{1})");
        this.add(Ops.DateTimeOps.DIFF_MONTHS, "datediff(month,{0},{1})");
        this.add(Ops.DateTimeOps.DIFF_WEEKS, "datediff(week,{0},{1})");
        this.add(Ops.DateTimeOps.DIFF_DAYS, "datediff(day,{0},{1})");
        this.add(Ops.DateTimeOps.DIFF_HOURS, "datediff(hour,{0},{1})");
        this.add(Ops.DateTimeOps.DIFF_MINUTES, "datediff(minute,{0},{1})");
        this.add(Ops.DateTimeOps.DIFF_SECONDS, "datediff(second,{0},{1})");
        this.add(Ops.DateTimeOps.TRUNC_YEAR, "CONVERT(DATETIME, CONVERT(VARCHAR(4), {0}, 120) + '-01-01')");
        this.add(Ops.DateTimeOps.TRUNC_MONTH, "CONVERT(DATETIME, CONVERT(VARCHAR(7), {0}, 120) + '-01')");
        this.add(Ops.DateTimeOps.TRUNC_DAY, "CONVERT(DATETIME, CONVERT(VARCHAR(10), {0}, 120))");
        this.add(Ops.DateTimeOps.TRUNC_HOUR, "CONVERT(DATETIME, CONVERT(VARCHAR(13), {0}, 120) + ':00:00')");
        this.add(Ops.DateTimeOps.TRUNC_MINUTE, "CONVERT(DATETIME, CONVERT(VARCHAR(16), {0}, 120) + ':00')");
        this.add(Ops.DateTimeOps.TRUNC_SECOND, "CONVERT(DATETIME, CONVERT(VARCHAR(19), {0}, 120))");
        this.add(Ops.DateTimeOps.DATE, "cast({0} as date)");
        this.add(Ops.DateTimeOps.CURRENT_DATE, "cast(getdate() as date)");
        this.addTypeNameToCode("bit", 16, true);
        this.addTypeNameToCode("decimal", 8, true);
        this.addTypeNameToCode("tinyint identity", -6);
        this.addTypeNameToCode("bigint identity", -5);
        this.addTypeNameToCode("timestamp", -2);
        this.addTypeNameToCode("nchar", 1);
        this.addTypeNameToCode("uniqueidentifier", 1);
        this.addTypeNameToCode("numeric() identity", 2);
        this.addTypeNameToCode("money", 3);
        this.addTypeNameToCode("smallmoney", 3);
        this.addTypeNameToCode("decimal() identity", 3);
        this.addTypeNameToCode("int", 4);
        this.addTypeNameToCode("int identity", 4);
        this.addTypeNameToCode("smallint identity", 5);
        this.addTypeNameToCode("float", 8);
        this.addTypeNameToCode("nvarchar", 12);
        this.addTypeNameToCode("date", 12);
        this.addTypeNameToCode("time", 12);
        this.addTypeNameToCode("datetime2", 12);
        this.addTypeNameToCode("datetimeoffset", 12);
        this.addTypeNameToCode("sysname", 12);
        this.addTypeNameToCode("sql_variant", 12);
        this.addTypeNameToCode("datetime", 93);
        this.addTypeNameToCode("smalldatetime", 93);
        this.addTypeNameToCode("image", 2004);
        this.addTypeNameToCode("ntext", 2005);
        this.addTypeNameToCode("xml", 2005);
        this.addTypeNameToCode("text", 2005);
    }

    @Override
    public String serialize(String literal, int jdbcType) {
        switch (jdbcType) {
            case 93: 
            case 2014: {
                return "{ts '" + literal + "'}";
            }
            case 91: {
                return "{d '" + literal + "'}";
            }
            case 92: 
            case 2013: {
                return "{t '" + literal + "'}";
            }
        }
        return super.serialize(literal, jdbcType);
    }

    @Override
    protected String escapeForLike(String str) {
        StringBuilder rv = new StringBuilder(str.length() + 3);
        for (char ch : str.toCharArray()) {
            if (ch == this.getEscapeChar() || ch == '%' || ch == '_' || ch == '[') {
                rv.append(this.getEscapeChar());
            }
            rv.append(ch);
        }
        return rv.toString();
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public void serialize(QueryMetadata metadata, boolean forCountRow, SQLSerializer context) {
        if (!forCountRow && metadata.getModifiers().isRestricting() && !metadata.getJoins().isEmpty()) {
            QueryModifiers mod = metadata.getModifiers();
            if (mod.getOffset() != null) throw new IllegalStateException("offset not supported");
            metadata = metadata.clone();
            metadata.addFlag(new QueryFlag(QueryFlag.Position.AFTER_SELECT, Expressions.template(Integer.class, this.topTemplate, mod.getLimit())));
            context.serializeForQuery(metadata, forCountRow);
        } else {
            context.serializeForQuery(metadata, forCountRow);
        }
        if (metadata.getFlags().isEmpty()) return;
        context.serialize(QueryFlag.Position.END, metadata.getFlags());
    }

    @Override
    protected void serializeModifiers(QueryMetadata metadata, SQLSerializer context) {
    }
}

