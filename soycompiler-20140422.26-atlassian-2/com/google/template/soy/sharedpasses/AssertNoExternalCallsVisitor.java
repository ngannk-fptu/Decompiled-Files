/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.google.template.soy.sharedpasses;

import com.google.common.base.Preconditions;
import com.google.template.soy.base.SoySyntaxException;
import com.google.template.soy.base.internal.SoyFileKind;
import com.google.template.soy.soytree.AbstractSoyNodeVisitor;
import com.google.template.soy.soytree.CallBasicNode;
import com.google.template.soy.soytree.SoyFileNode;
import com.google.template.soy.soytree.SoyFileSetNode;
import com.google.template.soy.soytree.SoyNode;
import com.google.template.soy.soytree.TemplateBasicNode;
import com.google.template.soy.soytree.TemplateNode;
import com.google.template.soy.soytree.TemplateRegistry;

public class AssertNoExternalCallsVisitor
extends AbstractSoyNodeVisitor<Void> {
    private StringBuilder errorBuffer;
    private TemplateRegistry templateRegistry;

    @Override
    public Void exec(SoyNode soyNode) {
        Preconditions.checkArgument((soyNode instanceof SoyFileSetNode || soyNode instanceof SoyFileNode ? 1 : 0) != 0);
        this.errorBuffer = new StringBuilder();
        this.templateRegistry = new TemplateRegistry(soyNode.getNearestAncestor(SoyFileSetNode.class));
        super.exec(soyNode);
        if (this.errorBuffer.length() != 0) {
            throw SoySyntaxException.createWithoutMetaInfo(this.errorBuffer.toString());
        }
        return null;
    }

    @Override
    protected void visitCallBasicNode(CallBasicNode node) {
        TemplateBasicNode callee = this.templateRegistry.getBasicTemplate(node.getCalleeName());
        if (callee == null) {
            this.addError(node, "Encountered call to undefined template '" + node.getCalleeName() + "'.");
        } else {
            SoyFileKind callerKind = node.getNearestAncestor(SoyFileNode.class).getSoyFileKind();
            SoyFileKind calleeKind = callee.getParent().getSoyFileKind();
            if (calleeKind == SoyFileKind.INDIRECT_DEP && callerKind == SoyFileKind.SRC) {
                this.addError(node, "Call to '" + callee.getTemplateNameForUserMsgs() + "' is satisfied only by indirect dependency " + callee.getSourceLocation().getFilePath() + ". Add it as a direct dependency, instead.");
            }
            if (calleeKind == SoyFileKind.SRC && callerKind != SoyFileKind.SRC) {
                this.addError(node, "Illegal call to '" + callee.getTemplateNameForUserMsgs() + "', because according to the dependency graph, " + callee.getSourceLocation().getFilePath() + " depends on " + node.getSourceLocation().getFilePath() + ", not the other way around.");
            }
        }
        this.visitChildren(node);
    }

    private void addError(CallBasicNode node, String errorStr) {
        TemplateNode containingTemplateNode = node.getNearestAncestor(TemplateNode.class);
        String fullError = node.getSourceLocation() + ", template " + containingTemplateNode.getTemplateNameForUserMsgs() + ": " + errorStr + "\n";
        this.errorBuffer.append(fullError);
    }

    @Override
    protected void visitSoyNode(SoyNode node) {
        if (node instanceof SoyNode.ParentSoyNode) {
            this.visitChildren((SoyNode.ParentSoyNode)node);
        }
    }
}

