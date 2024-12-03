/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 */
package com.querydsl.core.types;

import com.google.common.collect.ImmutableSet;
import com.querydsl.core.types.Operator;
import java.util.Set;

public enum Ops implements Operator
{
    EQ(Boolean.class),
    NE(Boolean.class),
    IS_NULL(Boolean.class),
    IS_NOT_NULL(Boolean.class),
    INSTANCE_OF(Boolean.class),
    NUMCAST(Number.class),
    STRING_CAST(String.class),
    ALIAS(Object.class),
    LIST(Object.class),
    SET(Object.class),
    SINGLETON(Object.class),
    ORDINAL(Integer.class),
    WRAPPED(Object.class),
    ORDER(Object.class),
    IN(Boolean.class),
    NOT_IN(Boolean.class),
    COL_IS_EMPTY(Boolean.class),
    COL_SIZE(Integer.class),
    ARRAY_SIZE(Number.class),
    CONTAINS_KEY(Boolean.class),
    CONTAINS_VALUE(Boolean.class),
    MAP_SIZE(Integer.class),
    MAP_IS_EMPTY(Boolean.class),
    AND(Boolean.class),
    NOT(Boolean.class),
    OR(Boolean.class),
    XNOR(Boolean.class),
    XOR(Boolean.class),
    BETWEEN(Boolean.class),
    GOE(Boolean.class),
    GT(Boolean.class),
    LOE(Boolean.class),
    LT(Boolean.class),
    NEGATE(Number.class),
    ADD(Number.class),
    DIV(Number.class),
    MULT(Number.class),
    SUB(Number.class),
    MOD(Number.class),
    CHAR_AT(Character.class),
    CONCAT(String.class),
    LOWER(String.class),
    SUBSTR_1ARG(String.class),
    SUBSTR_2ARGS(String.class),
    TRIM(String.class),
    UPPER(String.class),
    MATCHES(Boolean.class),
    MATCHES_IC(Boolean.class),
    STRING_LENGTH(Integer.class),
    STRING_IS_EMPTY(Boolean.class),
    STARTS_WITH(Boolean.class),
    STARTS_WITH_IC(Boolean.class),
    INDEX_OF_2ARGS(Integer.class),
    INDEX_OF(Integer.class),
    EQ_IGNORE_CASE(Boolean.class),
    ENDS_WITH(Boolean.class),
    ENDS_WITH_IC(Boolean.class),
    STRING_CONTAINS(Boolean.class),
    STRING_CONTAINS_IC(Boolean.class),
    LIKE(Boolean.class),
    LIKE_IC(Boolean.class),
    LIKE_ESCAPE(Boolean.class),
    LIKE_ESCAPE_IC(Boolean.class),
    CASE(Object.class),
    CASE_WHEN(Object.class),
    CASE_ELSE(Object.class),
    CASE_EQ(Object.class),
    CASE_EQ_WHEN(Object.class),
    CASE_EQ_ELSE(Object.class),
    COALESCE(Object.class),
    NULLIF(Object.class),
    EXISTS(Boolean.class);

    private final Class<?> type;
    public static final Set<Operator> equalsOps;
    public static final Set<Operator> notEqualsOps;
    public static final Set<Operator> compareOps;
    public static final Set<Operator> aggOps;

    private Ops(Class<?> type) {
        this.type = type;
    }

    @Override
    public Class<?> getType() {
        return this.type;
    }

    static {
        equalsOps = ImmutableSet.of((Object)EQ);
        notEqualsOps = ImmutableSet.of((Object)NE);
        compareOps = ImmutableSet.of((Object)EQ, (Object)NE, (Object)LT, (Object)GT, (Object)GOE, (Object)LOE, (Object[])new Operator[0]);
        aggOps = ImmutableSet.of((Object)AggOps.AVG_AGG, (Object)AggOps.COUNT_AGG, (Object)AggOps.COUNT_DISTINCT_AGG, (Object)AggOps.MAX_AGG, (Object)AggOps.MIN_AGG, (Object)AggOps.SUM_AGG, (Object[])new Operator[0]);
    }

    public static enum StringOps implements Operator
    {
        LEFT(String.class),
        RIGHT(String.class),
        LTRIM(String.class),
        RTRIM(String.class),
        LPAD(String.class),
        RPAD(String.class),
        LPAD2(String.class),
        RPAD2(String.class),
        LOCATE(Number.class),
        LOCATE2(Number.class);

