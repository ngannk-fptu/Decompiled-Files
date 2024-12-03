/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.soytree;

import com.google.template.soy.base.SoySyntaxException;
import com.google.template.soy.soytree.AbstractBlockCommandNode;

public abstract class CaseOrDefaultNode
extends AbstractBlockCommandNode {
    public CaseOrDefaultNode(int id, String commandName, String commandText) throws SoySyntaxException {
        super(id, commandName, commandText);
    }

    protected CaseOrDefaultNode(CaseOrDefaultNode orig) {
        super(orig);
    }

    @Override
    public String toSourceString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getTagString());
        this.appendSourceStringForChildren(sb);
        return sb.toString();
    }
}

