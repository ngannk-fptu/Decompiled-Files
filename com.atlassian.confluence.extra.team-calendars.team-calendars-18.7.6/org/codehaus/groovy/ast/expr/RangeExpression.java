/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.ast.expr;

import org.codehaus.groovy.ast.GroovyCodeVisitor;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.ExpressionTransformer;

public class RangeExpression
extends Expression {
    private Expression from;
    private Expression to;
    private boolean inclusive;

    public RangeExpression(Expression from, Expression to, boolean inclusive) {
        this.from = from;
        this.to = to;
        this.inclusive = inclusive;
    }

    @Override
    public void visit(GroovyCodeVisitor visitor) {
        visitor.visitRangeExpression(this);
    }

    @Override
    public Expression transformExpression(ExpressionTransformer transformer) {
        RangeExpression ret = new RangeExpression(transformer.transform(this.from), transformer.transform(this.to), this.inclusive);
        ret.setSourcePosition(this);
        ret.copyNodeMetaData(this);
        return ret;
    }

    public Expression getFrom() {
        return this.from;
    }

    public Expression getTo() {
        return this.to;
    }

    public boolean isInclusive() {
        return this.inclusive;
    }

    @Override
    public String getText() {
        return "(" + this.from.getText() + (!this.isInclusive() ? "..<" : "..") + this.to.getText() + ")";
    }
}

