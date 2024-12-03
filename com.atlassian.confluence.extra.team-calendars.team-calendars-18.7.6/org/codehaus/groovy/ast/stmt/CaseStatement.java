/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.ast.stmt;

import org.codehaus.groovy.ast.GroovyCodeVisitor;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.stmt.Statement;

public class CaseStatement
extends Statement {
    private Statement code;
    private Expression expression;

    public CaseStatement(Expression expression, Statement code) {
        this.expression = expression;
        this.code = code;
    }

    public Statement getCode() {
        return this.code;
    }

    public void setCode(Statement code) {
        this.code = code;
    }

    public Expression getExpression() {
        return this.expression;
    }

    public void setExpression(Expression e) {
        this.expression = e;
    }

    @Override
    public void visit(GroovyCodeVisitor visitor) {
        visitor.visitCaseStatement(this);
    }

    public String toString() {
        return super.toString() + "[expression: " + this.expression + "; code: " + this.code + "]";
    }
}

