/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package com.google.template.soy.parsepasses;

import com.google.common.collect.Lists;
import com.google.template.soy.base.internal.IdGenerator;
import com.google.template.soy.basetree.Node;
import com.google.template.soy.exprtree.DataAccessNode;
import com.google.template.soy.exprtree.GlobalNode;
import com.google.template.soy.exprtree.VarRefNode;
import com.google.template.soy.shared.SoyGeneralOptions;
import com.google.template.soy.soytree.AbstractSoyNode;
import com.google.template.soy.soytree.AbstractSoyNodeVisitor;
import com.google.template.soy.soytree.CssNode;
import com.google.template.soy.soytree.PrintDirectiveNode;
import com.google.template.soy.soytree.PrintNode;
import com.google.template.soy.soytree.RawTextNode;
import com.google.template.soy.soytree.SoyFileSetNode;
import com.google.template.soy.soytree.SoyNode;
import com.google.template.soy.soytree.SoySyntaxExceptionUtils;
import java.util.List;

public class HandleCssCommandVisitor
extends AbstractSoyNodeVisitor<Void> {
    private final SoyGeneralOptions.CssHandlingScheme cssHandlingScheme;
    private List<CssNode> cssNodes;

    public HandleCssCommandVisitor(SoyGeneralOptions.CssHandlingScheme cssHandlingScheme) {
        this.cssHandlingScheme = cssHandlingScheme;
    }

    @Override
    public Void exec(SoyNode node) {
        this.cssNodes = Lists.newArrayList();
        this.visit(node);
        return null;
    }

    @Override
    protected void visitSoyFileSetNode(SoyFileSetNode node) {
        if (this.cssHandlingScheme == SoyGeneralOptions.CssHandlingScheme.BACKEND_SPECIFIC) {
            return;
        }
        this.visitChildren(node);
        IdGenerator nodeIdGen = node.getNearestAncestor(SoyFileSetNode.class).getNodeIdGenerator();
        for (CssNode cssNode : this.cssNodes) {
            AbstractSoyNode newNode;
            if (this.cssHandlingScheme == SoyGeneralOptions.CssHandlingScheme.LITERAL) {
                newNode = new RawTextNode(nodeIdGen.genId(), cssNode.getCommandText());
            } else if (this.cssHandlingScheme == SoyGeneralOptions.CssHandlingScheme.REFERENCE) {
                PrintNode newPrintNode = new PrintNode(nodeIdGen.genId(), false, cssNode.getCommandText(), null);
                newPrintNode.addChild(new PrintDirectiveNode(nodeIdGen.genId(), "|noAutoescape", ""));
                newNode = newPrintNode;
                boolean isInvalidExpr = false;
                if (newPrintNode.getExprUnion().getExpr() == null) {
                    isInvalidExpr = true;
                } else {
                    Node exprNode = newPrintNode.getExprUnion().getExpr().getChild(0);
                    if (!(exprNode instanceof GlobalNode || exprNode instanceof VarRefNode || exprNode instanceof DataAccessNode)) {
                        isInvalidExpr = true;
                    }
                }
                if (isInvalidExpr) {
                    throw SoySyntaxExceptionUtils.createWithNode("The css-handling scheme is 'reference', but tag " + cssNode.getTagString() + " does not contain a valid reference.", node);
                }
            } else {
                throw new AssertionError();
            }
            cssNode.getParent().replaceChild(cssNode, newNode);
        }
    }

    @Override
    protected void visitCssNode(CssNode node) {
        this.cssNodes.add(node);
    }

    @Override
    protected void visitSoyNode(SoyNode node) {
        if (node instanceof SoyNode.ParentSoyNode) {
            this.visitChildren((SoyNode.ParentSoyNode)node);
        }
    }
}

