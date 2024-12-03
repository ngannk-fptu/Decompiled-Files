/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.ast.expr;

import java.util.ArrayList;
import java.util.List;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.GroovyCodeVisitor;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.ExpressionTransformer;

public class ListExpression
extends Expression {
    private List<Expression> expressions;
    private boolean wrapped = false;

    public ListExpression() {
        this(new ArrayList<Expression>());
    }

    public ListExpression(List<Expression> expressions) {
        this.expressions = expressions;
        this.setType(ClassHelper.LIST_TYPE);
    }

    public void addExpression(Expression expression) {
        this.expressions.add(expression);
    }

    public List<Expression> getExpressions() {
        return this.expressions;
    }

    public void setWrapped(boolean value) {
        this.wrapped = value;
    }

    public boolean isWrapped() {
        return this.wrapped;
    }

    @Override
    public void visit(GroovyCodeVisitor visitor) {
        visitor.visitListExpression(this);
    }

    @Override
    public Expression transformExpression(ExpressionTransformer transformer) {
        ListExpression ret = new ListExpression(this.transformExpressions(this.getExpressions(), transformer));
        ret.setSourcePosition(this);
        ret.copyNodeMetaData(this);
        return ret;
    }

    public Expression getExpression(int i) {
        return this.expressions.get(i);
    }

    @Override
    public String getText() {
        StringBuilder buffer = new StringBuilder("[");
        boolean first = true;
        for (Expression expression : this.expressions) {
            if (first) {
                first = false;
            } else {
                buffer.append(", ");
            }
            buffer.append(expression.getText());
        }
        buffer.append("]");
        return buffer.toString();
    }

    public String toString() {
        return super.toString() + this.expressions;
    }
}

