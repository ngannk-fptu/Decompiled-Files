/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  antlr.SemanticException
 *  antlr.collections.AST
 */
package org.hibernate.hql.internal.ast.tree;

import antlr.SemanticException;
import antlr.collections.AST;
import org.hibernate.hql.internal.ast.tree.AbstractSelectExpression;
import org.hibernate.hql.internal.ast.tree.DisplayableNode;
import org.hibernate.hql.internal.ast.tree.FromElement;
import org.hibernate.hql.internal.ast.tree.InitializeableNode;
import org.hibernate.hql.internal.ast.tree.PathNode;
import org.hibernate.hql.internal.ast.tree.ResolvableNode;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;

public abstract class FromReferenceNode
extends AbstractSelectExpression
implements ResolvableNode,
DisplayableNode,
InitializeableNode,
PathNode {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(FromReferenceNode.class);
    private FromElement fromElement;
    private boolean resolved;
    public static final int ROOT_LEVEL = 0;

    @Override
    public FromElement getFromElement() {
        return this.fromElement;
    }

    public void setFromElement(FromElement fromElement) {
        this.fromElement = fromElement;
    }

    public void resolveFirstChild() throws SemanticException {
    }

    @Override
    public String getPath() {
        return this.getOriginalText();
    }

    public boolean isResolved() {
        return this.resolved;
    }

    public void setResolved() {
        this.resolved = true;
        if (LOG.isDebugEnabled()) {
            LOG.debugf("Resolved : %s -> %s", this.getPath(), this.getText());
        }
    }

    @Override
    public String getDisplayText() {
        StringBuilder buf = new StringBuilder();
        buf.append("{").append(this.fromElement == null ? "no fromElement" : this.fromElement.getDisplayText());
        buf.append("}");
        return buf.toString();
    }

    public void recursiveResolve(int level, boolean impliedAtRoot, String classAlias) throws SemanticException {
        this.recursiveResolve(level, impliedAtRoot, classAlias, (AST)this);
    }

    public void recursiveResolve(int level, boolean impliedAtRoot, String classAlias, AST parent) throws SemanticException {
        AST lhs = this.getFirstChild();
        int nextLevel = level + 1;
        if (lhs != null) {
            FromReferenceNode n = (FromReferenceNode)lhs;
            n.recursiveResolve(nextLevel, impliedAtRoot, null, (AST)this);
        }
        this.resolveFirstChild();
        boolean impliedJoin = true;
        if (level == 0 && !impliedAtRoot) {
            impliedJoin = false;
        }
        this.resolve(true, impliedJoin, classAlias, parent);
    }

    @Override
    public boolean isReturnableEntity() throws SemanticException {
        return !this.isScalar() && this.fromElement.isEntity();
    }

    @Override
    public void resolveInFunctionCall(boolean generateJoin, boolean implicitJoin) throws SemanticException {
        this.resolve(generateJoin, implicitJoin);
    }

    @Override
    public void resolve(boolean generateJoin, boolean implicitJoin) throws SemanticException {
        this.resolve(generateJoin, implicitJoin, null);
    }

    @Override
    public void resolve(boolean generateJoin, boolean implicitJoin, String classAlias) throws SemanticException {
        this.resolve(generateJoin, implicitJoin, classAlias, null);
    }

    @Override
    public void resolve(boolean generateJoin, boolean implicitJoin, String classAlias, AST parent) throws SemanticException {
        this.resolve(generateJoin, implicitJoin, classAlias, parent, null);
    }

    public void prepareForDot(String propertyName) throws SemanticException {
    }

    public FromElement getImpliedJoin() {
        return null;
    }

    protected boolean isFromElementUpdateOrDeleteRoot(FromElement element) {
        if (element.getFromClause().getParentFromClause() != null) {
            return false;
        }
        return this.getWalker().getStatementType() == 13 || this.getWalker().getStatementType() == 51;
    }
}

