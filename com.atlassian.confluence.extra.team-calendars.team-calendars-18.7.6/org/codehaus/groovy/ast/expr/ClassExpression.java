/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.ast.expr;

import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.GroovyCodeVisitor;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.ExpressionTransformer;

public class ClassExpression
extends Expression {
    public ClassExpression(ClassNode type) {
        super.setType(type);
    }

    @Override
    public void visit(GroovyCodeVisitor visitor) {
        visitor.visitClassExpression(this);
    }

    @Override
    public Expression transformExpression(ExpressionTransformer transformer) {
        return this;
    }

    @Override
    public String getText() {
        return this.getType().getName();
    }

    public String toString() {
        return super.toString() + "[type: " + this.getType().getName() + "]";
    }
}

