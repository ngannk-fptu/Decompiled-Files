/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.internal.ast.tree;

import org.hibernate.hql.internal.ast.tree.AbstractMapComponentNode;
import org.hibernate.hql.internal.ast.tree.FromElement;
import org.hibernate.hql.internal.ast.tree.MapKeyEntityFromElement;
import org.hibernate.persister.collection.QueryableCollection;
import org.hibernate.type.Type;

public class MapKeyNode
extends AbstractMapComponentNode {
    private MapKeyEntityFromElement mapKeyEntityFromElement;

    @Override
    protected String expressionDescription() {
        return "key(*)";
    }

    @Override
    protected String[] resolveColumns(QueryableCollection collectionPersister) {
        this.mapKeyEntityFromElement = this.findOrAddMapKeyEntityFromElement(collectionPersister);
        if (this.mapKeyEntityFromElement != null) {
            this.setFromElement(this.mapKeyEntityFromElement);
        }
        FromElement fromElement = this.getMapFromElement();
        return fromElement.toColumns(fromElement.getCollectionTableAlias(), "index", this.getWalker().isInSelect());
    }

    @Override
    protected Type resolveType(QueryableCollection collectionPersister) {
        return collectionPersister.getIndexType();
    }

    public MapKeyEntityFromElement getMapKeyEntityFromElement() {
        return this.mapKeyEntityFromElement;
    }
}

