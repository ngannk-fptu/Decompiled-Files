/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.sql;

import com.querydsl.core.types.Ops;
import com.querydsl.sql.Keywords;
import com.querydsl.sql.SQLTemplates;

public class H2Templates
extends SQLTemplates {
    public static final H2Templates DEFAULT = new H2Templates();

    public static SQLTemplates.Builder builder() {
        return new SQLTemplates.Builder(){

            @Override
            protected SQLTemplates build(char escape, boolean quote) {
                return new H2Templates(escape, quote);
            }
        };
    }

    public H2Templates() {
        this('\\', false);
    }

    public H2Templates(boolean quote) {
        this('\\', quote);
    }

    public H2Templates(char escape, boolean quote) {
        super(Keywords.H2, "\"", escape, quote);
        this.setNativeMerge(true);
        this.setMaxLimit(29);
        this.setLimitRequired(true);
        this.setCountDistinctMultipleColumns(true);
        this.setPrecedence(41, Ops.CONCAT);
        this.setPrecedence(50, Ops.EQ, Ops.EQ_IGNORE_CASE, Ops.NE);
        this.add(Ops.MOD, "{0} % {1}", 30);
        this.add(Ops.MathOps.ROUND, "round({0},0)");
        this.add(Ops.TRIM, "trim(both from {0})");
        this.add(Ops.DateTimeOps.DAY_OF_WEEK, "day_of_week({0})");
        this.add(Ops.MathOps.LN, "log({0})");
        this.add(Ops.MathOps.LOG, "(log({0}) / log({1}))");
        this.add(Ops.MathOps.COTH, "(cosh({0}) / sinh({0}))");
        this.add(Ops.DateTimeOps.DATE, "convert({0}, date)");
        this.addTypeNameToCode("result_set", -10);
        this.addTypeNameToCode("identity", -5);
        this.addTypeNameToCode("uuid", -2);
        this.addTypeNameToCode("serial", 4);
        this.addTypeNameToCode("varchar_ignorecase", 12);
        this.add(Ops.DateTimeOps.TRUNC_YEAR, "parsedatetime(formatdatetime({0},'yyyy'),'yyyy')");
        this.add(Ops.DateTimeOps.TRUNC_MONTH, "parsedatetime(formatdatetime({0},'yyyy-MM'),'yyyy-MM')");
        this.add(Ops.DateTimeOps.TRUNC_WEEK, "parsedatetime(formatdatetime({0},'YYYY-ww'),'YYYY-ww')");
        this.add(Ops.DateTimeOps.TRUNC_DAY, "parsedatetime(formatdatetime({0},'yyyy-MM-dd'),'yyyy-MM-dd')");
        this.add(Ops.DateTimeOps.TRUNC_HOUR, "parsedatetime(formatdatetime({0},'yyyy-MM-dd HH'),'yyyy-MM-dd HH')");
        this.add(Ops.DateTimeOps.TRUNC_MINUTE, "parsedatetime(formatdatetime({0},'yyyy-MM-dd HH:mm'),'yyyy-MM-dd HH:mm')");
        this.add(Ops.DateTimeOps.TRUNC_SECOND, "parsedatetime(formatdatetime({0},'yyyy-MM-dd HH:mm:ss'),'yyyy-MM-dd HH:mm:ss')");
    }
}

