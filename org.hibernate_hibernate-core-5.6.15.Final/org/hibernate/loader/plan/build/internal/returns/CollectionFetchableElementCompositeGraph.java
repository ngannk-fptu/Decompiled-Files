/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.build.internal.returns;

import org.hibernate.loader.plan.build.internal.returns.AbstractCompositeReference;
import org.hibernate.loader.plan.build.spi.ExpandingCompositeQuerySpace;
import org.hibernate.loader.plan.spi.CollectionFetchableElement;
import org.hibernate.loader.plan.spi.CollectionReference;
import org.hibernate.loader.plan.spi.EntityReference;
import org.hibernate.loader.plan.spi.Fetch;

public class CollectionFetchableElementCompositeGraph
extends AbstractCompositeReference
implements CollectionFetchableElement {
    private final CollectionReference collectionReference;

    public CollectionFetchableElementCompositeGraph(CollectionReference collectionReference, ExpandingCompositeQuerySpace compositeQuerySpace) {
        super(compositeQuerySpace, false, collectionReference.getPropertyPath().append("<element>"));
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

