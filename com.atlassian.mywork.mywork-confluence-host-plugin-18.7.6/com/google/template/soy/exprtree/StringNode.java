/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.exprtree;

import com.google.template.soy.base.internal.BaseUtils;
import com.google.template.soy.exprtree.AbstractPrimitiveNode;
import com.google.template.soy.exprtree.ExprNode;
import com.google.template.soy.types.SoyType;
import com.google.template.soy.types.primitive.StringType;

public class StringNode
extends AbstractPrimitiveNode {
    private final String value;

    public StringNode(String value) {
        this.value = value;
    }

    protected StringNode(StringNode orig) {
        super(orig);
        this.value = orig.value;
    }

    @Override
    public ExprNode.Kind getKind() {
        return ExprNode.Kind.STRING_NODE;
    }

    @Override
    public SoyType getType() {
        return StringType.getInstance();
    }

    public String getValue() {
        return this.value;
    }

    @Override
    public String toSourceString() {
        return this.toSourceString(false);
    }

    public String toSourceString(boolean escapeToAscii) {
        return BaseUtils.escapeToSoyString(this.value, escapeToAscii);
    }

    @Override
    public StringNode clone() {
        return new StringNode(this);
    }
}

