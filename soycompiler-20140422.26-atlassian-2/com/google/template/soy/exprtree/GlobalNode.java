/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.exprtree;

import com.google.template.soy.exprtree.AbstractExprNode;
import com.google.template.soy.exprtree.ExprNode;
import com.google.template.soy.types.SoyType;
import com.google.template.soy.types.primitive.UnknownType;

public class GlobalNode
extends AbstractExprNode {
    private final String name;

    public GlobalNode(String name) {
        this.name = name;
    }

    protected GlobalNode(GlobalNode orig) {
        super(orig);
        this.name = orig.name;
    }

    @Override
    public ExprNode.Kind getKind() {
        return ExprNode.Kind.GLOBAL_NODE;
    }

    @Override
    public SoyType getType() {
        return UnknownType.getInstance();
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String toSourceString() {
        return this.name;
    }

    @Override
    public GlobalNode clone() {
        return new GlobalNode(this);
    }
}

