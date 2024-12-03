/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.ast.expr;

import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.GroovyCodeVisitor;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.ExpressionTransformer;

public class ConstantExpression
extends Expression {
    public static final ConstantExpression NULL = new ConstantExpression(null);
    public static final ConstantExpression TRUE = new ConstantExpression(Boolean.TRUE);
    public static final ConstantExpression FALSE = new ConstantExpression(Boolean.FALSE);
    public static final ConstantExpression EMPTY_STRING = new ConstantExpression("");
    public static final ConstantExpression PRIM_TRUE = new ConstantExpression(Boolean.TRUE, true);
    public static final ConstantExpression PRIM_FALSE = new ConstantExpression(Boolean.FALSE, true);
    public static final ConstantExpression VOID = new ConstantExpression(Void.class);
    public static final ConstantExpression EMPTY_EXPRESSION = new ConstantExpression(null);
    private Object value;
    private String constantName;

    public ConstantExpression(Object value) {
        this(value, false);
    }

    public ConstantExpression(Object value, boolean keepPrimitive) {
        this.value = value;
        if (value != null) {
            if (keepPrimitive) {
                if (value instanceof Integer) {
                    this.setType(ClassHelper.int_TYPE);
                } else if (value instanceof Long) {
                    this.setType(ClassHelper.long_TYPE);
                } else if (value instanceof Boolean) {
                    this.setType(ClassHelper.boolean_TYPE);
                } else if (value instanceof Double) {
                    this.setType(ClassHelper.double_TYPE);
                } else if (value instanceof Float) {
                    this.setType(ClassHelper.float_TYPE);
                } else if (value instanceof Character) {
                    this.setType(ClassHelper.char_TYPE);
                } else {
                    this.setType(ClassHelper.make(value.getClass()));
                }
            } else {
                this.setType(ClassHelper.make(value.getClass()));
            }
        }
    }

    public String toString() {
        return "ConstantExpression[" + this.value + "]";
    }

    @Override
    public void visit(GroovyCodeVisitor visitor) {
        visitor.visitConstantExpression(this);
    }

    @Override
    public Expression transformExpression(ExpressionTransformer transformer) {
        return this;
    }

    public Object getValue() {
        return this.value;
    }

    @Override
    public String getText() {
        return this.value == null ? "null" : this.value.toString();
    }

    public String getConstantName() {
        return this.constantName;
    }

    public void setConstantName(String constantName) {
        this.constantName = constantName;
    }

    public boolean isNullExpression() {
        return this.value == null;
    }

    public boolean isTrueExpression() {
        return Boolean.TRUE.equals(this.value);
    }

    public boolean isFalseExpression() {
        return Boolean.FALSE.equals(this.value);
    }

    public boolean isEmptyStringExpression() {
        return "".equals(this.value);
    }
}

