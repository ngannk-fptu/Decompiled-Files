/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Sets
 */
package com.querydsl.core.types;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.querydsl.core.types.Constant;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Operator;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.util.MathUtils;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import javax.annotation.concurrent.Immutable;

@Immutable
public final class Template
implements Serializable {
    private static final long serialVersionUID = -1697705745769542204L;
    private static final Set<? extends Operator> CONVERTIBLES = Sets.immutableEnumSet((Enum)Ops.ADD, (Enum[])new Ops[]{Ops.SUB});
    private final ImmutableList<Element> elements;
    private final String template;

    Template(String template, ImmutableList<Element> elements) {
        this.template = template;
        this.elements = elements;
    }

    public List<Element> getElements() {
        return this.elements;
    }

    public String toString() {
        return this.template;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof Template) {
            return ((Template)o).template.equals(this.template);
        }
        return false;
    }

    public int hashCode() {
        return this.template.hashCode();
    }

    private static Number asNumber(Object arg) {
        if (arg instanceof Number) {
            return (Number)arg;
        }
        if (arg instanceof Constant) {
            return (Number)((Constant)arg).getConstant();
        }
        throw new IllegalArgumentException(arg.toString());
    }

    private static boolean isNumber(Object o) {
        return o instanceof Number || o instanceof Constant && ((Constant)o).getConstant() instanceof Number;
    }

    private static Expression<?> asExpression(Object arg) {
        if (arg instanceof Expression) {
            return ExpressionUtils.extract((Expression)arg);
        }
        return Expressions.constant(arg);
    }

    public static final class OperationConst
    extends Element {
        private static final long serialVersionUID = 1400801176778801584L;
        private final int index1;
        private final Number arg2;
        private final Expression<Number> expr2;
        private final Operator operator;
        private final boolean asString;

        @Deprecated
        public OperationConst(int index1, BigDecimal arg2, Operator operator, boolean asString) {
            this(index1, (Number)arg2, operator, asString);
        }

        public OperationConst(int index1, Number arg2, Operator operator, boolean asString) {
            this.index1 = index1;
            this.arg2 = arg2;
            this.expr2 = Expressions.constant(arg2);
            this.operator = operator;
            this.asString = asString;
        }

        @Override
        public Object convert(List<?> args) {
            Object arg1 = args.get(this.index1);
            if (Template.isNumber(arg1)) {
                return MathUtils.result(Template.asNumber(arg1), this.arg2, this.operator);
            }
            Expression expr1 = Template.asExpression(arg1);
            if (CONVERTIBLES.contains(this.operator) && expr1 instanceof com.querydsl.core.types.Operation) {
                com.querydsl.core.types.Operation operation = (com.querydsl.core.types.Operation)expr1;
                if (CONVERTIBLES.contains(operation.getOperator()) && operation.getArg(1) instanceof Constant) {
                    Number num1 = (Number)((Constant)operation.getArg(1)).getConstant();
                    Number num2 = this.operator == operation.getOperator() ? (Number)MathUtils.result(num1, this.arg2, Ops.ADD) : (Number)(this.operator == Ops.ADD ? (Number)MathUtils.result(this.arg2, num1, Ops.SUB) : (Number)MathUtils.result(num1, this.arg2, Ops.SUB));
                    return ExpressionUtils.operation(expr1.getType(), this.operator, operation.getArg(0), Expressions.constant(num2));
                }
            }
            return ExpressionUtils.operation(expr1.getType(), this.operator, expr1, this.expr2);
        }

        @Override
        public boolean isString() {
            return this.asString;
        }

        public String toString() {
            return this.index1 + " " + this.operator + " " + this.arg2;
        }
    }

    public static final class Operation
    extends Element {
        private static final long serialVersionUID = 1400801176778801584L;
        private final int index1;
        private final int index2;
        private final Operator operator;
        private final boolean asString;

        public Operation(int index1, int index2, Operator operator, boolean asString) {
            this.index1 = index1;
            this.index2 = index2;
            this.operator = operator;
            this.asString = asString;
        }

        @Override
        public Object convert(List<?> args) {
            Object arg1 = args.get(this.index1);
            Object arg2 = args.get(this.index2);
            if (Template.isNumber(arg1) && Template.isNumber(arg2)) {
                return MathUtils.result(Template.asNumber(arg1), Template.asNumber(arg2), this.operator);
            }
            Expression expr1 = Template.asExpression(arg1);
            Expression expr2 = Template.asExpression(arg2);
            if (arg2 instanceof Number && CONVERTIBLES.contains(this.operator) && expr1 instanceof com.querydsl.core.types.Operation) {
                com.querydsl.core.types.Operation operation = (com.querydsl.core.types.Operation)expr1;
                if (CONVERTIBLES.contains(operation.getOperator()) && operation.getArg(1) instanceof Constant) {
                    Number num1 = (Number)((Constant)operation.getArg(1)).getConstant();
                    Number num2 = this.operator == operation.getOperator() ? (Number)MathUtils.result(num1, (Number)arg2, Ops.ADD) : (Number)(this.operator == Ops.ADD ? (Number)MathUtils.result((Number)arg2, num1, Ops.SUB) : (Number)MathUtils.result(num1, (Number)arg2, Ops.SUB));
                    return ExpressionUtils.operation(expr1.getType(), this.operator, operation.getArg(0), Expressions.constant(num2));
                }
            }
            return ExpressionUtils.operation(expr1.getType(), this.operator, expr1, expr2);
        }

        @Override
        public boolean isString() {
            return this.asString;
        }

        public String toString() {
            return this.index1 + " " + this.operator + " " + this.index2;
        }
    }

    public static final class ByIndex
    extends Element {
        private static final long serialVersionUID = 4711323946026029998L;
        private final int index;
        private final String toString;

        public ByIndex(int index) {
            this.index = index;
            this.toString = String.valueOf(index);
        }

        @Override
        public Object convert(List<?> args) {
            Object arg = args.get(this.index);
            if (arg instanceof Expression) {
                return ExpressionUtils.extract((Expression)arg);
            }
            return arg;
        }

        public int getIndex() {
            return this.index;
        }

        @Override
        public boolean isString() {
            return false;
        }

        public String toString() {
            return this.toString;
        }
    }

    public static final class Transformed
    extends Element {
        private static final long serialVersionUID = 702677732175745567L;
        private final int index;
        private final transient Function<Object, Object> transformer;
        private final String toString;

        public Transformed(int index, Function<Object, Object> transformer) {
            this.index = index;
            this.transformer = transformer;
            this.toString = String.valueOf(index);
        }

        public int getIndex() {
            return this.index;
        }

        @Override
        public Object convert(List<?> args) {
            return this.transformer.apply(args.get(this.index));
        }

        @Override
        public boolean isString() {
            return false;
        }

        public String toString() {
            return this.toString;
        }
    }

    public static final class StaticText
    extends Element {
        private static final long serialVersionUID = -2791869625053368023L;
        private final String text;
        private final String toString;

        public StaticText(String text) {
            this.text = text;
            this.toString = "'" + text + "'";
        }

        public String getText() {
            return this.text;
        }

        @Override
        public boolean isString() {
            return true;
        }

        @Override
        public Object convert(List<?> args) {
            return this.text;
        }

        public String toString() {
            return this.toString;
        }
    }

    public static final class AsString
    extends Element {
        private static final long serialVersionUID = -655362047873616197L;
        private final int index;
        private final String toString;

        public AsString(int index) {
            this.index = index;
            this.toString = index + "s";
        }

        @Override
        public Object convert(List<?> args) {
            Object arg = args.get(this.index);
            return arg instanceof Constant ? arg.toString() : arg;
        }

        public int getIndex() {
            return this.index;
        }

        @Override
        public boolean isString() {
            return true;
        }

        public String toString() {
            return this.toString;
        }
    }

    @Immutable
    public static abstract class Element
    implements Serializable {
        private static final long serialVersionUID = 3396877288101929387L;

        public abstract Object convert(List<?> var1);

        public abstract boolean isString();
    }
}

