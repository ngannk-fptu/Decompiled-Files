/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.ast.expr;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.GenericsType;
import org.codehaus.groovy.ast.GroovyCodeVisitor;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.ExpressionTransformer;
import org.codehaus.groovy.ast.expr.MethodCall;
import org.codehaus.groovy.ast.expr.TupleExpression;

public class MethodCallExpression
extends Expression
implements MethodCall {
    private Expression objectExpression;
    private Expression method;
    private Expression arguments;
    private boolean spreadSafe = false;
    private boolean safe = false;
    private boolean implicitThis;
    private GenericsType[] genericsTypes = null;
    private boolean usesGenerics = false;
    private MethodNode target;
    public static final Expression NO_ARGUMENTS = new TupleExpression();

    public MethodCallExpression(Expression objectExpression, String method, Expression arguments) {
        this(objectExpression, new ConstantExpression(method), arguments);
    }

    public MethodCallExpression(Expression objectExpression, Expression method, Expression arguments) {
        this.objectExpression = objectExpression;
        this.method = method;
        this.arguments = !(arguments instanceof TupleExpression) ? new TupleExpression(arguments) : arguments;
        this.setType(ClassHelper.DYNAMIC_TYPE);
        this.setImplicitThis(true);
    }

    @Override
    public void visit(GroovyCodeVisitor visitor) {
        visitor.visitMethodCallExpression(this);
    }

    @Override
    public Expression transformExpression(ExpressionTransformer transformer) {
        MethodCallExpression answer = new MethodCallExpression(transformer.transform(this.objectExpression), transformer.transform(this.method), transformer.transform(this.arguments));
        answer.setSafe(this.safe);
        answer.setSpreadSafe(this.spreadSafe);
        answer.setImplicitThis(this.implicitThis);
        answer.setGenericsTypes(this.genericsTypes);
        answer.setSourcePosition(this);
        answer.setMethodTarget(this.target);
        answer.copyNodeMetaData(this);
        return answer;
    }

    @Override
    public Expression getArguments() {
        return this.arguments;
    }

    public void setArguments(Expression arguments) {
        this.arguments = !(arguments instanceof TupleExpression) ? new TupleExpression(arguments) : arguments;
    }

    public Expression getMethod() {
        return this.method;
    }

    public void setMethod(Expression method) {
        this.method = method;
    }

    @Override
    public ASTNode getReceiver() {
        return this.getObjectExpression();
    }

    @Override
    public String getMethodAsString() {
        if (!(this.method instanceof ConstantExpression)) {
            return null;
        }
        ConstantExpression constant = (ConstantExpression)this.method;
        return constant.getText();
    }

    public void setObjectExpression(Expression objectExpression) {
        this.objectExpression = objectExpression;
    }

    public Expression getObjectExpression() {
        return this.objectExpression;
    }

    @Override
    public String getText() {
        String object = this.objectExpression.getText();
        String meth = this.method.getText();
        String args = this.arguments.getText();
        String spread = this.spreadSafe ? "*" : "";
        String dereference = this.safe ? "?" : "";
        return object + spread + dereference + "." + meth + args;
    }

    public boolean isSafe() {
        return this.safe;
    }

    public void setSafe(boolean safe) {
        this.safe = safe;
    }

    public boolean isSpreadSafe() {
        return this.spreadSafe;
    }

    public void setSpreadSafe(boolean value) {
        this.spreadSafe = value;
    }

    public boolean isImplicitThis() {
        return this.implicitThis;
    }

    public void setImplicitThis(boolean implicitThis) {
        this.implicitThis = implicitThis;
    }

    public String toString() {
        return super.toString() + "[object: " + this.objectExpression + " method: " + this.method + " arguments: " + this.arguments + "]";
    }

    public GenericsType[] getGenericsTypes() {
        return this.genericsTypes;
    }

    public void setGenericsTypes(GenericsType[] genericsTypes) {
        this.usesGenerics = this.usesGenerics || genericsTypes != null;
        this.genericsTypes = genericsTypes;
    }

    public boolean isUsingGenerics() {
        return this.usesGenerics;
    }

    public void setMethodTarget(MethodNode mn) {
        this.target = mn;
        if (mn != null) {
            this.setType(this.target.getReturnType());
        } else {
            this.setType(ClassHelper.OBJECT_TYPE);
        }
    }

    public MethodNode getMethodTarget() {
        return this.target;
    }
}

