/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.soytree;

import com.google.template.soy.soytree.AbstractParentSoyNode;
import com.google.template.soy.soytree.MsgNode;
import com.google.template.soy.soytree.SoyNode;

public class MsgFallbackGroupNode
extends AbstractParentSoyNode<MsgNode>
implements SoyNode.StandaloneNode,
SoyNode.SplitLevelTopNode<MsgNode>,
SoyNode.StatementNode {
    public MsgFallbackGroupNode(int id) {
        super(id);
    }

    protected MsgFallbackGroupNode(MsgFallbackGroupNode orig) {
        super(orig);
    }

    @Override
    public SoyNode.Kind getKind() {
        return SoyNode.Kind.MSG_FALLBACK_GROUP_NODE;
    }

    @Override
    public String toSourceString() {
        StringBuilder sb = new StringBuilder();
        this.appendSourceStringForChildren(sb);
        sb.append("{/msg}");
        return sb.toString();
    }

    @Override
    public SoyNode.BlockNode getParent() {
        return (SoyNode.BlockNode)super.getParent();
    }

    @Override
    public MsgFallbackGroupNode clone() {
        return new MsgFallbackGroupNode(this);
    }
}

