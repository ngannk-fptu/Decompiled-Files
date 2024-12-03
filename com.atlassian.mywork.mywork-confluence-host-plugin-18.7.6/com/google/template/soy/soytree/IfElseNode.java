/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.soytree;

import com.google.template.soy.soytree.AbstractBlockCommandNode;
import com.google.template.soy.soytree.SoyNode;

public class IfElseNode
extends AbstractBlockCommandNode
implements SoyNode.ConditionalBlockNode {
    public IfElseNode(int id) {
        super(id, "else", "");
    }

    protected IfElseNode(IfElseNode orig) {
        super(orig);
    }

    @Override
    public SoyNode.Kind getKind() {
        return SoyNode.Kind.IF_ELSE_NODE;
    }

    @Override
    public String toSourceString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getTagString());
        this.appendSourceStringForChildren(sb);
        return sb.toString();
    }

    @Override
    public IfElseNode clone() {
        return new IfElseNode(this);
    }
}

