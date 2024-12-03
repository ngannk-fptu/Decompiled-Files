/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.ast.stmt;

import org.codehaus.groovy.ast.GroovyCodeVisitor;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.stmt.Statement;

public class ExpressionStatement
extends Statement {
    private Expression expression;

    public ExpressionStatement(Expression expression) {
        if (expression == null) {
            throw new IllegalArgumentException("expression cannot be null");
        }
        this.expression = expression;
    }

    @Override
    public void visit(GroovyCodeVisitor visitor) {
        visitor.visitExpressionStatement(this);
    }

    public Expression getExpression() {
        return this.expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    @Override
    public String getText() {
        return this.expression.getText();
    }

    public String toString() {
        return super.toString() + "[expression:" + this.expression + "]";
    }
}

