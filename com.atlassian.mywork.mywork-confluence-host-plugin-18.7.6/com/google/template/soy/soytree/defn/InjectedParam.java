/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.soytree.defn;

import com.google.template.soy.exprtree.VarDefn;
import com.google.template.soy.soytree.defn.AbstractVarDefn;
import com.google.template.soy.types.primitive.UnknownType;

public final class InjectedParam
extends AbstractVarDefn {
    public InjectedParam(String name) {
        super(name, UnknownType.getInstance());
    }

    @Override
    public VarDefn.Kind kind() {
        return VarDefn.Kind.IJ_PARAM;
    }
}

