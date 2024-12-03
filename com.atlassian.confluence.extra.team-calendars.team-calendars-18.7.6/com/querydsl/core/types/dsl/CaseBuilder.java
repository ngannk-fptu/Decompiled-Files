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
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.core.types.dsl.SimpleOperation;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.TimeExpression;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

public final class CaseBuilder {
    public Initial when(Predicate b) {
        return new Initial(b);
    }

    public static class Initial {
        private final Predicate when;

        public Initial(Predicate b) {
            this.when = b;
        }

        public <A> Cases<A, SimpleExpression<A>> then(Expression<A> expr) {
            if (expr instanceof Predicate) {
                return this.then((A)((Object)((Predicate)expr)));
            }
            if (expr instanceof StringExpression) {
                return this.then((StringExpression)expr);
            }
            if (expr instanceof NumberExpression) {
                return this.then((A)((Object)((NumberExpression)expr)));
            }
            if (expr instanceof DateExpression) {
                return this.then((A)((Object)((DateExpression)expr)));
            }
            if (expr instanceof DateTimeExpression) {
                return this.then((A)((Object)((DateTimeExpression)expr)));
            }
            if (expr instanceof TimeExpression) {
                return this.then((TimeExpression)expr);
            }
            if (expr instanceof ComparableExpression) {
                return this.then((A)((Object)((ComparableExpression)expr)));
            }
            return this.thenSimple(expr);
        }

        private <A> Cases<A, SimpleExpression<A>> thenSimple(Expression<A> expr) {
            return new Cases<A, SimpleExpression<A>>(expr.getType()){

                @Override
                protected SimpleExpression<A> createResult(Class<? extends A> type, Expression<A> last) {
                    return Expressions.operation(type, Ops.CASE, last);
                }
            }.addCase(this.when, expr);
        }

        public <A> Cases<A, SimpleExpression<A>> then(A constant) {
            return this.thenSimple(ConstantImpl.create(constant));
        }

        public Cases<Boolean, BooleanExpression> then(Predicate expr) {
            return this.thenBoolean(expr);
        }

        private Cases<Boolean, BooleanExpression> thenBoolean(Expression<Boolean> expr) {
            return new Cases<Boolean, BooleanExpression>(Boolean.class){

                @Override
                protected BooleanExpression createResult(Class<? extends Boolean> type, Expression<Boolean> last) {
                    return Expressions.booleanOperation(Ops.CASE, last);
                }
            }.addCase(this.when, expr);
        }

        public Cases<Boolean, BooleanExpression> then(boolean b) {
            return this.thenBoolean(ConstantImpl.create(b));
        }

        public <T extends Comparable> Cases<T, ComparableExpression<T>> then(ComparableExpression<T> expr) {
            return this.thenComparable(expr);
        }

        private <T extends Comparable> Cases<T, ComparableExpression<T>> thenComparable(Expression<T> expr) {
            return new Cases<T, ComparableExpression<T>>(expr.getType()){

                @Override
                protected ComparableExpression<T> createResult(Class<? extends T> type, Expression<T> last) {
                    return Expressions.comparableOperation(type, Ops.CASE, last);
                }
            }.addCase(this.when, expr);
        }

        public <A extends Comparable> Cases<A, ComparableExpression<A>> then(A arg) {
            return this.thenComparable(ConstantImpl.create(arg));
        }

        public <T extends Comparable> Cases<T, DateExpression<T>> then(DateExpression<T> expr) {
            return this.thenDate(expr);
        }

        private <T extends Comparable> Cases<T, DateExpression<T>> thenDate(Expression<T> expr) {
            return new Cases<T, DateExpression<T>>(expr.getType()){

                @Override
                protected DateExpression<T> createResult(Class<? extends T> type, Expression<T> last) {
                    return Expressions.dateOperation(type, Ops.CASE, last);
                }
            }.addCase(this.when, expr);
        }

        public Cases<Date, DateExpression<Date>> then(Date date) {
            return this.thenDate(ConstantImpl.create(date));
        }

        public <T extends Comparable> Cases<T, DateTimeExpression<T>> then(DateTimeExpression<T> expr) {
            return this.thenDateTime(expr);
        }

        private <T extends Comparable> Cases<T, DateTimeExpression<T>> thenDateTime(Expression<T> expr) {
            return new Cases<T, DateTimeExpression<T>>(expr.getType()){

                @Override
                protected DateTimeExpression<T> createResult(Class<? extends T> type, Expression<T> last) {
                    return Expressions.dateTimeOperation(type, Ops.CASE, last);
                }
            }.addCase(this.when, expr);
        }

        public Cases<Timestamp, DateTimeExpression<Timestamp>> then(Timestamp ts) {
            return this.thenDateTime(ConstantImpl.create(ts));
        }

        public Cases<java.util.Date, DateTimeExpression<java.util.Date>> then(java.util.Date date) {
            return this.thenDateTime(ConstantImpl.create(date));
        }

