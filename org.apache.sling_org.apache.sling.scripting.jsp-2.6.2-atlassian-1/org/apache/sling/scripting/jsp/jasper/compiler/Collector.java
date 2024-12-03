/*
 * Decompiled with CFR 0.152.
 */
package org.apache.sling.scripting.jsp.jasper.compiler;

import org.apache.sling.scripting.jsp.jasper.JasperException;
import org.apache.sling.scripting.jsp.jasper.compiler.Compiler;
import org.apache.sling.scripting.jsp.jasper.compiler.Node;
import org.apache.sling.scripting.jsp.jasper.compiler.PageInfo;

class Collector {
    Collector() {
    }

    public static void collect(Compiler compiler, Node.Nodes page) throws JasperException {
        CollectVisitor collectVisitor = new CollectVisitor();
        page.visit(collectVisitor);
        collectVisitor.updatePageInfo(compiler.getPageInfo());
    }

    static class CollectVisitor
    extends Node.Visitor {
        private boolean scriptingElementSeen = false;
        private boolean usebeanSeen = false;
        private boolean includeActionSeen = false;
        private boolean paramActionSeen = false;
        private boolean setPropertySeen = false;
        private boolean hasScriptingVars = false;

        CollectVisitor() {
        }

        @Override
        public void visit(Node.ParamAction n) throws JasperException {
            if (n.getValue().isExpression()) {
                this.scriptingElementSeen = true;
            }
            this.paramActionSeen = true;
        }

        @Override
        public void visit(Node.IncludeAction n) throws JasperException {
            if (n.getPage().isExpression()) {
                this.scriptingElementSeen = true;
            }
            this.includeActionSeen = true;
            this.visitBody(n);
        }

        @Override
        public void visit(Node.ForwardAction n) throws JasperException {
            if (n.getPage().isExpression()) {
                this.scriptingElementSeen = true;
            }
            this.visitBody(n);
        }

        @Override
        public void visit(Node.SetProperty n) throws JasperException {
            if (n.getValue() != null && n.getValue().isExpression()) {
                this.scriptingElementSeen = true;
            }
            this.setPropertySeen = true;
        }

        @Override
        public void visit(Node.UseBean n) throws JasperException {
            if (n.getBeanName() != null && n.getBeanName().isExpression()) {
                this.scriptingElementSeen = true;
            }
            this.usebeanSeen = true;
            this.visitBody(n);
        }

        @Override
        public void visit(Node.PlugIn n) throws JasperException {
            if (n.getHeight() != null && n.getHeight().isExpression()) {
                this.scriptingElementSeen = true;
            }
            if (n.getWidth() != null && n.getWidth().isExpression()) {
                this.scriptingElementSeen = true;
            }
            this.visitBody(n);
        }

        @Override
        public void visit(Node.CustomTag n) throws JasperException {
            this.checkSeen(n.getChildInfo(), n);
        }

        private void checkSeen(Node.ChildInfo ci, Node n) throws JasperException {
            Node.CustomTag ct;
            boolean scriptingElementSeenSave = this.scriptingElementSeen;
            this.scriptingElementSeen = false;
            boolean usebeanSeenSave = this.usebeanSeen;
            this.usebeanSeen = false;
            boolean includeActionSeenSave = this.includeActionSeen;
            this.includeActionSeen = false;
            boolean paramActionSeenSave = this.paramActionSeen;
            this.paramActionSeen = false;
            boolean setPropertySeenSave = this.setPropertySeen;
            this.setPropertySeen = false;
            boolean hasScriptingVarsSave = this.hasScriptingVars;
            this.hasScriptingVars = false;
            if (n instanceof Node.CustomTag) {
                ct = (Node.CustomTag)n;
                Node.JspAttribute[] attrs = ct.getJspAttributes();
                for (int i = 0; attrs != null && i < attrs.length; ++i) {
                    if (!attrs[i].isExpression()) continue;
                    this.scriptingElementSeen = true;
                    break;
                }
            }
            this.visitBody(n);
            if (n instanceof Node.CustomTag && !this.hasScriptingVars) {
                ct = (Node.CustomTag)n;
                this.hasScriptingVars = ct.getVariableInfos().length > 0 || ct.getTagVariableInfos().length > 0;
            }
            ci.setScriptless(!this.scriptingElementSeen);
            ci.setHasUseBean(this.usebeanSeen);
            ci.setHasIncludeAction(this.includeActionSeen);
            ci.setHasParamAction(this.paramActionSeen);
            ci.setHasSetProperty(this.setPropertySeen);
            ci.setHasScriptingVars(this.hasScriptingVars);
            this.scriptingElementSeen = this.scriptingElementSeen || scriptingElementSeenSave;
            this.usebeanSeen = this.usebeanSeen || usebeanSeenSave;
            this.setPropertySeen = this.setPropertySeen || setPropertySeenSave;
            this.includeActionSeen = this.includeActionSeen || includeActionSeenSave;
            this.paramActionSeen = this.paramActionSeen || paramActionSeenSave;
            this.hasScriptingVars = this.hasScriptingVars || hasScriptingVarsSave;
        }

        @Override
        public void visit(Node.JspElement n) throws JasperException {
            if (n.getNameAttribute().isExpression()) {
                this.scriptingElementSeen = true;
            }
            Node.JspAttribute[] attrs = n.getJspAttributes();
            for (int i = 0; i < attrs.length; ++i) {
                if (!attrs[i].isExpression()) continue;
                this.scriptingElementSeen = true;
                break;
            }
            this.visitBody(n);
        }

        @Override
        public void visit(Node.JspBody n) throws JasperException {
            this.checkSeen(n.getChildInfo(), n);
        }

        @Override
        public void visit(Node.NamedAttribute n) throws JasperException {
            this.checkSeen(n.getChildInfo(), n);
        }

        @Override
        public void visit(Node.Declaration n) throws JasperException {
            this.scriptingElementSeen = true;
        }

        @Override
        public void visit(Node.Expression n) throws JasperException {
            this.scriptingElementSeen = true;
        }

        @Override
        public void visit(Node.Scriptlet n) throws JasperException {
            this.scriptingElementSeen = true;
        }

        public void updatePageInfo(PageInfo pageInfo) {
            pageInfo.setScriptless(!this.scriptingElementSeen);
        }
    }
}

