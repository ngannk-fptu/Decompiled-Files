/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.google.template.soy.parsepasses;

import com.google.common.base.Preconditions;
import com.google.template.soy.exprtree.ExprRootNode;
import com.google.template.soy.exprtree.VarRefNode;
import com.google.template.soy.sharedpasses.MarkLocalVarDataRefsVisitor;
import com.google.template.soy.sharedpasses.UnmarkLocalVarDataRefsVisitor;
import com.google.template.soy.soytree.AbstractSoyNodeVisitor;
import com.google.template.soy.soytree.CallBasicNode;
import com.google.template.soy.soytree.CallDelegateNode;
import com.google.template.soy.soytree.CallNode;
import com.google.template.soy.soytree.CallParamNode;
import com.google.template.soy.soytree.CallParamValueNode;
import com.google.template.soy.soytree.SoyFileNode;
import com.google.template.soy.soytree.SoyFileSetNode;
import com.google.template.soy.soytree.SoyNode;
import com.google.template.soy.soytree.TemplateNode;

public class ChangeCallsToPassAllDataVisitor
extends AbstractSoyNodeVisitor<Void> {
    @Override
    public Void exec(SoyNode node) {
        Preconditions.checkArgument((node instanceof SoyFileSetNode || node instanceof SoyFileNode || node instanceof TemplateNode ? 1 : 0) != 0);
        new MarkLocalVarDataRefsVisitor().exec(node);
        this.visit(node);
        new UnmarkLocalVarDataRefsVisitor().exec(node);
        return null;
    }

    @Override
    protected void visitCallNode(CallNode node) {
        CallNode newCallNode;
        CallNode nodeCast;
        if (node.numChildren() == 0) {
            return;
        }
        this.visitChildrenAllowingConcurrentModification(node);
        if (node.isPassingData() && !node.isPassingAllData()) {
            return;
        }
        for (CallParamNode param : node.getChildren()) {
            if (!(param instanceof CallParamValueNode)) {
                return;
            }
            CallParamValueNode valueParam = (CallParamValueNode)param;
            if (!("$" + valueParam.getKey()).equals(valueParam.getValueExprText())) {
                return;
            }
            ExprRootNode<?> valueExprRoot = ((CallParamValueNode)param).getValueExprUnion().getExpr();
            if (valueExprRoot == null) {
                return;
            }
            VarRefNode valueDataRef = (VarRefNode)valueExprRoot.getChild(0);
            if (!valueDataRef.isLocalVar().booleanValue() && !valueDataRef.isInjected()) continue;
            return;
        }
        if (node instanceof CallBasicNode) {
            nodeCast = (CallBasicNode)node;
            newCallNode = new CallBasicNode(node.getId(), ((CallBasicNode)nodeCast).getCalleeName(), ((CallBasicNode)nodeCast).getSrcCalleeName(), false, false, true, true, null, node.getUserSuppliedPhName(), null, node.getEscapingDirectiveNames());
        } else {
            nodeCast = (CallDelegateNode)node;
            newCallNode = new CallDelegateNode(node.getId(), ((CallDelegateNode)nodeCast).getDelCalleeName(), ((CallDelegateNode)nodeCast).getDelCalleeVariantExpr(), false, ((CallDelegateNode)nodeCast).allowsEmptyDefault(), true, true, null, node.getUserSuppliedPhName(), node.getEscapingDirectiveNames());
        }
        node.getParent().replaceChild(node, newCallNode);
    }

    @Override
    protected void visitSoyNode(SoyNode node) {
        if (node instanceof SoyNode.ParentSoyNode) {
            this.visitChildrenAllowingConcurrentModification((SoyNode.ParentSoyNode)node);
        }
    }
}

