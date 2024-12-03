/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.soytree;

import com.google.template.soy.soytree.AbstractParentCommandNode;
import com.google.template.soy.soytree.SoyNode;

public abstract class AbstractBlockCommandNode
extends AbstractParentCommandNode<SoyNode.StandaloneNode>
implements SoyNode.BlockCommandNode {
    public AbstractBlockCommandNode(int id, String commandName, String commandText) {
        super(id, commandName, commandText);
    }

    protected AbstractBlockCommandNode(AbstractBlockCommandNode orig) {
        super(orig);
    }
}

