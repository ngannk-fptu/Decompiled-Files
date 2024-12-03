/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Objects
 *  com.google.common.base.Preconditions
 */
package com.google.template.soy.exprtree;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.template.soy.exprtree.DataAccessNode;
import com.google.template.soy.exprtree.ExprNode;

public final class FieldAccessNode
extends DataAccessNode {
    private final String fieldName;

    public FieldAccessNode(ExprNode base, String fieldName, boolean isNullSafe) {
        super(base, isNullSafe);
        Preconditions.checkArgument((fieldName != null ? 1 : 0) != 0);
        this.fieldName = fieldName;
    }

    @Override
    public ExprNode.Kind getKind() {
        return ExprNode.Kind.FIELD_ACCESS_NODE;
    }

    public String getFieldName() {
        return this.fieldName;
    }

    @Override
    public String getSourceStringSuffix() {
        return (this.isNullSafe ? "?." : ".") + this.fieldName;
    }

    @Override
    public ExprNode clone() {
        return new FieldAccessNode(this.getChild(0).clone(), this.fieldName, this.isNullSafe);
    }

    public boolean equals(Object other) {
        if (other == null || other.getClass() != this.getClass()) {
            return false;
        }
        FieldAccessNode otherFieldRef = (FieldAccessNode)other;
        return this.getChild(0).equals(otherFieldRef.getChild(0)) && this.fieldName.equals(otherFieldRef.fieldName) && this.isNullSafe == otherFieldRef.isNullSafe;
    }

    public int hashCode() {
        return Objects.hashCode((Object[])new Object[]{this.getClass(), this.getChild(0), this.fieldName, this.isNullSafe});
    }
}

