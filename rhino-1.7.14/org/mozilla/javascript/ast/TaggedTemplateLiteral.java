/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript.ast;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.NodeVisitor;

public class TaggedTemplateLiteral
extends AstNode {
    private AstNode target;
    private AstNode templateLiteral;

    public TaggedTemplateLiteral() {
        this.type = 173;
    }

    public TaggedTemplateLiteral(int pos) {
        super(pos);
        this.type = 173;
    }

    public TaggedTemplateLiteral(int pos, int len) {
        super(pos, len);
        this.type = 173;
    }

    public AstNode getTarget() {
        return this.target;
    }

    public void setTarget(AstNode target) {
        this.target = target;
    }

    public AstNode getTemplateLiteral() {
        return this.templateLiteral;
    }

    public void setTemplateLiteral(AstNode templateLiteral) {
        this.templateLiteral = templateLiteral;
    }

    @Override
    public String toSource(int depth) {
        StringBuilder sb = new StringBuilder();
        sb.append(this.makeIndent(depth));
        sb.append(this.target.toSource(0));
        sb.append(this.templateLiteral.toSource(0));
        return sb.toString();
    }

    @Override
    public void visit(NodeVisitor v) {
        if (v.visit(this)) {
            this.target.visit(v);
            this.templateLiteral.visit(v);
        }
    }
}

