/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript.ast;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.Loop;
import org.mozilla.javascript.ast.NodeVisitor;

public class WhileLoop
extends Loop {
    private AstNode condition;

    public WhileLoop() {
        this.type = 121;
    }

    public WhileLoop(int pos) {
        super(pos);
        this.type = 121;
    }

    public WhileLoop(int pos, int len) {
        super(pos, len);
        this.type = 121;
    }

    public AstNode getCondition() {
        return this.condition;
    }

    public void setCondition(AstNode condition) {
        this.assertNotNull(condition);
        this.condition = condition;
        condition.setParent(this);
    }

    @Override
    public String toSource(int depth) {
        StringBuilder sb = new StringBuilder();
        sb.append(this.makeIndent(depth));
        sb.append("while (");
        sb.append(this.condition.toSource(0));
        sb.append(") ");
        if (this.getInlineComment() != null) {
            sb.append(this.getInlineComment().toSource(depth + 1)).append("\n");
        }
        if (this.body.getType() == 133) {
            sb.append(this.body.toSource(depth).trim());
            sb.append("\n");
        } else {
            if (this.getInlineComment() == null) {
                sb.append("\n");
            }
            sb.append(this.body.toSource(depth + 1));
        }
        return sb.toString();
    }

    @Override
    public void visit(NodeVisitor v) {
        if (v.visit(this)) {
            this.condition.visit(v);
            this.body.visit(v);
        }
    }
}

