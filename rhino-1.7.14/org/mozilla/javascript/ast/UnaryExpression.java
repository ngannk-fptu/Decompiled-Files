/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript.ast;

import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.NodeVisitor;

public class UnaryExpression
extends AstNode {
    private AstNode operand;

    public UnaryExpression() {
    }

    public UnaryExpression(int pos) {
        super(pos);
    }

    public UnaryExpression(int pos, int len) {
        super(pos, len);
    }

    public UnaryExpression(int operator, int operatorPosition, AstNode operand) {
        this.assertNotNull(operand);
        int beg = operand.getPosition();
        int end = operand.getPosition() + operand.getLength();
        this.setBounds(beg, end);
        this.setOperator(operator);
        this.setOperand(operand);
    }

    public int getOperator() {
        return this.type;
    }

    public void setOperator(int operator) {
        if (!Token.isValidToken(operator)) {
            throw new IllegalArgumentException("Invalid token: " + operator);
        }
        this.setType(operator);
    }

    public AstNode getOperand() {
        return this.operand;
    }

    public void setOperand(AstNode operand) {
        this.assertNotNull(operand);
        this.operand = operand;
        operand.setParent(this);
    }

    @Override
    public String toSource(int depth) {
        StringBuilder sb = new StringBuilder();
        sb.append(this.makeIndent(depth));
        int type = this.getType();
        sb.append(UnaryExpression.operatorToString(type));
        if (type == 32 || type == 31 || type == 130) {
            sb.append(" ");
        }
        sb.append(this.operand.toSource());
        return sb.toString();
    }

    @Override
    public void visit(NodeVisitor v) {
        if (v.visit(this)) {
            this.operand.visit(v);
        }
    }
}

