/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.exprtree;

import com.google.template.soy.basetree.AbstractNodeVisitor;
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

public abstract class AbstractExprNodeVisitor<R>
extends AbstractNodeVisitor<ExprNode, R> {
    @Override
    protected void visit(ExprNode node) {
        switch (node.getKind()) {
            case EXPR_ROOT_NODE: {
                this.visitExprRootNode((ExprRootNode)node);
                break;
            }
            case NULL_NODE: {
                this.visitNullNode((NullNode)node);
                break;
            }
            case BOOLEAN_NODE: {
                this.visitBooleanNode((BooleanNode)node);
                break;
            }
            case INTEGER_NODE: {
                this.visitIntegerNode((IntegerNode)node);
                break;
            }
            case FLOAT_NODE: {
                this.visitFloatNode((FloatNode)node);
                break;
            }
            case STRING_NODE: {
                this.visitStringNode((StringNode)node);
                break;
            }
            case LIST_LITERAL_NODE: {
                this.visitListLiteralNode((ListLiteralNode)node);
                break;
            }
            case MAP_LITERAL_NODE: {
                this.visitMapLiteralNode((MapLiteralNode)node);
                break;
            }
            case VAR_REF_NODE: {
                this.visitVarRefNode((VarRefNode)node);
                break;
            }
            case FIELD_ACCESS_NODE: {
                this.visitFieldAccessNode((FieldAccessNode)node);
                break;
            }
            case ITEM_ACCESS_NODE: {
                this.visitItemAccessNode((ItemAccessNode)node);
                break;
            }
            case GLOBAL_NODE: {
                this.visitGlobalNode((GlobalNode)node);
                break;
            }
            case NEGATIVE_OP_NODE: {
                this.visitNegativeOpNode((OperatorNodes.NegativeOpNode)node);
                break;
            }
            case NOT_OP_NODE: {
                this.visitNotOpNode((OperatorNodes.NotOpNode)node);
                break;
            }
            case TIMES_OP_NODE: {
                this.visitTimesOpNode((OperatorNodes.TimesOpNode)node);
                break;
            }
            case DIVIDE_BY_OP_NODE: {
                this.visitDivideByOpNode((OperatorNodes.DivideByOpNode)node);
                break;
            }
            case MOD_OP_NODE: {
                this.visitModOpNode((OperatorNodes.ModOpNode)node);
                break;
            }
            case PLUS_OP_NODE: {
                this.visitPlusOpNode((OperatorNodes.PlusOpNode)node);
                break;
            }
            case MINUS_OP_NODE: {
                this.visitMinusOpNode((OperatorNodes.MinusOpNode)node);
                break;
            }
            case LESS_THAN_OP_NODE: {
                this.visitLessThanOpNode((OperatorNodes.LessThanOpNode)node);
                break;
            }
            case GREATER_THAN_OP_NODE: {
                this.visitGreaterThanOpNode((OperatorNodes.GreaterThanOpNode)node);
                break;
            }
            case LESS_THAN_OR_EQUAL_OP_NODE: {
                this.visitLessThanOrEqualOpNode((OperatorNodes.LessThanOrEqualOpNode)node);
                break;
            }
            case GREATER_THAN_OR_EQUAL_OP_NODE: {
                this.visitGreaterThanOrEqualOpNode((OperatorNodes.GreaterThanOrEqualOpNode)node);
                break;
            }
            case EQUAL_OP_NODE: {
                this.visitEqualOpNode((OperatorNodes.EqualOpNode)node);
                break;
            }
            case NOT_EQUAL_OP_NODE: {
                this.visitNotEqualOpNode((OperatorNodes.NotEqualOpNode)node);
                break;
            }
            case AND_OP_NODE: {
                this.visitAndOpNode((OperatorNodes.AndOpNode)node);
                break;
            }
            case OR_OP_NODE: {
                this.visitOrOpNode((OperatorNodes.OrOpNode)node);
                break;
            }
            case NULL_COALESCING_OP_NODE: {
                this.visitNullCoalescingOpNode((OperatorNodes.NullCoalescingOpNode)node);
                break;
            }
            case CONDITIONAL_OP_NODE: {
                this.visitConditionalOpNode((OperatorNodes.ConditionalOpNode)node);
                break;
            }
            case FUNCTION_NODE: {
                this.visitFunctionNode((FunctionNode)node);
                break;
            }
            default: {
                throw new UnsupportedOperationException();
            }
        }
    }

    protected void visitChildren(ExprNode.ParentExprNode node) {
        this.visitChildren(node);
    }

    protected void visitChildrenAllowingConcurrentModification(ExprNode.ParentExprNode node) {
        this.visitChildrenAllowingConcurrentModification(node);
    }

    protected void visitExprRootNode(ExprRootNode<?> node) {
        this.visitExprNode(node);
    }

    protected void visitNullNode(NullNode node) {
        this.visitPrimitiveNode(node);
    }

    protected void visitBooleanNode(BooleanNode node) {
        this.visitPrimitiveNode(node);
    }

    protected void visitIntegerNode(IntegerNode node) {
        this.visitPrimitiveNode(node);
    }

    protected void visitFloatNode(FloatNode node) {
        this.visitPrimitiveNode(node);
    }

    protected void visitStringNode(StringNode node) {
        this.visitPrimitiveNode(node);
    }

    protected void visitPrimitiveNode(ExprNode.PrimitiveNode node) {
        this.visitExprNode(node);
    }

    protected void visitListLiteralNode(ListLiteralNode node) {
        this.visitExprNode(node);
    }

    protected void visitMapLiteralNode(MapLiteralNode node) {
        this.visitExprNode(node);
    }

    protected void visitVarRefNode(VarRefNode node) {
        this.visitExprNode(node);
    }

    protected void visitDataAccessNode(DataAccessNode node) {
        this.visitExprNode(node);
    }

    protected void visitFieldAccessNode(FieldAccessNode node) {
        this.visitDataAccessNode(node);
    }

    protected void visitItemAccessNode(ItemAccessNode node) {
        this.visitDataAccessNode(node);
    }

    protected void visitGlobalNode(GlobalNode node) {
        this.visitExprNode(node);
    }

    protected void visitNegativeOpNode(OperatorNodes.NegativeOpNode node) {
        this.visitOperatorNode(node);
    }

    protected void visitNotOpNode(OperatorNodes.NotOpNode node) {
        this.visitOperatorNode(node);
    }

    protected void visitTimesOpNode(OperatorNodes.TimesOpNode node) {
        this.visitOperatorNode(node);
    }

    protected void visitDivideByOpNode(OperatorNodes.DivideByOpNode node) {
        this.visitOperatorNode(node);
    }

    protected void visitModOpNode(OperatorNodes.ModOpNode node) {
        this.visitOperatorNode(node);
    }

    protected void visitPlusOpNode(OperatorNodes.PlusOpNode node) {
        this.visitOperatorNode(node);
    }

    protected void visitMinusOpNode(OperatorNodes.MinusOpNode node) {
        this.visitOperatorNode(node);
    }

    protected void visitLessThanOpNode(OperatorNodes.LessThanOpNode node) {
        this.visitOperatorNode(node);
    }

    protected void visitGreaterThanOpNode(OperatorNodes.GreaterThanOpNode node) {
        this.visitOperatorNode(node);
    }

    protected void visitLessThanOrEqualOpNode(OperatorNodes.LessThanOrEqualOpNode node) {
        this.visitOperatorNode(node);
    }

    protected void visitGreaterThanOrEqualOpNode(OperatorNodes.GreaterThanOrEqualOpNode node) {
        this.visitOperatorNode(node);
    }

    protected void visitEqualOpNode(OperatorNodes.EqualOpNode node) {
        this.visitOperatorNode(node);
    }

    protected void visitNotEqualOpNode(OperatorNodes.NotEqualOpNode node) {
        this.visitOperatorNode(node);
    }

    protected void visitAndOpNode(OperatorNodes.AndOpNode node) {
        this.visitOperatorNode(node);
    }

    protected void visitOrOpNode(OperatorNodes.OrOpNode node) {
        this.visitOperatorNode(node);
    }

    protected void visitNullCoalescingOpNode(OperatorNodes.NullCoalescingOpNode node) {
        this.visitOperatorNode(node);
    }

    protected void visitConditionalOpNode(OperatorNodes.ConditionalOpNode node) {
        this.visitOperatorNode(node);
    }

    protected void visitOperatorNode(ExprNode.OperatorNode node) {
        this.visitExprNode(node);
    }

    protected void visitFunctionNode(FunctionNode node) {
        this.visitExprNode(node);
    }

    protected void visitExprNode(ExprNode node) {
        throw new UnsupportedOperationException();
    }
}

