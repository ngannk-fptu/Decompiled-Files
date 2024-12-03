/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Maps
 */
package com.google.template.soy.sharedpasses;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.template.soy.base.SoySyntaxException;
import com.google.template.soy.basetree.SyntaxVersion;
import com.google.template.soy.exprtree.AbstractExprNodeVisitor;
import com.google.template.soy.exprtree.ExprNode;
import com.google.template.soy.exprtree.ExprRootNode;
import com.google.template.soy.exprtree.VarDefn;
import com.google.template.soy.exprtree.VarRefNode;
import com.google.template.soy.soytree.AbstractSoyNodeVisitor;
import com.google.template.soy.soytree.ExprUnion;
import com.google.template.soy.soytree.ForNode;
import com.google.template.soy.soytree.ForeachNonemptyNode;
import com.google.template.soy.soytree.LetContentNode;
import com.google.template.soy.soytree.LetValueNode;
import com.google.template.soy.soytree.PrintNode;
import com.google.template.soy.soytree.SoyNode;
import com.google.template.soy.soytree.SoySyntaxExceptionUtils;
import com.google.template.soy.soytree.TemplateNode;
import com.google.template.soy.soytree.defn.InjectedParam;
import com.google.template.soy.soytree.defn.TemplateParam;
import com.google.template.soy.soytree.defn.UndeclaredVar;
import java.util.Map;

public final class ResolveNamesVisitor
extends AbstractSoyNodeVisitor<Void> {
    private Scope currentScope = null;
    private Scope paramScope = null;
    private Scope injectedParamScope = null;
    private final SyntaxVersion declaredSyntaxVersion;

    public ResolveNamesVisitor(SyntaxVersion declaredSyntaxVersion) {
        this.declaredSyntaxVersion = declaredSyntaxVersion;
    }

    @Override
    protected void visitTemplateNode(TemplateNode node) {
        Scope savedScope = this.currentScope;
        this.paramScope = this.currentScope = new Scope(savedScope);
        if (node.getParams() != null) {
            for (TemplateParam param : node.getParams()) {
                this.currentScope.define(param);
            }
        }
        this.visitSoyNode(node);
    }

    @Override
    protected void visitPrintNode(PrintNode node) {
        this.visitSoyNode(node);
    }

    @Override
    protected void visitLetValueNode(LetValueNode node) {
        this.visitSoyNode(node);
        this.currentScope.define(node.getVar());
    }

    @Override
    protected void visitLetContentNode(LetContentNode node) {
        this.visitSoyNode(node);
        this.currentScope.define(node.getVar());
    }

    @Override
    protected void visitForNode(ForNode node) {
        this.visitExpressions(node);
        Scope savedScope = this.currentScope;
        this.currentScope = new Scope(savedScope, node.getVar());
        this.visitChildren(node);
        this.currentScope = savedScope;
    }

    @Override
    protected void visitForeachNonemptyNode(ForeachNonemptyNode node) {
        this.visitExpressions(node.getParent());
        Scope savedScope = this.currentScope;
        this.currentScope = new Scope(savedScope, node.getVar());
        this.visitChildren(node);
        this.currentScope = savedScope;
    }

    @Override
    protected void visitSoyNode(SoyNode node) {
        if (node instanceof SoyNode.ExprHolderNode) {
            this.visitExpressions((SoyNode.ExprHolderNode)node);
        }
        if (node instanceof SoyNode.ParentSoyNode) {
            if (node instanceof SoyNode.BlockNode) {
                Scope savedScope = this.currentScope;
                this.currentScope = new Scope(savedScope);
                this.visitChildren((SoyNode.BlockNode)node);
                this.currentScope = savedScope;
            } else {
                this.visitChildren((SoyNode.ParentSoyNode)node);
            }
        }
    }

    private void visitExpressions(SoyNode.ExprHolderNode node) {
        ResolveNamesExprVisitor exprVisitor = new ResolveNamesExprVisitor(node);
        for (ExprUnion exprUnion : node.getAllExprUnions()) {
            if (exprUnion.getExpr() == null) continue;
            exprVisitor.exec(exprUnion.getExpr());
        }
    }

    private class ResolveNamesExprVisitor
    extends AbstractExprNodeVisitor<Void> {
        private final SoyNode.ExprHolderNode owningSoyNode;
        private ExprRootNode<?> currExprRootNode;

        public ResolveNamesExprVisitor(SoyNode.ExprHolderNode owningSoyNode) {
            this.owningSoyNode = owningSoyNode;
        }

        @Override
        public Void exec(ExprNode node) {
            Preconditions.checkArgument((boolean)(node instanceof ExprRootNode));
            this.currExprRootNode = (ExprRootNode)node;
            this.visit(node);
            this.currExprRootNode = null;
            return null;
        }

        @Override
        protected void visit(ExprNode node) {
            super.visit(node);
        }

        @Override
        protected void visitExprRootNode(ExprRootNode<?> node) {
            this.visitChildren(node);
        }

        @Override
        protected void visitExprNode(ExprNode node) {
            if (node instanceof ExprNode.ParentExprNode) {
                this.visitChildren((ExprNode.ParentExprNode)node);
            }
        }

        @Override
        protected void visitVarRefNode(VarRefNode varRef) {
            if (varRef.isInjected()) {
                VarDefn varDefn;
                if (ResolveNamesVisitor.this.injectedParamScope == null) {
                    ResolveNamesVisitor.this.injectedParamScope = new Scope(null);
                }
                if ((varDefn = ResolveNamesVisitor.this.injectedParamScope.lookup(varRef.getName())) == null) {
                    varDefn = new InjectedParam(varRef.getName());
                    ResolveNamesVisitor.this.injectedParamScope.define(varDefn);
                }
                varRef.setDefn(varDefn);
                return;
            }
            VarDefn varDefn = ResolveNamesVisitor.this.currentScope.lookup(varRef.getName());
            if (varDefn == null) {
                if (((ResolveNamesVisitor)ResolveNamesVisitor.this).declaredSyntaxVersion.num >= SyntaxVersion.V9_9.num) {
                    throw this.createExceptionForInvalidExpr("Undefined variable: " + varRef.getName());
                }
                varDefn = new UndeclaredVar(varRef.getName());
                ResolveNamesVisitor.this.paramScope.define(varDefn);
            }
            varRef.setDefn(varDefn);
        }

        private SoySyntaxException createExceptionForInvalidExpr(String errorMsg) {
            return SoySyntaxExceptionUtils.createWithNode("Invalid expression \"" + this.currExprRootNode.toSourceString() + "\": " + errorMsg, this.owningSoyNode);
        }
    }

    public static class Scope {
        private final Scope enclosingScope;
        private final Map<String, VarDefn> varDefnMap = Maps.newHashMap();

        public Scope(Scope enclosingScope) {
            this.enclosingScope = enclosingScope;
        }

        public Scope(Scope enclosingScope, VarDefn varDefn) {
            this.enclosingScope = enclosingScope;
            this.define(varDefn);
        }

        public VarDefn lookup(String name) {
            Scope searchScope = this;
            while (searchScope != null) {
                VarDefn var = searchScope.varDefnMap.get(name);
                if (var != null) {
                    return var;
                }
                searchScope = searchScope.enclosingScope;
            }
            return null;
        }

        public void define(VarDefn varDefn) {
            this.varDefnMap.put(varDefn.name(), varDefn);
        }
    }
}

