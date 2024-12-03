/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.build.internal.returns;

import java.util.ArrayList;
import java.util.List;
import org.hibernate.engine.FetchStrategy;
import org.hibernate.loader.PropertyPath;
import org.hibernate.loader.plan.build.internal.returns.AnyAttributeFetchImpl;
import org.hibernate.loader.plan.build.internal.returns.BidirectionalEntityReferenceImpl;
import org.hibernate.loader.plan.build.internal.returns.CollectionAttributeFetchImpl;
import org.hibernate.loader.plan.build.internal.returns.EntityAttributeFetchImpl;
import org.hibernate.loader.plan.build.internal.spaces.QuerySpaceHelper;
import org.hibernate.loader.plan.build.spi.ExpandingCollectionQuerySpace;
import org.hibernate.loader.plan.build.spi.ExpandingCompositeQuerySpace;
import org.hibernate.loader.plan.build.spi.ExpandingEntityQuerySpace;
import org.hibernate.loader.plan.build.spi.ExpandingFetchSource;
import org.hibernate.loader.plan.build.spi.ExpandingQuerySpace;
import org.hibernate.loader.plan.build.spi.ExpandingQuerySpaces;
import org.hibernate.loader.plan.spi.AnyAttributeFetch;
import org.hibernate.loader.plan.spi.BidirectionalEntityReference;
import org.hibernate.loader.plan.spi.CollectionAttributeFetch;
import org.hibernate.loader.plan.spi.CompositeAttributeFetch;
import org.hibernate.loader.plan.spi.EntityFetch;
import org.hibernate.loader.plan.spi.EntityReference;
import org.hibernate.loader.plan.spi.Fetch;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
import org.hibernate.persister.walking.spi.AttributeDefinition;
import org.hibernate.persister.walking.spi.WalkingException;
import org.hibernate.type.EntityType;

public abstract class AbstractExpandingFetchSource
implements ExpandingFetchSource {
    private static final Fetch[] NO_FETCHES = new Fetch[0];
    private static final BidirectionalEntityReference[] NO_BIDIRECTIONAL_ENTITY_REFERENCES = new BidirectionalEntityReference[0];
    private final ExpandingQuerySpace querySpace;
    private final PropertyPath propertyPath;
    private List<Fetch> fetches;
    private List<BidirectionalEntityReference> bidirectionalEntityReferences;

    public AbstractExpandingFetchSource(ExpandingQuerySpace querySpace, PropertyPath propertyPath) {
        this.querySpace = querySpace;
        this.propertyPath = propertyPath;
    }

    @Override
    public final String getQuerySpaceUid() {
        return this.querySpace.getUid();
    }

    protected final ExpandingQuerySpace expandingQuerySpace() {
        return this.querySpace;
    }

    @Override
    public final PropertyPath getPropertyPath() {
        return this.propertyPath;
    }

    @Override
    public Fetch[] getFetches() {
        return this.fetches == null ? NO_FETCHES : this.fetches.toArray(new Fetch[this.fetches.size()]);
    }

    private void addFetch(Fetch fetch) {
        if (this.fetches == null) {
            this.fetches = new ArrayList<Fetch>();
        }
        this.fetches.add(fetch);
    }

    @Override
    public BidirectionalEntityReference[] getBidirectionalEntityReferences() {
        return this.bidirectionalEntityReferences == null ? NO_BIDIRECTIONAL_ENTITY_REFERENCES : this.bidirectionalEntityReferences.toArray(new BidirectionalEntityReference[this.bidirectionalEntityReferences.size()]);
    }

    private void addBidirectionalEntityReference(BidirectionalEntityReference bidirectionalEntityReference) {
        if (this.bidirectionalEntityReferences == null) {
            this.bidirectionalEntityReferences = new ArrayList<BidirectionalEntityReference>();
        }
        this.bidirectionalEntityReferences.add(bidirectionalEntityReference);
    }

    @Override
    public EntityFetch buildEntityAttributeFetch(AssociationAttributeDefinition attributeDefinition, FetchStrategy fetchStrategy) {
        ExpandingEntityQuerySpace entityQuerySpace = QuerySpaceHelper.INSTANCE.makeEntityQuerySpace(this.expandingQuerySpace(), attributeDefinition, this.getQuerySpaces().generateImplicitUid(), fetchStrategy);
        EntityAttributeFetchImpl fetch = new EntityAttributeFetchImpl(this, attributeDefinition, fetchStrategy, entityQuerySpace);
        this.addFetch(fetch);
        return fetch;
    }

    @Override
    public BidirectionalEntityReference buildBidirectionalEntityReference(AssociationAttributeDefinition attributeDefinition, FetchStrategy fetchStrategy, EntityReference targetEntityReference) {
        EntityType fetchedType = (EntityType)attributeDefinition.getType();
        EntityPersister fetchedPersister = attributeDefinition.toEntityDefinition().getEntityPersister();
        if (fetchedPersister == null) {
            throw new WalkingException(String.format("Unable to locate EntityPersister [%s] for bidirectional entity reference [%s]", fetchedType.getAssociatedEntityName(), attributeDefinition.getName()));
        }
        BidirectionalEntityReferenceImpl bidirectionalEntityReference = new BidirectionalEntityReferenceImpl(this, attributeDefinition, targetEntityReference);
        this.addBidirectionalEntityReference(bidirectionalEntityReference);
        return bidirectionalEntityReference;
    }

    protected abstract CompositeAttributeFetch createCompositeAttributeFetch(AttributeDefinition var1, ExpandingCompositeQuerySpace var2);

    protected ExpandingQuerySpaces getQuerySpaces() {
        return this.querySpace.getExpandingQuerySpaces();
    }

    @Override
    public CompositeAttributeFetch buildCompositeAttributeFetch(AttributeDefinition attributeDefinition) {
        ExpandingCompositeQuerySpace compositeQuerySpace = QuerySpaceHelper.INSTANCE.makeCompositeQuerySpace(this.expandingQuerySpace(), attributeDefinition, this.getQuerySpaces().generateImplicitUid(), true);
        CompositeAttributeFetch fetch = this.createCompositeAttributeFetch(attributeDefinition, compositeQuerySpace);
        this.addFetch(fetch);
        return fetch;
    }

    @Override
    public CollectionAttributeFetch buildCollectionAttributeFetch(AssociationAttributeDefinition attributeDefinition, FetchStrategy fetchStrategy) {
        ExpandingCollectionQuerySpace collectionQuerySpace = QuerySpaceHelper.INSTANCE.makeCollectionQuerySpace(this.querySpace, attributeDefinition, this.getQuerySpaces().generateImplicitUid(), fetchStrategy);
        CollectionAttributeFetchImpl fetch = new CollectionAttributeFetchImpl(this, attributeDefinition, fetchStrategy, collectionQuerySpace);
        this.addFetch(fetch);
        return fetch;
    }

    @Override
    public AnyAttributeFetch buildAnyAttributeFetch(AssociationAttributeDefinition attributeDefinition, FetchStrategy fetchStrategy) {
        AnyAttributeFetchImpl fetch = new AnyAttributeFetchImpl(this, attributeDefinition, fetchStrategy);
        this.addFetch(fetch);
        return fetch;
    }
}

