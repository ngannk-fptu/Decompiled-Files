/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.ast.expr;

import groovy.lang.MetaMethod;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.GroovyCodeVisitor;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.ExpressionTransformer;
import org.codehaus.groovy.ast.expr.MethodCall;

public class StaticMethodCallExpression
extends Expression
implements MethodCall {
    private ClassNode ownerType;
    private String method;
    private Expression arguments;
    private MetaMethod metaMethod = null;

    public StaticMethodCallExpression(ClassNode type, String method, Expression arguments) {
        this.ownerType = type;
        this.method = method;
        this.arguments = arguments;
    }

    @Override
    public void visit(GroovyCodeVisitor visitor) {
        visitor.visitStaticMethodCallExpression(this);
    }

    @Override
    public Expression transformExpression(ExpressionTransformer transformer) {
        StaticMethodCallExpression ret = new StaticMethodCallExpression(this.getOwnerType(), this.method, transformer.transform(this.arguments));
        ret.setSourcePosition(this);
        ret.copyNodeMetaData(this);
        return ret;
    }

    @Override
    public ASTNode getReceiver() {
        return this.ownerType;
    }

    @Override
    public String getMethodAsString() {
        return this.method;
    }

    @Override
    public Expression getArguments() {
        return this.arguments;
    }

    public String getMethod() {
        return this.method;
    }

    @Override
    public String getText() {
        return this.getOwnerType().getName() + "." + this.method + this.arguments.getText();
    }

    public String toString() {
        return super.toString() + "[" + this.getOwnerType().getName() + "#" + this.method + " arguments: " + this.arguments + "]";
    }

    public ClassNode getOwnerType() {
        return this.ownerType;
    }

    public void setOwnerType(ClassNode ownerType) {
        this.ownerType = ownerType;
    }

    public void setMetaMethod(MetaMethod metaMethod) {
        this.metaMethod = metaMethod;
    }

    public MetaMethod getMetaMethod() {
        return this.metaMethod;
    }
}

