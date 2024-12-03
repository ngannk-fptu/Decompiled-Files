/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.ast.expr;

import org.codehaus.groovy.ast.GroovyCodeVisitor;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.ExpressionTransformer;

public class MapEntryExpression
extends Expression {
    private Expression keyExpression;
    private Expression valueExpression;

    public MapEntryExpression(Expression keyExpression, Expression valueExpression) {
        this.keyExpression = keyExpression;
        this.valueExpression = valueExpression;
    }

    @Override
    public void visit(GroovyCodeVisitor visitor) {
        visitor.visitMapEntryExpression(this);
    }

    @Override
    public Expression transformExpression(ExpressionTransformer transformer) {
        MapEntryExpression ret = new MapEntryExpression(transformer.transform(this.keyExpression), transformer.transform(this.valueExpression));
        ret.setSourcePosition(this);
        ret.copyNodeMetaData(this);
        return ret;
    }

    public String toString() {
        return super.toString() + "(key: " + this.keyExpression + ", value: " + this.valueExpression + ")";
    }

    public Expression getKeyExpression() {
        return this.keyExpression;
    }

    public Expression getValueExpression() {
        return this.valueExpression;
    }

    public void setKeyExpression(Expression keyExpression) {
        this.keyExpression = keyExpression;
    }

    public void setValueExpression(Expression valueExpression) {
        this.valueExpression = valueExpression;
    }
}

