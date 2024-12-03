/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 */
package com.google.template.soy.soytree;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.template.soy.base.SoySyntaxException;
import com.google.template.soy.base.internal.IdGenerator;
import com.google.template.soy.exprtree.AbstractExprNodeVisitor;
import com.google.template.soy.soytree.AbstractSoyNodeVisitor;
import com.google.template.soy.soytree.ExprUnion;
import com.google.template.soy.soytree.SoyNode;
import com.google.template.soy.soytree.SoySyntaxExceptionUtils;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

public class SoytreeUtils {
    private SoytreeUtils() {
    }

    public static <T extends SoyNode> List<T> getAllNodesOfType(SoyNode rootSoyNode, Class<T> classObject) {
        return SoytreeUtils.getAllNodesOfType(rootSoyNode, classObject, true);
    }

    public static <T extends SoyNode> List<T> getAllNodesOfType(SoyNode rootSoyNode, final Class<T> classObject, final boolean doSearchSubtreesOfMatchedNodes) {
        final ImmutableList.Builder matchedNodesBuilder = ImmutableList.builder();
        AbstractSoyNodeVisitor<Void> visitor = new AbstractSoyNodeVisitor<Void>(){

            @Override
            public void visitSoyNode(SoyNode soyNode) {
                if (classObject.isAssignableFrom(soyNode.getClass())) {
                    matchedNodesBuilder.add((Object)soyNode);
                    if (!doSearchSubtreesOfMatchedNodes) {
                        return;
                    }
                }
                if (soyNode instanceof SoyNode.ParentSoyNode) {
                    this.visitChildren((SoyNode.ParentSoyNode)soyNode);
                }
            }
        };
        visitor.exec(rootSoyNode);
        return matchedNodesBuilder.build();
    }

    public static <R> void execOnAllV2Exprs(SoyNode node, AbstractExprNodeVisitor<R> exprNodeVisitor) {
        SoytreeUtils.execOnAllV2ExprsShortcircuitably(node, exprNodeVisitor, null);
    }

    public static <R> void execOnAllV2ExprsShortcircuitably(SoyNode node, AbstractExprNodeVisitor<R> exprNodeVisitor, Shortcircuiter<R> shortcircuiter) {
        new VisitAllV2ExprsVisitor<R>(exprNodeVisitor, shortcircuiter).exec(node);
    }

    public static <T extends SoyNode> T cloneWithNewIds(T origNode, IdGenerator nodeIdGen) {
        SoyNode clone = origNode.clone();
        new GenNewIdsVisitor(nodeIdGen).exec(clone);
        return (T)clone;
    }

    public static <T extends SoyNode> List<T> cloneListWithNewIds(List<T> origNodes, IdGenerator nodeIdGen) {
        Preconditions.checkNotNull(origNodes);
        if (origNodes.size() == 0) {
            return Lists.newArrayListWithCapacity((int)0);
        }
        ArrayList clones = Lists.newArrayListWithCapacity((int)origNodes.size());
        for (SoyNode origNode : origNodes) {
            SoyNode clone = origNode.clone();
            new GenNewIdsVisitor(nodeIdGen).exec(clone);
            clones.add(clone);
        }
        return clones;
    }

    private static class GenNewIdsVisitor
    extends AbstractSoyNodeVisitor<Void> {
        private IdGenerator nodeIdGen;

        public GenNewIdsVisitor(IdGenerator nodeIdGen) {
            this.nodeIdGen = nodeIdGen;
        }

        @Override
        protected void visitSoyNode(SoyNode node) {
            node.setId(this.nodeIdGen.genId());
            if (node instanceof SoyNode.ParentSoyNode) {
                this.visitChildren((SoyNode.ParentSoyNode)node);
            }
        }
    }

    private static class VisitAllV2ExprsVisitor<R>
    extends AbstractSoyNodeVisitor<R> {
        private final AbstractExprNodeVisitor<R> exprNodeVisitor;
        private final Shortcircuiter<R> shortcircuiter;

        public VisitAllV2ExprsVisitor(AbstractExprNodeVisitor<R> exprNodeVisitor, @Nullable Shortcircuiter<R> shortcircuiter) {
            this.exprNodeVisitor = exprNodeVisitor;
            this.shortcircuiter = shortcircuiter;
        }

        @Override
        protected void visitSoyNode(SoyNode node) {
            if (node instanceof SoyNode.ParentSoyNode) {
                for (SoyNode child : ((SoyNode.ParentSoyNode)node).getChildren()) {
                    this.visit(child);
                    if (this.shortcircuiter == null || !this.shortcircuiter.shouldShortcircuit(this.exprNodeVisitor)) continue;
                    return;
                }
            }
            if (node instanceof SoyNode.ExprHolderNode) {
                for (ExprUnion exprUnion : ((SoyNode.ExprHolderNode)node).getAllExprUnions()) {
                    if (exprUnion.getExpr() == null) continue;
                    try {
                        this.exprNodeVisitor.exec(exprUnion.getExpr());
                    }
                    catch (SoySyntaxException sse) {
                        throw SoySyntaxExceptionUtils.associateNode(sse, node);
                    }
                }
            }
        }
    }

    public static interface Shortcircuiter<R> {
        public boolean shouldShortcircuit(AbstractExprNodeVisitor<R> var1);
    }
}

