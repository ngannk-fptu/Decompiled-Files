/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.ast.stmt;

import org.codehaus.groovy.ast.GroovyCodeVisitor;
import org.codehaus.groovy.ast.expr.BooleanExpression;
import org.codehaus.groovy.ast.stmt.Statement;

public class IfStatement
extends Statement {
    private BooleanExpression booleanExpression;
    private Statement ifBlock;
    private Statement elseBlock;

    public IfStatement(BooleanExpression booleanExpression, Statement ifBlock, Statement elseBlock) {
        this.booleanExpression = booleanExpression;
        this.ifBlock = ifBlock;
        this.elseBlock = elseBlock;
    }

    @Override
    public void visit(GroovyCodeVisitor visitor) {
        visitor.visitIfElse(this);
    }

    public BooleanExpression getBooleanExpression() {
        return this.booleanExpression;
    }

    public Statement getIfBlock() {
        return this.ifBlock;
    }

    public Statement getElseBlock() {
        return this.elseBlock;
    }

    public void setBooleanExpression(BooleanExpression booleanExpression) {
        this.booleanExpression = booleanExpression;
    }

    public void setIfBlock(Statement statement) {
        this.ifBlock = statement;
    }

    public void setElseBlock(Statement statement) {
        this.elseBlock = statement;
    }
}

