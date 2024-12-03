/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.ast.stmt;

import org.codehaus.groovy.ast.GroovyCodeVisitor;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.stmt.Statement;

public class ThrowStatement
extends Statement {
    private Expression expression;

    public ThrowStatement(Expression expression) {
        this.expression = expression;
    }

    public Expression getExpression() {
        return this.expression;
    }

    @Override
    public void visit(GroovyCodeVisitor visitor) {
        visitor.visitThrowStatement(this);
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    @Override
    public String getText() {
        return "throw " + this.expression.getText();
    }
}

