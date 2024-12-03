/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.types.dsl;

import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.NullExpression;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ComparableExpression;
import com.querydsl.core.types.dsl.DateExpression;
import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.core.types.dsl.EnumExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.SimpleOperation;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.TimeExpression;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

public final class CaseForEqBuilder<D> {
    private final Expression<D> base;
    private final Expression<? extends D> other;
    private final List<CaseElement<D>> caseElements = new ArrayList<CaseElement<D>>();
    private Class<?> type;

    public CaseForEqBuilder(Expression<D> base, Expression<? extends D> other) {
        this.base = base;
        this.other = other;
    }

    public <T> Cases<T, Expression<T>> then(Expression<T> expr) {
        if (expr instanceof Predicate) {
            return this.then((T)((Object)((Predicate)expr)));
        }
        if (expr instanceof StringExpression) {
            return this.then((StringExpression)expr);
        }
        if (expr instanceof NumberExpression) {
            return this.then((NumberExpression)expr);
        }
        if (expr instanceof DateExpression) {
            return this.then((T)((Object)((DateExpression)expr)));
        }
        if (expr instanceof DateTimeExpression) {
            return this.then((T)((Object)((DateTimeExpression)expr)));
        }
        if (expr instanceof TimeExpression) {
            return this.then((TimeExpression)expr);
        }
        if (expr instanceof ComparableExpression) {
            return this.then((T)((Object)((ComparableExpression)expr)));
        }
        return this.thenSimple(expr);
    }

    private <T> Cases<T, Expression<T>> thenSimple(Expression<T> expr) {
        this.type = expr.getType();
        return new Cases<T, Expression<T>>(){

            @Override
            protected Expression<T> createResult(Class<T> type, Expression<T> last) {
                return Expressions.operation(type, Ops.CASE_EQ, CaseForEqBuilder.this.base, last);
            }
        }.when(this.other).then(expr);
    }

    public <T> Cases<T, Expression<T>> then(T then) {
        return this.then((T)((Object)ConstantImpl.create(then)));
    }

    public <T> Cases<T, Expression<T>> thenNull() {
        return this.then((T)((Object)NullExpression.DEFAULT));
    }

    public Cases<Boolean, BooleanExpression> then(Boolean then) {
        return this.thenBoolean(ConstantImpl.create(then));
    }

    public Cases<Boolean, BooleanExpression> then(BooleanExpression then) {
        return this.thenBoolean(then);
    }

    private Cases<Boolean, BooleanExpression> thenBoolean(Expression<Boolean> then) {
        this.type = then.getType();
        return new Cases<Boolean, BooleanExpression>(){

            @Override
            protected BooleanExpression createResult(Class<Boolean> type, Expression<Boolean> last) {
                return Expressions.booleanOperation(Ops.CASE_EQ, CaseForEqBuilder.this.base, last);
            }
        }.when(this.other).then(then);
    }

    public <T extends Comparable> Cases<T, ComparableExpression<T>> then(T then) {
        return this.thenComparable(ConstantImpl.create(then));
    }

    public <T extends Comparable> Cases<T, ComparableExpression<T>> then(ComparableExpression<T> then) {
        return this.thenComparable(then);
    }

    private <T extends Comparable> Cases<T, ComparableExpression<T>> thenComparable(Expression<T> then) {
        this.type = then.getType();
        return new Cases<T, ComparableExpression<T>>(){

            @Override
            protected ComparableExpression<T> createResult(Class<T> type, Expression<T> last) {
                return Expressions.comparableOperation(type, Ops.CASE_EQ, CaseForEqBuilder.this.base, last);
            }
        }.when(this.other).then(then);
    }

    public Cases<Date, DateExpression<Date>> then(Date then) {
        return this.thenDate(ConstantImpl.create(then));
    }

    public <T extends Comparable> Cases<T, DateExpression<T>> then(DateExpression<T> then) {
        return this.thenDate(then);
    }

    private <T extends Comparable> Cases<T, DateExpression<T>> thenDate(Expression<T> then) {
        this.type = then.getType();
        return new Cases<T, DateExpression<T>>(){

            @Override
            protected DateExpression<T> createResult(Class<T> type, Expression<T> last) {
                return Expressions.dateOperation(type, Ops.CASE_EQ, CaseForEqBuilder.this.base, last);
            }
        }.when(this.other).then(then);
    }

    public Cases<java.util.Date, DateTimeExpression<java.util.Date>> then(java.util.Date then) {
        return this.thenDateTime(ConstantImpl.create(then));
    }

    public Cases<Timestamp, DateTimeExpression<Timestamp>> then(Timestamp then) {
        return this.thenDateTime(ConstantImpl.create(then));
    }

    public <T extends Comparable> Cases<T, DateTimeExpression<T>> then(DateTimeExpression<T> then) {
        return this.thenDateTime(then);
    }

    private <T extends Comparable> Cases<T, DateTimeExpression<T>> thenDateTime(Expression<T> then) {
        this.type = then.getType();
        return new Cases<T, DateTimeExpression<T>>(){

            @Override
            protected DateTimeExpression<T> createResult(Class<T> type, Expression<T> last) {
                return Expressions.dateTimeOperation(type, Ops.CASE_EQ, CaseForEqBuilder.this.base, last);
            }
        }.when(this.other).then(then);
    }

