/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.sharedpasses;

import com.google.template.soy.exprtree.AbstractExprNodeVisitor;
import com.google.template.soy.exprtree.ExprNode;
import com.google.template.soy.exprtree.VarRefNode;
import com.google.template.soy.soytree.SoytreeUtils;
import com.google.template.soy.soytree.TemplateNode;
import com.google.template.soy.soytree.defn.TemplateParam;
import java.util.List;

public class ShouldEnsureDataIsDefinedVisitor {
    public boolean exec(TemplateNode template) {
        List<TemplateParam> params = template.getParams();
        if (params != null) {
            for (TemplateParam param : params) {
                if (!param.isRequired()) continue;
                return false;
            }
        }
        ExistsRegDataRefInExprVisitor helperVisitor = new ExistsRegDataRefInExprVisitor();
        SoytreeUtils.execOnAllV2ExprsShortcircuitably(template, helperVisitor, new SoytreeUtils.Shortcircuiter<Void>(){

            @Override
            public boolean shouldShortcircuit(AbstractExprNodeVisitor<Void> exprNodeVisitor) {
                return ((ExistsRegDataRefInExprVisitor)exprNodeVisitor).foundRegDataRef();
            }
        });
        return helperVisitor.foundRegDataRef();
    }

    private static class ExistsRegDataRefInExprVisitor
    extends AbstractExprNodeVisitor<Void> {
        private boolean foundRegDataRef = false;

        private ExistsRegDataRefInExprVisitor() {
        }

        public boolean foundRegDataRef() {
            return this.foundRegDataRef;
        }

        @Override
        protected void visitVarRefNode(VarRefNode node) {
            if (node.isPossibleParam().booleanValue()) {
                this.foundRegDataRef = true;
            }
        }

        @Override
        protected void visitExprNode(ExprNode node) {
            if (node instanceof ExprNode.ParentExprNode) {
                this.visitChildren((ExprNode.ParentExprNode)node);
            }
        }
    }
}

