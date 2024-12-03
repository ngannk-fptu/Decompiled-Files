/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package com.querydsl.sql;

import com.google.common.collect.ImmutableList;
import com.querydsl.core.QueryFlag;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Operator;

public enum SQLOps implements Operator
{
    ALL(Object.class),
    CAST(Object.class),
    CORR(Double.class),
    COVARPOP(Double.class),
    COVARSAMP(Double.class),
    CUMEDIST(Double.class),
    CUMEDIST2(Double.class),
    DENSERANK(Long.class),
    DENSERANK2(Long.class),
    FIRSTVALUE(Object.class),
    FOR_SHARE(Object.class),
    FOR_UPDATE(Object.class),
    LAG(Object.class),
    LASTVALUE(Object.class),
    LEAD(Object.class),
    LISTAGG(Object.class),
    NEXTVAL(Object.class),
    NO_WAIT(Object.class),
    NTHVALUE(Object.class),
    NTILE(Object.class),
    PERCENTRANK(Double.class),
    PERCENTRANK2(Double.class),
    PERCENTILECONT(Object.class),
    PERCENTILEDISC(Object.class),
    QUALIFY(Boolean.class),
    RANK(Long.class),
    RANK2(Long.class),
    REGR_SLOPE(Object.class),
    REGR_INTERCEPT(Object.class),
    REGR_COUNT(Object.class),
    REGR_R2(Object.class),
    REGR_AVGX(Object.class),
    REGR_AVGY(Object.class),
    REGR_SXX(Object.class),
    REGR_SYY(Object.class),
    REGR_SXY(Object.class),
    RATIOTOREPORT(Object.class),
    ROWNUMBER(Long.class),
    STDDEV(Object.class),
    STDDEVPOP(Object.class),
    STDDEVSAMP(Object.class),
    STDDEV_DISTINCT(Object.class),
    UNION(Object.class),
    UNION_ALL(Object.class),
    VARIANCE(Object.class),
    VARPOP(Object.class),
    VARSAMP(Object.class),
    WITH_ALIAS(Object.class),
    WITH_COLUMNS(Object.class),
    LOCK_IN_SHARE_MODE(Object.class),
    WITH_REPEATABLE_READ(Object.class),
    GROUP_CONCAT(String.class),
    GROUP_CONCAT2(String.class),
    SET_PATH(Object.class),
    SET_LITERAL(Object.class);

    private final Class<?> type;
    @Deprecated
    public static final QueryFlag FOR_SHARE_FLAG;
    @Deprecated
    public static final QueryFlag FOR_UPDATE_FLAG;
    @Deprecated
    public static final QueryFlag NO_WAIT_FLAG;

    private SQLOps(Class<?> type) {
        this.type = type;
    }

    @Override
    public Class<?> getType() {
        return this.type;
    }

    static {
        FOR_SHARE_FLAG = new QueryFlag(QueryFlag.Position.END, ExpressionUtils.operation(Object.class, (Operator)FOR_SHARE, ImmutableList.of()));
        FOR_UPDATE_FLAG = new QueryFlag(QueryFlag.Position.END, ExpressionUtils.operation(Object.class, (Operator)FOR_UPDATE, ImmutableList.of()));
        NO_WAIT_FLAG = new QueryFlag(QueryFlag.Position.END, ExpressionUtils.operation(Object.class, (Operator)NO_WAIT, ImmutableList.of()));
    }
}

