/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.ast.stmt;

import org.codehaus.groovy.ast.GroovyCodeVisitor;
import org.codehaus.groovy.ast.expr.BooleanExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.stmt.Statement;

public class AssertStatement
extends Statement {
    private BooleanExpression booleanExpression;
    private Expression messageExpression;

    public AssertStatement(BooleanExpression booleanExpression) {
        this(booleanExpression, ConstantExpression.NULL);
    }

    public AssertStatement(BooleanExpression booleanExpression, Expression messageExpression) {
        this.booleanExpression = booleanExpression;
        this.messageExpression = messageExpression;
    }

    @Override
    public void visit(GroovyCodeVisitor visitor) {
        visitor.visitAssertStatement(this);
    }

    public Expression getMessageExpression() {
        return this.messageExpression;
    }

    public BooleanExpression getBooleanExpression() {
        return this.booleanExpression;
    }

    public void setBooleanExpression(BooleanExpression booleanExpression) {
        this.booleanExpression = booleanExpression;
    }

    public void setMessageExpression(Expression messageExpression) {
        this.messageExpression = messageExpression;
    }
}

