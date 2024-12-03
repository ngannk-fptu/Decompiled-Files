/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nullable
 *  javax.annotation.concurrent.Immutable
 */
package com.google.template.soy.soytree.defn;

import com.google.common.base.Preconditions;
import com.google.template.soy.soytree.defn.TemplateParam;
import com.google.template.soy.types.SoyType;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@Immutable
public final class HeaderParam
extends TemplateParam {
    @Nullable
    private final String typeSrc;

    public HeaderParam(String name, String typeSrc, SoyType type, boolean isRequired, @Nullable String desc) {
        super(name, type, isRequired, desc);
        Preconditions.checkArgument((type != null ? 1 : 0) != 0);
        this.typeSrc = typeSrc;
    }

    @Override
    public TemplateParam.DeclLoc declLoc() {
        return TemplateParam.DeclLoc.HEADER;
    }

    public String typeSrc() {
        return this.typeSrc;
    }

    @Override
    public HeaderParam cloneEssential() {
        return new HeaderParam(this.name(), null, this.type, this.isRequired(), null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        HeaderParam other = (HeaderParam)o;
        return super.abstractEquals(o) && this.type.equals(other.type);
    }

    @Override
    public int hashCode() {
        return super.abstractHashCode() * 31 + this.type.hashCode();
    }
}

