/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform.sc;

import java.util.LinkedList;
import java.util.List;
import org.codehaus.groovy.ast.GroovyCodeVisitor;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.ExpressionTransformer;

public class ListOfExpressionsExpression
extends Expression {
    private final List<Expression> expressions;

    public ListOfExpressionsExpression() {
        this.expressions = new LinkedList<Expression>();
    }

    public ListOfExpressionsExpression(List<Expression> expressions) {
        this.expressions = expressions;
    }

    @Override
    public Expression transformExpression(ExpressionTransformer transformer) {
        return new ListOfExpressionsExpression(this.transformExpressions(this.expressions, transformer));
    }

    @Override
    public void visit(GroovyCodeVisitor visitor) {
        for (Expression expression : this.expressions) {
            expression.visit(visitor);
        }
    }

    public void addExpression(Expression expression) {
        this.expressions.add(expression);
    }
}

