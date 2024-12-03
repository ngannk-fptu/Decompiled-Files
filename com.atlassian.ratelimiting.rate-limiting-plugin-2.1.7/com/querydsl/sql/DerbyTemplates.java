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

public class DerbyTemplates
extends SQLTemplates {
    public static final DerbyTemplates DEFAULT = new DerbyTemplates();
    private String limitOffsetTemplate = "\noffset {1s} rows fetch next {0s} rows only";
    private String limitTemplate = "\nfetch first {0s} rows only";
    private String offsetTemplate = "\noffset {0s} rows";

    public static SQLTemplates.Builder builder() {
        return new SQLTemplates.Builder(){

            @Override
            protected SQLTemplates build(char escape, boolean quote) {
                return new DerbyTemplates(escape, quote);
            }
        };
    }

    public DerbyTemplates() {
        this('\\', false);
    }

    public DerbyTemplates(boolean quote) {
        this('\\', quote);
    }

    public DerbyTemplates(char escape, boolean quote) {
        super(Keywords.DERBY, "\"", escape, quote);
        this.setDummyTable("sysibm.sysdummy1");
        this.setAutoIncrement(" generated always as identity");
        this.setFunctionJoinsWrapped(true);
        this.setDefaultValues("\nvalues (default)");
        this.setPrecedence(50, Ops.EQ, Ops.EQ_IGNORE_CASE, Ops.NE, Ops.EXISTS);
        this.add(Ops.CONCAT, "varchar({0} || {1})", -1);
        this.add(SQLOps.NEXTVAL, "next value for {0s}");
        this.add(Ops.CASE_EQ, "case {1} end");
        this.add(Ops.CASE_EQ_WHEN, "when {0} = {1} then {2} {3}");
        this.add(Ops.CASE_EQ_ELSE, "else {0}");
        this.add(Ops.MathOps.RANDOM, "random()");
        this.add(Ops.MathOps.ROUND, "floor({0})");
        this.add(Ops.MathOps.POWER, "exp({1} * log({0}))");
        this.add(Ops.MathOps.LN, "log({0})");
        this.add(Ops.MathOps.LOG, "(log({0}) / log({1}))");
        this.add(Ops.MathOps.COTH, "(exp({0*'2'}) + 1) / (exp({0*'2'}) - 1)");
        this.add(Ops.DateTimeOps.SECOND, "second({0})");
        this.add(Ops.DateTimeOps.MINUTE, "minute({0})");
        this.add(Ops.DateTimeOps.HOUR, "hour({0})");
        this.add(Ops.DateTimeOps.WEEK, "week({0})");
        this.add(Ops.DateTimeOps.MONTH, "month({0})");
        this.add(Ops.DateTimeOps.YEAR, "year({0})");
        this.add(Ops.DateTimeOps.YEAR_MONTH, "(year({0}) * 100 + month({0}))");
        this.add(Ops.DateTimeOps.YEAR_WEEK, "(year({0}) * 100 + week({0}))");
        this.add(Ops.DateTimeOps.DAY_OF_WEEK, "dayofweek({0})");
        this.add(Ops.DateTimeOps.DAY_OF_MONTH, "day({0})");
        this.add(Ops.DateTimeOps.DAY_OF_YEAR, "dayofyear({0})");
        this.add(Ops.DateTimeOps.ADD_YEARS, "{fn timestampadd(SQL_TSI_YEAR, {1}, {0})}");
        this.add(Ops.DateTimeOps.ADD_MONTHS, "{fn timestampadd(SQL_TSI_MONTH, {1}, {0})}");
        this.add(Ops.DateTimeOps.ADD_WEEKS, "{fn timestampadd(SQL_TSI_WEEK, {1}, {0})}");
        this.add(Ops.DateTimeOps.ADD_DAYS, "{fn timestampadd(SQL_TSI_DAY, {1}, {0})}");
        this.add(Ops.DateTimeOps.ADD_HOURS, "{fn timestampadd(SQL_TSI_HOUR, {1}, {0})}");
        this.add(Ops.DateTimeOps.ADD_MINUTES, "{fn timestampadd(SQL_TSI_MINUTE, {1}, {0})}");
        this.add(Ops.DateTimeOps.ADD_SECONDS, "{fn timestampadd(SQL_TSI_SECOND, {1}, {0})}");
        this.add(Ops.DateTimeOps.DIFF_YEARS, "{fn timestampdiff(SQL_TSI_YEAR, {0}, {1})}");
        this.add(Ops.DateTimeOps.DIFF_MONTHS, "{fn timestampdiff(SQL_TSI_MONTH, {0}, {1})}");
        this.add(Ops.DateTimeOps.DIFF_WEEKS, "{fn timestampdiff(SQL_TSI_WEEK, {0}, {1})}");
        this.add(Ops.DateTimeOps.DIFF_DAYS, "{fn timestampdiff(SQL_TSI_DAY, {0}, {1})}");
        this.add(Ops.DateTimeOps.DIFF_HOURS, "{fn timestampdiff(SQL_TSI_HOUR, {0}, {1})}");
        this.add(Ops.DateTimeOps.DIFF_MINUTES, "{fn timestampdiff(SQL_TSI_MINUTE, {0}, {1})}");
        this.add(Ops.DateTimeOps.DIFF_SECONDS, "{fn timestampdiff(SQL_TSI_SECOND, {0}, {1})}");
        this.add(Ops.DateTimeOps.TRUNC_YEAR, "timestamp(substr(cast({0} as char(30)),1,4)||'-01-01 00:00:00')");
        this.add(Ops.DateTimeOps.TRUNC_MONTH, "timestamp(substr(cast({0} as char(30)),1,7)||'-01 00:00:00')");
        this.add(Ops.DateTimeOps.TRUNC_DAY, "timestamp(substr(cast({0} as char(30)),1,10)||' 00:00:00')");
        this.add(Ops.DateTimeOps.TRUNC_HOUR, "timestamp(substr(cast({0} as char(30)),1,13)||':00:00')");
        this.add(Ops.DateTimeOps.TRUNC_MINUTE, "timestamp(substr(cast({0} as char(30)),1,16)||':00')");
        this.add(Ops.DateTimeOps.TRUNC_SECOND, "timestamp(substr(cast({0} as char(30)),1,19))");
        this.add(Ops.StringOps.LEFT, "substr({0},1,{1})");
        this.addTypeNameToCode("smallint", -6, true);
        this.addTypeNameToCode("long varchar for bit data", -4);
        this.addTypeNameToCode("varchar () for bit data", -3);
        this.addTypeNameToCode("char () for bit data", -2);
        this.addTypeNameToCode("long varchar", -1, true);
        this.addTypeNameToCode("object", 2000, true);
        this.addTypeNameToCode("xml", 2009, true);
    }

    @Override
    public String serialize(String literal, int jdbcType) {
        switch (jdbcType) {
            case 16: {
                return "1".equals(literal) ? "true" : "false";
            }
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
    protected void serializeModifiers(QueryMetadata metadata, SQLSerializer context) {
        QueryModifiers mod = metadata.getModifiers();
        if (mod.getLimit() == null) {
            context.handle(this.offsetTemplate, mod.getOffset());
        } else if (mod.getOffset() == null) {
            context.handle(this.limitTemplate, mod.getLimit());
        } else {
            context.handle(this.limitOffsetTemplate, mod.getLimit(), mod.getOffset());
        }
    }
}

