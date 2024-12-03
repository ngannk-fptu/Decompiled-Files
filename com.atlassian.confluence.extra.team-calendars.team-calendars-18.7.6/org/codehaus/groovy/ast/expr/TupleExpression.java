/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.ast.expr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.codehaus.groovy.ast.GroovyCodeVisitor;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.ExpressionTransformer;

public class TupleExpression
extends Expression
implements Iterable<Expression> {
    private List<Expression> expressions;

    public TupleExpression() {
        this(0);
    }

    public TupleExpression(Expression expr) {
        this(1);
        this.addExpression(expr);
    }

    public TupleExpression(Expression expr1, Expression expr2) {
        this(2);
        this.addExpression(expr1);
        this.addExpression(expr2);
    }

    public TupleExpression(Expression expr1, Expression expr2, Expression expr3) {
        this(3);
        this.addExpression(expr1);
        this.addExpression(expr2);
        this.addExpression(expr3);
    }

    public TupleExpression(int length) {
        this.expressions = new ArrayList<Expression>(length);
    }

    public TupleExpression(List<Expression> expressions) {
        this.expressions = expressions;
    }

    public TupleExpression(Expression[] expressionArray) {
        this();
        this.expressions.addAll(Arrays.asList(expressionArray));
    }

    public TupleExpression addExpression(Expression expression) {
        this.expressions.add(expression);
        return this;
    }

    public List<Expression> getExpressions() {
        return this.expressions;
    }

    @Override
    public void visit(GroovyCodeVisitor visitor) {
        visitor.visitTupleExpression(this);
    }

    @Override
    public Expression transformExpression(ExpressionTransformer transformer) {
        TupleExpression ret = new TupleExpression(this.transformExpressions(this.getExpressions(), transformer));
        ret.setSourcePosition(this);
        ret.copyNodeMetaData(this);
        return ret;
    }

    public Expression getExpression(int i) {
        return this.expressions.get(i);
    }

    @Override
    public String getText() {
        StringBuilder buffer = new StringBuilder("(");
        boolean first = true;
        for (Expression expression : this.expressions) {
            if (first) {
                first = false;
            } else {
                buffer.append(", ");
            }
            buffer.append(expression.getText());
        }
        buffer.append(")");
        return buffer.toString();
    }

    public String toString() {
        return super.toString() + this.expressions;
    }

    @Override
    public Iterator<Expression> iterator() {
        return Collections.unmodifiableList(this.expressions).iterator();
    }
}

