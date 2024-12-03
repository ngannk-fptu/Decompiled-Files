/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 */
package com.google.template.soy.soytree.jssrc;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.template.soy.soytree.AbstractParentSoyNode;
import com.google.template.soy.soytree.MsgFallbackGroupNode;
import com.google.template.soy.soytree.MsgNode;
import com.google.template.soy.soytree.SoyNode;
import java.util.List;

public class GoogMsgDefNode
extends AbstractParentSoyNode<MsgNode>
implements SoyNode.StandaloneNode,
SoyNode.SplitLevelTopNode<MsgNode>,
SoyNode.LocalVarInlineNode {
    private final ImmutableMap<MsgNode, Long> childToMsgIdMap;
    private final String renderedGoogMsgVarName;
    private final String sourceString;

    public GoogMsgDefNode(int id, MsgFallbackGroupNode origMsgFbGrpNode, List<Long> childMsgIds) {
        super(id);
        int numChildren = origMsgFbGrpNode.numChildren();
        Preconditions.checkArgument((childMsgIds.size() == numChildren ? 1 : 0) != 0);
        this.addChildren(origMsgFbGrpNode.getChildren());
        ImmutableMap.Builder childToMsgIdMapBuilder = ImmutableMap.builder();
        for (int i = 0; i < numChildren; ++i) {
            childToMsgIdMapBuilder.put((Object)this.getChild(i), (Object)childMsgIds.get(i));
        }
        this.childToMsgIdMap = childToMsgIdMapBuilder.build();
        this.renderedGoogMsgVarName = "msg_s" + id;
        this.sourceString = "[GoogMsgDefNode " + this.renderedGoogMsgVarName + " " + origMsgFbGrpNode.toSourceString() + "]";
    }

    protected GoogMsgDefNode(GoogMsgDefNode orig) {
        super(orig);
        this.childToMsgIdMap = orig.childToMsgIdMap;
        this.renderedGoogMsgVarName = orig.renderedGoogMsgVarName;
        this.sourceString = orig.sourceString;
    }

    @Override
    public SoyNode.Kind getKind() {
        return SoyNode.Kind.GOOG_MSG_DEF_NODE;
    }

    public long getChildMsgId(MsgNode child) {
        return (Long)this.childToMsgIdMap.get((Object)child);
    }

    public String getRenderedGoogMsgVarName() {
        return this.renderedGoogMsgVarName;
    }

    @Override
    public String getVarName() {
        return this.getRenderedGoogMsgVarName();
    }

    @Override
    public String toSourceString() {
        return this.sourceString;
    }

    @Override
    public SoyNode.BlockNode getParent() {
        return (SoyNode.BlockNode)super.getParent();
    }

    @Override
    public GoogMsgDefNode clone() {
        return new GoogMsgDefNode(this);
    }
}

