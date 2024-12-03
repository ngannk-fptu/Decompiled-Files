/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.build.internal.returns;

import org.hibernate.loader.plan.build.internal.returns.AbstractAnyReference;
import org.hibernate.loader.plan.spi.CollectionFetchableElement;
import org.hibernate.loader.plan.spi.CollectionReference;
import org.hibernate.loader.plan.spi.EntityReference;
import org.hibernate.loader.plan.spi.Fetch;

public class CollectionFetchableElementAnyGraph
extends AbstractAnyReference
implements CollectionFetchableElement {
    private final CollectionReference collectionReference;

    public CollectionFetchableElementAnyGraph(CollectionReference collectionReference) {
        super(collectionReference.getPropertyPath().append("<element>"));
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

