/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.types;

import com.querydsl.core.types.Operator;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.PathType;
import com.querydsl.core.types.Template;
import com.querydsl.core.types.TemplateFactory;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.Map;
import javax.annotation.Nullable;

public class Templates {
    public static final Templates DEFAULT = new Templates();
    private final Map<Operator, Template> templates = new IdentityHashMap<Operator, Template>(150);
    private final Map<Operator, Integer> precedence = new IdentityHashMap<Operator, Integer>(150);
    private final TemplateFactory templateFactory;
    private final char escape;

    protected Templates() {
        this('\\');
    }

    protected Templates(char escape) {
        this.escape = escape;
        this.templateFactory = new TemplateFactory(escape){

            @Override
            public String escapeForLike(String str) {
                return Templates.this.escapeForLike(str);
            }
        };
        this.add(Ops.LIST, "{0}, {1}", 70);
        this.add(Ops.SET, "{0}, {1}", 70);
        this.add(Ops.SINGLETON, "{0}", 70);
        this.add(Ops.WRAPPED, "({0})");
        this.add(Ops.ORDER, "order()");
        this.add(Ops.AND, "{0} && {1}", 90);
        this.add(Ops.NOT, "!{0}", 10);
        this.add(Ops.OR, "{0} || {1}", 110);
        this.add(Ops.XNOR, "{0} xnor {1}", 100);
        this.add(Ops.XOR, "{0} xor {1}", 100);
        this.add(Ops.COL_IS_EMPTY, "empty({0})");
        this.add(Ops.COL_SIZE, "size({0})");
        this.add(Ops.ARRAY_SIZE, "size({0})");
        this.add(Ops.MAP_SIZE, "size({0})");
        this.add(Ops.MAP_IS_EMPTY, "empty({0})");
        this.add(Ops.CONTAINS_KEY, "containsKey({0},{1})");
        this.add(Ops.CONTAINS_VALUE, "containsValue({0},{1})");
        this.add(Ops.BETWEEN, "{0} between {1} and {2}", 50);
        this.add(Ops.GOE, "{0} >= {1}", 50);
        this.add(Ops.GT, "{0} > {1}", 50);
        this.add(Ops.LOE, "{0} <= {1}", 50);
        this.add(Ops.LT, "{0} < {1}", 50);
        this.add(Ops.NEGATE, "-{0}", 20);
        this.add(Ops.ADD, "{0} + {1}", 40);
        this.add(Ops.DIV, "{0} / {1}", 30);
        this.add(Ops.MOD, "{0} % {1}", 30);
        this.add(Ops.MULT, "{0} * {1}", 30);
        this.add(Ops.SUB, "{0} - {1}", 40);
        this.add(Ops.EQ, "{0} = {1}", 60);
        this.add(Ops.EQ_IGNORE_CASE, "eqIc({0},{1})", 60);
        this.add(Ops.INSTANCE_OF, "{0} instanceof {1}", 50);
        this.add(Ops.NE, "{0} != {1}", 60);
        this.add(Ops.IN, "{0} in {1}", 50);
        this.add(Ops.NOT_IN, "{0} not in {1}", 50);
        this.add(Ops.IS_NULL, "{0} is null", 50);
        this.add(Ops.IS_NOT_NULL, "{0} is not null", 50);
        this.add(Ops.ALIAS, "{0} as {1}", 0);
        this.add(Ops.NUMCAST, "cast({0},{1})");
        this.add(Ops.STRING_CAST, "str({0})");
        this.add(Ops.CONCAT, "{0} + {1}", 40);
        this.add(Ops.LOWER, "lower({0})");
        this.add(Ops.SUBSTR_1ARG, "substring({0},{1})");
        this.add(Ops.SUBSTR_2ARGS, "substring({0},{1},{2})");
        this.add(Ops.TRIM, "trim({0})");
        this.add(Ops.UPPER, "upper({0})");
        this.add(Ops.MATCHES, "matches({0},{1})");
        this.add(Ops.MATCHES_IC, "matchesIgnoreCase({0},{1})");
        this.add(Ops.STARTS_WITH, "startsWith({0},{1})");
        this.add(Ops.STARTS_WITH_IC, "startsWithIgnoreCase({0},{1})");
        this.add(Ops.ENDS_WITH, "endsWith({0},{1})");
        this.add(Ops.ENDS_WITH_IC, "endsWithIgnoreCase({0},{1})");
        this.add(Ops.STRING_CONTAINS, "contains({0},{1})");
        this.add(Ops.STRING_CONTAINS_IC, "containsIc({0},{1})");
        this.add(Ops.CHAR_AT, "charAt({0},{1})");
        this.add(Ops.STRING_LENGTH, "length({0})");
        this.add(Ops.INDEX_OF, "indexOf({0},{1})");
        this.add(Ops.INDEX_OF_2ARGS, "indexOf({0},{1},{2})");
        this.add(Ops.STRING_IS_EMPTY, "empty({0})");
        this.add(Ops.LIKE, "{0} like {1}", 50);
        this.add(Ops.LIKE_IC, "{0l} like {1l}", 50);
        this.add(Ops.LIKE_ESCAPE, "{0} like {1} escape '{2s}'", 50);
        this.add(Ops.LIKE_ESCAPE_IC, "{0l} like {1l} escape '{2s}'", 50);
        this.add(Ops.StringOps.LEFT, "left({0},{1})");
        this.add(Ops.StringOps.RIGHT, "right({0},{1})");
        this.add(Ops.StringOps.LTRIM, "ltrim({0})");
        this.add(Ops.StringOps.RTRIM, "rtrim({0})");
        this.add(Ops.StringOps.LOCATE, "locate({0},{1})");
        this.add(Ops.StringOps.LOCATE2, "locate({0},{1},{2s})");
        this.add(Ops.StringOps.LPAD, "lpad({0},{1})");
        this.add(Ops.StringOps.RPAD, "rpad({0},{1})");
        this.add(Ops.StringOps.LPAD2, "lpad({0},{1},'{2s}')");
        this.add(Ops.StringOps.RPAD2, "rpad({0},{1},'{2s}')");
        this.add(Ops.DateTimeOps.SYSDATE, "sysdate");
        this.add(Ops.DateTimeOps.CURRENT_DATE, "current_date()");
        this.add(Ops.DateTimeOps.CURRENT_TIME, "current_time()");
        this.add(Ops.DateTimeOps.CURRENT_TIMESTAMP, "current_timestamp()");
        this.add(Ops.DateTimeOps.DATE, "date({0})");
        this.add(Ops.DateTimeOps.MILLISECOND, "millisecond({0})");
        this.add(Ops.DateTimeOps.SECOND, "second({0})");
        this.add(Ops.DateTimeOps.MINUTE, "minute({0})");
        this.add(Ops.DateTimeOps.HOUR, "hour({0})");
        this.add(Ops.DateTimeOps.WEEK, "week({0})");
        this.add(Ops.DateTimeOps.MONTH, "month({0})");
        this.add(Ops.DateTimeOps.YEAR, "year({0})");
        this.add(Ops.DateTimeOps.YEAR_MONTH, "yearMonth({0})");
        this.add(Ops.DateTimeOps.YEAR_WEEK, "yearweek({0})");
        this.add(Ops.DateTimeOps.DAY_OF_WEEK, "dayofweek({0})");
        this.add(Ops.DateTimeOps.DAY_OF_MONTH, "dayofmonth({0})");
        this.add(Ops.DateTimeOps.DAY_OF_YEAR, "dayofyear({0})");
        this.add(Ops.DateTimeOps.ADD_YEARS, "add_years({0},{1})");
        this.add(Ops.DateTimeOps.ADD_MONTHS, "add_months({0},{1})");
        this.add(Ops.DateTimeOps.ADD_WEEKS, "add_weeks({0},{1})");
        this.add(Ops.DateTimeOps.ADD_DAYS, "add_days({0},{1})");
        this.add(Ops.DateTimeOps.ADD_HOURS, "add_hours({0},{1})");
        this.add(Ops.DateTimeOps.ADD_MINUTES, "add_minutes({0},{1})");
        this.add(Ops.DateTimeOps.ADD_SECONDS, "add_seconds({0},{1})");
        this.add(Ops.DateTimeOps.DIFF_YEARS, "diff_years({0},{1})");
        this.add(Ops.DateTimeOps.DIFF_MONTHS, "diff_months({0},{1})");
        this.add(Ops.DateTimeOps.DIFF_WEEKS, "diff_weeks({0},{1})");
        this.add(Ops.DateTimeOps.DIFF_DAYS, "diff_days({0},{1})");
        this.add(Ops.DateTimeOps.DIFF_HOURS, "diff_hours({0},{1})");
        this.add(Ops.DateTimeOps.DIFF_MINUTES, "diff_minutes({0},{1})");
        this.add(Ops.DateTimeOps.DIFF_SECONDS, "diff_seconds({0},{1})");
        this.add(Ops.DateTimeOps.TRUNC_YEAR, "trunc_year({0})");
        this.add(Ops.DateTimeOps.TRUNC_MONTH, "trunc_month({0})");
        this.add(Ops.DateTimeOps.TRUNC_WEEK, "trunc_week({0})");
        this.add(Ops.DateTimeOps.TRUNC_DAY, "trunc_day({0})");
        this.add(Ops.DateTimeOps.TRUNC_HOUR, "trunc_hour({0})");
        this.add(Ops.DateTimeOps.TRUNC_MINUTE, "trunc_minute({0})");
        this.add(Ops.DateTimeOps.TRUNC_SECOND, "trunc_second({0})");
        this.add(Ops.MathOps.ABS, "abs({0})");
        this.add(Ops.MathOps.ACOS, "acos({0})");
        this.add(Ops.MathOps.ASIN, "asin({0})");
        this.add(Ops.MathOps.ATAN, "atan({0})");
        this.add(Ops.MathOps.CEIL, "ceil({0})");
        this.add(Ops.MathOps.COS, "cos({0})");
        this.add(Ops.MathOps.COSH, "cosh({0})");
        this.add(Ops.MathOps.COT, "cot({0})");
        this.add(Ops.MathOps.COTH, "coth({0})");
        this.add(Ops.MathOps.DEG, "degrees({0})");
        this.add(Ops.MathOps.TAN, "tan({0})");
        this.add(Ops.MathOps.TANH, "tanh({0})");
        this.add(Ops.MathOps.SQRT, "sqrt({0})");
        this.add(Ops.MathOps.SIGN, "sign({0})");
        this.add(Ops.MathOps.SIN, "sin({0})");
        this.add(Ops.MathOps.SINH, "sinh({0})");
        this.add(Ops.MathOps.ROUND, "round({0})");
        this.add(Ops.MathOps.ROUND2, "round({0},{1})");
        this.add(Ops.MathOps.RAD, "radians({0})");
        this.add(Ops.MathOps.RANDOM, "random()");
        this.add(Ops.MathOps.RANDOM2, "random({0})");
        this.add(Ops.MathOps.POWER, "pow({0},{1})");
        this.add(Ops.MathOps.MIN, "min({0},{1})");
        this.add(Ops.MathOps.MAX, "max({0},{1})");
        this.add(Ops.MathOps.LOG, "log({0},{1})");
        this.add(Ops.MathOps.LN, "ln({0})");
        this.add(Ops.MathOps.FLOOR, "floor({0})");
        this.add(Ops.MathOps.EXP, "exp({0})");
        this.add(PathType.PROPERTY, "{0}.{1s}");
        this.add(PathType.VARIABLE, "{0s}");
        this.add(PathType.DELEGATE, "{0}");
        this.add(Ops.ORDINAL, "ordinal({0})");
        for (PathType type : new PathType[]{PathType.LISTVALUE, PathType.MAPVALUE, PathType.MAPVALUE_CONSTANT}) {
            this.add(type, "{0}.get({1})");
        }
        this.add(PathType.ARRAYVALUE, "{0}[{1}]");
        this.add(PathType.COLLECTION_ANY, "any({0})");
        this.add(PathType.LISTVALUE_CONSTANT, "{0}.get({1s})");
        this.add(PathType.ARRAYVALUE_CONSTANT, "{0}[{1s}]");
        this.add(Ops.CASE, "case {0} end", 70);
        this.add(Ops.CASE_WHEN, "when {0} then {1} {2}", 70);
        this.add(Ops.CASE_ELSE, "else {0}", 70);
        this.add(Ops.CASE_EQ, "case {0} {1} end", 70);
        this.add(Ops.CASE_EQ_WHEN, "when {1} then {2} {3}", 70);
        this.add(Ops.CASE_EQ_ELSE, "else {0}", 70);
        this.add(Ops.COALESCE, "coalesce({0})");
        this.add(Ops.NULLIF, "nullif({0},{1})");
        this.add(Ops.EXISTS, "exists {0}", 0);
        this.add(Ops.AggOps.BOOLEAN_ALL, "all({0})");
        this.add(Ops.AggOps.BOOLEAN_ANY, "any({0})");
        this.add(Ops.AggOps.AVG_AGG, "avg({0})");
        this.add(Ops.AggOps.MAX_AGG, "max({0})");
        this.add(Ops.AggOps.MIN_AGG, "min({0})");
        this.add(Ops.AggOps.SUM_AGG, "sum({0})");
        this.add(Ops.AggOps.COUNT_AGG, "count({0})");
        this.add(Ops.AggOps.COUNT_DISTINCT_AGG, "count(distinct {0})");
        this.add(Ops.AggOps.COUNT_DISTINCT_ALL_AGG, "count(distinct *)");
        this.add(Ops.AggOps.COUNT_ALL_AGG, "count(*)");
        this.add(Ops.QuantOps.AVG_IN_COL, "avg({0})");
        this.add(Ops.QuantOps.MAX_IN_COL, "max({0})");
        this.add(Ops.QuantOps.MIN_IN_COL, "min({0})");
        this.add(Ops.QuantOps.ANY, "any {0}");
        this.add(Ops.QuantOps.ALL, "all {0}");
    }

