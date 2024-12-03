/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.exprtree;

import com.google.template.soy.basetree.Node;
import com.google.template.soy.basetree.ParentNode;
import com.google.template.soy.exprtree.Operator;
import com.google.template.soy.types.SoyType;

public interface ExprNode
extends Node {
    public Kind getKind();

    public SoyType getType();

    public ParentExprNode getParent();

    @Override
    public ExprNode clone();

    public static interface PrimitiveNode
    extends ConstantNode {
    }

    public static interface ConstantNode
    extends ExprNode {
    }

    public static interface OperatorNode
    extends ParentExprNode {
        public Operator getOperator();
    }

    public static interface ParentExprNode
    extends ExprNode,
    ParentNode<ExprNode> {
    }

    public static enum Kind {
        EXPR_ROOT_NODE,
        NULL_NODE,
        BOOLEAN_NODE,
        INTEGER_NODE,
        FLOAT_NODE,
        STRING_NODE,
        LIST_LITERAL_NODE,
        MAP_LITERAL_NODE,
        VAR_NODE,
        VAR_REF_NODE,
        FIELD_ACCESS_NODE,
        ITEM_ACCESS_NODE,
        GLOBAL_NODE,
        NEGATIVE_OP_NODE,
        NOT_OP_NODE,
        TIMES_OP_NODE,
        DIVIDE_BY_OP_NODE,
        MOD_OP_NODE,
        PLUS_OP_NODE,
        MINUS_OP_NODE,
        LESS_THAN_OP_NODE,
        GREATER_THAN_OP_NODE,
        LESS_THAN_OR_EQUAL_OP_NODE,
        GREATER_THAN_OR_EQUAL_OP_NODE,
        EQUAL_OP_NODE,
        NOT_EQUAL_OP_NODE,
        AND_OP_NODE,
        OR_OP_NODE,
        NULL_COALESCING_OP_NODE,
        CONDITIONAL_OP_NODE,
        FUNCTION_NODE;

    }
}

