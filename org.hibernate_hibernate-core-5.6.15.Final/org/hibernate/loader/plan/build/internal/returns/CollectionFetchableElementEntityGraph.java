/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.build.internal.returns;

import org.hibernate.engine.FetchStrategy;
import org.hibernate.loader.plan.build.internal.returns.AbstractEntityReference;
import org.hibernate.loader.plan.build.spi.ExpandingEntityQuerySpace;
import org.hibernate.loader.plan.spi.CollectionFetchableElement;
import org.hibernate.loader.plan.spi.CollectionReference;
import org.hibernate.persister.walking.spi.AttributeDefinition;

public class CollectionFetchableElementEntityGraph
extends AbstractEntityReference
implements CollectionFetchableElement {
    private final CollectionReference collectionReference;

    public CollectionFetchableElementEntityGraph(CollectionReference collectionReference, ExpandingEntityQuerySpace entityQuerySpace) {
        super(entityQuerySpace, collectionReference.getPropertyPath().append("<elements>"));
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

