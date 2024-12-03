/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.google.template.soy.exprtree;

import com.google.common.base.Preconditions;
import com.google.template.soy.exprtree.AbstractParentExprNode;
import com.google.template.soy.exprtree.ExprNode;

public abstract class DataAccessNode
extends AbstractParentExprNode {
    protected final boolean isNullSafe;

    public DataAccessNode(ExprNode base, boolean isNullSafe) {
        Preconditions.checkArgument((base != null ? 1 : 0) != 0);
        this.addChild(base);
        this.isNullSafe = isNullSafe;
    }

    public ExprNode getBaseExprChild() {
        return this.getChild(0);
    }

    public boolean isNullSafe() {
        return this.isNullSafe;
    }

    public abstract String getSourceStringSuffix();

    @Override
    public String toSourceString() {
        return this.getBaseExprChild().toSourceString() + this.getSourceStringSuffix();
    }
}

