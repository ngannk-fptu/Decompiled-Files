/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.build.internal.returns;

import org.hibernate.engine.FetchStrategy;
import org.hibernate.loader.PropertyPath;
import org.hibernate.loader.plan.build.internal.returns.AbstractExpandingFetchSource;
import org.hibernate.loader.plan.build.internal.returns.NestedCompositeAttributeFetchImpl;
import org.hibernate.loader.plan.build.spi.ExpandingCompositeQuerySpace;
import org.hibernate.loader.plan.spi.CollectionAttributeFetch;
import org.hibernate.loader.plan.spi.CompositeAttributeFetch;
import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
import org.hibernate.persister.walking.spi.AttributeDefinition;
import org.hibernate.persister.walking.spi.WalkingException;

public abstract class AbstractCompositeReference
extends AbstractExpandingFetchSource {
    private final boolean allowCollectionFetches;

    protected AbstractCompositeReference(ExpandingCompositeQuerySpace compositeQuerySpace, boolean allowCollectionFetches, PropertyPath propertyPath) {
        super(compositeQuerySpace, propertyPath);
        this.allowCollectionFetches = allowCollectionFetches;
    }

    @Override
    public void validateFetchPlan(FetchStrategy fetchStrategy, AttributeDefinition attributeDefinition) {
    }

    @Override
    protected CompositeAttributeFetch createCompositeAttributeFetch(AttributeDefinition attributeDefinition, ExpandingCompositeQuerySpace compositeQuerySpace) {
        return new NestedCompositeAttributeFetchImpl(this, attributeDefinition, compositeQuerySpace, this.allowCollectionFetches);
    }

    @Override
    public CollectionAttributeFetch buildCollectionAttributeFetch(AssociationAttributeDefinition attributeDefinition, FetchStrategy fetchStrategy) {
        if (!this.allowCollectionFetches) {
            throw new WalkingException(String.format("This composite path [%s] does not allow collection fetches (composite id or composite collection index/element", this.getPropertyPath().getFullPath()));
        }
        return super.buildCollectionAttributeFetch(attributeDefinition, fetchStrategy);
    }
}

