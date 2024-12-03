/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.ast.expr;

import org.codehaus.groovy.ast.GroovyCodeVisitor;
import org.codehaus.groovy.ast.expr.BooleanExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.ExpressionTransformer;

public class NotExpression
extends BooleanExpression {
    public NotExpression(Expression expression) {
        super(expression);
    }

    @Override
    public void visit(GroovyCodeVisitor visitor) {
        visitor.visitNotExpression(this);
    }

    public boolean isDynamic() {
        return false;
    }

    @Override
    public Expression transformExpression(ExpressionTransformer transformer) {
        NotExpression ret = new NotExpression(transformer.transform(this.getExpression()));
        ret.setSourcePosition(this);
        ret.copyNodeMetaData(this);
        return ret;
    }
}

