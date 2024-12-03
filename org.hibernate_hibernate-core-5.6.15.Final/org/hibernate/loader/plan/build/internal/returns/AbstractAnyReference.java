/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.build.internal.returns;

import org.hibernate.loader.PropertyPath;
import org.hibernate.loader.plan.spi.BidirectionalEntityReference;
import org.hibernate.loader.plan.spi.Fetch;
import org.hibernate.loader.plan.spi.FetchSource;

public abstract class AbstractAnyReference
implements FetchSource {
    private static final Fetch[] NO_FETCHES = new Fetch[0];
    private static final BidirectionalEntityReference[] NO_BIDIRECTIONAL_ENTITY_REFERENCES = new BidirectionalEntityReference[0];
    private final PropertyPath propertyPath;

    public AbstractAnyReference(PropertyPath propertyPath) {
        this.propertyPath = propertyPath;
    }

    @Override
    public PropertyPath getPropertyPath() {
        return this.propertyPath;
    }

    @Override
    public Fetch[] getFetches() {
        return NO_FETCHES;
    }

    @Override
    public BidirectionalEntityReference[] getBidirectionalEntityReferences() {
        return NO_BIDIRECTIONAL_ENTITY_REFERENCES;
    }

    @Override
    public String getQuerySpaceUid() {
        return null;
    }
}

