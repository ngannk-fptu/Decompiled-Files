/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.ast.expr;

import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.GroovyCodeVisitor;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.ExpressionTransformer;

public class FieldExpression
extends Expression {
    private final FieldNode field;
    private boolean useRef;

    public FieldExpression(FieldNode field) {
        this.field = field;
    }

    @Override
    public void visit(GroovyCodeVisitor visitor) {
        visitor.visitFieldExpression(this);
    }

    @Override
    public Expression transformExpression(ExpressionTransformer transformer) {
        return this;
    }

    public String getFieldName() {
        return this.field.getName();
    }

    public FieldNode getField() {
        return this.field;
    }

    @Override
    public String getText() {
        return "this." + this.field.getName();
    }

    public boolean isDynamicTyped() {
        return this.field.isDynamicTyped();
    }

    @Override
    public void setType(ClassNode type) {
        super.setType(type);
        this.field.setType(type);
    }

    @Override
    public ClassNode getType() {
        return this.field.getType();
    }

    public void setUseReferenceDirectly(boolean useRef) {
        this.useRef = useRef;
    }

    public boolean isUseReferenceDirectly() {
        return this.useRef;
    }

    public String toString() {
        return "field(" + this.getType() + " " + this.getFieldName() + ")";
    }
}

