/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.ast.expr;

import java.util.Map;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.GroovyCodeVisitor;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.Expression;

public class AnnotationConstantExpression
extends ConstantExpression {
    public AnnotationConstantExpression(AnnotationNode node) {
        super(node);
        this.setType(node.getClassNode());
    }

    @Override
    public void visit(GroovyCodeVisitor visitor) {
        AnnotationNode node = (AnnotationNode)this.getValue();
        Map<String, Expression> attrs = node.getMembers();
        for (Expression expr : attrs.values()) {
            expr.visit(visitor);
        }
        super.visit(visitor);
    }

    @Override
    public String toString() {
        return "AnnotationConstantExpression[" + this.getValue() + "]";
    }
}

