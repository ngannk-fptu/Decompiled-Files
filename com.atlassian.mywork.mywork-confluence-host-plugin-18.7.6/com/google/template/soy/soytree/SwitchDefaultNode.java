/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.soytree;

import com.google.template.soy.soytree.CaseOrDefaultNode;
import com.google.template.soy.soytree.SoyNode;

public class SwitchDefaultNode
extends CaseOrDefaultNode
implements SoyNode.ConditionalBlockNode {
    public SwitchDefaultNode(int id) {
        super(id, "default", "");
    }

    protected SwitchDefaultNode(SwitchDefaultNode orig) {
        super(orig);
    }

    @Override
    public SoyNode.Kind getKind() {
        return SoyNode.Kind.SWITCH_DEFAULT_NODE;
    }

    @Override
    public SwitchDefaultNode clone() {
        return new SwitchDefaultNode(this);
    }
}

