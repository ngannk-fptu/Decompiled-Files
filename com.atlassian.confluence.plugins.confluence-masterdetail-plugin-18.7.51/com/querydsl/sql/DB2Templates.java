/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.sql;

import com.querydsl.core.QueryFlag;
import com.querydsl.core.QueryMetadata;
import com.querydsl.core.QueryModifiers;
import com.querydsl.core.types.AppendingFactoryExpression;
import com.querydsl.core.types.FactoryExpressionUtils;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.sql.Keywords;
import com.querydsl.sql.SQLExpressions;
import com.querydsl.sql.SQLOps;
import com.querydsl.sql.SQLSerializer;
import com.querydsl.sql.SQLTemplates;
import com.querydsl.sql.WindowFunction;

public class DB2Templates
extends SQLTemplates {
    public static final DB2Templates DEFAULT = new DB2Templates();
    private String limitTemplate = "\nfetch first {0s} rows only";
    private String outerQueryStart = "select * from (\n  ";
    private String outerQueryEnd = ") a where ";
    private String limitOffsetTemplate = "rn > {0} and rn <= {1}";
    private String offsetTemplate = "rn > {0}";
    private String outerQuerySuffix = " order by rn";

    public static SQLTemplates.Builder builder() {
        return new SQLTemplates.Builder(){

            @Override
            protected SQLTemplates build(char escape, boolean quote) {
                return new DB2Templates(escape, quote);
            }
        };
    }

    public DB2Templates() {
        this('\\', false);
    }

    public DB2Templates(boolean quote) {
        this('\\', quote);
    }

    public DB2Templates(char escape, boolean quote) {
        super(Keywords.DB2, "\"", escape, quote);
        this.setDummyTable("sysibm.sysdummy1");
        this.setAutoIncrement(" generated always as identity");
        this.setFunctionJoinsWrapped(true);
        this.setDefaultValues("\nvalues (default)");
        this.setNullsFirst(null);
        this.setNullsLast(null);
        this.setPrecedence(30, Ops.CONCAT);
        this.setPrecedence(49, Ops.EQ, Ops.EQ_IGNORE_CASE, Ops.NE, Ops.LT, Ops.GT, Ops.LOE, Ops.GOE);
        this.setPrecedence(50, Ops.IS_NULL, Ops.IS_NOT_NULL, Ops.LIKE, Ops.LIKE_ESCAPE, Ops.BETWEEN, Ops.IN, Ops.NOT_IN, Ops.EXISTS);
        this.setPrecedence(50, OTHER_LIKE_CASES);
        this.add(SQLOps.NEXTVAL, "next value for {0s}");
        this.add(Ops.MathOps.RANDOM, "rand()");
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
        this.add(Ops.DateTimeOps.ADD_YEARS, "{0} + {1} years");
        this.add(Ops.DateTimeOps.ADD_MONTHS, "{0} + {1} months");
        this.add(Ops.DateTimeOps.ADD_WEEKS, "{0} + {1} weeks");
        this.add(Ops.DateTimeOps.ADD_DAYS, "{0} + {1} days");
        this.add(Ops.DateTimeOps.ADD_HOURS, "{0} + {1} hours");
        this.add(Ops.DateTimeOps.ADD_MINUTES, "{0} + {1} minutes");
        this.add(Ops.DateTimeOps.ADD_SECONDS, "{0} + {1} seconds");
        this.add(Ops.DateTimeOps.DIFF_YEARS, "timestampdiff(256, char(timestamp({1}) - timestamp({0})))");
        this.add(Ops.DateTimeOps.DIFF_MONTHS, "timestampdiff(64, char(timestamp({1}) - timestamp({0})))");
        this.add(Ops.DateTimeOps.DIFF_WEEKS, "timestampdiff(32, char(timestamp({1}) - timestamp({0})))");
        this.add(Ops.DateTimeOps.DIFF_DAYS, "timestampdiff(16, char(timestamp({1}) - timestamp({0})))");
        this.add(Ops.DateTimeOps.DIFF_HOURS, "timestampdiff(8, char(timestamp({1}) - timestamp({0})))");
        this.add(Ops.DateTimeOps.DIFF_MINUTES, "timestampdiff(4, char(timestamp({1}) - timestamp({0})))");
        this.add(Ops.DateTimeOps.DIFF_SECONDS, "timestampdiff(2, char(timestamp({1}) - timestamp({0})))");
        this.add(Ops.DateTimeOps.TRUNC_YEAR, "trunc_timestamp({0}, 'year')");
        this.add(Ops.DateTimeOps.TRUNC_MONTH, "trunc_timestamp({0}, 'month')");
        this.add(Ops.DateTimeOps.TRUNC_WEEK, "trunc_timestamp({0}, 'ww')");
        this.add(Ops.DateTimeOps.TRUNC_DAY, "trunc_timestamp({0}, 'dd')");
        this.add(Ops.DateTimeOps.TRUNC_HOUR, "trunc_timestamp({0}, 'hh')");
        this.add(Ops.DateTimeOps.TRUNC_MINUTE, "trunc_timestamp({0}, 'mi')");
        this.add(Ops.DateTimeOps.TRUNC_SECOND, "trunc_timestamp({0}, 'ss')");
        this.addTypeNameToCode("smallint", 16, true);
        this.addTypeNameToCode("smallint", -6, true);
        this.addTypeNameToCode("long varchar for bit data", -4);
        this.addTypeNameToCode("varchar () for bit data", -3);
        this.addTypeNameToCode("char () for bit data", -2);
        this.addTypeNameToCode("long varchar", -1, true);
        this.addTypeNameToCode("object", 2000, true);
        this.addTypeNameToCode("xml", 2009, true);
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
    public void serialize(QueryMetadata metadata, boolean forCountRow, SQLSerializer context) {
        if (!forCountRow && metadata.getModifiers().isRestricting() && !metadata.getJoins().isEmpty()) {
            QueryModifiers mod = metadata.getModifiers();
            if (mod.getOffset() == null) {
                context.serializeForQuery(metadata, forCountRow);
                context.handle(this.limitTemplate, mod.getLimit());
            } else {
                context.append(this.outerQueryStart);
                metadata = metadata.clone();
                WindowFunction<Long> rn = SQLExpressions.rowNumber().over();
                for (OrderSpecifier<?> os : metadata.getOrderBy()) {
                    rn.orderBy(os);
                }
                AppendingFactoryExpression<?> pr = Projections.appending(metadata.getProjection(), rn.as("rn"));
                metadata.setProjection(FactoryExpressionUtils.wrap(pr));
                metadata.clearOrderBy();
                context.serializeForQuery(metadata, forCountRow);
                context.append(this.outerQueryEnd);
                if (mod.getLimit() == null) {
                    context.handle(this.offsetTemplate, mod.getOffset());
                } else {
                    context.handle(this.limitOffsetTemplate, mod.getOffset(), mod.getLimit() + mod.getOffset());
                }
                context.append(this.outerQuerySuffix);
            }
        } else {
            context.serializeForQuery(metadata, forCountRow);
        }
        if (!metadata.getFlags().isEmpty()) {
            context.serialize(QueryFlag.Position.END, metadata.getFlags());
        }
    }

    @Override
    protected void serializeModifiers(QueryMetadata metadata, SQLSerializer context) {
    }
}

