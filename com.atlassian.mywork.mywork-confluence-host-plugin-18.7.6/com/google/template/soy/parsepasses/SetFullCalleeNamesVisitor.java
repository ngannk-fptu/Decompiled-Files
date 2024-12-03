/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.google.template.soy.parsepasses;

import com.google.common.base.Preconditions;
import com.google.template.soy.basetree.SyntaxVersion;
import com.google.template.soy.soytree.AbstractSoyNodeVisitor;
import com.google.template.soy.soytree.CallBasicNode;
import com.google.template.soy.soytree.SoyFileNode;
import com.google.template.soy.soytree.SoyFileSetNode;
import com.google.template.soy.soytree.SoyNode;
import com.google.template.soy.soytree.SoySyntaxExceptionUtils;
import java.util.Map;

public class SetFullCalleeNamesVisitor
extends AbstractSoyNodeVisitor<Void> {
    private String currNamespace;
    private Map<String, String> currAliasToNamespaceMap;

    @Override
    public Void exec(SoyNode soyNode) {
        Preconditions.checkArgument((soyNode instanceof SoyFileSetNode || soyNode instanceof SoyFileNode ? 1 : 0) != 0);
        return (Void)super.exec(soyNode);
    }

    @Override
    protected void visitSoyFileNode(SoyFileNode node) {
        this.currNamespace = node.getNamespace();
        this.currAliasToNamespaceMap = node.getAliasToNamespaceMap();
        this.visitChildren(node);
    }

    @Override
    protected void visitCallBasicNode(CallBasicNode node) {
        if (this.currNamespace == null) {
            String srcCalleeName = node.getSrcCalleeName();
            if (node.couldHaveSyntaxVersionAtLeast(SyntaxVersion.V2_0) && srcCalleeName.startsWith(".")) {
                throw SoySyntaxExceptionUtils.createWithNode("Missing namespace in Soy file containing 'call' with namespace-relative callee name (" + node.getTagString() + ").", node);
            }
            node.setCalleeName(node.getSrcCalleeName());
        } else {
            String srcCalleeName = node.getSrcCalleeName();
            if (srcCalleeName.startsWith(".")) {
                node.setCalleeName(this.currNamespace + srcCalleeName);
            } else if (srcCalleeName.contains(".")) {
                String[] parts = srcCalleeName.split("[.]", 2);
                if (this.currAliasToNamespaceMap.containsKey(parts[0])) {
                    String aliasNamespace = this.currAliasToNamespaceMap.get(parts[0]);
                    node.setCalleeName(aliasNamespace + '.' + parts[1]);
                } else {
                    node.setCalleeName(srcCalleeName);
                }
            } else {
                if (this.currAliasToNamespaceMap.containsKey(srcCalleeName)) {
                    throw SoySyntaxExceptionUtils.createWithNode("In 'call' tag, found callee that is a single identifier (not dotted) and matches a namespace alias ('" + srcCalleeName + "'), which is not allowed.", node);
                }
                node.setCalleeName(srcCalleeName);
            }
        }
        this.visitChildren(node);
    }

    @Override
    protected void visitSoyNode(SoyNode node) {
        if (node instanceof SoyNode.ParentSoyNode) {
            this.visitChildren((SoyNode.ParentSoyNode)node);
        }
    }
}

