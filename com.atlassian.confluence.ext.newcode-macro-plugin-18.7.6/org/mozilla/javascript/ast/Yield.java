/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript.ast;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.NodeVisitor;

public class Yield
extends AstNode {
    private AstNode value;

    public Yield() {
        this.type = 73;
    }

    public Yield(int pos) {
        super(pos);
        this.type = 73;
    }

    public Yield(int pos, int len) {
        super(pos, len);
        this.type = 73;
    }

    public Yield(int pos, int len, AstNode value, boolean isStar) {
        super(pos, len);
        this.type = isStar ? 169 : 73;
        this.setValue(value);
    }

    public AstNode getValue() {
        return this.value;
    }

    public void setValue(AstNode expr) {
        this.value = expr;
        if (expr != null) {
            expr.setParent(this);
        }
    }

    @Override
    public String toSource(int depth) {
        return this.value == null ? "yield" : "yield " + this.value.toSource(0);
    }

    @Override
    public void visit(NodeVisitor v) {
        if (v.visit(this) && this.value != null) {
            this.value.visit(v);
        }
    }
}

