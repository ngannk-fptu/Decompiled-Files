/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.exprtree;

import com.google.template.soy.basetree.AbstractReturningNodeVisitor;
import com.google.template.soy.exprtree.BooleanNode;
import com.google.template.soy.exprtree.DataAccessNode;
import com.google.template.soy.exprtree.ExprNode;
import com.google.template.soy.exprtree.ExprRootNode;
import com.google.template.soy.exprtree.FieldAccessNode;
import com.google.template.soy.exprtree.FloatNode;
import com.google.template.soy.exprtree.FunctionNode;
import com.google.template.soy.exprtree.GlobalNode;
import com.google.template.soy.exprtree.IntegerNode;
import com.google.template.soy.exprtree.ItemAccessNode;
import com.google.template.soy.exprtree.ListLiteralNode;
import com.google.template.soy.exprtree.MapLiteralNode;
import com.google.template.soy.exprtree.NullNode;
import com.google.template.soy.exprtree.OperatorNodes;
import com.google.template.soy.exprtree.StringNode;
import com.google.template.soy.exprtree.VarRefNode;
import java.util.List;

public abstract class AbstractReturningExprNodeVisitor<R>
extends AbstractReturningNodeVisitor<ExprNode, R> {
    @Override
    protected R visit(ExprNode node) {
        switch (node.getKind()) {
            case EXPR_ROOT_NODE: {
                return this.visitExprRootNode((ExprRootNode)node);
            }
            case NULL_NODE: {
                return this.visitNullNode((NullNode)node);
            }
            case BOOLEAN_NODE: {
                return this.visitBooleanNode((BooleanNode)node);
            }
            case INTEGER_NODE: {
                return this.visitIntegerNode((IntegerNode)node);
            }
            case FLOAT_NODE: {
                return this.visitFloatNode((FloatNode)node);
            }
            case STRING_NODE: {
                return this.visitStringNode((StringNode)node);
            }
            case LIST_LITERAL_NODE: {
                return this.visitListLiteralNode((ListLiteralNode)node);
            }
            case MAP_LITERAL_NODE: {
                return this.visitMapLiteralNode((MapLiteralNode)node);
            }
            case VAR_REF_NODE: {
                return this.visitVarRefNode((VarRefNode)node);
            }
            case FIELD_ACCESS_NODE: {
                return this.visitFieldAccessNode((FieldAccessNode)node);
            }
            case ITEM_ACCESS_NODE: {
                return this.visitItemAccessNode((ItemAccessNode)node);
            }
            case GLOBAL_NODE: {
                return this.visitGlobalNode((GlobalNode)node);
            }
            case NEGATIVE_OP_NODE: {
                return this.visitNegativeOpNode((OperatorNodes.NegativeOpNode)node);
            }
            case NOT_OP_NODE: {
                return this.visitNotOpNode((OperatorNodes.NotOpNode)node);
            }
            case TIMES_OP_NODE: {
                return this.visitTimesOpNode((OperatorNodes.TimesOpNode)node);
            }
            case DIVIDE_BY_OP_NODE: {
                return this.visitDivideByOpNode((OperatorNodes.DivideByOpNode)node);
            }
            case MOD_OP_NODE: {
                return this.visitModOpNode((OperatorNodes.ModOpNode)node);
            }
            case PLUS_OP_NODE: {
                return this.visitPlusOpNode((OperatorNodes.PlusOpNode)node);
            }
            case MINUS_OP_NODE: {
                return this.visitMinusOpNode((OperatorNodes.MinusOpNode)node);
            }
            case LESS_THAN_OP_NODE: {
                return this.visitLessThanOpNode((OperatorNodes.LessThanOpNode)node);
            }
            case GREATER_THAN_OP_NODE: {
                return this.visitGreaterThanOpNode((OperatorNodes.GreaterThanOpNode)node);
            }
            case LESS_THAN_OR_EQUAL_OP_NODE: {
                return this.visitLessThanOrEqualOpNode((OperatorNodes.LessThanOrEqualOpNode)node);
            }
            case GREATER_THAN_OR_EQUAL_OP_NODE: {
                return this.visitGreaterThanOrEqualOpNode((OperatorNodes.GreaterThanOrEqualOpNode)node);
            }
            case EQUAL_OP_NODE: {
                return this.visitEqualOpNode((OperatorNodes.EqualOpNode)node);
            }
            case NOT_EQUAL_OP_NODE: {
                return this.visitNotEqualOpNode((OperatorNodes.NotEqualOpNode)node);
            }
            case AND_OP_NODE: {
                return this.visitAndOpNode((OperatorNodes.AndOpNode)node);
            }
            case OR_OP_NODE: {
                return this.visitOrOpNode((OperatorNodes.OrOpNode)node);
            }
            case NULL_COALESCING_OP_NODE: {
                return this.visitNullCoalescingOpNode((OperatorNodes.NullCoalescingOpNode)node);
            }
            case CONDITIONAL_OP_NODE: {
                return this.visitConditionalOpNode((OperatorNodes.ConditionalOpNode)node);
            }
            case FUNCTION_NODE: {
                return this.visitFunctionNode((FunctionNode)node);
            }
        }
        throw new UnsupportedOperationException();
    }

    protected List<R> visitChildren(ExprNode.ParentExprNode node) {
        return this.visitChildren(node);
    }

    protected List<R> visitChildrenAllowingConcurrentModification(ExprNode.ParentExprNode node) {
        return this.visitChildrenAllowingConcurrentModification(node);
    }

    protected R visitExprRootNode(ExprRootNode<?> node) {
        return this.visitExprNode(node);
    }

    protected R visitNullNode(NullNode node) {
        return this.visitPrimitiveNode(node);
    }

    protected R visitBooleanNode(BooleanNode node) {
        return this.visitPrimitiveNode(node);
    }

    protected R visitIntegerNode(IntegerNode node) {
        return this.visitPrimitiveNode(node);
    }

    protected R visitFloatNode(FloatNode node) {
        return this.visitPrimitiveNode(node);
    }

    protected R visitStringNode(StringNode node) {
        return this.visitPrimitiveNode(node);
    }

    protected R visitPrimitiveNode(ExprNode.PrimitiveNode node) {
        return this.visitExprNode(node);
    }

    protected R visitListLiteralNode(ListLiteralNode node) {
        return this.visitExprNode(node);
    }

    protected R visitMapLiteralNode(MapLiteralNode node) {
        return this.visitExprNode(node);
    }

    protected R visitVarRefNode(VarRefNode node) {
        return this.visitExprNode(node);
    }

    protected R visitDataAccessNode(DataAccessNode node) {
        return this.visitExprNode(node);
    }

    protected R visitFieldAccessNode(FieldAccessNode node) {
        return this.visitDataAccessNode(node);
    }

    protected R visitItemAccessNode(ItemAccessNode node) {
        return this.visitDataAccessNode(node);
    }

    protected R visitGlobalNode(GlobalNode node) {
        return this.visitExprNode(node);
    }

    protected R visitNegativeOpNode(OperatorNodes.NegativeOpNode node) {
        return this.visitOperatorNode(node);
    }

    protected R visitNotOpNode(OperatorNodes.NotOpNode node) {
        return this.visitOperatorNode(node);
    }

    protected R visitTimesOpNode(OperatorNodes.TimesOpNode node) {
        return this.visitOperatorNode(node);
    }

    protected R visitDivideByOpNode(OperatorNodes.DivideByOpNode node) {
        return this.visitOperatorNode(node);
    }

    protected R visitModOpNode(OperatorNodes.ModOpNode node) {
        return this.visitOperatorNode(node);
    }

    protected R visitPlusOpNode(OperatorNodes.PlusOpNode node) {
        return this.visitOperatorNode(node);
    }

    protected R visitMinusOpNode(OperatorNodes.MinusOpNode node) {
        return this.visitOperatorNode(node);
    }

    protected R visitLessThanOpNode(OperatorNodes.LessThanOpNode node) {
        return this.visitOperatorNode(node);
    }

    protected R visitGreaterThanOpNode(OperatorNodes.GreaterThanOpNode node) {
        return this.visitOperatorNode(node);
    }

    protected R visitLessThanOrEqualOpNode(OperatorNodes.LessThanOrEqualOpNode node) {
        return this.visitOperatorNode(node);
    }

    protected R visitGreaterThanOrEqualOpNode(OperatorNodes.GreaterThanOrEqualOpNode node) {
        return this.visitOperatorNode(node);
    }

    protected R visitEqualOpNode(OperatorNodes.EqualOpNode node) {
        return this.visitOperatorNode(node);
    }

    protected R visitNotEqualOpNode(OperatorNodes.NotEqualOpNode node) {
        return this.visitOperatorNode(node);
    }

    protected R visitAndOpNode(OperatorNodes.AndOpNode node) {
        return this.visitOperatorNode(node);
    }

    protected R visitOrOpNode(OperatorNodes.OrOpNode node) {
        return this.visitOperatorNode(node);
    }

    protected R visitConditionalOpNode(OperatorNodes.ConditionalOpNode node) {
        return this.visitOperatorNode(node);
    }

    protected R visitNullCoalescingOpNode(OperatorNodes.NullCoalescingOpNode node) {
        return this.visitOperatorNode(node);
    }

    protected R visitOperatorNode(ExprNode.OperatorNode node) {
        return this.visitExprNode(node);
    }

    protected R visitFunctionNode(FunctionNode node) {
        return this.visitExprNode(node);
    }

    protected R visitExprNode(ExprNode node) {
        throw new UnsupportedOperationException();
    }
}

