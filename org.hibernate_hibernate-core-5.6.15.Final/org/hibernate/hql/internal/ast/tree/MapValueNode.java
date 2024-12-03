/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.internal.ast.tree;

import org.hibernate.hql.internal.ast.tree.AbstractMapComponentNode;
import org.hibernate.hql.internal.ast.tree.FromElement;
import org.hibernate.persister.collection.QueryableCollection;
import org.hibernate.type.Type;

public class MapValueNode
extends AbstractMapComponentNode {
    @Override
    protected String expressionDescription() {
        return "value(*)";
    }

    @Override
    protected String[] resolveColumns(QueryableCollection collectionPersister) {
        FromElement fromElement = this.getFromElement();
        return fromElement.toColumns(fromElement.getCollectionTableAlias(), "elements", this.getWalker().isInSelect());
    }

    @Override
    protected Type resolveType(QueryableCollection collectionPersister) {
        return collectionPersister.getElementType();
    }
}

