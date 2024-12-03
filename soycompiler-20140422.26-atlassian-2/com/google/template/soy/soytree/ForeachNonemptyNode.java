/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.soytree;

import com.google.template.soy.exprtree.ExprRootNode;
import com.google.template.soy.soytree.AbstractBlockNode;
import com.google.template.soy.soytree.ForeachNode;
import com.google.template.soy.soytree.SoyNode;
import com.google.template.soy.soytree.defn.LocalVar;

public class ForeachNonemptyNode
extends AbstractBlockNode
implements SoyNode.ConditionalBlockNode,
SoyNode.LoopNode,
SoyNode.LocalVarBlockNode {
    public ForeachNonemptyNode(int id) {
        super(id);
    }

    protected ForeachNonemptyNode(ForeachNonemptyNode orig) {
        super(orig);
    }

    @Override
    public SoyNode.Kind getKind() {
        return SoyNode.Kind.FOREACH_NONEMPTY_NODE;
    }

    public int getForeachNodeId() {
        return this.getParent().getId();
    }

    public final LocalVar getVar() {
        return this.getParent().getVar();
    }

    @Override
    public final String getVarName() {
        return this.getParent().getVarName();
    }

    public String getExprText() {
        return this.getParent().getExprText();
    }

    public ExprRootNode<?> getExpr() {
        return this.getParent().getExpr();
    }

    @Override
    public String toSourceString() {
        StringBuilder sb = new StringBuilder();
        this.appendSourceStringForChildren(sb);
        return sb.toString();
    }

    @Override
    public ForeachNode getParent() {
        return (ForeachNode)super.getParent();
    }

    @Override
    public ForeachNonemptyNode clone() {
        return new ForeachNonemptyNode(this);
    }
}

