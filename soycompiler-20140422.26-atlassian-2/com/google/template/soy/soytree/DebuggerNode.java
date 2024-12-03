/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.soytree;

import com.google.template.soy.soytree.AbstractCommandNode;
import com.google.template.soy.soytree.SoyNode;

public class DebuggerNode
extends AbstractCommandNode
implements SoyNode.StandaloneNode,
SoyNode.StatementNode {
    public DebuggerNode(int id) {
        super(id, "debugger", "");
    }

    protected DebuggerNode(DebuggerNode orig) {
        super(orig);
    }

    @Override
    public SoyNode.Kind getKind() {
        return SoyNode.Kind.DEBUGGER_NODE;
    }

    @Override
    public SoyNode.BlockNode getParent() {
        return (SoyNode.BlockNode)super.getParent();
    }

    @Override
    public DebuggerNode clone() {
        return new DebuggerNode(this);
    }
}

