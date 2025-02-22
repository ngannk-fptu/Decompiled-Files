/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript.ast;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.NodeVisitor;

public class EmptyStatement
extends AstNode {
    public EmptyStatement() {
        this.type = 132;
    }

    public EmptyStatement(int pos) {
        super(pos);
        this.type = 132;
    }

    public EmptyStatement(int pos, int len) {
        super(pos, len);
        this.type = 132;
    }

    @Override
    public String toSource(int depth) {
        StringBuilder sb = new StringBuilder();
        sb.append(this.makeIndent(depth)).append(";\n");
        return sb.toString();
    }

    @Override
    public void visit(NodeVisitor v) {
        v.visit(this);
    }
}

