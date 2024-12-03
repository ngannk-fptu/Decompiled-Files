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
import com.google.template.soy.soytree.SoyFileNode;
import com.google.template.soy.soytree.SoyNode;
import com.google.template.soy.soytree.TemplateNode;
import com.google.template.soy.soytree.defn.HeaderParam;
import com.google.template.soy.soytree.defn.TemplateParam;
import java.util.List;

public class InferRequiredSyntaxVersionVisitor
extends AbstractSoyNodeVisitor<SyntaxVersion> {
    private SyntaxVersion knownRequiredSyntaxVersion;

    @Override
    public SyntaxVersion exec(SoyNode node) {
        Preconditions.checkArgument((boolean)(node instanceof SoyFileNode));
        this.knownRequiredSyntaxVersion = SyntaxVersion.V1_0;
        this.visit(node);
        return this.knownRequiredSyntaxVersion;
    }

    @Override
    protected void visitTemplateNode(TemplateNode node) {
        List<TemplateParam> params;
        if (this.knownRequiredSyntaxVersion.num < SyntaxVersion.V2_3.num && (params = node.getParams()) != null) {
            for (TemplateParam param : params) {
                if (!(param instanceof HeaderParam)) continue;
                this.knownRequiredSyntaxVersion = SyntaxVersion.V2_3;
                break;
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

