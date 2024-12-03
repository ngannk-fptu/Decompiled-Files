/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.ast.expr;

import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.GroovyCodeVisitor;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.ExpressionTransformer;

public class CastExpression
extends Expression {
    private final Expression expression;
    private boolean ignoreAutoboxing = false;
    private boolean coerce = false;
    private boolean strict = false;

    public static CastExpression asExpression(ClassNode type, Expression expression) {
        CastExpression answer = new CastExpression(type, expression);
        answer.setCoerce(true);
        return answer;
    }

    public CastExpression(ClassNode type, Expression expression) {
        this(type, expression, false);
    }

    public CastExpression(ClassNode type, Expression expression, boolean ignoreAutoboxing) {
        super.setType(type);
        this.expression = expression;
        this.ignoreAutoboxing = ignoreAutoboxing;
    }

    public boolean isIgnoringAutoboxing() {
        return this.ignoreAutoboxing;
    }

    public boolean isCoerce() {
        return this.coerce;
    }

    public void setCoerce(boolean coerce) {
        this.coerce = coerce;
    }

    public boolean isStrict() {
        return this.strict;
    }

    public void setStrict(boolean strict) {
        this.strict = strict;
    }

    public String toString() {
        return super.toString() + "[(" + this.getType().getName() + ") " + this.expression + "]";
    }

    @Override
    public void visit(GroovyCodeVisitor visitor) {
        visitor.visitCastExpression(this);
    }

    @Override
    public Expression transformExpression(ExpressionTransformer transformer) {
        CastExpression ret = new CastExpression(this.getType(), transformer.transform(this.expression));
        ret.setSourcePosition(this);
        ret.setCoerce(this.isCoerce());
        ret.setStrict(this.isStrict());
        ret.copyNodeMetaData(this);
        return ret;
    }

    @Override
    public String getText() {
        return "(" + this.getType() + ") " + this.expression.getText();
    }

    public Expression getExpression() {
        return this.expression;
    }

    @Override
    public void setType(ClassNode t) {
        super.setType(t);
    }
}

