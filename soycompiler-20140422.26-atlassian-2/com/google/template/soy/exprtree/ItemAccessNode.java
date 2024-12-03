/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Objects
 */
package com.google.template.soy.exprtree;

import com.google.common.base.Objects;
import com.google.template.soy.basetree.SyntaxVersion;
import com.google.template.soy.basetree.SyntaxVersionBound;
import com.google.template.soy.exprtree.DataAccessNode;
import com.google.template.soy.exprtree.ExprNode;

public final class ItemAccessNode
extends DataAccessNode {
    private final boolean isDotSyntax;

    public ItemAccessNode(ExprNode base, ExprNode key, boolean isNullSafe, boolean isDotSyntax) {
        super(base, isNullSafe);
        this.isDotSyntax = isDotSyntax;
        if (isDotSyntax) {
            this.maybeSetSyntaxVersionBound(new SyntaxVersionBound(SyntaxVersion.V2_2, "Dot access for list items is no longer allowed; use bracket access instead (i.e. $x[0] instead of $x.0)."));
        }
        this.addChild(key);
    }

    @Override
    public ExprNode.Kind getKind() {
        return ExprNode.Kind.ITEM_ACCESS_NODE;
    }

    public ExprNode getKeyExprChild() {
        return this.getChild(1);
    }

    @Override
    public String getSourceStringSuffix() {
        if (this.isDotSyntax) {
            return (this.isNullSafe ? "?." : ".") + this.getChild(1).toSourceString();
        }
        return (this.isNullSafe ? "?[" : "[") + this.getChild(1).toSourceString() + "]";
    }

    @Override
    public ExprNode clone() {
        return new ItemAccessNode(this.getChild(0).clone(), this.getChild(1).clone(), this.isNullSafe, this.isDotSyntax);
    }

    public boolean equals(Object other) {
        if (other == null || other.getClass() != this.getClass()) {
            return false;
        }
        ItemAccessNode otherItemRef = (ItemAccessNode)other;
        return this.getChild(0).equals(otherItemRef.getChild(0)) && this.getChild(1).equals(otherItemRef.getChild(1)) && this.isNullSafe == otherItemRef.isNullSafe && this.isDotSyntax == otherItemRef.isDotSyntax;
    }

    public int hashCode() {
        return Objects.hashCode((Object[])new Object[]{this.getClass(), this.getChild(0), this.getChild(1)});
    }
}

