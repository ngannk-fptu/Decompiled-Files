/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.persister.collection;

import org.hibernate.QueryException;
import org.hibernate.persister.collection.QueryableCollection;
import org.hibernate.persister.entity.PropertyMapping;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;

public class CollectionPropertyMapping
implements PropertyMapping {
    private final QueryableCollection memberPersister;

    public CollectionPropertyMapping(QueryableCollection memberPersister) {
        this.memberPersister = memberPersister;
    }

    @Override
    public Type toType(String propertyName) throws QueryException {
        if (propertyName.equals("elements")) {
            return this.memberPersister.getElementType();
        }
        if (propertyName.equals("indices")) {
            if (!this.memberPersister.hasIndex()) {
                throw new QueryException("unindexed collection before indices()");
            }
            return this.memberPersister.getIndexType();
        }
        if (propertyName.equals("size")) {
            return StandardBasicTypes.INTEGER;
        }
        if (propertyName.equals("maxIndex")) {
            return this.memberPersister.getIndexType();
        }
        if (propertyName.equals("minIndex")) {
            return this.memberPersister.getIndexType();
        }
        if (propertyName.equals("maxElement")) {
            return this.memberPersister.getElementType();
        }
        if (propertyName.equals("minElement")) {
            return this.memberPersister.getElementType();
        }
        throw new QueryException("illegal syntax near collection: " + propertyName);
    }

    @Override
    public String[] toColumns(String alias, String propertyName) throws QueryException {
        if (propertyName.equals("elements")) {
            return this.memberPersister.getElementColumnNames(alias);
        }
        if (propertyName.equals("indices")) {
            if (!this.memberPersister.hasIndex()) {
                throw new QueryException("unindexed collection in indices()");
            }
            return this.memberPersister.getIndexColumnNames(alias);
        }
        if (propertyName.equals("size")) {
            String[] cols = this.memberPersister.getKeyColumnNames();
            return new String[]{"count(" + alias + '.' + cols[0] + ')'};
        }
        if (propertyName.equals("maxIndex")) {
            if (!this.memberPersister.hasIndex()) {
                throw new QueryException("unindexed collection in maxIndex()");
            }
            String[] cols = this.memberPersister.getIndexColumnNames(alias);
            if (cols.length != 1) {
                throw new QueryException("composite collection index in maxIndex()");
            }
            return new String[]{"max(" + cols[0] + ')'};
        }
        if (propertyName.equals("minIndex")) {
            if (!this.memberPersister.hasIndex()) {
                throw new QueryException("unindexed collection in minIndex()");
            }
            String[] cols = this.memberPersister.getIndexColumnNames(alias);
            if (cols.length != 1) {
                throw new QueryException("composite collection index in minIndex()");
            }
            return new String[]{"min(" + cols[0] + ')'};
        }
        if (propertyName.equals("maxElement")) {
            String[] cols = this.memberPersister.getElementColumnNames(alias);
            if (cols.length != 1) {
                throw new QueryException("composite collection element in maxElement()");
            }
            return new String[]{"max(" + cols[0] + ')'};
        }
        if (propertyName.equals("minElement")) {
            String[] cols = this.memberPersister.getElementColumnNames(alias);
            if (cols.length != 1) {
                throw new QueryException("composite collection element in minElement()");
            }
            return new String[]{"min(" + cols[0] + ')'};
        }
        throw new QueryException("illegal syntax near collection: " + propertyName);
    }

    @Override
    public String[] toColumns(String propertyName) throws QueryException, UnsupportedOperationException {
        throw new UnsupportedOperationException("References to collections must be define a SQL alias");
    }

    @Override
    public Type getType() {
        return this.memberPersister.getCollectionType();
    }
}

