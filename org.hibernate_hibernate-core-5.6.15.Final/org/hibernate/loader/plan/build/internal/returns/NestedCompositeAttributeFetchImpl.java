/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.build.internal.returns;

import org.hibernate.loader.plan.build.internal.returns.AbstractCompositeFetch;
import org.hibernate.loader.plan.build.spi.ExpandingCompositeQuerySpace;
import org.hibernate.loader.plan.spi.CompositeAttributeFetch;
import org.hibernate.loader.plan.spi.EntityReference;
import org.hibernate.loader.plan.spi.FetchSource;
import org.hibernate.persister.walking.spi.AttributeDefinition;
import org.hibernate.type.Type;

public class NestedCompositeAttributeFetchImpl
extends AbstractCompositeFetch
implements CompositeAttributeFetch {
    private final FetchSource source;
    private final AttributeDefinition fetchedAttributeDefinition;

    public NestedCompositeAttributeFetchImpl(FetchSource source, AttributeDefinition fetchedAttributeDefinition, ExpandingCompositeQuerySpace compositeQuerySpace, boolean allowCollectionFetches) {
        super(compositeQuerySpace, allowCollectionFetches, source.getPropertyPath().append(fetchedAttributeDefinition.getName()));
        this.source = source;
        this.fetchedAttributeDefinition = fetchedAttributeDefinition;
    }

    @Override
    public FetchSource getSource() {
        return this.source;
    }

    @Override
    public Type getFetchedType() {
        return this.fetchedAttributeDefinition.getType();
    }

    @Override
    public boolean isNullable() {
        return this.fetchedAttributeDefinition.isNullable();
    }

    @Override
    public AttributeDefinition getFetchedAttributeDefinition() {
        return this.fetchedAttributeDefinition;
    }

    @Override
    public EntityReference resolveEntityReference() {
        return this.source.resolveEntityReference();
    }
}

