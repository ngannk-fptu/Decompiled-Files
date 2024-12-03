/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript.ast;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.NodeVisitor;

public class IfStatement
extends AstNode {
    private AstNode condition;
    private AstNode thenPart;
    private int elsePosition = -1;
    private AstNode elsePart;
    private AstNode elseKeyWordInlineComment;
    private int lp = -1;
    private int rp = -1;

    public IfStatement() {
        this.type = 116;
    }

    public IfStatement(int pos) {
        super(pos);
        this.type = 116;
    }

    public IfStatement(int pos, int len) {
        super(pos, len);
        this.type = 116;
    }

    public AstNode getCondition() {
        return this.condition;
    }

    public void setCondition(AstNode condition) {
        this.assertNotNull(condition);
        this.condition = condition;
        condition.setParent(this);
    }

    public AstNode getThenPart() {
        return this.thenPart;
    }

    public void setThenPart(AstNode thenPart) {
        this.assertNotNull(thenPart);
        this.thenPart = thenPart;
        thenPart.setParent(this);
    }

    public AstNode getElsePart() {
        return this.elsePart;
    }

    public void setElsePart(AstNode elsePart) {
        this.elsePart = elsePart;
        if (elsePart != null) {
            elsePart.setParent(this);
        }
    }

    public int getElsePosition() {
        return this.elsePosition;
    }

    public void setElsePosition(int elsePosition) {
        this.elsePosition = elsePosition;
    }

    public int getLp() {
        return this.lp;
    }

    public void setLp(int lp) {
        this.lp = lp;
    }

    public int getRp() {
        return this.rp;
    }

    public void setRp(int rp) {
        this.rp = rp;
    }

    public void setParens(int lp, int rp) {
        this.lp = lp;
        this.rp = rp;
    }

    @Override
    public String toSource(int depth) {
        String pad = this.makeIndent(depth);
        StringBuilder sb = new StringBuilder(32);
        sb.append(pad);
        sb.append("if (");
        sb.append(this.condition.toSource(0));
        sb.append(") ");
        if (this.getInlineComment() != null) {
            sb.append("    ").append(this.getInlineComment().toSource()).append("\n");
        }
        if (this.thenPart.getType() != 133) {
            if (this.getInlineComment() == null) {
                sb.append("\n");
            }
            sb.append(this.makeIndent(depth + 1));
        }
        sb.append(this.thenPart.toSource(depth).trim());
        if (this.elsePart != null) {
            if (this.thenPart.getType() != 133) {
                sb.append("\n").append(pad).append("else ");
            } else {
                sb.append(" else ");
            }
            if (this.getElseKeyWordInlineComment() != null) {
                sb.append("    ").append(this.getElseKeyWordInlineComment().toSource()).append("\n");
            }
            if (this.elsePart.getType() != 133 && this.elsePart.getType() != 116) {
                if (this.getElseKeyWordInlineComment() == null) {
                    sb.append("\n");
                }
                sb.append(this.makeIndent(depth + 1));
            }
            sb.append(this.elsePart.toSource(depth).trim());
        }
        sb.append("\n");
        return sb.toString();
    }

    @Override
    public void visit(NodeVisitor v) {
        if (v.visit(this)) {
            this.condition.visit(v);
            this.thenPart.visit(v);
            if (this.elsePart != null) {
                this.elsePart.visit(v);
            }
        }
    }

    public AstNode getElseKeyWordInlineComment() {
        return this.elseKeyWordInlineComment;
    }

    public void setElseKeyWordInlineComment(AstNode elseKeyWordInlineComment) {
        this.elseKeyWordInlineComment = elseKeyWordInlineComment;
    }
}

