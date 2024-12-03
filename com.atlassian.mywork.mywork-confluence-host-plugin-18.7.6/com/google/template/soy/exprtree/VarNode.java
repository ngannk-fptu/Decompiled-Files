/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.exprtree;

import com.google.template.soy.exprtree.AbstractExprNode;
import com.google.template.soy.exprtree.ExprNode;
import com.google.template.soy.types.SoyType;

public class VarNode
extends AbstractExprNode {
    private final String name;

    public VarNode(String name) {
        this.name = name;
    }

    protected VarNode(VarNode orig) {
        super(orig);
        this.name = orig.name;
    }

    @Override
    public ExprNode.Kind getKind() {
        return ExprNode.Kind.VAR_NODE;
    }

    @Override
    public SoyType getType() {
        throw new UnsupportedOperationException();
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String toSourceString() {
        return "$" + this.name;
    }

    @Override
    public VarNode clone() {
        return new VarNode(this);
    }
}

