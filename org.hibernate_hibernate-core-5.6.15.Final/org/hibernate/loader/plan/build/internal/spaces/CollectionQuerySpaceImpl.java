/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.build.internal.spaces;

import org.hibernate.loader.plan.build.internal.spaces.AbstractQuerySpace;
import org.hibernate.loader.plan.build.spi.ExpandingCollectionQuerySpace;
import org.hibernate.loader.plan.build.spi.ExpandingQuerySpaces;
import org.hibernate.loader.plan.spi.Join;
import org.hibernate.loader.plan.spi.JoinDefinedByMetadata;
import org.hibernate.loader.plan.spi.QuerySpace;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.collection.QueryableCollection;
import org.hibernate.persister.entity.PropertyMapping;

public class CollectionQuerySpaceImpl
extends AbstractQuerySpace
implements ExpandingCollectionQuerySpace {
    private final CollectionPersister persister;
    private JoinDefinedByMetadata elementJoin;
    private JoinDefinedByMetadata indexJoin;

    public CollectionQuerySpaceImpl(CollectionPersister persister, String uid, ExpandingQuerySpaces querySpaces, boolean canJoinsBeRequired) {
        super(uid, QuerySpace.Disposition.COLLECTION, querySpaces, canJoinsBeRequired);
        this.persister = persister;
    }

    @Override
    public CollectionPersister getCollectionPersister() {
        return this.persister;
    }

    @Override
    public PropertyMapping getPropertyMapping() {
        return (PropertyMapping)((Object)this.persister);
    }

    @Override
    public String[] toAliasedColumns(String alias, String propertyName) {
        QueryableCollection queryableCollection = (QueryableCollection)this.persister;
        if (propertyName.equals("elements")) {
            return queryableCollection.getElementColumnNames(alias);
        }
        if (propertyName.equals("indices")) {
            return queryableCollection.getIndexColumnNames(alias);
        }
        throw new IllegalArgumentException(String.format("Collection propertyName must be either %s or %s; instead it was %s.", "elements", "indices", propertyName));
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public void addJoin(Join join) {
        if (JoinDefinedByMetadata.class.isInstance(join)) {
            JoinDefinedByMetadata joinDefinedByMetadata = (JoinDefinedByMetadata)join;
            if (joinDefinedByMetadata.getJoinedPropertyName().equals("elements")) {
                if (this.elementJoin != null) throw new IllegalStateException("Attempt to add an element join, but an element join already exists.");
                this.elementJoin = joinDefinedByMetadata;
            } else {
                if (!joinDefinedByMetadata.getJoinedPropertyName().equals("indices")) throw new IllegalArgumentException(String.format("Collection propertyName must be either %s or %s; instead the joined property name was %s.", "elements", "indices", joinDefinedByMetadata.getJoinedPropertyName()));
                if (this.indexJoin != null) throw new IllegalStateException("Attempt to add an index join, but an index join already exists.");
                this.indexJoin = joinDefinedByMetadata;
            }
        }
        this.internalGetJoins().add(join);
    }

    @Override
    public ExpandingQuerySpaces getExpandingQuerySpaces() {
        return super.getExpandingQuerySpaces();
    }

    public void addJoin(JoinDefinedByMetadata join) {
        this.addJoin((Join)join);
    }
}

