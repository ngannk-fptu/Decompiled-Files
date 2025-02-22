/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript.ast;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.NodeVisitor;

public class EmptyExpression
extends AstNode {
    public EmptyExpression() {
        this.type = 132;
    }

    public EmptyExpression(int pos) {
        super(pos);
        this.type = 132;
    }

    public EmptyExpression(int pos, int len) {
        super(pos, len);
        this.type = 132;
    }

    @Override
    public String toSource(int depth) {
        return this.makeIndent(depth);
    }

    @Override
    public void visit(NodeVisitor v) {
        v.visit(this);
    }
}

