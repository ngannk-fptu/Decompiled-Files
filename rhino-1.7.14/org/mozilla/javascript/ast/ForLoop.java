/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript.ast;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.Loop;
import org.mozilla.javascript.ast.NodeVisitor;

public class ForLoop
extends Loop {
    private AstNode initializer;
    private AstNode condition;
    private AstNode increment;

    public ForLoop() {
        this.type = 123;
    }

    public ForLoop(int pos) {
        super(pos);
        this.type = 123;
    }

    public ForLoop(int pos, int len) {
        super(pos, len);
        this.type = 123;
    }

    public AstNode getInitializer() {
        return this.initializer;
    }

    public void setInitializer(AstNode initializer) {
        this.assertNotNull(initializer);
        this.initializer = initializer;
        initializer.setParent(this);
    }

    public AstNode getCondition() {
        return this.condition;
    }

    public void setCondition(AstNode condition) {
        this.assertNotNull(condition);
        this.condition = condition;
        condition.setParent(this);
    }

    public AstNode getIncrement() {
        return this.increment;
    }

    public void setIncrement(AstNode increment) {
        this.assertNotNull(increment);
        this.increment = increment;
        increment.setParent(this);
    }

    @Override
    public String toSource(int depth) {
        StringBuilder sb = new StringBuilder();
        sb.append(this.makeIndent(depth));
        sb.append("for (");
        sb.append(this.initializer.toSource(0));
        sb.append("; ");
        sb.append(this.condition.toSource(0));
        sb.append("; ");
        sb.append(this.increment.toSource(0));
        sb.append(") ");
        if (this.getInlineComment() != null) {
            sb.append(this.getInlineComment().toSource()).append("\n");
        }
        if (this.body.getType() == 133) {
            String bodySource = this.body.toSource(depth);
            if (this.getInlineComment() == null) {
                bodySource = bodySource.trim();
            }
            sb.append(bodySource).append("\n");
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
            this.initializer.visit(v);
            this.condition.visit(v);
            this.increment.visit(v);
            this.body.visit(v);
        }
    }
}

