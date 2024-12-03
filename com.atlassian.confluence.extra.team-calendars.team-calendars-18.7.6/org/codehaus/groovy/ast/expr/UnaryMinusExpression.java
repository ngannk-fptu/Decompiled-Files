/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.ast.expr;

import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.GroovyCodeVisitor;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.ExpressionTransformer;

public class UnaryMinusExpression
extends Expression {
    private final Expression expression;

    public UnaryMinusExpression(Expression expression) {
        this.expression = expression;
    }

    public Expression getExpression() {
        return this.expression;
    }

    @Override
    public void visit(GroovyCodeVisitor visitor) {
        visitor.visitUnaryMinusExpression(this);
    }

    @Override
    public Expression transformExpression(ExpressionTransformer transformer) {
        UnaryMinusExpression ret = new UnaryMinusExpression(transformer.transform(this.expression));
        ret.setSourcePosition(this);
        ret.copyNodeMetaData(this);
        return ret;
    }

    @Override
    public String getText() {
        return this.expression.getText();
    }

    @Override
    public ClassNode getType() {
        return this.expression.getType();
    }

    public boolean isDynamic() {
        return false;
    }
}

