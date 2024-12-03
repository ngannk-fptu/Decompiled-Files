/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.build.internal.returns;

import org.hibernate.loader.PropertyPath;
import org.hibernate.loader.plan.build.spi.ExpandingFetchSource;
import org.hibernate.loader.plan.spi.BidirectionalEntityReference;
import org.hibernate.loader.plan.spi.EntityIdentifierDescription;
import org.hibernate.loader.plan.spi.EntityReference;
import org.hibernate.loader.plan.spi.Fetch;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;

public class BidirectionalEntityReferenceImpl
implements BidirectionalEntityReference {
    private final EntityReference targetEntityReference;
    private final PropertyPath propertyPath;

    public BidirectionalEntityReferenceImpl(ExpandingFetchSource fetchSource, AssociationAttributeDefinition fetchedAttribute, EntityReference targetEntityReference) {
        this.targetEntityReference = targetEntityReference;
        this.propertyPath = fetchSource.getPropertyPath().append(fetchedAttribute.getName());
    }

    @Override
    public EntityReference getTargetEntityReference() {
        return this.targetEntityReference;
    }

    @Override
    public PropertyPath getPropertyPath() {
        return this.propertyPath;
    }

    @Override
    public String getQuerySpaceUid() {
        return this.targetEntityReference.getQuerySpaceUid();
    }

    @Override
    public Fetch[] getFetches() {
        return this.targetEntityReference.getFetches();
    }

    @Override
    public BidirectionalEntityReference[] getBidirectionalEntityReferences() {
        return this.targetEntityReference.getBidirectionalEntityReferences();
    }

    @Override
    public EntityReference resolveEntityReference() {
        return this;
    }

    @Override
    public EntityPersister getEntityPersister() {
        return this.targetEntityReference.getEntityPersister();
    }

    @Override
    public EntityIdentifierDescription getIdentifierDescription() {
        return this.targetEntityReference.getIdentifierDescription();
    }
}

