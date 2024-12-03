/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.soytree;

import com.google.template.soy.base.SoySyntaxException;
import com.google.template.soy.base.internal.IdGenerator;
import com.google.template.soy.soytree.AbstractParentSoyNode;
import com.google.template.soy.soytree.SoyFileNode;
import com.google.template.soy.soytree.SoyNode;

public class SoyFileSetNode
extends AbstractParentSoyNode<SoyFileNode>
implements SoyNode.SplitLevelTopNode<SoyFileNode> {
    private final IdGenerator nodeIdGen;

    public SoyFileSetNode(int id, IdGenerator nodeIdGen) throws SoySyntaxException {
        super(id);
        this.nodeIdGen = nodeIdGen;
    }

    protected SoyFileSetNode(SoyFileSetNode orig) {
        super(orig);
        this.nodeIdGen = orig.nodeIdGen.clone();
    }

    @Override
    public SoyNode.Kind getKind() {
        return SoyNode.Kind.SOY_FILE_SET_NODE;
    }

    public IdGenerator getNodeIdGenerator() {
        return this.nodeIdGen;
    }

    @Override
    public String toSourceString() {
        throw new UnsupportedOperationException();
    }

    @Override
    public SoyFileSetNode clone() {
        return new SoyFileSetNode(this);
    }
}

