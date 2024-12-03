/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.sql;

import com.querydsl.core.QueryMetadata;
import com.querydsl.core.QueryModifiers;
import com.querydsl.core.types.Ops;
import com.querydsl.sql.Keywords;
import com.querydsl.sql.SQLOps;
import com.querydsl.sql.SQLSerializer;
import com.querydsl.sql.SQLTemplates;

public class FirebirdTemplates
extends SQLTemplates {
    public static final FirebirdTemplates DEFAULT = new FirebirdTemplates();
    private String limitOffsetTemplate = "\nrows {0} to {1}";
    private String limitTemplate = "\nrows {0}";
    private String offsetTemplate = "\nrows {0} to 2147483647";

    public static SQLTemplates.Builder builder() {
        return new SQLTemplates.Builder(){

            @Override
            protected SQLTemplates build(char escape, boolean quote) {
                return new FirebirdTemplates(escape, quote);
            }
        };
    }

    public FirebirdTemplates() {
        this('\\', false);
    }

    public FirebirdTemplates(boolean quote) {
        this('\\', quote);
    }

    public FirebirdTemplates(char escape, boolean quote) {
        super(Keywords.FIREBIRD, "\"", escape, quote);
        this.setDummyTable("RDB$DATABASE");
        this.setUnionsWrapped(false);
        this.setWrapSelectParameters(true);
        this.setArraysSupported(false);
        this.setBatchToBulkSupported(false);
        this.setPrecedence(50, Ops.EQ, Ops.EQ_IGNORE_CASE, Ops.NE);
        this.add(Ops.CONCAT, "{0} || {1}", 19);
        this.add(Ops.CHAR_AT, "cast(substring({0} from {1+'1's} for 1) as char)");
        this.add(Ops.SUBSTR_1ARG, "substring({0} from {1+'1's})");
        this.add(Ops.SUBSTR_2ARGS, "substring({0} from {1+'1's} for {2-1s})");
        this.add(Ops.INDEX_OF, "position({1},{0})-1", 40);
        this.add(Ops.INDEX_OF_2ARGS, "position({1},{0},{2+'1's})-1", 40);
        this.add(Ops.StringOps.LOCATE, "position({0},{1})");
        this.add(Ops.StringOps.LOCATE2, "position({0},{1},{2})");
        this.add(Ops.STRING_LENGTH, "char_length({0})");
        this.add(Ops.STRING_IS_EMPTY, "char_length({0}) = 0");
        this.add(Ops.AggOps.BOOLEAN_ANY, "any({0})");
        this.add(Ops.AggOps.BOOLEAN_ALL, "all({0})");
        this.add(Ops.MathOps.LOG, "log({1},{0})");
        this.add(Ops.MathOps.COSH, "(exp({0}) + exp({0*'-1'})) / 2");
        this.add(Ops.MathOps.COTH, "(exp({0*'2'}) + 1) / (exp({0*'2'}) - 1)");
        this.add(Ops.MathOps.SINH, "(exp({0}) - exp({0} * -1)) / 2");
        this.add(Ops.MathOps.TANH, "(exp({0*'2'}) - 1) / (exp({0*'2'}) + 1)");
        this.add(Ops.MathOps.DEG, "{0*'180.0'} / pi()", 30);
        this.add(Ops.MathOps.RAD, "{0}*pi() / 180.0 ", 30);
        this.add(Ops.DateTimeOps.DATE, "cast({0} as date)");
        this.add(Ops.DateTimeOps.MILLISECOND, "extract(millisecond from {0})");
        this.add(Ops.DateTimeOps.YEAR_MONTH, "extract(year from {0}) * 100 + extract(month from {0})", 40);
        this.add(Ops.DateTimeOps.YEAR_WEEK, "extract(year from {0}) * 100 + extract(week from {0})", 40);
        this.add(Ops.DateTimeOps.DAY_OF_WEEK, "extract(weekday from {0})");
        this.add(Ops.DateTimeOps.DAY_OF_MONTH, "extract(day from {0})");
        this.add(Ops.DateTimeOps.ADD_YEARS, "dateadd(year,{1},{0})");
        this.add(Ops.DateTimeOps.ADD_MONTHS, "dateadd(month,{1},{0})");
        this.add(Ops.DateTimeOps.ADD_WEEKS, "dateadd(week,{1},{0})");
        this.add(Ops.DateTimeOps.ADD_DAYS, "dateadd(day,{1},{0})");
        this.add(Ops.DateTimeOps.ADD_HOURS, "dateadd(hour,{1},{0})");
        this.add(Ops.DateTimeOps.ADD_MINUTES, "dateadd(minute,{1},{0})");
        this.add(Ops.DateTimeOps.ADD_SECONDS, "dateadd(second,{1},{0})");
        this.add(Ops.DateTimeOps.DIFF_YEARS, "datediff(year,{0},{1})");
        this.add(Ops.DateTimeOps.DIFF_MONTHS, "datediff(month,{0},{1})");
        this.add(Ops.DateTimeOps.DIFF_WEEKS, "datediff(week,{0},{1})");
        this.add(Ops.DateTimeOps.DIFF_DAYS, "datediff(day,{0},{1})");
        this.add(Ops.DateTimeOps.DIFF_HOURS, "datediff(hour,{0},{1})");
        this.add(Ops.DateTimeOps.DIFF_MINUTES, "datediff(minute,{0},{1})");
        this.add(Ops.DateTimeOps.DIFF_SECONDS, "datediff(second,{0},{1})");
        this.add(Ops.DateTimeOps.TRUNC_YEAR, "cast(extract(year from {0}) || '-1-1' as date)");
        this.add(Ops.DateTimeOps.TRUNC_MONTH, "cast(substring(cast({0} as char(100)) from 1 for 7) || '-1' as date)");
        this.add(Ops.DateTimeOps.TRUNC_DAY, "cast(substring(cast({0} as char(100)) from 1 for 10) as date)");
        this.add(Ops.DateTimeOps.TRUNC_HOUR, "cast(substring(cast({0} as char(100)) from 1 for 13) || ':00:00' as timestamp)");
        this.add(Ops.DateTimeOps.TRUNC_MINUTE, "cast(substring(cast({0} as char(100)) from 1 for 16) || ':00' as timestamp)");
        this.add(Ops.DateTimeOps.TRUNC_SECOND, "cast(substring(cast({0} as char(100)) from 1 for 19) as timestamp)");
        this.add(SQLOps.GROUP_CONCAT, "list({0},',')");
        this.add(SQLOps.GROUP_CONCAT2, "list({0},{1})");
        this.addTypeNameToCode("smallint", 16, true);
        this.addTypeNameToCode("smallint", -7, true);
        this.addTypeNameToCode("smallint", -6, true);
        this.addTypeNameToCode("decimal", 8, true);
        this.addTypeNameToCode("blob sub_type 0", -4);
        this.addTypeNameToCode("blob sub_type 1", -1);
        this.addTypeNameToCode("double precision", 8);
        this.addTypeNameToCode("array", 1111);
        this.addTypeNameToCode("blob sub_type 0 ", 2004);
    }

    @Override
    public String getCastTypeNameForCode(int code) {
        if (code == 12) {
            return "varchar(256)";
        }
        return super.getCastTypeNameForCode(code);
    }

    @Override
    protected void serializeModifiers(QueryMetadata metadata, SQLSerializer context) {
        QueryModifiers mod = metadata.getModifiers();
        if (mod.isRestricting()) {
            if (mod.getLimit() != null) {
                if (mod.getOffset() != null) {
                    context.handle(this.limitOffsetTemplate, mod.getOffset() + 1L, mod.getOffset() + mod.getLimit());
                } else {
                    context.handle(this.limitTemplate, mod.getLimit());
                }
            } else {
                context.handle(this.offsetTemplate, mod.getOffset() + 1L);
            }
        }
    }
}

