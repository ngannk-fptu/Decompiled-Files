/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 */
package com.google.template.soy.sharedpasses;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.template.soy.exprtree.AbstractExprNodeVisitor;
import com.google.template.soy.exprtree.ExprNode;
import com.google.template.soy.exprtree.VarRefNode;
import com.google.template.soy.soytree.AbstractSoyNodeVisitor;
import com.google.template.soy.soytree.ExprUnion;
import com.google.template.soy.soytree.LetNode;
import com.google.template.soy.soytree.SoyFileNode;
import com.google.template.soy.soytree.SoyFileSetNode;
import com.google.template.soy.soytree.SoyNode;
import com.google.template.soy.soytree.TemplateNode;
import com.google.template.soy.soytree.jssrc.GoogMsgDefNode;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

public class BuildAllDependeesMapVisitor
extends AbstractSoyNodeVisitor<Map<SoyNode, List<SoyNode>>> {
    private Deque<List<SoyNode>> potentialDependeeFrames;
    private Map<SoyNode, List<SoyNode>> allDependeesMap;
    private static final Pattern TOP_LEVEL_REF = Pattern.compile("\\$([a-zA-Z0-9_]+)");

    @Override
    public Map<SoyNode, List<SoyNode>> exec(SoyNode node) {
        Preconditions.checkArgument((node instanceof SoyFileSetNode || node instanceof SoyFileNode || node instanceof TemplateNode ? 1 : 0) != 0);
        this.allDependeesMap = Maps.newHashMap();
        this.visit(node);
        return this.allDependeesMap;
    }

    @Override
    protected void visitSoyFileSetNode(SoyFileSetNode node) {
        this.visitChildren(node);
    }

    @Override
    protected void visitSoyFileNode(SoyFileNode node) {
        this.visitChildren(node);
    }

    @Override
    protected void visitTemplateNode(TemplateNode node) {
        this.potentialDependeeFrames = new ArrayDeque<List<SoyNode>>();
        this.potentialDependeeFrames.push(Lists.newArrayList((Object[])new SoyNode[]{node}));
        this.visitChildren(node);
        this.potentialDependeeFrames.pop();
    }

    @Override
    protected void visitGoogMsgDefNode(GoogMsgDefNode node) {
        this.visitSoyNode(node);
        this.potentialDependeeFrames.peek().add(node);
    }

    @Override
    protected void visitLetNode(LetNode node) {
        this.visitSoyNode(node);
        this.potentialDependeeFrames.peek().add(node);
    }

    @Override
    protected void visitSoyNode(SoyNode node) {
        HashSet topLevelRefs;
        if (node instanceof SoyNode.ParentSoyNode) {
            ArrayList newPotentialDependeeFrame = Lists.newArrayList();
            if (node instanceof TemplateNode || node instanceof SoyNode.SplitLevelTopNode || node instanceof SoyNode.ConditionalBlockNode || node instanceof SoyNode.LocalVarBlockNode || node instanceof SoyNode.MsgBlockNode) {
                newPotentialDependeeFrame.add(node);
            }
            this.potentialDependeeFrames.push(newPotentialDependeeFrame);
            this.visitChildren((SoyNode.ParentSoyNode)node);
            this.potentialDependeeFrames.pop();
        }
        if (node instanceof SoyNode.ExprHolderNode) {
            topLevelRefs = Sets.newHashSet();
            for (ExprUnion exprUnion : ((SoyNode.ExprHolderNode)node).getAllExprUnions()) {
                topLevelRefs.addAll(BuildAllDependeesMapVisitor.getTopLevelRefsInExpr(exprUnion));
            }
        } else {
            topLevelRefs = null;
        }
        ArrayList allDependees = Lists.newArrayList();
        for (List<SoyNode> potentialDependeeFrame : this.potentialDependeeFrames) {
            for (int i = potentialDependeeFrame.size() - 1; i >= 0; --i) {
                SoyNode potentialDependee = potentialDependeeFrame.get(i);
                if (!this.isDependent(potentialDependee, node, topLevelRefs)) continue;
                allDependees.add(potentialDependee);
            }
        }
        this.allDependeesMap.put(node, allDependees);
        if (allDependees.size() == 0) {
            throw new AssertionError();
        }
    }

    private boolean isDependent(SoyNode potentialDependee, SoyNode node, @Nullable Set<String> topLevelRefs) {
        if (potentialDependee instanceof TemplateNode || potentialDependee instanceof SoyNode.ConditionalBlockNode && !(potentialDependee instanceof SoyNode.LoopNode)) {
            return true;
        }
        if (node.getParent() == potentialDependee && (potentialDependee instanceof SoyNode.SplitLevelTopNode || potentialDependee instanceof SoyNode.MsgBlockNode)) {
            return true;
        }
        if (potentialDependee instanceof SoyNode.LocalVarNode) {
            if (topLevelRefs != null && topLevelRefs.contains(((SoyNode.LocalVarNode)potentialDependee).getVarName())) {
                return true;
            }
            if (node instanceof SoyNode.ParentSoyNode) {
                for (SoyNode child : ((SoyNode.ParentSoyNode)node).getChildren()) {
                    List<SoyNode> allDependeesOfChild = this.allDependeesMap.get(child);
                    if (allDependeesOfChild == null) {
                        throw new AssertionError((Object)"Child has not been visited.");
                    }
                    if (!allDependeesOfChild.contains(potentialDependee)) continue;
                    return true;
                }
            }
        }
        return false;
    }

    private static Set<String> getTopLevelRefsInExpr(ExprUnion exprUnion) {
        if (exprUnion.getExpr() != null) {
            return new GetTopLevelRefsInExprVisitor().exec(exprUnion.getExpr());
        }
        return BuildAllDependeesMapVisitor.getTopLevelRefsInV1Expr(exprUnion.getExprText());
    }

    private static Set<String> getTopLevelRefsInV1Expr(String exprText) {
        HashSet topLevelRefs = Sets.newHashSet();
        Matcher matcher = TOP_LEVEL_REF.matcher(exprText);
        while (matcher.find()) {
            topLevelRefs.add(matcher.group(1));
        }
        return topLevelRefs;
    }

    private static class GetTopLevelRefsInExprVisitor
    extends AbstractExprNodeVisitor<Set<String>> {
        private Set<String> topLevelRefs;

        private GetTopLevelRefsInExprVisitor() {
        }

        @Override
        public Set<String> exec(ExprNode node) {
            this.topLevelRefs = Sets.newHashSet();
            this.visit(node);
            return this.topLevelRefs;
        }

        @Override
        protected void visitVarRefNode(VarRefNode node) {
            this.topLevelRefs.add(node.getName());
        }

        @Override
        protected void visitExprNode(ExprNode node) {
            if (node instanceof ExprNode.ParentExprNode) {
                this.visitChildren((ExprNode.ParentExprNode)node);
            }
        }
    }
}

