/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.sql;

import com.querydsl.core.types.Ops;
import com.querydsl.sql.Keywords;
import com.querydsl.sql.SQLOps;
import com.querydsl.sql.SQLTemplates;

public class PostgreSQLTemplates
extends SQLTemplates {
    public static final PostgreSQLTemplates DEFAULT = new PostgreSQLTemplates();

    public static SQLTemplates.Builder builder() {
        return new SQLTemplates.Builder(){

            @Override
            protected SQLTemplates build(char escape, boolean quote) {
                return new PostgreSQLTemplates(escape, quote);
            }
        };
    }

    public PostgreSQLTemplates() {
        this('\\', false);
    }

    public PostgreSQLTemplates(boolean quote) {
        this('\\', quote);
    }

    public PostgreSQLTemplates(char escape, boolean quote) {
        super(Keywords.POSTGRESQL, "\"", escape, quote);
        this.setDummyTable(null);
        this.setCountDistinctMultipleColumns(true);
        this.setCountViaAnalytics(true);
        this.setDefaultValues("\ndefault values");
        this.setSupportsUnquotedReservedWordsAsIdentifier(true);
        this.setForShareSupported(true);
        this.setPrecedence(47, Ops.IS_NULL, Ops.IS_NOT_NULL);
        this.setPrecedence(48, Ops.CONCAT, Ops.MATCHES);
        this.setPrecedence(49, Ops.IN);
        this.setPrecedence(50, Ops.BETWEEN);
        this.setPrecedence(51, Ops.LIKE, Ops.LIKE_ESCAPE);
        this.setPrecedence(52, Ops.LT, Ops.GT, Ops.LOE, Ops.GOE);
        this.setPrecedence(53, Ops.EQ, Ops.EQ_IGNORE_CASE);
        this.setPrecedence(51, OTHER_LIKE_CASES);
        this.add(Ops.MOD, "{0} % {1}", 30);
        this.add(Ops.MATCHES, "{0} ~ {1}");
        this.add(Ops.INDEX_OF, "strpos({0},{1})-1", 40);
        this.add(Ops.INDEX_OF_2ARGS, "strpos({0},{1})-1", 40);
        this.add(Ops.StringOps.LOCATE, "strpos({1},{0})");
        this.add(Ops.StringOps.LOCATE2, "strpos(repeat('^',{2-'1's}) || substr({1},{2s}),{0})");
        this.add(SQLOps.GROUP_CONCAT, "string_agg({0},',')");
        this.add(SQLOps.GROUP_CONCAT2, "string_agg({0},{1})");
        this.add(Ops.LIKE_ESCAPE_IC, "{0} ilike {1} escape '{2s}'");
        if (escape == '\\') {
            this.add(Ops.LIKE, "{0} like {1}");
            this.add(Ops.LIKE_IC, "{0} ilike {1}");
            this.add(Ops.ENDS_WITH, "{0} like {%1}");
            this.add(Ops.ENDS_WITH_IC, "{0} ilike {%1}");
            this.add(Ops.STARTS_WITH, "{0} like {1%}");
            this.add(Ops.STARTS_WITH_IC, "{0} ilike {1%}");
            this.add(Ops.STRING_CONTAINS, "{0} like {%1%}");
            this.add(Ops.STRING_CONTAINS_IC, "{0} ilike {%1%}");
        } else {
            this.add(Ops.LIKE_IC, "{0} ilike {1} escape '" + escape + "'");
            this.add(Ops.ENDS_WITH_IC, "{0} ilike {%1} escape '" + escape + "'");
            this.add(Ops.STARTS_WITH_IC, "{0} ilike {1%} escape '" + escape + "'");
            this.add(Ops.STRING_CONTAINS_IC, "{0} ilike {%1%} escape '" + escape + "'");
        }
        this.add(Ops.MathOps.RANDOM, "random()");
        this.add(Ops.MathOps.LN, "ln({0})");
        this.add(Ops.MathOps.LOG, "log({1s},{0s})");
        this.add(Ops.MathOps.COSH, "(exp({0}) + exp({0*'-1'})) / 2");
        this.add(Ops.MathOps.COTH, "(exp({0*'2'}) + 1) / (exp({0*'2'}) - 1)");
        this.add(Ops.MathOps.SINH, "(exp({0}) - exp({0} * -1)) / 2");
        this.add(Ops.MathOps.TANH, "(exp({0*'2'}) - 1) / (exp({0*'2'}) + 1)");
        this.add(Ops.DateTimeOps.DAY_OF_MONTH, "cast(extract(day from {0}) as integer)");
        this.add(Ops.DateTimeOps.DAY_OF_WEEK, "cast(extract(dow from {0}) + 1 as integer)");
        this.add(Ops.DateTimeOps.DAY_OF_YEAR, "cast(extract(doy from {0}) as integer)");
        this.add(Ops.DateTimeOps.HOUR, "cast(extract(hour from {0}) as integer)");
        this.add(Ops.DateTimeOps.MINUTE, "cast(extract(minute from {0}) as integer)");
        this.add(Ops.DateTimeOps.MONTH, "cast(extract(month from {0}) as integer)");
        this.add(Ops.DateTimeOps.SECOND, "cast(extract(second from {0}) as integer)");
        this.add(Ops.DateTimeOps.WEEK, "cast(extract(week from {0}) as integer)");
        this.add(Ops.DateTimeOps.YEAR, "cast(extract(year from {0}) as integer)");
        this.add(Ops.DateTimeOps.YEAR_MONTH, "cast(extract(year from {0}) * 100 + extract(month from {0}) as integer)");
        this.add(Ops.DateTimeOps.YEAR_WEEK, "cast(extract(isoyear from {0}) * 100 + extract(week from {0}) as integer)");
        this.add(Ops.AggOps.BOOLEAN_ANY, "bool_or({0})", 0);
        this.add(Ops.AggOps.BOOLEAN_ALL, "bool_and({0})", 0);
        this.add(Ops.DateTimeOps.ADD_YEARS, "{0} + interval '{1s} years'");
        this.add(Ops.DateTimeOps.ADD_MONTHS, "{0} + interval '{1s} months'");
        this.add(Ops.DateTimeOps.ADD_WEEKS, "{0} + interval '{1s} weeks'");
        this.add(Ops.DateTimeOps.ADD_DAYS, "{0} + interval '{1s} days'");
        this.add(Ops.DateTimeOps.ADD_HOURS, "{0} + interval '{1s} hours'");
        this.add(Ops.DateTimeOps.ADD_MINUTES, "{0} + interval '{1s} minutes'");
        this.add(Ops.DateTimeOps.ADD_SECONDS, "{0} + interval '{1s} seconds'");
        String yearsDiff = "date_part('year', age({1}, {0}))";
        String monthsDiff = "(" + yearsDiff + " * 12 + date_part('month', age({1}, {0})))";
        String weeksDiff = "trunc((cast({1} as date) - cast({0} as date))/7)";
        String daysDiff = "(cast({1} as date) - cast({0} as date))";
        String hoursDiff = "(" + daysDiff + " * 24 + date_part('hour', age({1}, {0})))";
        String minutesDiff = "(" + hoursDiff + " * 60 + date_part('minute', age({1}, {0})))";
        String secondsDiff = "(" + minutesDiff + " * 60 + date_part('second', age({1}, {0})))";
        this.add(Ops.DateTimeOps.DIFF_YEARS, yearsDiff);
        this.add(Ops.DateTimeOps.DIFF_MONTHS, monthsDiff);
        this.add(Ops.DateTimeOps.DIFF_WEEKS, weeksDiff);
        this.add(Ops.DateTimeOps.DIFF_DAYS, daysDiff);
        this.add(Ops.DateTimeOps.DIFF_HOURS, hoursDiff);
        this.add(Ops.DateTimeOps.DIFF_MINUTES, minutesDiff);
        this.add(Ops.DateTimeOps.DIFF_SECONDS, secondsDiff);
        this.addTypeNameToCode("bool", -7, true);
        this.addTypeNameToCode("bytea", -2);
        this.addTypeNameToCode("name", 12);
        this.addTypeNameToCode("int8", -5, true);
        this.addTypeNameToCode("bigserial", -5);
        this.addTypeNameToCode("int2", 5, true);
        this.addTypeNameToCode("int2", -6, true);
        this.addTypeNameToCode("int4", 4, true);
        this.addTypeNameToCode("serial", 4);
        this.addTypeNameToCode("text", 12);
        this.addTypeNameToCode("oid", -5);
        this.addTypeNameToCode("xml", 2009, true);
        this.addTypeNameToCode("float4", 7, true);
        this.addTypeNameToCode("float8", 8, true);
        this.addTypeNameToCode("bpchar", 1);
        this.addTypeNameToCode("timestamptz", 93);
    }

    @Override
    public String serialize(String literal, int jdbcType) {
        if (jdbcType == 16) {
            return "1".equals(literal) ? "true" : "false";
        }
        return super.serialize(literal, jdbcType);
    }
}

