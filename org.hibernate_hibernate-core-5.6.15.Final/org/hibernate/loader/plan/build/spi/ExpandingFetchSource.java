/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.build.spi;

import org.hibernate.engine.FetchStrategy;
import org.hibernate.loader.plan.spi.AnyAttributeFetch;
import org.hibernate.loader.plan.spi.BidirectionalEntityReference;
import org.hibernate.loader.plan.spi.CollectionAttributeFetch;
import org.hibernate.loader.plan.spi.CompositeAttributeFetch;
import org.hibernate.loader.plan.spi.EntityFetch;
import org.hibernate.loader.plan.spi.EntityReference;
import org.hibernate.loader.plan.spi.FetchSource;
import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
import org.hibernate.persister.walking.spi.AttributeDefinition;

public interface ExpandingFetchSource
extends FetchSource {
    public void validateFetchPlan(FetchStrategy var1, AttributeDefinition var2);

    public EntityFetch buildEntityAttributeFetch(AssociationAttributeDefinition var1, FetchStrategy var2);

    public BidirectionalEntityReference buildBidirectionalEntityReference(AssociationAttributeDefinition var1, FetchStrategy var2, EntityReference var3);

    public CompositeAttributeFetch buildCompositeAttributeFetch(AttributeDefinition var1);

    public CollectionAttributeFetch buildCollectionAttributeFetch(AssociationAttributeDefinition var1, FetchStrategy var2);

    public AnyAttributeFetch buildAnyAttributeFetch(AssociationAttributeDefinition var1, FetchStrategy var2);
}

