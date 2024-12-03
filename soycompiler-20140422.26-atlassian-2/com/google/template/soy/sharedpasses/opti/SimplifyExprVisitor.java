/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Inject
 */
package com.google.template.soy.sharedpasses.opti;

import com.google.template.soy.data.SoyValue;
import com.google.template.soy.data.SoyValueHelper;
import com.google.template.soy.data.internalutils.InternalValueUtils;
import com.google.template.soy.data.restricted.BooleanData;
import com.google.template.soy.data.restricted.FloatData;
import com.google.template.soy.data.restricted.IntegerData;
import com.google.template.soy.data.restricted.NullData;
import com.google.template.soy.data.restricted.PrimitiveData;
import com.google.template.soy.data.restricted.StringData;
import com.google.template.soy.exprtree.AbstractExprNodeVisitor;
import com.google.template.soy.exprtree.BooleanNode;
import com.google.template.soy.exprtree.ExprNode;
import com.google.template.soy.exprtree.ExprRootNode;
import com.google.template.soy.exprtree.FloatNode;
import com.google.template.soy.exprtree.FunctionNode;
import com.google.template.soy.exprtree.IntegerNode;
import com.google.template.soy.exprtree.ListLiteralNode;
import com.google.template.soy.exprtree.MapLiteralNode;
import com.google.template.soy.exprtree.OperatorNodes;
import com.google.template.soy.exprtree.StringNode;
import com.google.template.soy.shared.internal.NonpluginFunction;
import com.google.template.soy.sharedpasses.opti.PreevalVisitor;
import com.google.template.soy.sharedpasses.opti.PreevalVisitorFactory;
import com.google.template.soy.sharedpasses.render.RenderException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import javax.inject.Inject;

class SimplifyExprVisitor
extends AbstractExprNodeVisitor<Void> {
    private static final Deque<Map<String, SoyValue>> EMPTY_ENV = new ArrayDeque<Map<String, SoyValue>>(0);
    private final PreevalVisitor preevalVisitor;

    @Inject
    SimplifyExprVisitor(PreevalVisitorFactory preevalVisitorFactory) {
        this.preevalVisitor = preevalVisitorFactory.create(SoyValueHelper.EMPTY_DICT, EMPTY_ENV);
    }

    @Override
    protected void visitExprRootNode(ExprRootNode<?> node) {
        this.visit((ExprNode)node.getChild(0));
    }

    @Override
    protected void visitListLiteralNode(ListLiteralNode node) {
        this.visitChildren(node);
    }

    @Override
    protected void visitMapLiteralNode(MapLiteralNode node) {
        this.visitChildren(node);
    }

    @Override
    protected void visitAndOpNode(OperatorNodes.AndOpNode node) {
        this.visitChildren(node);
        SoyValue operand0 = SimplifyExprVisitor.getConstantOrNull(node.getChild(0));
        if (operand0 != null) {
            ExprNode replacementNode = operand0.coerceToBoolean() ? node.getChild(1) : node.getChild(0);
            node.getParent().replaceChild(node, replacementNode);
        }
    }

    @Override
    protected void visitOrOpNode(OperatorNodes.OrOpNode node) {
        this.visitChildren(node);
        SoyValue operand0 = SimplifyExprVisitor.getConstantOrNull(node.getChild(0));
        if (operand0 != null) {
            ExprNode replacementNode = operand0.coerceToBoolean() ? node.getChild(0) : node.getChild(1);
            node.getParent().replaceChild(node, replacementNode);
        }
    }

    @Override
    protected void visitConditionalOpNode(OperatorNodes.ConditionalOpNode node) {
        this.visitChildren(node);
        SoyValue operand0 = SimplifyExprVisitor.getConstantOrNull(node.getChild(0));
        if (operand0 == null) {
            return;
        }
        ExprNode replacementNode = operand0.coerceToBoolean() ? node.getChild(1) : node.getChild(2);
        node.getParent().replaceChild(node, replacementNode);
    }

    @Override
    protected void visitFunctionNode(FunctionNode node) {
        if (NonpluginFunction.forFunctionName(node.getFunctionName()) != null) {
            return;
        }
        this.visitExprNode(node);
    }

    @Override
    protected void visitExprNode(ExprNode node) {
        if (!(node instanceof ExprNode.ParentExprNode)) {
            return;
        }
        ExprNode.ParentExprNode nodeAsParent = (ExprNode.ParentExprNode)node;
        this.visitChildren(nodeAsParent);
        for (ExprNode child : nodeAsParent.getChildren()) {
            if (child instanceof ExprNode.ConstantNode) continue;
            return;
        }
        this.attemptPreeval(nodeAsParent);
    }

    private void attemptPreeval(ExprNode node) {
        SoyValue preevalResult;
        try {
            preevalResult = (SoyValue)this.preevalVisitor.exec(node);
        }
        catch (RenderException e) {
            return;
        }
        ExprNode.PrimitiveNode newNode = InternalValueUtils.convertPrimitiveDataToExpr((PrimitiveData)preevalResult);
        node.getParent().replaceChild(node, newNode);
    }

    private static SoyValue getConstantOrNull(ExprNode expr) {
        switch (expr.getKind()) {
            case NULL_NODE: {
                return NullData.INSTANCE;
            }
            case BOOLEAN_NODE: {
                return BooleanData.forValue(((BooleanNode)expr).getValue());
            }
            case INTEGER_NODE: {
                return IntegerData.forValue(((IntegerNode)expr).getValue());
            }
            case FLOAT_NODE: {
                return FloatData.forValue(((FloatNode)expr).getValue());
            }
            case STRING_NODE: {
                return StringData.forValue(((StringNode)expr).getValue());
            }
        }
        return null;
    }
}

