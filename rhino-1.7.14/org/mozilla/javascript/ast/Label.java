/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript.ast;

import org.mozilla.javascript.ast.Jump;
import org.mozilla.javascript.ast.NodeVisitor;

public class Label
extends Jump {
    private String name;

    public Label() {
        this.type = 134;
    }

    public Label(int pos) {
        this(pos, -1);
    }

    public Label(int pos, int len) {
        this.type = 134;
        this.position = pos;
        this.length = len;
    }

    public Label(int pos, int len, String name) {
        this(pos, len);
        this.setName(name);
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        String string = name = name == null ? null : name.trim();
        if (name == null || "".equals(name)) {
            throw new IllegalArgumentException("invalid label name");
        }
        this.name = name;
    }

    @Override
    public String toSource(int depth) {
        StringBuilder sb = new StringBuilder();
        sb.append(this.makeIndent(depth));
        sb.append(this.name);
        sb.append(":\n");
        return sb.toString();
    }

    @Override
    public void visit(NodeVisitor v) {
        v.visit(this);
    }
}

