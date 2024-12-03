/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.build.internal.returns;

import org.hibernate.engine.FetchStrategy;
import org.hibernate.loader.plan.build.internal.returns.AbstractCollectionReference;
import org.hibernate.loader.plan.build.internal.spaces.QuerySpaceHelper;
import org.hibernate.loader.plan.build.spi.ExpandingCollectionQuerySpace;
import org.hibernate.loader.plan.build.spi.ExpandingFetchSource;
import org.hibernate.loader.plan.spi.CollectionAttributeFetch;
import org.hibernate.loader.plan.spi.FetchSource;
import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
import org.hibernate.persister.walking.spi.AttributeDefinition;
import org.hibernate.type.CollectionType;

public class CollectionAttributeFetchImpl
extends AbstractCollectionReference
implements CollectionAttributeFetch {
    private final ExpandingFetchSource fetchSource;
    private final AttributeDefinition fetchedAttribute;
    private final FetchStrategy fetchStrategy;

    public CollectionAttributeFetchImpl(ExpandingFetchSource fetchSource, AssociationAttributeDefinition fetchedAttribute, FetchStrategy fetchStrategy, ExpandingCollectionQuerySpace collectionQuerySpace) {
        super(collectionQuerySpace, fetchSource.getPropertyPath().append(fetchedAttribute.getName()), QuerySpaceHelper.INSTANCE.shouldIncludeJoin(fetchStrategy));
        this.fetchSource = fetchSource;
        this.fetchedAttribute = fetchedAttribute;
        this.fetchStrategy = fetchStrategy;
    }

    @Override
    public FetchSource getSource() {
        return this.fetchSource;
    }

    @Override
    public CollectionType getFetchedType() {
        return (CollectionType)this.fetchedAttribute.getType();
    }

    @Override
    public boolean isNullable() {
        return true;
    }

    @Override
    public String getAdditionalJoinConditions() {
        return null;
    }

    @Override
    public FetchStrategy getFetchStrategy() {
        return this.fetchStrategy;
    }

    @Override
    public String[] toSqlSelectFragments(String alias) {
        return null;
    }

    @Override
    public AttributeDefinition getFetchedAttributeDefinition() {
        return this.fetchedAttribute;
    }
}

