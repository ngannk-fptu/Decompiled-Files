/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  javax.annotation.concurrent.Immutable
 */
package com.google.template.soy.soytree.defn;

import com.google.template.soy.soytree.defn.TemplateParam;
import com.google.template.soy.types.SoyType;
import com.google.template.soy.types.primitive.UnknownType;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@Immutable
public final class SoyDocParam
extends TemplateParam {
    public SoyDocParam(String name, boolean isRequired, @Nullable String desc) {
        super(name, UnknownType.getInstance(), isRequired, desc);
    }

    @Override
    public TemplateParam.DeclLoc declLoc() {
        return TemplateParam.DeclLoc.SOY_DOC;
    }

    @Override
    public SoyType type() {
        return UnknownType.getInstance();
    }

    @Override
    public SoyDocParam cloneEssential() {
        return new SoyDocParam(this.name(), this.isRequired(), null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        return super.abstractEquals(o);
    }

    @Override
    public int hashCode() {
        return super.abstractHashCode();
    }
}

