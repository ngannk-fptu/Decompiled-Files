/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript.ast;

import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.NodeVisitor;

public class UpdateExpression
extends AstNode {
    private AstNode operand;
    private boolean isPostfix;

    public UpdateExpression() {
    }

    public UpdateExpression(int pos) {
        super(pos);
    }

    public UpdateExpression(int pos, int len) {
        super(pos, len);
    }

    public UpdateExpression(int operator, int operatorPosition, AstNode operand) {
        this(operator, operatorPosition, operand, false);
    }

    public UpdateExpression(int operator, int operatorPosition, AstNode operand, boolean postFix) {
        this.assertNotNull(operand);
        int beg = postFix ? operand.getPosition() : operatorPosition;
        int end = postFix ? operatorPosition + 2 : operand.getPosition() + operand.getLength();
        this.setBounds(beg, end);
        this.setOperator(operator);
        this.setOperand(operand);
        this.isPostfix = postFix;
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

    public boolean isPostfix() {
        return this.isPostfix;
    }

    public boolean isPrefix() {
        return !this.isPostfix;
    }

    public void setIsPostfix(boolean isPostfix) {
        this.isPostfix = isPostfix;
    }

    @Override
    public String toSource(int depth) {
        StringBuilder sb = new StringBuilder();
        sb.append(this.makeIndent(depth));
        int type = this.getType();
        if (!this.isPostfix) {
            sb.append(UpdateExpression.operatorToString(type));
        }
        sb.append(this.operand.toSource());
        if (this.isPostfix) {
            sb.append(UpdateExpression.operatorToString(type));
        }
        return sb.toString();
    }

    @Override
    public void visit(NodeVisitor v) {
        if (v.visit(this)) {
            this.operand.visit(v);
        }
    }
}

