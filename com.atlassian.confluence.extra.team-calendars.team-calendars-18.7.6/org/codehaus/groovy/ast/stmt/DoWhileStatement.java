/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.ast.stmt;

import org.codehaus.groovy.ast.GroovyCodeVisitor;
import org.codehaus.groovy.ast.expr.BooleanExpression;
import org.codehaus.groovy.ast.stmt.LoopingStatement;
import org.codehaus.groovy.ast.stmt.Statement;

public class DoWhileStatement
extends Statement
implements LoopingStatement {
    private BooleanExpression booleanExpression;
    private Statement loopBlock;

    public DoWhileStatement(BooleanExpression booleanExpression, Statement loopBlock) {
        this.booleanExpression = booleanExpression;
        this.loopBlock = loopBlock;
    }

    @Override
    public void visit(GroovyCodeVisitor visitor) {
        visitor.visitDoWhileLoop(this);
    }

    public BooleanExpression getBooleanExpression() {
        return this.booleanExpression;
    }

    @Override
    public Statement getLoopBlock() {
        return this.loopBlock;
    }

    public void setBooleanExpression(BooleanExpression booleanExpression) {
        this.booleanExpression = booleanExpression;
    }

    @Override
    public void setLoopBlock(Statement loopBlock) {
        this.loopBlock = loopBlock;
    }
}

