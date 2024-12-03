/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.soytree;

import com.google.template.soy.soytree.AbstractBlockCommandNode;
import com.google.template.soy.soytree.ForeachNode;
import com.google.template.soy.soytree.SoyNode;

public class ForeachIfemptyNode
extends AbstractBlockCommandNode
implements SoyNode.ConditionalBlockNode {
    public ForeachIfemptyNode(int id) {
        super(id, "ifempty", "");
    }

    protected ForeachIfemptyNode(ForeachIfemptyNode orig) {
        super(orig);
    }

    @Override
    public SoyNode.Kind getKind() {
        return SoyNode.Kind.FOREACH_IFEMPTY_NODE;
    }

    @Override
    public String toSourceString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getTagString());
        this.appendSourceStringForChildren(sb);
        return sb.toString();
    }

    @Override
    public ForeachNode getParent() {
        return (ForeachNode)super.getParent();
    }

    @Override
    public ForeachIfemptyNode clone() {
        return new ForeachIfemptyNode(this);
    }
}

