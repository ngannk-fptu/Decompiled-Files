/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.LinkedHashMultimap
 *  com.google.common.collect.Multimap
 *  com.google.common.collect.Sets
 */
package com.google.template.soy.parsepasses.contextautoesc;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.template.soy.soytree.AbstractSoyNodeVisitor;
import com.google.template.soy.soytree.CallBasicNode;
import com.google.template.soy.soytree.SoyNode;
import com.google.template.soy.soytree.TemplateNode;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

final class TemplateCallGraph {
    private final Multimap<TemplateNode, TemplateNode> callers = LinkedHashMultimap.create();

    TemplateCallGraph(final Map<String, ImmutableList<TemplateNode>> templatesByName) {
        for (ImmutableList<TemplateNode> templateNodes : templatesByName.values()) {
            for (final TemplateNode tn : templateNodes) {
                class CallGraphBuilder
                extends AbstractSoyNodeVisitor<Void> {
                    CallGraphBuilder() {
                    }

                    @Override
                    public void visitCallBasicNode(CallBasicNode call) {
                        ImmutableList callees = (ImmutableList)templatesByName.get(call.getCalleeName());
                        if (callees != null) {
                            for (TemplateNode callee : callees) {
                                TemplateCallGraph.this.callers.put((Object)callee, (Object)tn);
                            }
                        }
                    }

                    @Override
                    protected void visitSoyNode(SoyNode node) {
                        if (node instanceof SoyNode.ParentSoyNode) {
                            this.visitChildren((SoyNode.ParentSoyNode)node);
                        }
                    }
                }
                new CallGraphBuilder().exec(tn);
            }
        }
    }

    Set<TemplateNode> callersOf(Iterable<TemplateNode> templates) {
        LinkedHashSet callerSet = Sets.newLinkedHashSet();
        for (TemplateNode templateNode : templates) {
            this.addTransitively(templateNode, callerSet);
        }
        return callerSet;
    }

    private void addTransitively(TemplateNode callee, Set<? super TemplateNode> out) {
        for (TemplateNode caller : this.callers.get((Object)callee)) {
            if (!out.add(caller)) continue;
            this.addTransitively(caller, out);
        }
    }
}

