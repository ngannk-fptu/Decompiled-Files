/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.soytree.defn;

import com.google.template.soy.exprtree.VarDefn;
import com.google.template.soy.soytree.SoyNode;
import com.google.template.soy.soytree.defn.AbstractVarDefn;
import com.google.template.soy.types.SoyType;

public final class LocalVar
extends AbstractVarDefn {
    private final SoyNode declaringNode;

    public LocalVar(String name, SoyNode declaringNode, SoyType type) {
        super(name, type);
        this.declaringNode = declaringNode;
    }

    @Override
    public VarDefn.Kind kind() {
        return VarDefn.Kind.LOCAL_VAR;
    }

    public SoyNode declaringNode() {
        return this.declaringNode;
    }

    public void setType(SoyType type) {
        this.type = type;
    }
}

