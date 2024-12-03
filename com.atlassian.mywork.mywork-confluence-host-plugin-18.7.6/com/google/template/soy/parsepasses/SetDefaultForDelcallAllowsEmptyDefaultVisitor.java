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
import com.google.template.soy.soytree.CallDelegateNode;
import com.google.template.soy.soytree.SoyFileNode;
import com.google.template.soy.soytree.SoyFileSetNode;
import com.google.template.soy.soytree.SoyNode;

public class SetDefaultForDelcallAllowsEmptyDefaultVisitor
extends AbstractSoyNodeVisitor<Void> {
    private final boolean defaultValueForAllowsEmptyDefault;

    public SetDefaultForDelcallAllowsEmptyDefaultVisitor(SyntaxVersion declaredSyntaxVersion) {
        this.defaultValueForAllowsEmptyDefault = declaredSyntaxVersion.num < SyntaxVersion.V2_2.num;
    }

    @Override
    public Void exec(SoyNode soyNode) {
        Preconditions.checkArgument((soyNode instanceof SoyFileSetNode || soyNode instanceof SoyFileNode ? 1 : 0) != 0);
        return (Void)super.exec(soyNode);
    }

    @Override
    protected void visitCallDelegateNode(CallDelegateNode node) {
        node.maybeSetAllowsEmptyDefault(this.defaultValueForAllowsEmptyDefault);
        this.visitChildren(node);
    }

    @Override
    protected void visitSoyNode(SoyNode node) {
        if (node instanceof SoyNode.ParentSoyNode) {
            this.visitChildren((SoyNode.ParentSoyNode)node);
        }
    }
}