        private final Class<?> type;

        private StringOps(Class<?> type) {
            this.type = type;
        }

        @Override
        public Class<?> getType() {
            return this.type;
        }
    }

    public static enum MathOps implements Operator
    {
        ABS(Number.class),
        ACOS(Number.class),
        ASIN(Number.class),
        ATAN(Number.class),
        CEIL(Number.class),
        COS(Number.class),
        TAN(Number.class),
        SQRT(Number.class),
        SIN(Number.class),
        ROUND(Number.class),
        ROUND2(Number.class),
        RANDOM(Number.class),
        RANDOM2(Number.class),
        POWER(Number.class),
        MIN(Number.class),
        MAX(Number.class),
        LOG(Number.class),
        FLOOR(Number.class),
        EXP(Number.class),
        COSH(Number.class),
        COT(Number.class),
        COTH(Number.class),
        DEG(Number.class),
        LN(Number.class),
        RAD(Number.class),
        SIGN(Number.class),
        SINH(Number.class),
        TANH(Number.class);

        private final Class<?> type;

        private MathOps(Class<?> type) {
            this.type = type;
        }

        @Override
        public Class<?> getType() {
            return this.type;
        }
    }

    public static enum DateTimeOps implements Operator
    {
        DATE(Comparable.class),
        CURRENT_DATE(Comparable.class),
        CURRENT_TIME(Comparable.class),
        CURRENT_TIMESTAMP(Comparable.class),
        ADD_YEARS(Comparable.class),
        ADD_MONTHS(Comparable.class),
        ADD_WEEKS(Comparable.class),
        ADD_DAYS(Comparable.class),
        ADD_HOURS(Comparable.class),
        ADD_MINUTES(Comparable.class),
        ADD_SECONDS(Comparable.class),
        DIFF_YEARS(Comparable.class),
        DIFF_MONTHS(Comparable.class),
        DIFF_WEEKS(Comparable.class),
        DIFF_DAYS(Comparable.class),
        DIFF_HOURS(Comparable.class),
        DIFF_MINUTES(Comparable.class),
        DIFF_SECONDS(Comparable.class),
        TRUNC_YEAR(Comparable.class),
        TRUNC_MONTH(Comparable.class),
        TRUNC_WEEK(Comparable.class),
        TRUNC_DAY(Comparable.class),
        TRUNC_HOUR(Comparable.class),
        TRUNC_MINUTE(Comparable.class),
        TRUNC_SECOND(Comparable.class),
        HOUR(Integer.class),
        MINUTE(Integer.class),
        MONTH(Integer.class),
        SECOND(Integer.class),
        MILLISECOND(Integer.class),
        SYSDATE(Comparable.class),
        YEAR(Integer.class),
        WEEK(Integer.class),
        YEAR_MONTH(Integer.class),
        YEAR_WEEK(Integer.class),
        DAY_OF_WEEK(Integer.class),
        DAY_OF_MONTH(Integer.class),
        DAY_OF_YEAR(Integer.class);

        private final Class<?> type;

        private DateTimeOps(Class<?> type) {
            this.type = type;
        }

        @Override
        public Class<?> getType() {
            return this.type;
        }
    }

    public static enum QuantOps implements Operator
    {
        AVG_IN_COL(Number.class),
        MAX_IN_COL(Comparable.class),
        MIN_IN_COL(Comparable.class),
        ANY(Object.class),
        ALL(Object.class);

        private final Class<?> type;

        private QuantOps(Class<?> type) {
            this.type = type;
        }

        @Override
        public Class<?> getType() {
            return this.type;
        }
    }

    public static enum AggOps implements Operator
    {
        BOOLEAN_ALL(Boolean.class),
        BOOLEAN_ANY(Boolean.class),
        MAX_AGG(Comparable.class),
        MIN_AGG(Comparable.class),
        AVG_AGG(Number.class),
        SUM_AGG(Number.class),
        COUNT_AGG(Number.class),
        COUNT_DISTINCT_AGG(Number.class),
        COUNT_DISTINCT_ALL_AGG(Number.class),
        COUNT_ALL_AGG(Number.class);

        private final Class<?> type;

        private AggOps(Class<?> type) {
            this.type = type;
        }

        @Override
        public Class<?> getType() {
            return this.type;
        }
    }
}

