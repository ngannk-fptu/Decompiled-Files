/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.exprtree;

import com.google.template.soy.exprtree.AbstractParentExprNode;
import com.google.template.soy.exprtree.ExprNode;

public class FunctionNode
extends AbstractParentExprNode {
    private final String functionName;

    public FunctionNode(String functionName) {
        this.functionName = functionName;
    }

    protected FunctionNode(FunctionNode orig) {
        super(orig);
        this.functionName = orig.functionName;
    }

    @Override
    public ExprNode.Kind getKind() {
        return ExprNode.Kind.FUNCTION_NODE;
    }

    public String getFunctionName() {
        return this.functionName;
    }

    @Override
    public String toSourceString() {
        StringBuilder sourceSb = new StringBuilder();
        sourceSb.append(this.functionName).append('(');
        boolean isFirst = true;
        for (ExprNode child : this.getChildren()) {
            if (isFirst) {
                isFirst = false;
            } else {
                sourceSb.append(", ");
            }
            sourceSb.append(child.toSourceString());
        }
        sourceSb.append(')');
        return sourceSb.toString();
    }

    @Override
    public FunctionNode clone() {
        return new FunctionNode(this);
    }
}

