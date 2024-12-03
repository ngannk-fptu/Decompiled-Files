/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.build.internal.returns;

import org.hibernate.engine.FetchStrategy;
import org.hibernate.loader.plan.build.internal.returns.AbstractEntityReference;
import org.hibernate.loader.plan.build.spi.ExpandingEntityQuerySpace;
import org.hibernate.loader.plan.build.spi.ExpandingFetchSource;
import org.hibernate.loader.plan.spi.EntityFetch;
import org.hibernate.loader.plan.spi.FetchSource;
import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
import org.hibernate.persister.walking.spi.AttributeDefinition;
import org.hibernate.type.EntityType;

public class EntityAttributeFetchImpl
extends AbstractEntityReference
implements EntityFetch {
    private final FetchSource fetchSource;
    private final AttributeDefinition fetchedAttribute;
    private final FetchStrategy fetchStrategy;

    public EntityAttributeFetchImpl(ExpandingFetchSource fetchSource, AssociationAttributeDefinition fetchedAttribute, FetchStrategy fetchStrategy, ExpandingEntityQuerySpace entityQuerySpace) {
        super(entityQuerySpace, fetchSource.getPropertyPath().append(fetchedAttribute.getName()));
        this.fetchSource = fetchSource;
        this.fetchedAttribute = fetchedAttribute;
        this.fetchStrategy = fetchStrategy;
    }

    @Override
    public FetchSource getSource() {
        return this.fetchSource;
    }

    @Override
    public FetchStrategy getFetchStrategy() {
        return this.fetchStrategy;
    }

    @Override
    public EntityType getFetchedType() {
        return (EntityType)this.fetchedAttribute.getType();
    }

    @Override
    public boolean isNullable() {
        return this.fetchedAttribute.isNullable();
    }

    @Override
    public String getAdditionalJoinConditions() {
        return null;
    }

    @Override
    public String[] toSqlSelectFragments(String alias) {
        return new String[0];
    }

    @Override
    public void validateFetchPlan(FetchStrategy fetchStrategy, AttributeDefinition attributeDefinition) {
    }

    @Override
    public AttributeDefinition getFetchedAttributeDefinition() {
        return this.fetchedAttribute;
    }
}

