/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.sql;

import com.querydsl.core.types.Ops;
import com.querydsl.sql.Keywords;
import com.querydsl.sql.SQLOps;
import com.querydsl.sql.SQLTemplates;

public class HSQLDBTemplates
extends SQLTemplates {
    public static final HSQLDBTemplates DEFAULT = new HSQLDBTemplates();

    public static SQLTemplates.Builder builder() {
        return new SQLTemplates.Builder(){

            @Override
            protected SQLTemplates build(char escape, boolean quote) {
                return new HSQLDBTemplates(escape, quote);
            }
        };
    }

    public HSQLDBTemplates() {
        this('\\', false);
    }

    public HSQLDBTemplates(boolean quote) {
        this('\\', quote);
    }

    public HSQLDBTemplates(char escape, boolean quote) {
        super(Keywords.HSQLDB, "\"", escape, quote);
        this.setLimitRequired(true);
        this.setAutoIncrement(" identity");
        this.setDefaultValues("\ndefault values");
        this.setFunctionJoinsWrapped(true);
        this.setUnionsWrapped(false);
        this.setPrecedence(30, Ops.CONCAT);
        this.setPrecedence(41, Ops.NOT);
        this.setPrecedence(50, Ops.EQ, Ops.EQ_IGNORE_CASE, Ops.NE);
        this.setPrecedence(51, Ops.IS_NULL, Ops.IS_NOT_NULL, Ops.LIKE, Ops.LIKE_ESCAPE, Ops.BETWEEN, Ops.IN, Ops.NOT_IN, Ops.EXISTS);
        this.setPrecedence(51, OTHER_LIKE_CASES);
        this.add(Ops.TRIM, "trim(both from {0})");
        this.add(Ops.NEGATE, "{0} * -1", 30);
        this.add(SQLOps.NEXTVAL, "next value for {0s}");
        this.add(Ops.MathOps.POWER, "power({0},{1s})");
        this.add(Ops.MathOps.ROUND, "round({0},0)");
        this.add(Ops.MathOps.LN, "log({0})");
        this.add(Ops.MathOps.LOG, "(log({0}) / log({1}))");
        this.add(Ops.MathOps.COSH, "(exp({0}) + exp({0*'-1'})) / 2");
        this.add(Ops.MathOps.COTH, "(exp({0*'2'}) + 1) / (exp({0*'2'}) - 1)");
        this.add(Ops.MathOps.SINH, "(exp({0}) - exp({0*'-1'})) / 2");
        this.add(Ops.MathOps.TANH, "(exp({0*'2'}) - 1) / (exp({0*'2'}) + 1)");
        this.add(Ops.DateTimeOps.WEEK, "extract(week_of_year from {0})");
        this.add(Ops.DateTimeOps.YEAR_WEEK, "extract(year from {0}) * 100 + extract(week_of_year from {0})", 40);
        this.add(Ops.DateTimeOps.ADD_YEARS, "dateadd('yy', {1s}, {0})");
        this.add(Ops.DateTimeOps.ADD_MONTHS, "dateadd('mm', {1s}, {0})");
        this.add(Ops.DateTimeOps.ADD_WEEKS, "dateadd('week', {1s}, {0})");
        this.add(Ops.DateTimeOps.ADD_DAYS, "dateadd('dd', {1s}, {0})");
        this.add(Ops.DateTimeOps.ADD_HOURS, "dateadd('hh', {1s}, {0})");
        this.add(Ops.DateTimeOps.ADD_MINUTES, "dateadd('mi', {1s}, {0})");
        this.add(Ops.DateTimeOps.ADD_SECONDS, "dateadd('ss', {1s}, {0})");
        this.add(Ops.DateTimeOps.DIFF_YEARS, "datediff('yy', {0}, {1})");
        this.add(Ops.DateTimeOps.DIFF_MONTHS, "datediff('mm', {0}, {1})");
        this.add(Ops.DateTimeOps.DIFF_WEEKS, "trunc(datediff('dd', {0}, {1}) / 7)");
        this.add(Ops.DateTimeOps.DIFF_DAYS, "datediff('dd', {0}, {1})");
        this.add(Ops.DateTimeOps.DIFF_HOURS, "datediff('hh', {0}, {1})");
        this.add(Ops.DateTimeOps.DIFF_MINUTES, "datediff('mi', {0}, {1})");
        this.add(Ops.DateTimeOps.DIFF_SECONDS, "datediff('ss', {0}, {1})");
        this.add(Ops.DateTimeOps.TRUNC_YEAR, "trunc({0},'YY')");
        this.add(Ops.DateTimeOps.TRUNC_MONTH, "trunc({0},'MM')");
        this.add(Ops.DateTimeOps.TRUNC_WEEK, "trunc({0},'WW')");
        this.add(Ops.DateTimeOps.TRUNC_DAY, "trunc({0},'DD')");
        this.add(Ops.DateTimeOps.TRUNC_HOUR, "trunc({0},'HH')");
        this.add(Ops.DateTimeOps.TRUNC_MINUTE, "trunc({0},'MI')");
        this.add(Ops.DateTimeOps.TRUNC_SECOND, "trunc({0},'SS')");
        this.add(Ops.DateTimeOps.DATE, "convert({0}, date)");
        this.add(SQLOps.GROUP_CONCAT2, "group_concat({0} separator '{1s}')");
        this.addTypeNameToCode("character", 1, true);
        this.addTypeNameToCode("float", 8, true);
        this.addTypeNameToCode("real", 8);
        this.addTypeNameToCode("nvarchar", 12);
    }

    @Override
    public String getCastTypeNameForCode(int code) {
        if (code == 12) {
            return "varchar(10)";
        }
        return super.getCastTypeNameForCode(code);
    }
}

