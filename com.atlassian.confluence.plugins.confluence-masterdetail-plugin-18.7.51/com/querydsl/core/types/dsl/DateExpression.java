/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.querydsl.core.types.dsl;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.TemporalExpression;
import java.util.Date;
import javax.annotation.Nullable;

public abstract class DateExpression<T extends Comparable>
extends TemporalExpression<T> {
    private static final long serialVersionUID = 6054664454254721302L;
    @Nullable
    private volatile transient NumberExpression<Integer> dayOfMonth;
    @Nullable
    private volatile transient NumberExpression<Integer> dayOfWeek;
    @Nullable
    private volatile transient NumberExpression<Integer> dayOfYear;
    @Nullable
    private volatile transient DateExpression<T> min;
    @Nullable
    private volatile transient DateExpression<T> max;
    @Nullable
    private volatile transient NumberExpression<Integer> week;
    @Nullable
    private volatile transient NumberExpression<Integer> month;
    @Nullable
    private volatile transient NumberExpression<Integer> year;
    @Nullable
    private volatile transient NumberExpression<Integer> yearMonth;
    @Nullable
    private volatile transient NumberExpression<Integer> yearWeek;

    public static DateExpression<Date> currentDate() {
        return Constants.CURRENT_DATE;
    }

    public static <T extends Comparable> DateExpression<T> currentDate(Class<T> cl) {
        return Expressions.dateOperation(cl, Ops.DateTimeOps.CURRENT_DATE, new Expression[0]);
    }

    public DateExpression(Expression<T> mixin) {
        super(mixin);
    }

    @Override
    public DateExpression<T> as(Path<T> alias) {
        return Expressions.dateOperation(this.getType(), Ops.ALIAS, this.mixin, alias);
    }

    @Override
    public DateExpression<T> as(String alias) {
        return this.as(ExpressionUtils.path(this.getType(), alias));
    }

    public NumberExpression<Integer> dayOfMonth() {
        if (this.dayOfMonth == null) {
            this.dayOfMonth = Expressions.numberOperation(Integer.class, Ops.DateTimeOps.DAY_OF_MONTH, this.mixin);
        }
        return this.dayOfMonth;
    }

    public NumberExpression<Integer> dayOfWeek() {
        if (this.dayOfWeek == null) {
            this.dayOfWeek = Expressions.numberOperation(Integer.class, Ops.DateTimeOps.DAY_OF_WEEK, this.mixin);
        }
        return this.dayOfWeek;
    }

    public NumberExpression<Integer> dayOfYear() {
        if (this.dayOfYear == null) {
            this.dayOfYear = Expressions.numberOperation(Integer.class, Ops.DateTimeOps.DAY_OF_YEAR, this.mixin);
        }
        return this.dayOfYear;
    }

    public DateExpression<T> max() {
        if (this.max == null) {
            this.max = Expressions.dateOperation(this.getType(), Ops.AggOps.MAX_AGG, this.mixin);
        }
        return this.max;
    }

    public DateExpression<T> min() {
        if (this.min == null) {
            this.min = Expressions.dateOperation(this.getType(), Ops.AggOps.MIN_AGG, this.mixin);
        }
        return this.min;
    }

    public NumberExpression<Integer> month() {
        if (this.month == null) {
            this.month = Expressions.numberOperation(Integer.class, Ops.DateTimeOps.MONTH, this.mixin);
        }
        return this.month;
    }

    public NumberExpression<Integer> week() {
        if (this.week == null) {
            this.week = Expressions.numberOperation(Integer.class, Ops.DateTimeOps.WEEK, this.mixin);
        }
        return this.week;
    }

    public NumberExpression<Integer> year() {
        if (this.year == null) {
            this.year = Expressions.numberOperation(Integer.class, Ops.DateTimeOps.YEAR, this.mixin);
        }
        return this.year;
    }

    public NumberExpression<Integer> yearMonth() {
        if (this.yearMonth == null) {
            this.yearMonth = Expressions.numberOperation(Integer.class, Ops.DateTimeOps.YEAR_MONTH, this.mixin);
        }
        return this.yearMonth;
    }

    public NumberExpression<Integer> yearWeek() {
        if (this.yearWeek == null) {
            this.yearWeek = Expressions.numberOperation(Integer.class, Ops.DateTimeOps.YEAR_WEEK, this.mixin);
        }
        return this.yearWeek;
    }

    private static class Constants {
        private static final DateExpression<Date> CURRENT_DATE = DateExpression.currentDate(Date.class);

        private Constants() {
        }
    }
}

