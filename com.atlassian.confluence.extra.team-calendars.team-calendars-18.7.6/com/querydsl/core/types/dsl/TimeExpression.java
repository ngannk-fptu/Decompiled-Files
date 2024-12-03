/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.types.dsl;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.TemporalExpression;
import java.sql.Time;
import javax.annotation.Nullable;

public abstract class TimeExpression<T extends Comparable>
extends TemporalExpression<T> {
    private static final long serialVersionUID = 7360552308332457990L;
    @Nullable
    private volatile transient NumberExpression<Integer> hours;
    @Nullable
    private volatile transient NumberExpression<Integer> minutes;
    @Nullable
    private volatile transient NumberExpression<Integer> seconds;
    @Nullable
    private volatile transient NumberExpression<Integer> milliseconds;

    public TimeExpression(Expression<T> mixin) {
        super(mixin);
    }

    @Override
    public TimeExpression<T> as(Path<T> alias) {
        return Expressions.timeOperation(this.getType(), Ops.ALIAS, this.mixin, alias);
    }

    @Override
    public TimeExpression<T> as(String alias) {
        return this.as(ExpressionUtils.path(this.getType(), alias));
    }

    public NumberExpression<Integer> hour() {
        if (this.hours == null) {
            this.hours = Expressions.numberOperation(Integer.class, Ops.DateTimeOps.HOUR, this.mixin);
        }
        return this.hours;
    }

    public NumberExpression<Integer> minute() {
        if (this.minutes == null) {
            this.minutes = Expressions.numberOperation(Integer.class, Ops.DateTimeOps.MINUTE, this.mixin);
        }
        return this.minutes;
    }

    public NumberExpression<Integer> second() {
        if (this.seconds == null) {
            this.seconds = Expressions.numberOperation(Integer.class, Ops.DateTimeOps.SECOND, this.mixin);
        }
        return this.seconds;
    }

    public NumberExpression<Integer> milliSecond() {
        if (this.milliseconds == null) {
            this.milliseconds = Expressions.numberOperation(Integer.class, Ops.DateTimeOps.MILLISECOND, this.mixin);
        }
        return this.milliseconds;
    }

    public static TimeExpression<Time> currentTime() {
        return Constants.CURRENT_TIME;
    }

    public static <T extends Comparable> TimeExpression<T> currentTime(Class<T> cl) {
        return Expressions.timeOperation(cl, Ops.DateTimeOps.CURRENT_TIME, new Expression[0]);
    }

    private static class Constants {
        private static final TimeExpression<Time> CURRENT_TIME = TimeExpression.currentTime(Time.class);

        private Constants() {
        }
    }
}

