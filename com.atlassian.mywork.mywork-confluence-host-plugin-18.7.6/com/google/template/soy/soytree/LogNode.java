/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.soytree;

import com.google.template.soy.soytree.AbstractBlockCommandNode;
import com.google.template.soy.soytree.SoyNode;

public class LogNode
extends AbstractBlockCommandNode
implements SoyNode.StandaloneNode,
SoyNode.StatementNode {
    public LogNode(int id) {
        super(id, "log", "");
    }

    protected LogNode(LogNode orig) {
        super(orig);
    }

    @Override
    public SoyNode.Kind getKind() {
        return SoyNode.Kind.LOG_NODE;
    }

    @Override
    public SoyNode.BlockNode getParent() {
        return (SoyNode.BlockNode)super.getParent();
    }

    @Override
    public LogNode clone() {
        return new LogNode(this);
    }
}

