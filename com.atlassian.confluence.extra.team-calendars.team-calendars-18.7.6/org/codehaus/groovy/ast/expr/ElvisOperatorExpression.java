/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.ast.expr;

import org.codehaus.groovy.ast.GroovyCodeVisitor;
import org.codehaus.groovy.ast.expr.BooleanExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.ExpressionTransformer;
import org.codehaus.groovy.ast.expr.TernaryExpression;

public class ElvisOperatorExpression
extends TernaryExpression {
    public ElvisOperatorExpression(Expression base, Expression falseExpression) {
        super(ElvisOperatorExpression.getBool(base), base, falseExpression);
    }

    private static BooleanExpression getBool(Expression base) {
        BooleanExpression be = new BooleanExpression(base);
        be.setSourcePosition(base);
        return be;
    }

    @Override
    public void visit(GroovyCodeVisitor visitor) {
        visitor.visitShortTernaryExpression(this);
    }

    @Override
    public Expression transformExpression(ExpressionTransformer transformer) {
        ElvisOperatorExpression ret = new ElvisOperatorExpression(transformer.transform(this.getTrueExpression()), transformer.transform(this.getFalseExpression()));
        ret.setSourcePosition(this);
        ret.copyNodeMetaData(this);
        return ret;
    }
}

