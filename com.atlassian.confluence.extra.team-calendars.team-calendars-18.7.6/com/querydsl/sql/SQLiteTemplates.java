/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.time.format.DateTimeFormat
 *  org.joda.time.format.DateTimeFormatter
 */
package com.querydsl.sql;

import com.querydsl.core.types.Ops;
import com.querydsl.sql.Keywords;
import com.querydsl.sql.SQLOps;
import com.querydsl.sql.SQLTemplates;
import com.querydsl.sql.types.BigDecimalAsDoubleType;
import com.querydsl.sql.types.BigIntegerAsLongType;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class SQLiteTemplates
extends SQLTemplates {
    private static final DateTimeFormatter dateFormatter = DateTimeFormat.forPattern((String)"yyyy-MM-dd");
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern((String)"yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter timeFormatter = DateTimeFormat.forPattern((String)"HH:mm:ss");
    public static final SQLiteTemplates DEFAULT = new SQLiteTemplates();

    public static SQLTemplates.Builder builder() {
        return new SQLTemplates.Builder(){

            @Override
            protected SQLTemplates build(char escape, boolean quote) {
                return new SQLiteTemplates(escape, quote);
            }
        };
    }

    public SQLiteTemplates() {
        this('\\', false);
    }

    public SQLiteTemplates(boolean quote) {
        this('\\', quote);
    }

    public SQLiteTemplates(char escape, boolean quote) {
        super(Keywords.SQLITE, "\"", escape, quote);
        this.setDummyTable(null);
        this.addCustomType(BigDecimalAsDoubleType.DEFAULT);
        this.addCustomType(BigIntegerAsLongType.DEFAULT);
        this.setUnionsWrapped(false);
        this.setLimitRequired(true);
        this.setNullsFirst(null);
        this.setNullsLast(null);
        this.setDefaultValues("\ndefault values");
        this.setArraysSupported(false);
        this.setBatchToBulkSupported(false);
        this.setPrecedence(49, Ops.LT, Ops.GT, Ops.LOE, Ops.GOE);
        this.setPrecedence(50, Ops.EQ, Ops.EQ_IGNORE_CASE, Ops.NE);
        this.add(Ops.MOD, "{0} % {1}", 30);
        this.add(Ops.INDEX_OF, "charindex({1},{0},1)-1", 40);
        this.add(Ops.INDEX_OF_2ARGS, "charindex({1},{0},{2s}+1)-1", 40);
        this.add(Ops.StringOps.LOCATE, "charindex({0},{1})");
        this.add(Ops.StringOps.LOCATE2, "charindex({0},{1},{2s})");
        this.add(Ops.DateTimeOps.YEAR, "cast(strftime('%Y',{0} / 1000, 'unixepoch', 'localtime') as integer)");
        this.add(Ops.DateTimeOps.MONTH, "cast(strftime('%m',{0} / 1000, 'unixepoch', 'localtime') as integer)");
        this.add(Ops.DateTimeOps.WEEK, "cast(strftime('%W',{0} / 1000, 'unixepoch', 'localtime') as integer) + 1");
        this.add(Ops.DateTimeOps.DAY_OF_MONTH, "cast(strftime('%d',{0} / 1000, 'unixepoch', 'localtime') as integer)");
        this.add(Ops.DateTimeOps.DAY_OF_WEEK, "cast(strftime('%w',{0} / 1000, 'unixepoch', 'localtime') as integer) + 1");
        this.add(Ops.DateTimeOps.DAY_OF_YEAR, "cast(strftime('%j',{0} / 1000, 'unixepoch', 'localtime') as integer)");
        this.add(Ops.DateTimeOps.HOUR, "cast(strftime('%H',{0} / 1000, 'unixepoch', 'localtime') as integer)");
        this.add(Ops.DateTimeOps.MINUTE, "cast(strftime('%M',{0} / 1000, 'unixepoch', 'localtime') as integer)");
        this.add(Ops.DateTimeOps.SECOND, "cast(strftime('%S',{0} / 1000, 'unixepoch', 'localtime') as integer)");
        this.add(Ops.DateTimeOps.YEAR_MONTH, "cast(strftime('%Y',{0} / 1000, 'unixepoch', 'localtime') * 100 + strftime('%m',{0} / 1000, 'unixepoch', 'localtime') as integer)");
        this.add(Ops.DateTimeOps.YEAR_WEEK, "cast(strftime('%Y%W',{0} / 1000, 'unixepoch', 'localtime') as integer)");
        this.add(Ops.DateTimeOps.ADD_YEARS, "date({0}, '+{1s} year')");
        this.add(Ops.DateTimeOps.ADD_MONTHS, "date({0}, '+{1s} month')");
        this.add(Ops.DateTimeOps.ADD_WEEKS, "date({0}, '+{1s} week')");
        this.add(Ops.DateTimeOps.ADD_DAYS, "date({0}, '+{1s} day')");
        this.add(Ops.DateTimeOps.ADD_HOURS, "date({0}, '+{1s} hour')");
        this.add(Ops.DateTimeOps.ADD_MINUTES, "date({0}, '+{1s} minute')");
        this.add(Ops.DateTimeOps.ADD_SECONDS, "date({0}, '+{1s} second')");
        this.add(Ops.MathOps.RANDOM, "random()");
        this.add(Ops.MathOps.RANDOM2, "random({0})");
        this.add(Ops.MathOps.LN, "log({0})");
        this.add(Ops.MathOps.LOG, "log({0}) / log({1})", 30);
        this.add(SQLOps.GROUP_CONCAT2, "group_concat({0},{1})");
        this.addTypeNameToCode("text", 12);
    }

    @Override
    public String serialize(String literal, int jdbcType) {
        switch (jdbcType) {
            case 93: 
            case 2014: {
                return String.valueOf(dateTimeFormatter.parseDateTime(literal).getMillis());
            }
            case 91: {
                return String.valueOf(dateFormatter.parseDateTime(literal).getMillis());
            }
            case 92: 
            case 2013: {
                return String.valueOf(timeFormatter.parseDateTime(literal).getMillis());
            }
        }
        return super.serialize(literal, jdbcType);
    }
}

