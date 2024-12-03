/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.sql;

import com.querydsl.core.QueryFlag;
import com.querydsl.core.QueryMetadata;
import com.querydsl.core.QueryModifiers;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Path;
import com.querydsl.sql.Keywords;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.SQLOps;
import com.querydsl.sql.SQLSerializer;
import com.querydsl.sql.SQLTemplates;
import com.querydsl.sql.dml.SQLInsertBatch;
import java.util.List;
import java.util.Map;

public class OracleTemplates
extends SQLTemplates {
    public static final OracleTemplates DEFAULT = new OracleTemplates();
    private String outerQueryStart = "select * from (\n select a.*, rownum rn from (\n  ";
    private String outerQueryEnd = "\n ) a) where ";
    private String limitQueryStart = "select * from (\n  ";
    private String limitQueryEnd = "\n) where rownum <= {0}";
    private String limitOffsetTemplate = "rn > {0s} and rownum <= {1s}";
    private String offsetTemplate = "rn > {0}";
    private String bulkInsertTemplate = "insert all";
    private String bulkInsertSeparator = " into ";

    public static SQLTemplates.Builder builder() {
        return new SQLTemplates.Builder(){

            @Override
            protected SQLTemplates build(char escape, boolean quote) {
                return new OracleTemplates(escape, quote);
            }
        };
    }

    public OracleTemplates() {
        this('\\', false);
    }

    public OracleTemplates(boolean quote) {
        this('\\', quote);
    }

    public OracleTemplates(char escape, boolean quote) {
        super(Keywords.ORACLE, "\"", escape, quote);
        this.setParameterMetadataAvailable(false);
        this.setBatchCountViaGetUpdateCount(true);
        this.setWithRecursive("with ");
        this.setCountViaAnalytics(true);
        this.setListMaxSize(1000);
        this.setPrecedence(50, Ops.EQ, Ops.EQ_IGNORE_CASE, Ops.NE);
        this.setPrecedence(51, Ops.IS_NULL, Ops.IS_NOT_NULL, Ops.LIKE, Ops.LIKE_ESCAPE, Ops.BETWEEN, Ops.IN, Ops.NOT_IN, Ops.EXISTS);
        this.setPrecedence(51, OTHER_LIKE_CASES);
        this.add(Ops.ALIAS, "{0} {1}");
        this.add(SQLOps.NEXTVAL, "{0s}.nextval");
        this.add(Ops.INDEX_OF, "instrb({0},{1})-1", 40);
        this.add(Ops.INDEX_OF_2ARGS, "instrb({0},{1},{2+'1'})-1", 40);
        this.add(Ops.MATCHES, "regexp_like({0},{1})", -1);
        this.add(Ops.StringOps.LOCATE, "instr({1},{0})");
        this.add(Ops.StringOps.LOCATE2, "instr({1},{0},{2s})");
        this.add(Ops.StringOps.LEFT, "substr({0},1,{1})");
        this.add(Ops.StringOps.RIGHT, "substr({0},-{1s},length({0}))");
        this.add(SQLOps.GROUP_CONCAT, "listagg({0},',')");
        this.add(SQLOps.GROUP_CONCAT2, "listagg({0},{1})");
        this.add(Ops.MathOps.CEIL, "ceil({0})");
        this.add(Ops.MathOps.RANDOM, "dbms_random.value");
        this.add(Ops.MathOps.LN, "ln({0})");
        this.add(Ops.MathOps.LOG, "log({1},{0})");
        this.add(Ops.MathOps.COT, "(cos({0}) / sin({0}))");
        this.add(Ops.MathOps.COTH, "(exp({0*'2'}) + 1) / (exp({0*'2'}) - 1)");
        this.add(Ops.MathOps.DEG, "({0*'180.0'} / 3.141592653589793)");
        this.add(Ops.MathOps.RAD, "({0*'3.141592653589793'} / 180.0)");
        this.add(Ops.DateTimeOps.DATE, "trunc({0})");
        this.add(Ops.DateTimeOps.WEEK, "to_number(to_char({0},'WW'))");
        this.add(Ops.DateTimeOps.DAY_OF_WEEK, "to_number(to_char({0},'D')) + 1");
        this.add(Ops.DateTimeOps.DAY_OF_YEAR, "to_number(to_char({0},'DDD'))");
        this.add(Ops.DateTimeOps.YEAR_WEEK, "to_number(to_char({0},'IYYY') || to_char({0},'IW'))");
        this.add(Ops.DateTimeOps.ADD_YEARS, "{0} + interval '{1s}' year");
        this.add(Ops.DateTimeOps.ADD_MONTHS, "{0} + interval '{1s}' month");
        this.add(Ops.DateTimeOps.ADD_WEEKS, "{0} + interval '{1s}' week");
        this.add(Ops.DateTimeOps.ADD_DAYS, "{0} + interval '{1s}' day");
        this.add(Ops.DateTimeOps.ADD_HOURS, "{0} + interval '{1s}' hour");
        this.add(Ops.DateTimeOps.ADD_MINUTES, "{0} + interval '{1s}' minute");
        this.add(Ops.DateTimeOps.ADD_SECONDS, "{0} + interval '{1s}' second");
        this.add(Ops.DateTimeOps.DIFF_YEARS, "trunc(months_between({1}, {0}) / 12)");
        this.add(Ops.DateTimeOps.DIFF_MONTHS, "trunc(months_between({1}, {0}))");
        this.add(Ops.DateTimeOps.DIFF_WEEKS, "round(({1} - {0}) / 7)");
        this.add(Ops.DateTimeOps.DIFF_DAYS, "round({1} - {0})");
        this.add(Ops.DateTimeOps.DIFF_HOURS, "round(({1} - {0}) * 24)");
        this.add(Ops.DateTimeOps.DIFF_MINUTES, "round(({1} - {0}) * 1440)");
        this.add(Ops.DateTimeOps.DIFF_SECONDS, "round(({1} - {0}) * 86400)");
        this.add(Ops.DateTimeOps.TRUNC_YEAR, "trunc({0}, 'year')");
        this.add(Ops.DateTimeOps.TRUNC_MONTH, "trunc({0}, 'month')");
        this.add(Ops.DateTimeOps.TRUNC_WEEK, "trunc({0}, 'w')");
        this.add(Ops.DateTimeOps.TRUNC_DAY, "trunc({0}, 'dd')");
        this.add(Ops.DateTimeOps.TRUNC_HOUR, "trunc({0}, 'hh')");
        this.add(Ops.DateTimeOps.TRUNC_MINUTE, "trunc({0}, 'mi')");
        this.add(Ops.DateTimeOps.TRUNC_SECOND, "{0}");
        this.addTypeNameToCode("intervalds", -104);
        this.addTypeNameToCode("intervalym", -103);
        this.addTypeNameToCode("timestamp with local time zone", -102);
        this.addTypeNameToCode("timestamp with time zone", -101);
        this.addTypeNameToCode("long raw", -4);
        this.addTypeNameToCode("raw", -3);
        this.addTypeNameToCode("long", -1);
        this.addTypeNameToCode("varchar2", 12);
        this.addTypeNameToCode("number(1,0)", 16, true);
        this.addTypeNameToCode("number(3,0)", -6, true);
        this.addTypeNameToCode("number(5,0)", 5, true);
        this.addTypeNameToCode("number(10,0)", 4, true);
        this.addTypeNameToCode("number(19,0)", -5, true);
        this.addTypeNameToCode("binary_float", 6, true);
        this.addTypeNameToCode("binary_double", 8, true);
    }

    @Override
    public String getCastTypeNameForCode(int code) {
        switch (code) {
            case 8: {
                return "double precision";
            }
            case 12: {
                return "varchar(4000 char)";
            }
        }
        return super.getCastTypeNameForCode(code);
    }

    @Override
    public String serialize(String literal, int jdbcType) {
        switch (jdbcType) {
            case 93: 
            case 2014: {
                return "timestamp '" + literal + "'";
            }
            case 91: {
                return "date '" + literal + "'";
            }
            case 92: 
            case 2013: {
                return "timestamp '1970-01-01 " + literal + "'";
            }
        }
        return super.serialize(literal, jdbcType);
    }

    @Override
    public void serialize(QueryMetadata metadata, boolean forCountRow, SQLSerializer context) {
        if (!forCountRow && metadata.getModifiers().isRestricting() && !metadata.getJoins().isEmpty()) {
            QueryModifiers mod = metadata.getModifiers();
            if (mod.getOffset() == null) {
                context.append(this.limitQueryStart);
                context.serializeForQuery(metadata, forCountRow);
                context.handle(this.limitQueryEnd, mod.getLimit());
            } else {
                context.append(this.outerQueryStart);
                context.serializeForQuery(metadata, forCountRow);
                context.append(this.outerQueryEnd);
                if (mod.getLimit() == null) {
                    context.handle(this.offsetTemplate, mod.getOffset());
                } else {
                    context.handle(this.limitOffsetTemplate, mod.getOffset(), mod.getLimit());
                }
            }
        } else {
            context.serializeForQuery(metadata, forCountRow);
        }
        if (!metadata.getFlags().isEmpty()) {
            context.serialize(QueryFlag.Position.END, metadata.getFlags());
        }
    }

    @Override
    public void serializeDelete(QueryMetadata metadata, RelationalPath<?> entity, SQLSerializer context) {
        context.serializeForDelete(metadata, entity);
        if (metadata.getModifiers().isRestricting()) {
            this.serializeModifiersForDML(metadata, context);
        }
        if (!metadata.getFlags().isEmpty()) {
            context.serialize(QueryFlag.Position.END, metadata.getFlags());
        }
    }

    @Override
    public void serializeInsert(QueryMetadata metadata, RelationalPath<?> entity, List<SQLInsertBatch> batches, SQLSerializer context) {
        context.append(this.bulkInsertTemplate);
        metadata.addFlag(new QueryFlag(QueryFlag.Position.START_OVERRIDE, this.bulkInsertSeparator));
        for (SQLInsertBatch batch : batches) {
            this.serializeInsert(metadata, entity, batch.getColumns(), batch.getValues(), batch.getSubQuery(), context);
        }
        context.append(" select * from dual");
    }

    @Override
    public void serializeUpdate(QueryMetadata metadata, RelationalPath<?> entity, Map<Path<?>, Expression<?>> updates, SQLSerializer context) {
        context.serializeForUpdate(metadata, entity, updates);
        if (metadata.getModifiers().isRestricting()) {
            this.serializeModifiersForDML(metadata, context);
        }
        if (!metadata.getFlags().isEmpty()) {
            context.serialize(QueryFlag.Position.END, metadata.getFlags());
        }
    }

    private void serializeModifiersForDML(QueryMetadata metadata, SQLSerializer context) {
        if (metadata.getWhere() != null) {
            context.append(" and ");
        } else {
            context.append(this.getWhere());
        }
        context.append("rownum <= ");
        context.visitConstant(metadata.getModifiers().getLimit());
    }

    @Override
    protected void serializeModifiers(QueryMetadata metadata, SQLSerializer context) {
    }
}