    public <T extends Enum<T>> Cases<T, EnumExpression<T>> then(T then) {
        return this.thenEnum(ConstantImpl.create(then));
    }

    public <T extends Enum<T>> Cases<T, EnumExpression<T>> then(EnumExpression<T> then) {
        return this.thenEnum(then);
    }

    private <T extends Enum<T>> Cases<T, EnumExpression<T>> thenEnum(Expression<T> then) {
        this.type = then.getType();
        return new Cases<T, EnumExpression<T>>(){

            @Override
            protected EnumExpression<T> createResult(Class<T> type, Expression<T> last) {
                return Expressions.enumOperation(type, Ops.CASE_EQ, CaseForEqBuilder.this.base, last);
            }
        }.when(this.other).then(then);
    }

    public <T extends Number> Cases<T, NumberExpression<T>> then(T then) {
        return this.thenNumber(ConstantImpl.create(then));
    }

    public <T extends Number> Cases<T, NumberExpression<T>> then(NumberExpression<T> then) {
        return this.thenNumber(then);
    }

    public <T extends Number> Cases<T, NumberExpression<T>> thenNumber(Expression<T> then) {
        this.type = then.getType();
        return new Cases<T, NumberExpression<T>>(){

            @Override
            protected NumberExpression<T> createResult(Class<T> type, Expression<T> last) {
                return Expressions.numberOperation(type, Ops.CASE_EQ, CaseForEqBuilder.this.base, last);
            }
        }.when(this.other).then(then);
    }

    public Cases<String, StringExpression> then(String then) {
        return this.thenString(ConstantImpl.create(then));
    }

    public Cases<String, StringExpression> then(StringExpression then) {
        return this.thenString(then);
    }

    private Cases<String, StringExpression> thenString(Expression<String> then) {
        this.type = then.getType();
        return new Cases<String, StringExpression>(){

            @Override
            protected StringExpression createResult(Class<String> type, Expression<String> last) {
                return Expressions.stringOperation(Ops.CASE_EQ, CaseForEqBuilder.this.base, last);
            }
        }.when(this.other).then(then);
    }

    public Cases<Time, TimeExpression<Time>> then(Time then) {
        return this.thenTime(ConstantImpl.create(then));
    }

    public <T extends Comparable> Cases<T, TimeExpression<T>> then(TimeExpression<T> then) {
        return this.thenTime(then);
    }

    private <T extends Comparable> Cases<T, TimeExpression<T>> thenTime(Expression<T> then) {
        this.type = then.getType();
        return new Cases<T, TimeExpression<T>>(){

            @Override
            protected TimeExpression<T> createResult(Class<T> type, Expression<T> last) {
                return Expressions.timeOperation(type, Ops.CASE_EQ, CaseForEqBuilder.this.base, last);
            }
        }.when(this.other).then(then);
    }

    public class CaseWhen<T, Q extends Expression<T>> {
        private final Cases<T, Q> cases;
        private final Expression<? extends D> when;

        public CaseWhen(Cases<T, Q> cases, Expression<? extends D> when) {
            this.cases = cases;
            this.when = when;
        }

        public Cases<T, Q> then(Expression<T> then) {
            CaseForEqBuilder.this.caseElements.add(0, new CaseElement(this.when, then));
            return this.cases;
        }

        public Cases<T, Q> then(T then) {
            return this.then((T)ConstantImpl.create(then));
        }
    }

    public abstract class Cases<T, Q extends Expression<T>> {
        public CaseWhen<T, Q> when(Expression<? extends D> when) {
            return new CaseWhen(this, when);
        }

        public CaseWhen<T, Q> when(D when) {
            return this.when((Object)ConstantImpl.create(when));
        }

        public Q otherwise(Expression<T> otherwise) {
            CaseForEqBuilder.this.caseElements.add(0, new CaseElement(null, otherwise));
            SimpleOperation last = null;
            for (CaseElement element : CaseForEqBuilder.this.caseElements) {
                if (last == null) {
                    last = Expressions.operation(CaseForEqBuilder.this.type, Ops.CASE_EQ_ELSE, element.getTarget());
                    continue;
                }
                last = Expressions.operation(CaseForEqBuilder.this.type, Ops.CASE_EQ_WHEN, CaseForEqBuilder.this.base, element.getEq(), element.getTarget(), last);
            }
            return this.createResult(CaseForEqBuilder.this.type, last);
        }

        protected abstract Q createResult(Class<T> var1, Expression<T> var2);

        public Q otherwise(T otherwise) {
            return this.otherwise((T)ConstantImpl.create(otherwise));
        }
    }

    private static class CaseElement<D> {
        @Nullable
        private final Expression<? extends D> eq;
        private final Expression<?> target;

        public CaseElement(@Nullable Expression<? extends D> eq, Expression<?> target) {
            this.eq = eq;
            this.target = target;
        }

        public Expression<? extends D> getEq() {
            return this.eq;
        }

        public Expression<?> getTarget() {
            return this.target;
        }
    }
}

