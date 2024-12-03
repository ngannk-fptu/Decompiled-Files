/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.ast.expr;

import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.GroovyCodeVisitor;
import org.codehaus.groovy.ast.expr.BooleanExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.ExpressionTransformer;

public class TernaryExpression
extends Expression {
    private BooleanExpression booleanExpression;
    private Expression trueExpression;
    private Expression falseExpression;

    public TernaryExpression(BooleanExpression booleanExpression, Expression trueExpression, Expression falseExpression) {
        this.booleanExpression = booleanExpression;
        this.trueExpression = trueExpression;
        this.falseExpression = falseExpression;
    }

    @Override
    public void visit(GroovyCodeVisitor visitor) {
        visitor.visitTernaryExpression(this);
    }

    @Override
    public Expression transformExpression(ExpressionTransformer transformer) {
        TernaryExpression ret = new TernaryExpression((BooleanExpression)transformer.transform(this.booleanExpression), transformer.transform(this.trueExpression), transformer.transform(this.falseExpression));
        ret.setSourcePosition(this);
        ret.copyNodeMetaData(this);
        return ret;
    }

    public String toString() {
        return super.toString() + "[" + this.booleanExpression + " ? " + this.trueExpression + " : " + this.falseExpression + "]";
    }

    public BooleanExpression getBooleanExpression() {
        return this.booleanExpression;
    }

    public Expression getFalseExpression() {
        return this.falseExpression;
    }

    public Expression getTrueExpression() {
        return this.trueExpression;
    }

    @Override
    public String getText() {
        return "(" + this.booleanExpression.getText() + ") ? " + this.trueExpression.getText() + " : " + this.falseExpression.getText();
    }

    @Override
    public ClassNode getType() {
        return ClassHelper.OBJECT_TYPE;
    }
}

