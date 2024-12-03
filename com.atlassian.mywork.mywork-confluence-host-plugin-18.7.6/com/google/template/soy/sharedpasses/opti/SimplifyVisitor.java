/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Lists
 */
package com.google.template.soy.sharedpasses.opti;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.template.soy.base.internal.IdGenerator;
import com.google.template.soy.basetree.Node;
import com.google.template.soy.basetree.SyntaxVersion;
import com.google.template.soy.data.SoyValue;
import com.google.template.soy.data.internal.ParamStore;
import com.google.template.soy.data.restricted.BooleanData;
import com.google.template.soy.data.restricted.FloatData;
import com.google.template.soy.data.restricted.IntegerData;
import com.google.template.soy.data.restricted.NullData;
import com.google.template.soy.data.restricted.StringData;
import com.google.template.soy.exprtree.BooleanNode;
import com.google.template.soy.exprtree.ExprNode;
import com.google.template.soy.exprtree.ExprRootNode;
import com.google.template.soy.exprtree.FloatNode;
import com.google.template.soy.exprtree.IntegerNode;
import com.google.template.soy.exprtree.StringNode;
import com.google.template.soy.sharedpasses.opti.PrerenderVisitorFactory;
import com.google.template.soy.sharedpasses.opti.SimplifyExprVisitor;
import com.google.template.soy.sharedpasses.render.RenderException;
import com.google.template.soy.soytree.AbstractSoyNodeVisitor;
import com.google.template.soy.soytree.IfCondNode;
import com.google.template.soy.soytree.IfElseNode;
import com.google.template.soy.soytree.IfNode;
import com.google.template.soy.soytree.PrintDirectiveNode;
import com.google.template.soy.soytree.PrintNode;
import com.google.template.soy.soytree.RawTextNode;
import com.google.template.soy.soytree.SoyFileSetNode;
import com.google.template.soy.soytree.SoyNode;
import com.google.template.soy.soytree.SoytreeUtils;
import com.google.template.soy.soytree.SwitchCaseNode;
import com.google.template.soy.soytree.SwitchDefaultNode;
import com.google.template.soy.soytree.SwitchNode;
import com.google.template.soy.soytree.TemplateRegistry;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class SimplifyVisitor
extends AbstractSoyNodeVisitor<Void> {
    private final SimplifyExprVisitor simplifyExprVisitor;
    private final PrerenderVisitorFactory prerenderVisitorFactory;
    private IdGenerator nodeIdGen;
    private TemplateRegistry templateRegistry;

    @Inject
    public SimplifyVisitor(SimplifyExprVisitor simplifyExprVisitor, PrerenderVisitorFactory prerenderVisitorFactory) {
        this.simplifyExprVisitor = simplifyExprVisitor;
        this.prerenderVisitorFactory = prerenderVisitorFactory;
    }

    @Override
    public Void exec(SoyNode node) {
        Preconditions.checkArgument((boolean)(node instanceof SoyFileSetNode));
        SoyFileSetNode nodeAsRoot = (SoyFileSetNode)node;
        SoytreeUtils.execOnAllV2Exprs(nodeAsRoot, this.simplifyExprVisitor);
        this.nodeIdGen = nodeAsRoot.getNodeIdGenerator();
        this.templateRegistry = new TemplateRegistry(nodeAsRoot);
        super.exec(nodeAsRoot);
        return null;
    }

    @Override
    protected void visitPrintNode(PrintNode node) {
        if (!node.couldHaveSyntaxVersionAtLeast(SyntaxVersion.V2_0) || node.getExprUnion().getExpr() == null) {
            return;
        }
        SoyNode.BlockNode parent = node.getParent();
        if (parent instanceof SoyNode.MsgBlockNode) {
            return;
        }
        if (!SimplifyVisitor.isConstant(node.getExprUnion().getExpr())) {
            return;
        }
        for (PrintDirectiveNode directive : node.getChildren()) {
            for (ExprRootNode<?> arg : directive.getArgs()) {
                if (SimplifyVisitor.isConstant(arg)) continue;
                return;
            }
        }
        StringBuilder prerenderOutputSb = new StringBuilder();
        try {
            this.prerenderVisitorFactory.create(prerenderOutputSb, this.templateRegistry, ParamStore.EMPTY_INSTANCE, null).exec(node);
        }
        catch (RenderException pe) {
            return;
        }
        parent.replaceChild(node, new RawTextNode(this.nodeIdGen.genId(), prerenderOutputSb.toString()));
    }

    @Override
    protected void visitIfNode(IfNode node) {
        this.visitSoyNode(node);
        for (SoyNode child : Lists.newArrayList(node.getChildren())) {
            IfCondNode condNode;
            ExprRootNode<?> condExpr;
            if (!(child instanceof IfCondNode) || !SimplifyVisitor.isConstant(condExpr = (condNode = (IfCondNode)child).getExprUnion().getExpr())) continue;
            if (SimplifyVisitor.getConstantOrNull(condExpr).coerceToBoolean()) {
                int condIndex = node.getChildIndex(condNode);
                for (int i = node.numChildren() - 1; i > condIndex; --i) {
                    node.removeChild(i);
                }
                IfElseNode newElseNode = new IfElseNode(this.nodeIdGen.genId());
                newElseNode.addChildren(condNode.getChildren());
                node.replaceChild(condIndex, newElseNode);
                break;
            }
            node.removeChild(condNode);
        }
        if (node.numChildren() == 0) {
            node.getParent().removeChild(node);
        }
        if (node.numChildren() == 1 && node.getChild(0) instanceof IfElseNode) {
            SimplifyVisitor.replaceNodeWithList(node, ((IfElseNode)node.getChild(0)).getChildren());
        }
    }

    @Override
    protected void visitSwitchNode(SwitchNode node) {
        this.visitSoyNode(node);
        SoyValue switchExprValue = SimplifyVisitor.getConstantOrNull(node.getExpr());
        if (switchExprValue == null) {
            return;
        }
        for (SoyNode child : Lists.newArrayList(node.getChildren())) {
            if (!(child instanceof SwitchCaseNode)) continue;
            SwitchCaseNode caseNode = (SwitchCaseNode)child;
            boolean hasMatchingConstant = false;
            boolean hasAllNonmatchingConstants = true;
            for (ExprRootNode<?> caseExpr : caseNode.getExprList()) {
                SoyValue caseExprValue = SimplifyVisitor.getConstantOrNull(caseExpr);
                if (caseExprValue == null) {
                    hasAllNonmatchingConstants = false;
                    continue;
                }
                if (!caseExprValue.equals(switchExprValue)) continue;
                hasMatchingConstant = true;
                hasAllNonmatchingConstants = false;
                break;
            }
            if (hasMatchingConstant) {
                int caseIndex = node.getChildIndex(caseNode);
                for (int i = node.numChildren() - 1; i > caseIndex; --i) {
                    node.removeChild(i);
                }
                SwitchDefaultNode newDefaultNode = new SwitchDefaultNode(this.nodeIdGen.genId());
                newDefaultNode.addChildren(caseNode.getChildren());
                node.replaceChild(caseIndex, newDefaultNode);
                break;
            }
            if (!hasAllNonmatchingConstants) continue;
            node.removeChild(caseNode);
        }
        if (node.numChildren() == 1 && node.getChild(0) instanceof SwitchDefaultNode) {
            SimplifyVisitor.replaceNodeWithList(node, ((SwitchDefaultNode)node.getChild(0)).getChildren());
        }
    }

    @Override
    protected void visitSoyNode(SoyNode node) {
        if (node instanceof SoyNode.ParentSoyNode) {
            this.visitChildrenAllowingConcurrentModification((SoyNode.ParentSoyNode)node);
        }
        if (!(node instanceof SoyNode.BlockNode)) {
            return;
        }
        SoyNode.BlockNode nodeAsBlock = (SoyNode.BlockNode)node;
        boolean hasConsecRawTextNodes = false;
        for (int i = 0; i <= nodeAsBlock.numChildren() - 2; ++i) {
            if (!(nodeAsBlock.getChild(i) instanceof RawTextNode) || !(nodeAsBlock.getChild(i + 1) instanceof RawTextNode)) continue;
            hasConsecRawTextNodes = true;
            break;
        }
        if (!hasConsecRawTextNodes) {
            return;
        }
        ArrayList copyOfOrigChildren = Lists.newArrayList(nodeAsBlock.getChildren());
        nodeAsBlock.clearChildren();
        ArrayList consecutiveRawTextNodes = Lists.newArrayList();
        for (SoyNode.StandaloneNode origChild : copyOfOrigChildren) {
            if (origChild instanceof RawTextNode) {
                consecutiveRawTextNodes.add((RawTextNode)origChild);
                continue;
            }
            this.addConsecutiveRawTextNodesAsOneNodeHelper(nodeAsBlock, consecutiveRawTextNodes);
            consecutiveRawTextNodes.clear();
            nodeAsBlock.addChild(origChild);
        }
        this.addConsecutiveRawTextNodesAsOneNodeHelper(nodeAsBlock, consecutiveRawTextNodes);
        consecutiveRawTextNodes.clear();
    }

    private static boolean isConstant(ExprRootNode<?> exprRoot) {
        return exprRoot != null && exprRoot.getChild(0) instanceof ExprNode.ConstantNode;
    }

    private static SoyValue getConstantOrNull(ExprRootNode<?> exprRoot) {
        if (exprRoot == null) {
            return null;
        }
        Node expr = exprRoot.getChild(0);
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

    private void addConsecutiveRawTextNodesAsOneNodeHelper(SoyNode.BlockNode parent, List<RawTextNode> consecutiveRawTextNodes) {
        if (consecutiveRawTextNodes.size() == 0) {
            return;
        }
        if (consecutiveRawTextNodes.size() == 1) {
            parent.addChild((Node)consecutiveRawTextNodes.get(0));
        } else {
            StringBuilder rawText = new StringBuilder();
            for (RawTextNode rtn : consecutiveRawTextNodes) {
                rawText.append(rtn.getRawText());
            }
            parent.addChild(new RawTextNode(this.nodeIdGen.genId(), rawText.toString()));
        }
    }

    private static void replaceNodeWithList(SoyNode.StandaloneNode origNode, List<? extends SoyNode.StandaloneNode> replacementNodes) {
        SoyNode.BlockNode parent = origNode.getParent();
        int indexInParent = parent.getChildIndex(origNode);
        parent.removeChild(indexInParent);
        parent.addChildren(indexInParent, replacementNodes);
    }
}

