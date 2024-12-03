/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.template.soy.sharedpasses;

import com.google.template.soy.base.internal.IdGenerator;
import com.google.template.soy.exprtree.ExprRootNode;
import com.google.template.soy.shared.SoyCssRenamingMap;
import com.google.template.soy.soytree.AbstractSoyNodeVisitor;
import com.google.template.soy.soytree.CssNode;
import com.google.template.soy.soytree.ExprUnion;
import com.google.template.soy.soytree.PrintDirectiveNode;
import com.google.template.soy.soytree.PrintNode;
import com.google.template.soy.soytree.RawTextNode;
import com.google.template.soy.soytree.SoyFileSetNode;
import com.google.template.soy.soytree.SoyNode;
import javax.annotation.Nullable;

public class RenameCssVisitor
extends AbstractSoyNodeVisitor<Void> {
    private final SoyCssRenamingMap cssRenamingMap;
    private IdGenerator nodeIdGen;

    public RenameCssVisitor(@Nullable SoyCssRenamingMap cssRenamingMap) {
        this.cssRenamingMap = cssRenamingMap;
    }

    @Override
    public Void exec(SoyNode node) {
        this.nodeIdGen = node.getNearestAncestor(SoyFileSetNode.class).getNodeIdGenerator();
        super.exec(node);
        return null;
    }

    @Override
    protected void visitCssNode(CssNode node) {
        String mappedText;
        SoyNode.BlockNode parent = node.getParent();
        int indexInParent = parent.getChildIndex(node);
        parent.removeChild(indexInParent);
        ExprRootNode<?> componentNameExpr = node.getComponentNameExpr();
        if (componentNameExpr != null) {
            PrintNode pn = new PrintNode(this.nodeIdGen.genId(), false, new ExprUnion(componentNameExpr), null);
            pn.addChild(new PrintDirectiveNode(this.nodeIdGen.genId(), "|id", ""));
            parent.addChild(indexInParent, pn);
            ++indexInParent;
        }
        String selectorText = node.getSelectorText();
        if (this.cssRenamingMap != null && (mappedText = this.cssRenamingMap.get(selectorText)) != null) {
            selectorText = mappedText;
        }
        if (componentNameExpr != null) {
            selectorText = "-" + selectorText;
        }
        parent.addChild(indexInParent, new RawTextNode(this.nodeIdGen.genId(), selectorText));
    }

    @Override
    protected void visitSoyNode(SoyNode node) {
        if (node instanceof SoyNode.ParentSoyNode) {
            this.visitChildrenAllowingConcurrentModification((SoyNode.ParentSoyNode)node);
        }
    }
}

