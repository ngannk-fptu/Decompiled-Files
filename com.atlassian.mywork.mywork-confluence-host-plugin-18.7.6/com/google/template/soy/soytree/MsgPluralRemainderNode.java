/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.soytree;

import com.google.template.soy.soytree.AbstractSoyNode;
import com.google.template.soy.soytree.PrintNode;
import com.google.template.soy.soytree.SoyNode;

public class MsgPluralRemainderNode
extends AbstractSoyNode
implements SoyNode.MsgSubstUnitNode {
    private final String sourceString;

    public MsgPluralRemainderNode(PrintNode origPrintNode) {
        super(origPrintNode.getId());
        this.sourceString = origPrintNode.toSourceString();
    }

    protected MsgPluralRemainderNode(MsgPluralRemainderNode orig) {
        super(orig);
        this.sourceString = orig.sourceString;
    }

    @Override
    public SoyNode.Kind getKind() {
        return SoyNode.Kind.MSG_PLURAL_REMAINDER_NODE;
    }

    @Override
    public String getBaseVarName() {
        throw new AssertionError();
    }

    @Override
    public boolean shouldUseSameVarNameAs(SoyNode.MsgSubstUnitNode other) {
        throw new AssertionError();
    }

    @Override
    public String toSourceString() {
        return this.sourceString;
    }

    @Override
    public SoyNode.MsgBlockNode getParent() {
        return (SoyNode.MsgBlockNode)super.getParent();
    }

    @Override
    public MsgPluralRemainderNode clone() {
        return new MsgPluralRemainderNode(this);
    }
}

