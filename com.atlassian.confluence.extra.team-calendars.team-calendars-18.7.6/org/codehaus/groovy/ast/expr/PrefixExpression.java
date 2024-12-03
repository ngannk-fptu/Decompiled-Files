/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.ast.expr;

import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.GroovyCodeVisitor;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.ExpressionTransformer;
import org.codehaus.groovy.syntax.Token;

public class PrefixExpression
extends Expression {
    private Token operation;
    private Expression expression;

    public PrefixExpression(Token operation, Expression expression) {
        this.operation = operation;
        this.expression = expression;
    }

    public String toString() {
        return super.toString() + "[" + this.operation + this.expression + "]";
    }

    @Override
    public void visit(GroovyCodeVisitor visitor) {
        visitor.visitPrefixExpression(this);
    }

    @Override
    public Expression transformExpression(ExpressionTransformer transformer) {
        PrefixExpression ret = new PrefixExpression(this.operation, transformer.transform(this.expression));
        ret.setSourcePosition(this);
        ret.copyNodeMetaData(this);
        return ret;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    public Token getOperation() {
        return this.operation;
    }

    public Expression getExpression() {
        return this.expression;
    }

    @Override
    public String getText() {
        return "(" + this.operation.getText() + this.expression.getText() + ")";
    }

    @Override
    public ClassNode getType() {
        return this.expression.getType();
    }
}

