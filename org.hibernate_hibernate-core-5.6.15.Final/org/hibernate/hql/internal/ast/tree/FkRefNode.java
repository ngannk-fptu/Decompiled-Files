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
import org.hibernate.QueryException;
import org.hibernate.hql.internal.ast.InvalidPathException;
import org.hibernate.hql.internal.ast.tree.DisplayableNode;
import org.hibernate.hql.internal.ast.tree.FromElement;
import org.hibernate.hql.internal.ast.tree.FromReferenceNode;
import org.hibernate.hql.internal.ast.tree.HqlSqlWalkerNode;
import org.hibernate.hql.internal.ast.tree.PathNode;
import org.hibernate.hql.internal.ast.tree.ResolvableNode;
import org.hibernate.type.BasicType;
import org.hibernate.type.CompositeType;
import org.hibernate.type.ManyToOneType;
import org.hibernate.type.Type;

public class FkRefNode
extends HqlSqlWalkerNode
implements ResolvableNode,
DisplayableNode,
PathNode {
    private FromReferenceNode toOnePath;
    private Type fkType;
    private String[] columns;

    private FromReferenceNode resolveToOnePath() {
        if (this.toOnePath == null) {
            try {
                this.resolve(false, true);
            }
            catch (SemanticException e) {
                String msg = "Unable to resolve to-one path `fk(" + this.toOnePath.getPath() + "`)";
                throw new QueryException(msg, (Exception)((Object)new InvalidPathException(msg)));
            }
        }
        assert (this.toOnePath != null);
        return this.toOnePath;
    }

    @Override
    public String getDisplayText() {
        FromReferenceNode toOnePath = this.resolveToOnePath();
        return "fk(`" + toOnePath.getDisplayText() + "` )";
    }

    @Override
    public String getPath() {
        return this.toOnePath.getDisplayText() + ".{fk}";
    }

    @Override
    public void resolve(boolean generateJoin, boolean implicitJoin) throws SemanticException {
        if (this.toOnePath != null) {
            return;
        }
        AST firstChild = this.getFirstChild();
        assert (firstChild instanceof FromReferenceNode);
        this.toOnePath = (FromReferenceNode)firstChild;
        this.toOnePath.resolve(false, true, null, (AST)this.toOnePath.getFromElement());
        Type sourcePathDataType = this.toOnePath.getDataType();
        if (!(sourcePathDataType instanceof ManyToOneType)) {
            throw new InvalidPathException("Argument to fk() function must be a to-one path, but found " + sourcePathDataType);
        }
        ManyToOneType toOneType = (ManyToOneType)sourcePathDataType;
        FromElement fromElement = this.toOnePath.getFromElement();
        this.fkType = toOneType.getIdentifierOrUniqueKeyType(this.getSessionFactoryHelper().getFactory());
        assert (this.fkType instanceof BasicType || this.fkType instanceof CompositeType);
        this.columns = fromElement.getElementType().toColumns(fromElement.getTableAlias(), toOneType.getPropertyName(), this.getWalker().isInSelect());
        assert (this.columns != null && this.columns.length > 0);
        this.setText(String.join((CharSequence)", ", this.columns));
    }

    @Override
    public void resolve(boolean generateJoin, boolean implicitJoin, String classAlias, AST parent, AST parentPredicate) throws SemanticException {
        this.resolve(false, true);
    }

    @Override
    public void resolve(boolean generateJoin, boolean implicitJoin, String classAlias, AST parent) throws SemanticException {
        this.resolve(false, true);
    }

    @Override
    public void resolve(boolean generateJoin, boolean implicitJoin, String classAlias) throws SemanticException {
        this.resolve(false, true);
    }

    @Override
    public void resolveInFunctionCall(boolean generateJoin, boolean implicitJoin) throws SemanticException {
        this.resolve(false, true);
    }

    @Override
    public void resolveIndex(AST parent) throws SemanticException {
        throw new InvalidPathException("fk() paths cannot be de-referenced as indexed path");
    }
}

