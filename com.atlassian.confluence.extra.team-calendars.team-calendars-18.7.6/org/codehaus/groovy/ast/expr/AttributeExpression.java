/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.ast.expr;

import org.codehaus.groovy.ast.GroovyCodeVisitor;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.ExpressionTransformer;
import org.codehaus.groovy.ast.expr.PropertyExpression;

public class AttributeExpression
extends PropertyExpression {
    public AttributeExpression(Expression objectExpression, Expression property) {
        super(objectExpression, property, false);
    }

    public AttributeExpression(Expression objectExpression, Expression property, boolean safe) {
        super(objectExpression, property, safe);
    }

    @Override
    public void visit(GroovyCodeVisitor visitor) {
        visitor.visitAttributeExpression(this);
    }

    @Override
    public Expression transformExpression(ExpressionTransformer transformer) {
        AttributeExpression ret = new AttributeExpression(transformer.transform(this.getObjectExpression()), transformer.transform(this.getProperty()), this.isSafe());
        ret.setSourcePosition(this);
        ret.setSpreadSafe(this.isSpreadSafe());
        ret.copyNodeMetaData(this);
        return ret;
    }
}

