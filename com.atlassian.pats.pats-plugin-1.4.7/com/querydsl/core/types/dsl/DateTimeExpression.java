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

public abstract class DateTimeExpression<T extends Comparable>
extends TemporalExpression<T> {
    private static final long serialVersionUID = -6879277113694148047L;
    @Nullable
    private volatile transient NumberExpression<Integer> dayOfMonth;
    @Nullable
    private volatile transient NumberExpression<Integer> dayOfWeek;
    @Nullable
    private volatile transient NumberExpression<Integer> dayOfYear;
    @Nullable
    private volatile transient NumberExpression<Integer> hours;
    @Nullable
    private volatile transient NumberExpression<Integer> minutes;
    @Nullable
    private volatile transient NumberExpression<Integer> seconds;
    @Nullable
    private volatile transient NumberExpression<Integer> milliseconds;
    @Nullable
    private volatile transient DateTimeExpression<T> min;
    @Nullable
    private volatile transient DateTimeExpression<T> max;
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

    public static DateTimeExpression<Date> currentDate() {
        return Constants.CURRENT_DATE;
    }

    public static <T extends Comparable> DateTimeExpression<T> currentDate(Class<T> cl) {
        return Expressions.dateTimeOperation(cl, Ops.DateTimeOps.CURRENT_DATE, new Expression[0]);
    }

    public static DateTimeExpression<Date> currentTimestamp() {
        return Constants.CURRENT_TIMESTAMP;
    }

    public static <T extends Comparable> DateTimeExpression<T> currentTimestamp(Class<T> cl) {
        return Expressions.dateTimeOperation(cl, Ops.DateTimeOps.CURRENT_TIMESTAMP, new Expression[0]);
    }

    public DateTimeExpression(Expression<T> mixin) {
        super(mixin);
    }

    @Override
    public DateTimeExpression<T> as(Path<T> alias) {
        return Expressions.dateTimeOperation(this.getType(), Ops.ALIAS, this.mixin, alias);
    }

    @Override
    public DateTimeExpression<T> as(String alias) {
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

    public NumberExpression<Integer> hour() {
        if (this.hours == null) {
            this.hours = Expressions.numberOperation(Integer.class, Ops.DateTimeOps.HOUR, this.mixin);
        }
        return this.hours;
    }

    public DateTimeExpression<T> max() {
        if (this.max == null) {
            this.max = Expressions.dateTimeOperation(this.getType(), Ops.AggOps.MAX_AGG, this.mixin);
        }
        return this.max;
    }

    public NumberExpression<Integer> milliSecond() {
        if (this.milliseconds == null) {
            this.milliseconds = Expressions.numberOperation(Integer.class, Ops.DateTimeOps.MILLISECOND, this.mixin);
        }
        return this.milliseconds;
    }

    public DateTimeExpression<T> min() {
        if (this.min == null) {
            this.min = Expressions.dateTimeOperation(this.getType(), Ops.AggOps.MIN_AGG, this.mixin);
        }
        return this.min;
    }

    public NumberExpression<Integer> minute() {
        if (this.minutes == null) {
            this.minutes = Expressions.numberOperation(Integer.class, Ops.DateTimeOps.MINUTE, this.mixin);
        }
        return this.minutes;
    }

    public NumberExpression<Integer> month() {
        if (this.month == null) {
            this.month = Expressions.numberOperation(Integer.class, Ops.DateTimeOps.MONTH, this.mixin);
        }
        return this.month;
    }

    public NumberExpression<Integer> second() {
        if (this.seconds == null) {
            this.seconds = Expressions.numberOperation(Integer.class, Ops.DateTimeOps.SECOND, this.mixin);
        }
        return this.seconds;
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
        private static final DateTimeExpression<Date> CURRENT_DATE = DateTimeExpression.currentDate(Date.class);
        private static final DateTimeExpression<Date> CURRENT_TIMESTAMP = DateTimeExpression.currentTimestamp(Date.class);

        private Constants() {
        }
    }
}

