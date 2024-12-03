/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.ast.expr;

import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.GroovyCodeVisitor;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.ExpressionTransformer;

public class BooleanExpression
extends Expression {
    private final Expression expression;

    public BooleanExpression(Expression expression) {
        this.expression = expression;
        this.setType(ClassHelper.boolean_TYPE);
    }

    public Expression getExpression() {
        return this.expression;
    }

    @Override
    public void visit(GroovyCodeVisitor visitor) {
        visitor.visitBooleanExpression(this);
    }

    @Override
    public Expression transformExpression(ExpressionTransformer transformer) {
        BooleanExpression ret = new BooleanExpression(transformer.transform(this.expression));
        ret.setSourcePosition(this);
        ret.copyNodeMetaData(this);
        return ret;
    }

    @Override
    public String getText() {
        return this.expression.getText();
    }
}

