/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.soytree;

import com.google.template.soy.soytree.CaseOrDefaultNode;
import com.google.template.soy.soytree.SoyNode;

public class MsgSelectDefaultNode
extends CaseOrDefaultNode
implements SoyNode.MsgBlockNode {
    public MsgSelectDefaultNode(int id) {
        super(id, "default", "");
    }

    protected MsgSelectDefaultNode(MsgSelectDefaultNode orig) {
        super(orig);
    }

    @Override
    public SoyNode.Kind getKind() {
        return SoyNode.Kind.MSG_SELECT_DEFAULT_NODE;
    }

    @Override
    public MsgSelectDefaultNode clone() {
        return new MsgSelectDefaultNode(this);
    }
}

