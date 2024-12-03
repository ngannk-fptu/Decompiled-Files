/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.ast.expr;

import org.codehaus.groovy.ast.GroovyCodeVisitor;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.ExpressionTransformer;

public class EmptyExpression
extends Expression {
    public static final EmptyExpression INSTANCE = new EmptyExpression();

    @Override
    public Expression transformExpression(ExpressionTransformer transformer) {
        return this;
    }

    @Override
    public void visit(GroovyCodeVisitor visitor) {
    }
}

