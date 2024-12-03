/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.google.template.soy.soytree.defn;

import com.google.common.base.Preconditions;
import com.google.template.soy.exprtree.VarDefn;
import com.google.template.soy.types.SoyType;

public abstract class AbstractVarDefn
implements VarDefn {
    private final String name;
    protected SoyType type;

    public AbstractVarDefn(String name, SoyType type) {
        Preconditions.checkArgument((name != null ? 1 : 0) != 0);
        this.name = name;
        this.type = type;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public SoyType type() {
        return this.type;
    }
}

