/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.build.internal.returns;

import org.hibernate.loader.plan.build.internal.returns.AbstractAnyReference;
import org.hibernate.loader.plan.spi.CollectionFetchableIndex;
import org.hibernate.loader.plan.spi.CollectionReference;
import org.hibernate.loader.plan.spi.EntityReference;
import org.hibernate.loader.plan.spi.Fetch;

public class CollectionFetchableIndexAnyGraph
extends AbstractAnyReference
implements CollectionFetchableIndex {
    private final CollectionReference collectionReference;

    public CollectionFetchableIndexAnyGraph(CollectionReference collectionReference) {
        super(collectionReference.getPropertyPath().append("<index>"));
        this.collectionReference = collectionReference;
    }

    @Override
    public CollectionReference getCollectionReference() {
        return this.collectionReference;
    }

    @Override
    public EntityReference resolveEntityReference() {
        return Fetch.class.isInstance(this.collectionReference) ? ((Fetch)Fetch.class.cast(this.collectionReference)).getSource().resolveEntityReference() : null;
    }
}

