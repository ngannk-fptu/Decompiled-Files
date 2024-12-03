/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Objects
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nullable
 */
package com.google.template.soy.exprtree;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.template.soy.exprtree.AbstractExprNode;
import com.google.template.soy.exprtree.ExprNode;
import com.google.template.soy.exprtree.VarDefn;
import com.google.template.soy.types.SoyType;
import javax.annotation.Nullable;

public final class VarRefNode
extends AbstractExprNode {
    private final String name;
    private final boolean isInjected;
    private final boolean isNullSafeInjected;
    private VarDefn defn;
    private SoyType subtituteType;
    private Boolean isLocalVar;

    public VarRefNode(String name, boolean injected, boolean nullSafeInjected, @Nullable VarDefn defn) {
        Preconditions.checkArgument((name != null ? 1 : 0) != 0);
        this.name = name;
        this.isInjected = injected;
        this.isNullSafeInjected = nullSafeInjected;
        this.defn = defn;
        this.isLocalVar = null;
    }

    @Override
    public ExprNode.Kind getKind() {
        return ExprNode.Kind.VAR_REF_NODE;
    }

    @Override
    public SoyType getType() {
        Preconditions.checkState((this.defn != null ? 1 : 0) != 0);
        return this.subtituteType != null ? this.subtituteType : this.defn.type();
    }

    public String getName() {
        return this.name;
    }

    public boolean isInjected() {
        return this.isInjected;
    }

    public boolean isNullSafeInjected() {
        return this.isNullSafeInjected;
    }

    public void setDefn(VarDefn defn) {
        this.defn = defn;
    }

    public VarDefn getDefnDecl() {
        return this.defn;
    }

    public Boolean isLocalVar() {
        return this.isLocalVar;
    }

    public Boolean isPossibleParam() {
        return this.defn == null || this.defn.kind() == VarDefn.Kind.PARAM || this.defn.kind() == VarDefn.Kind.UNDECLARED;
    }

    public void setIsLocalVar(Boolean isLocalVar) {
        this.isLocalVar = isLocalVar;
    }

    public void setSubstituteType(SoyType type) {
        this.subtituteType = type;
    }

    @Override
    public String toSourceString() {
        return "$" + (this.isInjected ? (this.isNullSafeInjected ? "ij?." : "ij.") : "") + this.name;
    }

    @Override
    public ExprNode clone() {
        return new VarRefNode(this.name, this.isInjected, this.isNullSafeInjected, this.defn);
    }

    public boolean equals(Object other) {
        if (other == null || other.getClass() != this.getClass()) {
            return false;
        }
        VarRefNode otherVar = (VarRefNode)other;
        return this.name.equals(otherVar.name) && this.isInjected == otherVar.isInjected && this.isNullSafeInjected == otherVar.isNullSafeInjected;
    }

    public int hashCode() {
        return Objects.hashCode((Object[])new Object[]{this.getClass(), this.name, this.isInjected, this.isNullSafeInjected});
    }
}

