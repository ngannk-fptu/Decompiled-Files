/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.sql;

import com.querydsl.core.QueryMetadata;
import com.querydsl.core.QueryModifiers;
import com.querydsl.core.types.Ops;
import com.querydsl.sql.SQLSerializer;
import com.querydsl.sql.SQLTemplates;

public class TeradataTemplates
extends SQLTemplates {
    public static final TeradataTemplates DEFAULT = new TeradataTemplates();
    private String limitOffsetStart = "\nqualify row_number() over (order by ";
    private String limitTemplate = " <= {0}";
    private String limitOffsetTemplate = " between {0} and {1}";
    private String offsetTemplate = " > {0}";

    public static SQLTemplates.Builder builder() {
        return new SQLTemplates.Builder(){

            @Override
            protected SQLTemplates build(char escape, boolean quote) {
                return new TeradataTemplates(escape, quote);
            }
        };
    }

    public TeradataTemplates() {
        this('\\', false);
    }

    public TeradataTemplates(boolean quote) {
        this('\\', quote);
    }

    public TeradataTemplates(char escape, boolean quote) {
        super("\"", escape, quote);
        this.setNullsFirst(null);
        this.setNullsLast(null);
        this.setDummyTable(null);
        this.setCountViaAnalytics(true);
        this.setDefaultValues("\ndefault values");
        this.setBatchToBulkSupported(false);
        this.setPrecedence(41, Ops.CONCAT);
        this.setPrecedence(50, Ops.EQ, Ops.EQ_IGNORE_CASE, Ops.NE);
        this.add(Ops.NE, "{0} <> {1}");
        this.add(Ops.MOD, "{0} % {1}", 30);
        this.add(Ops.STRING_LENGTH, "character_length({0})");
        this.add(Ops.INDEX_OF, "(instr({0},{1})-1)");
        this.add(Ops.INDEX_OF_2ARGS, "(instr({0},{1},{2+'1'})-1)");
        this.add(Ops.STRING_CAST, "cast({0} as varchar(255))");
        this.add(Ops.StringOps.LOCATE, "instr({1},{0})");
        this.add(Ops.StringOps.LOCATE2, "instr({1},{0},{2s})");
        this.add(Ops.StringOps.LEFT, "substr({0}, 1, {1})");
        this.add(Ops.StringOps.RIGHT, "substr({0}, (character_length({0})-{1s}) + 1, {1})");
        this.add(Ops.MATCHES, "(regexp_instr({0}, {1}) = 1)");
        this.add(Ops.MATCHES_IC, "(regex_instr({0l}, {1}) = 1)");
        this.add(Ops.MOD, "{0} mod {1}");
        this.add(Ops.MathOps.LOG, "(ln({0}) / ln({1}))");
        this.add(Ops.MathOps.RANDOM, "cast(random(0, 1000000000) as numeric(20,10))/1000000000");
        this.add(Ops.MathOps.COT, "(cos({0}) / sin({0}))");
        this.add(Ops.MathOps.COTH, "(exp({0*'2'}) + 1) / (exp({0*'2'}) - 1)");
        this.add(Ops.DateTimeOps.DATE, "cast({0} as date)");
        this.add(Ops.DateTimeOps.WEEK, "(td_week_of_year({0}) + 1)");
        this.add(Ops.DateTimeOps.DAY_OF_WEEK, "td_day_of_week({0})");
        this.add(Ops.DateTimeOps.DAY_OF_YEAR, "td_day_of_year({0})");
        this.add(Ops.DateTimeOps.YEAR_WEEK, "(extract (year from {0}) * 100 + td_week_of_year({0}))");
        this.add(Ops.DateTimeOps.ADD_YEARS, "{0} + interval '{1s}' year");
        this.add(Ops.DateTimeOps.ADD_MONTHS, "{0} + interval '{1s}' month");
        this.add(Ops.DateTimeOps.ADD_DAYS, "{0} + interval '{1s}' day");
        this.add(Ops.DateTimeOps.DIFF_YEARS, "cast((({1} - {0}) year) as integer)");
        this.add(Ops.DateTimeOps.DIFF_MONTHS, "cast((({1} - {0}) month) as integer)");
        this.add(Ops.DateTimeOps.DIFF_DAYS, "({1} - {0})");
        this.add(Ops.DateTimeOps.TRUNC_YEAR, "trunc({0}, 'year')");
        this.add(Ops.DateTimeOps.TRUNC_MONTH, "trunc({0}, 'month')");
        this.add(Ops.DateTimeOps.TRUNC_WEEK, "trunc({0}, 'w')");
        this.add(Ops.DateTimeOps.TRUNC_DAY, "trunc({0}, 'dd')");
        this.add(Ops.DateTimeOps.TRUNC_HOUR, "trunc({0}, 'hh24')");
        this.add(Ops.DateTimeOps.TRUNC_MINUTE, "trunc({0}, 'mi')");
        this.add(Ops.DateTimeOps.TRUNC_SECOND, "{0}");
        this.addTypeNameToCode("byteint", -7, true);
        this.addTypeNameToCode("byteint", 16, true);
        this.addTypeNameToCode("byteint", -6, true);
        this.addTypeNameToCode("float", 8, true);
    }

    @Override
    public String getCastTypeNameForCode(int code) {
        switch (code) {
            case 12: {
                return "varchar(4000)";
            }
        }
        return super.getCastTypeNameForCode(code);
    }

    @Override
    protected void serializeModifiers(QueryMetadata metadata, SQLSerializer context) {
        QueryModifiers mod = metadata.getModifiers();
        context.append(this.limitOffsetStart);
        if (!metadata.getOrderBy().isEmpty()) {
            context.handleOrderBy(metadata.getOrderBy());
        } else {
            context.append("1");
        }
        context.append(")");
        if (mod.getLimit() == null) {
            context.handle(this.offsetTemplate, mod.getOffset());
        } else if (mod.getOffset() == null) {
            context.handle(this.limitTemplate, mod.getLimit());
        } else {
            context.handle(this.limitOffsetTemplate, mod.getOffset() + 1L, mod.getOffset() + mod.getLimit());
        }
    }
}

