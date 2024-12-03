/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.soytree;

import com.google.template.soy.soytree.AbstractParentSoyNode;
import com.google.template.soy.soytree.SoyNode;

public class IfNode
extends AbstractParentSoyNode<SoyNode>
implements SoyNode.StandaloneNode,
SoyNode.SplitLevelTopNode<SoyNode>,
SoyNode.StatementNode {
    public IfNode(int id) {
        super(id);
    }

    protected IfNode(IfNode orig) {
        super(orig);
    }

    @Override
    public SoyNode.Kind getKind() {
        return SoyNode.Kind.IF_NODE;
    }

    @Override
    public String toSourceString() {
        StringBuilder sb = new StringBuilder();
        this.appendSourceStringForChildren(sb);
        sb.append("{/if}");
        return sb.toString();
    }

    @Override
    public SoyNode.BlockNode getParent() {
        return (SoyNode.BlockNode)super.getParent();
    }

    @Override
    public IfNode clone() {
        return new IfNode(this);
    }
}

