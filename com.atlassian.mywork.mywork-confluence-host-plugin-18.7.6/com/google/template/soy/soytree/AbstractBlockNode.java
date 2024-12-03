/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.soytree;

import com.google.template.soy.soytree.AbstractParentSoyNode;
import com.google.template.soy.soytree.SoyNode;

public abstract class AbstractBlockNode
extends AbstractParentSoyNode<SoyNode.StandaloneNode>
implements SoyNode.BlockNode {
    public AbstractBlockNode(int id) {
        super(id);
    }

    protected AbstractBlockNode(AbstractBlockNode orig) {
        super(orig);
    }
}

