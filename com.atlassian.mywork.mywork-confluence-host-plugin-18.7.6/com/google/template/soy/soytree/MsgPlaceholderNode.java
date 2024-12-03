/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.soytree;

import com.google.template.soy.soytree.AbstractBlockNode;
import com.google.template.soy.soytree.SoyNode;

public class MsgPlaceholderNode
extends AbstractBlockNode
implements SoyNode.MsgSubstUnitNode {
    private final String basePhName;
    private final SoyNode.Kind initialNodeKind;
    private final Object samenessKey;

    public MsgPlaceholderNode(int id, SoyNode.MsgPlaceholderInitialNode initialNode) {
        super(id);
        this.basePhName = initialNode.genBasePhName();
        this.initialNodeKind = initialNode.getKind();
        this.samenessKey = initialNode.genSamenessKey();
        this.addChild(initialNode);
    }

    protected MsgPlaceholderNode(MsgPlaceholderNode orig) {
        super(orig);
        this.basePhName = orig.basePhName;
        this.initialNodeKind = orig.initialNodeKind;
        this.samenessKey = orig.samenessKey;
    }

    @Override
    public SoyNode.Kind getKind() {
        return SoyNode.Kind.MSG_PLACEHOLDER_NODE;
    }

    @Override
    public String getBaseVarName() {
        return this.basePhName;
    }

    @Override
    public boolean shouldUseSameVarNameAs(SoyNode.MsgSubstUnitNode other) {
        return other instanceof MsgPlaceholderNode && this.initialNodeKind == ((MsgPlaceholderNode)other).initialNodeKind && this.samenessKey.equals(((MsgPlaceholderNode)other).samenessKey);
    }

    @Override
    public String toSourceString() {
        return ((SoyNode.StandaloneNode)this.getChild(0)).toSourceString();
    }

    @Override
    public SoyNode.MsgBlockNode getParent() {
        return (SoyNode.MsgBlockNode)super.getParent();
    }

    @Override
    public MsgPlaceholderNode clone() {
        return new MsgPlaceholderNode(this);
    }
}

