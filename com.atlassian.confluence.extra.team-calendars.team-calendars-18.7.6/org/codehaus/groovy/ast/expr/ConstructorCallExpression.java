/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.ast.expr;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.GroovyCodeVisitor;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.ExpressionTransformer;
import org.codehaus.groovy.ast.expr.MethodCall;
import org.codehaus.groovy.ast.expr.TupleExpression;

public class ConstructorCallExpression
extends Expression
implements MethodCall {
    private final Expression arguments;
    private boolean usesAnonymousInnerClass;

    public ConstructorCallExpression(ClassNode type, Expression arguments) {
        super.setType(type);
        this.arguments = !(arguments instanceof TupleExpression) ? new TupleExpression(arguments) : arguments;
    }

    @Override
    public void visit(GroovyCodeVisitor visitor) {
        visitor.visitConstructorCallExpression(this);
    }

    @Override
    public Expression transformExpression(ExpressionTransformer transformer) {
        Expression args = transformer.transform(this.arguments);
        ConstructorCallExpression ret = new ConstructorCallExpression(this.getType(), args);
        ret.setSourcePosition(this);
        ret.setUsingAnonymousInnerClass(this.isUsingAnonymousInnerClass());
        ret.copyNodeMetaData(this);
        return ret;
    }

    @Override
    public ASTNode getReceiver() {
        return null;
    }

    @Override
    public String getMethodAsString() {
        return "<init>";
    }

    @Override
    public Expression getArguments() {
        return this.arguments;
    }

    @Override
    public String getText() {
        String text = null;
        text = this.isSuperCall() ? "super " : (this.isThisCall() ? "this " : "new " + this.getType().getName());
        return text + this.arguments.getText();
    }

    public String toString() {
        return super.toString() + "[type: " + this.getType() + " arguments: " + this.arguments + "]";
    }

    public boolean isSuperCall() {
        return this.getType() == ClassNode.SUPER;
    }

    public boolean isSpecialCall() {
        return this.isThisCall() || this.isSuperCall();
    }

    public boolean isThisCall() {
        return this.getType() == ClassNode.THIS;
    }

    public void setUsingAnonymousInnerClass(boolean usage) {
        this.usesAnonymousInnerClass = usage;
    }

    public boolean isUsingAnonymousInnerClass() {
        return this.usesAnonymousInnerClass;
    }
}