    protected final void add(Operator op, String pattern) {
        this.templates.put(op, this.templateFactory.create(pattern));
        if (!this.precedence.containsKey(op)) {
            this.precedence.put(op, -1);
        }
    }

    protected final void add(Operator op, String pattern, int pre) {
        this.templates.put(op, this.templateFactory.create(pattern));
        this.precedence.put(op, pre);
    }

    protected final void add(Map<Operator, String> ops) {
        for (Map.Entry<Operator, String> entry : ops.entrySet()) {
            this.add(entry.getKey(), entry.getValue());
        }
    }

    public final char getEscapeChar() {
        return this.escape;
    }

    protected String escapeForLike(String str) {
        StringBuilder rv = new StringBuilder(str.length() + 3);
        for (char ch : str.toCharArray()) {
            if (ch == this.escape || ch == '%' || ch == '_') {
                rv.append(this.escape);
            }
            rv.append(ch);
        }
        return rv.toString();
    }

    @Nullable
    public final Template getTemplate(Operator op) {
        return this.templates.get(op);
    }

    public final int getPrecedence(Operator op) {
        return this.precedence.get(op);
    }

    protected void setPrecedence(int p, Operator ... ops) {
        this.setPrecedence(p, Arrays.asList(ops));
    }

    protected void setPrecedence(int p, Iterable<? extends Operator> ops) {
        for (Operator operator : ops) {
            this.precedence.put(operator, p);
        }
    }

    protected static class Precedence {
        public static final int HIGHEST = -1;
        public static final int DOT = 5;
        public static final int NOT_HIGH = 10;
        public static final int NEGATE = 20;
        public static final int ARITH_HIGH = 30;
        public static final int ARITH_LOW = 40;
        public static final int COMPARISON = 50;
        public static final int EQUALITY = 60;
        public static final int CASE = 70;
        public static final int LIST = 70;
        public static final int NOT = 80;
        public static final int AND = 90;
        public static final int XOR = 100;
        public static final int XNOR = 100;
        public static final int OR = 110;

        protected Precedence() {
        }
    }
}

