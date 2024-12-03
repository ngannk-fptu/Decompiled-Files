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
import java.util.Map;
import org.hibernate.hql.internal.antlr.HqlSqlTokenTypes;
import org.hibernate.hql.internal.ast.tree.FromElement;
import org.hibernate.hql.internal.ast.tree.FromReferenceNode;
import org.hibernate.hql.internal.ast.tree.MapKeyEntityFromElement;
import org.hibernate.hql.internal.ast.tree.TableReferenceNode;
import org.hibernate.hql.internal.ast.util.ColumnHelper;
import org.hibernate.persister.collection.QueryableCollection;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.type.CollectionType;
import org.hibernate.type.Type;

public abstract class AbstractMapComponentNode
extends FromReferenceNode
implements HqlSqlTokenTypes,
TableReferenceNode {
    private FromElement mapFromElement;
    private String[] columns;

    public FromReferenceNode getMapReference() {
        return (FromReferenceNode)this.getFirstChild();
    }

    public String[] getColumns() {
        return this.columns;
    }

    @Override
    public void setScalarColumnText(int i) {
        ColumnHelper.generateScalarColumns(this, this.getColumns(), i);
    }

    @Override
    public void resolve(boolean generateJoin, boolean implicitJoin, String classAlias, AST parent, AST parentPredicate) throws SemanticException {
        if (this.mapFromElement == null) {
            CollectionType collectionType;
            FromReferenceNode mapReference = this.getMapReference();
            mapReference.resolve(true, true);
            FromElement sourceFromElement = null;
            if (this.isAliasRef(mapReference)) {
                QueryableCollection collectionPersister = mapReference.getFromElement().getQueryableCollection();
                if (Map.class.isAssignableFrom(collectionPersister.getCollectionType().getReturnedClass())) {
                    sourceFromElement = mapReference.getFromElement();
                }
            } else if (mapReference.getDataType().isCollectionType() && Map.class.isAssignableFrom((collectionType = (CollectionType)mapReference.getDataType()).getReturnedClass())) {
                sourceFromElement = mapReference.getFromElement();
            }
            if (sourceFromElement == null) {
                throw this.nonMap();
            }
            this.mapFromElement = sourceFromElement;
        }
        this.setFromElement(this.mapFromElement);
        this.setDataType(this.resolveType(this.mapFromElement.getQueryableCollection()));
        this.columns = this.resolveColumns(this.mapFromElement.getQueryableCollection());
        this.initText(this.columns);
        this.setFirstChild(null);
    }

    public FromElement getMapFromElement() {
        return this.mapFromElement;
    }

    private boolean isAliasRef(FromReferenceNode mapReference) {
        return 148 == mapReference.getType();
    }

    private void initText(String[] columns) {
        String text = String.join((CharSequence)", ", columns);
        if (columns.length > 1 && this.getWalker().isComparativeExpressionClause()) {
            text = "(" + text + ")";
        }
        this.setText(text);
    }

    protected abstract String expressionDescription();

    protected abstract String[] resolveColumns(QueryableCollection var1);

    protected abstract Type resolveType(QueryableCollection var1);

    protected SemanticException nonMap() {
        return new SemanticException(this.expressionDescription() + " expression did not reference map property");
    }

    @Override
    public void resolveIndex(AST parent) {
        throw new UnsupportedOperationException(this.expressionDescription() + " expression cannot be the source for an index operation");
    }

    protected MapKeyEntityFromElement findOrAddMapKeyEntityFromElement(QueryableCollection collectionPersister) {
        if (!collectionPersister.getIndexType().isEntityType()) {
            return null;
        }
        for (FromElement destination : this.getFromElement().getDestinations()) {
            if (!(destination instanceof MapKeyEntityFromElement)) continue;
            return (MapKeyEntityFromElement)destination;
        }
        return MapKeyEntityFromElement.buildKeyJoin(this.getFromElement());
    }

    @Override
    public String[] getReferencedTables() {
        EntityPersister entityPersister;
        String[] referencedTables = null;
        FromElement fromElement = this.getFromElement();
        if (fromElement != null && (entityPersister = fromElement.getEntityPersister()) != null && entityPersister instanceof AbstractEntityPersister) {
            AbstractEntityPersister abstractEntityPersister = (AbstractEntityPersister)entityPersister;
            referencedTables = abstractEntityPersister.getTableNames();
        }
        return referencedTables;
    }
}

