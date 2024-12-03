/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript.ast;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.InfixExpression;
import org.mozilla.javascript.ast.XmlRef;

public class XmlMemberGet
extends InfixExpression {
    public XmlMemberGet() {
        this.type = 147;
    }

    public XmlMemberGet(int pos) {
        super(pos);
        this.type = 147;
    }

    public XmlMemberGet(int pos, int len) {
        super(pos, len);
        this.type = 147;
    }

    public XmlMemberGet(int pos, int len, AstNode target, XmlRef ref) {
        super(pos, len, target, ref);
        this.type = 147;
    }

    public XmlMemberGet(AstNode target, XmlRef ref) {
        super(target, ref);
        this.type = 147;
    }

    public XmlMemberGet(AstNode target, XmlRef ref, int opPos) {
        super(147, target, (AstNode)ref, opPos);
        this.type = 147;
    }

    public AstNode getTarget() {
        return this.getLeft();
    }

    public void setTarget(AstNode target) {
        this.setLeft(target);
    }

    public XmlRef getMemberRef() {
        return (XmlRef)this.getRight();
    }

    public void setProperty(XmlRef ref) {
        this.setRight(ref);
    }

    @Override
    public String toSource(int depth) {
        StringBuilder sb = new StringBuilder();
        sb.append(this.makeIndent(depth));
        sb.append(this.getLeft().toSource(0));
        sb.append(this.dotsToString());
        sb.append(this.getRight().toSource(0));
        return sb.toString();
    }

    private String dotsToString() {
        switch (this.getType()) {
            case 112: {
                return ".";
            }
            case 147: {
                return "..";
            }
        }
        throw new IllegalArgumentException("Invalid type of XmlMemberGet: " + this.getType());
    }
}

