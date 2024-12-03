/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.soytree;

import com.google.template.soy.soytree.CaseOrDefaultNode;
import com.google.template.soy.soytree.SoyNode;

public class MsgPluralDefaultNode
extends CaseOrDefaultNode
implements SoyNode.MsgBlockNode {
    public MsgPluralDefaultNode(int id) {
        super(id, "default", "");
    }

    protected MsgPluralDefaultNode(MsgPluralDefaultNode orig) {
        super(orig);
    }

    @Override
    public SoyNode.Kind getKind() {
        return SoyNode.Kind.MSG_PLURAL_DEFAULT_NODE;
    }

    @Override
    public MsgPluralDefaultNode clone() {
        return new MsgPluralDefaultNode(this);
    }
}

