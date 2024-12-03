/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.ast.stmt;

import org.codehaus.groovy.ast.GroovyCodeVisitor;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.stmt.Statement;

public class SynchronizedStatement
extends Statement {
    private Statement code;
    private Expression expression;

    public SynchronizedStatement(Expression expression, Statement code) {
        this.expression = expression;
        this.code = code;
    }

    public Statement getCode() {
        return this.code;
    }

    public void setCode(Statement statement) {
        this.code = statement;
    }

    public Expression getExpression() {
        return this.expression;
    }

    @Override
    public void visit(GroovyCodeVisitor visitor) {
        visitor.visitSynchronizedStatement(this);
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }
}

