/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.build.internal.returns;

import org.hibernate.engine.FetchStrategy;
import org.hibernate.loader.plan.build.internal.returns.AbstractEntityReference;
import org.hibernate.loader.plan.build.spi.ExpandingEntityQuerySpace;
import org.hibernate.loader.plan.spi.CollectionFetchableIndex;
import org.hibernate.loader.plan.spi.CollectionReference;
import org.hibernate.persister.walking.spi.AttributeDefinition;

public class CollectionFetchableIndexEntityGraph
extends AbstractEntityReference
implements CollectionFetchableIndex {
    private final CollectionReference collectionReference;

    public CollectionFetchableIndexEntityGraph(CollectionReference collectionReference, ExpandingEntityQuerySpace entityQuerySpace) {
        super(entityQuerySpace, collectionReference.getPropertyPath().append("<index>"));
        this.collectionReference = collectionReference;
    }

    @Override
    public CollectionReference getCollectionReference() {
        return this.collectionReference;
    }

    @Override
    public void validateFetchPlan(FetchStrategy fetchStrategy, AttributeDefinition attributeDefinition) {
    }
}

