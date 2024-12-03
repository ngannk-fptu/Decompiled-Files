/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Objects
 *  javax.annotation.Nullable
 *  javax.annotation.concurrent.Immutable
 */
package com.google.template.soy.soytree.defn;

import com.google.common.base.Objects;
import com.google.template.soy.exprtree.VarDefn;
import com.google.template.soy.soytree.defn.AbstractVarDefn;
import com.google.template.soy.types.SoyType;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@Immutable
public abstract class TemplateParam
extends AbstractVarDefn {
    private final boolean isRequired;
    private final String desc;

    public TemplateParam(String name, SoyType type, boolean isRequired, @Nullable String desc) {
        super(name, type);
        this.isRequired = isRequired;
        this.desc = desc;
    }

    @Override
    public VarDefn.Kind kind() {
        return VarDefn.Kind.PARAM;
    }

    public abstract DeclLoc declLoc();

    public boolean isRequired() {
        return this.isRequired;
    }

    public String desc() {
        return this.desc;
    }

    public abstract TemplateParam cloneEssential();

    public abstract boolean equals(Object var1);

    public abstract int hashCode();

    protected boolean abstractEquals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AbstractVarDefn other = (AbstractVarDefn)o;
        return this.name().equals(other.name()) && this.kind() == other.kind();
    }

    protected int abstractHashCode() {
        return Objects.hashCode((Object[])new Object[]{this.getClass(), this.name(), this.isRequired});
    }

    public static enum DeclLoc {
        SOY_DOC,
        HEADER;

    }
}

