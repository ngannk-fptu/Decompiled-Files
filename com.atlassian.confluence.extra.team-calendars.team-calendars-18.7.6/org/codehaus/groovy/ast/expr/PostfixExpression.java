/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.ast.expr;

import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.GroovyCodeVisitor;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.ExpressionTransformer;
import org.codehaus.groovy.syntax.Token;

public class PostfixExpression
extends Expression {
    private Token operation;
    private Expression expression;

    public PostfixExpression(Expression expression, Token operation) {
        this.operation = operation;
        this.expression = expression;
    }

    public String toString() {
        return super.toString() + "[" + this.expression + this.operation + "]";
    }

    @Override
    public void visit(GroovyCodeVisitor visitor) {
        visitor.visitPostfixExpression(this);
    }

    @Override
    public Expression transformExpression(ExpressionTransformer transformer) {
        PostfixExpression ret = new PostfixExpression(transformer.transform(this.expression), this.operation);
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
        return "(" + this.expression.getText() + this.operation.getText() + ")";
    }

    @Override
    public ClassNode getType() {
        return this.expression.getType();
    }
}

