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
import com.querydsl.sql.types.NumericBooleanType;

public class CUBRIDTemplates
extends SQLTemplates {
    public static final CUBRIDTemplates DEFAULT = new CUBRIDTemplates();
    private String limitTemplate = "\nlimit {0}";
    private String offsetLimitTemplate = "\nlimit {0}, {1}";

    public static SQLTemplates.Builder builder() {
        return new SQLTemplates.Builder(){

            @Override
            protected SQLTemplates build(char escape, boolean quote) {
                return new CUBRIDTemplates(escape, quote);
            }
        };
    }

    public CUBRIDTemplates() {
        this('\\', false);
    }

    public CUBRIDTemplates(boolean quote) {
        this('\\', quote);
    }

    public CUBRIDTemplates(char escape, boolean quote) {
        super(Keywords.CUBRID, "\"", escape, quote);
        this.setDummyTable(null);
        this.addCustomType(NumericBooleanType.DEFAULT);
        this.setParameterMetadataAvailable(false);
        this.setNullsFirst(null);
        this.setNullsLast(null);
        this.setDefaultValues("\ndefault values");
        this.setArraysSupported(false);
        this.add(Ops.DateTimeOps.DATE, "trunc({0})");
        this.add(Ops.DateTimeOps.DAY_OF_YEAR, "dayofyear({0})");
        this.add(Ops.DateTimeOps.DAY_OF_WEEK, "dayofweek({0})");
        this.add(Ops.DateTimeOps.YEAR_WEEK, "(year({0}) * 100 + week({0}))");
        this.add(Ops.DateTimeOps.ADD_YEARS, "date_add({0}, interval {1s} year)");
        this.add(Ops.DateTimeOps.ADD_MONTHS, "date_add({0}, interval {1s} month)");
        this.add(Ops.DateTimeOps.ADD_WEEKS, "date_add({0}, interval {1s} week)");
        this.add(Ops.DateTimeOps.ADD_DAYS, "date_add({0}, interval {1s} day)");
        this.add(Ops.DateTimeOps.ADD_HOURS, "date_add({0}, interval {1s} hour)");
        this.add(Ops.DateTimeOps.ADD_MINUTES, "date_add({0}, interval {1s} minute)");
        this.add(Ops.DateTimeOps.ADD_SECONDS, "date_add({0}, interval {1s} second)");
        String diffSeconds = "(unix_timestamp({1}) - unix_timestamp({0}))";
        this.add(Ops.DateTimeOps.DIFF_YEARS, "(year({1}) - year({0}))");
        this.add(Ops.DateTimeOps.DIFF_MONTHS, "months_between({1}, {0})");
        this.add(Ops.DateTimeOps.DIFF_WEEKS, "ceil(({1}-{0}) / 7)");
        this.add(Ops.DateTimeOps.DIFF_DAYS, "({1}-{0})");
        this.add(Ops.DateTimeOps.DIFF_HOURS, "ceil(" + diffSeconds + " / 3600)");
        this.add(Ops.DateTimeOps.DIFF_MINUTES, "ceil(" + diffSeconds + " / 60)");
        this.add(Ops.DateTimeOps.DIFF_SECONDS, diffSeconds);
        this.add(Ops.DateTimeOps.TRUNC_YEAR, "trunc({0},'yyyy')");
        this.add(Ops.DateTimeOps.TRUNC_MONTH, "trunc({0},'mm')");
        this.add(Ops.DateTimeOps.TRUNC_WEEK, "trunc({0},'day')");
        this.add(Ops.DateTimeOps.TRUNC_DAY, "trunc({0},'dd')");
        this.add(Ops.DateTimeOps.TRUNC_HOUR, "timestamp(date({0}),concat(hour({0}),':00:00'))");
        this.add(Ops.DateTimeOps.TRUNC_MINUTE, "timestamp(date({0}),concat(hour({0}),':',minute({0}),':00'))");
        this.add(Ops.DateTimeOps.TRUNC_SECOND, "timestamp(date({0}),concat(hour({0}),':',minute({0}),':',second({0})))");
        this.add(Ops.MathOps.LN, "ln({0})");
        this.add(Ops.MathOps.LOG, "(ln({0}) / ln({1}))");
        this.add(Ops.MathOps.COSH, "(exp({0}) + exp({0*'-1'})) / 2");
        this.add(Ops.MathOps.COTH, "(exp({0*'2'}) + 1) / (exp({0*'2'}) - 1)");
        this.add(Ops.MathOps.SINH, "(exp({0}) - exp({0*'-1'})) / 2");
        this.add(Ops.MathOps.TANH, "(exp({0*'2'}) - 1) / (exp({0*'2'}) + 1)");
        this.add(SQLOps.GROUP_CONCAT2, "group_concat({0} separator '{1s}')");
        this.addTypeNameToCode("numeric(1,0)", 16, true);
        this.addTypeNameToCode("numeric(3,0)", -6, true);
        this.addTypeNameToCode("numeric(38,0)", -5, true);
        this.addTypeNameToCode("bit varying", -4);
        this.addTypeNameToCode("bit varying", -3);
        this.addTypeNameToCode("bit", -2, true);
        this.addTypeNameToCode("varchar", -1, true);
        this.addTypeNameToCode("double", 6, true);
        this.addTypeNameToCode("float", 7, true);
    }

    @Override
    public String serialize(String literal, int jdbcType) {
        switch (jdbcType) {
            case 93: {
                return "timestamp'" + literal + "'";
            }
            case 91: {
                return "date'" + literal + "'";
            }
            case 92: {
                return "time'" + literal + "'";
            }
        }
        return super.serialize(literal, jdbcType);
    }

    @Override
    protected void serializeModifiers(QueryMetadata metadata, SQLSerializer context) {
        QueryModifiers mod = metadata.getModifiers();
        if (mod.getLimit() != null) {
            if (mod.getOffset() != null) {
                context.handle(this.offsetLimitTemplate, mod.getOffset(), mod.getLimit());
            } else {
                context.handle(this.limitTemplate, mod.getLimit());
            }
        } else if (mod.getOffset() != null) {
            context.handle(this.offsetLimitTemplate, mod.getOffset(), Integer.MAX_VALUE);
        }
    }
}

