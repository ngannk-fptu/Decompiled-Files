/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.internal.lang.reflect;

import org.aspectj.lang.reflect.PointcutExpression;

public class PointcutExpressionImpl
implements PointcutExpression {
    private String expression;

    public PointcutExpressionImpl(String aPointcutExpression) {
        this.expression = aPointcutExpression;
    }

    @Override
    public String asString() {
        return this.expression;
    }

    public String toString() {
        return this.asString();
    }
}

