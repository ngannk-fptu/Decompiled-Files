/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform.stc;

import org.codehaus.groovy.ast.expr.Expression;

class SecondPassExpression<T> {
    private final Expression expression;
    private final T data;

    SecondPassExpression(Expression expression) {
        this.expression = expression;
        this.data = null;
    }

    SecondPassExpression(Expression expression, T data) {
        this.data = data;
        this.expression = expression;
    }

    public T getData() {
        return this.data;
    }

    public Expression getExpression() {
        return this.expression;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        SecondPassExpression that = (SecondPassExpression)o;
        if (this.data != null ? !this.data.equals(that.data) : that.data != null) {
            return false;
        }
        return this.expression.equals(that.expression);
    }

    public int hashCode() {
        int result = this.expression.hashCode();
        result = 31 * result + (this.data != null ? this.data.hashCode() : 0);
        return result;
    }
}

