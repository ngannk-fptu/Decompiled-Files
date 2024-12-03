/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.soytree.jssrc;

import com.google.template.soy.soytree.AbstractSoyNode;
import com.google.template.soy.soytree.SoyNode;

public class GoogMsgRefNode
extends AbstractSoyNode
implements SoyNode.StandaloneNode {
    private final String renderedGoogMsgVarName;

    public GoogMsgRefNode(int id, String renderedGoogMsgVarName) {
        super(id);
        this.renderedGoogMsgVarName = renderedGoogMsgVarName;
    }

    protected GoogMsgRefNode(GoogMsgRefNode orig) {
        super(orig);
        this.renderedGoogMsgVarName = orig.renderedGoogMsgVarName;
    }

    @Override
    public SoyNode.Kind getKind() {
        return SoyNode.Kind.GOOG_MSG_REF_NODE;
    }

    public String getRenderedGoogMsgVarName() {
        return this.renderedGoogMsgVarName;
    }

    @Override
    public String toSourceString() {
        return "[GoogMsgRefNode " + this.renderedGoogMsgVarName + "]";
    }

    @Override
    public SoyNode.BlockNode getParent() {
        return (SoyNode.BlockNode)super.getParent();
    }

    @Override
    public GoogMsgRefNode clone() {
        return new GoogMsgRefNode(this);
    }
}

