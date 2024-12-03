/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.ast.expr;

import groovy.lang.Closure;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.GroovyCodeVisitor;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.ExpressionTransformer;
import org.codehaus.groovy.ast.expr.VariableExpression;

public class MethodPointerExpression
extends Expression {
    private Expression expression;
    private Expression methodName;

    public MethodPointerExpression(Expression expression, Expression methodName) {
        this.expression = expression;
        this.methodName = methodName;
    }

    public Expression getExpression() {
        if (this.expression == null) {
            return VariableExpression.THIS_EXPRESSION;
        }
        return this.expression;
    }

    public Expression getMethodName() {
        return this.methodName;
    }

    @Override
    public void visit(GroovyCodeVisitor visitor) {
        visitor.visitMethodPointerExpression(this);
    }

    @Override
    public Expression transformExpression(ExpressionTransformer transformer) {
        Expression mname = transformer.transform(this.methodName);
        MethodPointerExpression ret = this.expression == null ? new MethodPointerExpression(VariableExpression.THIS_EXPRESSION, mname) : new MethodPointerExpression(transformer.transform(this.expression), mname);
        ret.setSourcePosition(this);
        ret.copyNodeMetaData(this);
        return ret;
    }

    @Override
    public String getText() {
        if (this.expression == null) {
            return "&" + this.methodName;
        }
        return this.expression.getText() + ".&" + this.methodName.getText();
    }

    @Override
    public ClassNode getType() {
        return ClassHelper.CLOSURE_TYPE.getPlainNodeReference();
    }

    public boolean isDynamic() {
        return false;
    }

    public Class getTypeClass() {
        return Closure.class;
    }
}