        public <T extends Enum<T>> Cases<T, EnumExpression<T>> then(EnumExpression<T> expr) {
            return this.thenEnum(expr);
        }

        private <T extends Enum<T>> Cases<T, EnumExpression<T>> thenEnum(Expression<T> expr) {
            return new Cases<T, EnumExpression<T>>(expr.getType()){

                @Override
                protected EnumExpression<T> createResult(Class<? extends T> type, Expression<T> last) {
                    return Expressions.enumOperation(type, Ops.CASE, last);
                }
            }.addCase(this.when, expr);
        }

        public <T extends Enum<T>> Cases<T, EnumExpression<T>> then(T arg) {
            return this.thenEnum(ConstantImpl.create(arg));
        }

        public <A extends Number> Cases<A, NumberExpression<A>> then(NumberExpression<A> expr) {
            return this.thenNumber(expr);
        }

        private <A extends Number> Cases<A, NumberExpression<A>> thenNumber(Expression<A> expr) {
            return new Cases<A, NumberExpression<A>>(expr.getType()){

                @Override
                protected NumberExpression<A> createResult(Class<? extends A> type, Expression<A> last) {
                    return Expressions.numberOperation(type, Ops.CASE, last);
                }
            }.addCase(this.when, expr);
        }

        public <A extends Number> Cases<A, NumberExpression<A>> then(A num) {
            return this.thenNumber(ConstantImpl.create(num));
        }

        public Cases<String, StringExpression> then(StringExpression expr) {
            return this.thenString(expr);
        }

        private Cases<String, StringExpression> thenString(Expression<String> expr) {
            return new Cases<String, StringExpression>(String.class){

                @Override
                protected StringExpression createResult(Class<? extends String> type, Expression<String> last) {
                    return Expressions.stringOperation(Ops.CASE, last);
                }
            }.addCase(this.when, expr);
        }

        public Cases<String, StringExpression> then(String str) {
            return this.thenString(ConstantImpl.create(str));
        }

        public <T extends Comparable> Cases<T, TimeExpression<T>> then(TimeExpression<T> expr) {
            return this.thenTime(expr);
        }

        private <T extends Comparable> Cases<T, TimeExpression<T>> thenTime(Expression<T> expr) {
            return new Cases<T, TimeExpression<T>>(expr.getType()){

                @Override
                protected TimeExpression<T> createResult(Class<? extends T> type, Expression<T> last) {
                    return Expressions.timeOperation(type, Ops.CASE, last);
                }
            }.addCase(this.when, expr);
        }

        public Cases<Time, TimeExpression<Time>> then(Time time) {
            return this.thenTime(ConstantImpl.create(time));
        }
    }

    public static class CaseWhen<A, Q extends Expression<A>> {
        private final Predicate b;
        private final Cases<A, Q> cases;

        public CaseWhen(Cases<A, Q> cases, Predicate b) {
            this.cases = cases;
            this.b = b;
        }

        public Cases<A, Q> then(A constant) {
            return this.then((Expression<A>)ConstantImpl.create(constant));
        }

        public Cases<A, Q> then(Expression<A> expr) {
            return this.cases.addCase(this.b, expr);
        }
    }

    public static abstract class Cases<A, Q extends Expression<A>> {
        private final List<CaseElement<A>> cases = new ArrayList<CaseElement<A>>();
        private final Class<? extends A> type;

        public Cases(Class<? extends A> type) {
            this.type = type;
        }

        Cases<A, Q> addCase(Predicate condition, Expression<A> expr) {
            this.cases.add(0, new CaseElement<A>(condition, expr));
            return this;
        }

        protected abstract Q createResult(Class<? extends A> var1, Expression<A> var2);

        public Q otherwise(A constant) {
            if (constant != null) {
                return this.otherwise((Expression<A>)ConstantImpl.create(constant));
            }
            return this.otherwise((Expression<A>)NullExpression.DEFAULT);
        }

        public Q otherwise(Expression<A> expr) {
            if (expr == null) {
                expr = NullExpression.DEFAULT;
            }
            this.cases.add(0, new CaseElement<A>(null, expr));
            SimpleOperation<? extends A> last = null;
            for (CaseElement<A> element : this.cases) {
                if (last == null) {
                    last = Expressions.operation(this.type, Ops.CASE_ELSE, element.getTarget());
                    continue;
                }
                last = Expressions.operation(this.type, Ops.CASE_WHEN, element.getCondition(), element.getTarget(), last);
            }
            return this.createResult(this.type, last);
        }

        public CaseWhen<A, Q> when(Predicate b) {
            return new CaseWhen(this, b);
        }
    }

    private static class CaseElement<A> {
        @Nullable
        private final Predicate condition;
        private final Expression<A> target;

        public CaseElement(@Nullable Predicate condition, Expression<A> target) {
            this.condition = condition;
            this.target = target;
        }

        public Predicate getCondition() {
            return this.condition;
        }

        public Expression<A> getTarget() {
            return this.target;
        }
    }
}

