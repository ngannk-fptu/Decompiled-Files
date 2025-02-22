/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript.ast;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.NodeVisitor;

public class ExpressionStatement
extends AstNode {
    private AstNode expr;

    public void setHasResult() {
        this.type = 138;
    }

    public ExpressionStatement() {
        this.type = 137;
    }

    public ExpressionStatement(AstNode expr, boolean hasResult) {
        this(expr);
        if (hasResult) {
            this.setHasResult();
        }
    }

    public ExpressionStatement(AstNode expr) {
        this(expr.getPosition(), expr.getLength(), expr);
    }

    public ExpressionStatement(int pos, int len) {
        super(pos, len);
        this.type = 137;
    }

    public ExpressionStatement(int pos, int len, AstNode expr) {
        super(pos, len);
        this.type = 137;
        this.setExpression(expr);
    }

    public AstNode getExpression() {
        return this.expr;
    }

    public void setExpression(AstNode expression) {
        this.assertNotNull(expression);
        this.expr = expression;
        expression.setParent(this);
        this.setLineno(expression.getLineno());
    }

    @Override
    public boolean hasSideEffects() {
        return this.type == 138 || this.expr.hasSideEffects();
    }

    @Override
    public String toSource(int depth) {
        StringBuilder sb = new StringBuilder();
        sb.append(this.expr.toSource(depth));
        sb.append(";");
        if (this.getInlineComment() != null) {
            sb.append(this.getInlineComment().toSource(depth));
        }
        sb.append("\n");
        return sb.toString();
    }

    @Override
    public void visit(NodeVisitor v) {
        if (v.visit(this)) {
            this.expr.visit(v);
        }
    }
}

