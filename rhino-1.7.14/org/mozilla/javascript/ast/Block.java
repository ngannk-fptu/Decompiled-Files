/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript.ast;

import org.mozilla.javascript.Node;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.NodeVisitor;

public class Block
extends AstNode {
    public Block() {
        this.type = 133;
    }

    public Block(int pos) {
        super(pos);
        this.type = 133;
    }

    public Block(int pos, int len) {
        super(pos, len);
        this.type = 133;
    }

    public void addStatement(AstNode statement) {
        this.addChild(statement);
    }

    @Override
    public String toSource(int depth) {
        StringBuilder sb = new StringBuilder();
        sb.append(this.makeIndent(depth));
        sb.append("{\n");
        for (Node kid : this) {
            AstNode astNodeKid = (AstNode)kid;
            sb.append(astNodeKid.toSource(depth + 1));
            if (astNodeKid.getType() != 165) continue;
            sb.append("\n");
        }
        sb.append(this.makeIndent(depth));
        sb.append("}");
        if (this.getInlineComment() != null) {
            sb.append(this.getInlineComment().toSource(depth));
        }
        sb.append("\n");
        return sb.toString();
    }

    @Override
    public void visit(NodeVisitor v) {
        if (v.visit(this)) {
            for (Node kid : this) {
                ((AstNode)kid).visit(v);
            }
        }
    }
}

