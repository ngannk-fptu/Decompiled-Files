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

public class CompositeAttributeFetchImpl
extends AbstractCompositeFetch
implements CompositeAttributeFetch {
    private final FetchSource source;
    private final AttributeDefinition fetchedAttribute;

    protected CompositeAttributeFetchImpl(FetchSource source, AttributeDefinition attributeDefinition, ExpandingCompositeQuerySpace compositeQuerySpace, boolean allowCollectionFetches) {
        super(compositeQuerySpace, allowCollectionFetches, source.getPropertyPath().append(attributeDefinition.getName()));
        this.source = source;
        this.fetchedAttribute = attributeDefinition;
    }

    @Override
    public FetchSource getSource() {
        return this.source;
    }

    @Override
    public AttributeDefinition getFetchedAttributeDefinition() {
        return this.fetchedAttribute;
    }

    @Override
    public Type getFetchedType() {
        return this.fetchedAttribute.getType();
    }

    @Override
    public boolean isNullable() {
        return this.fetchedAttribute.isNullable();
    }

    @Override
    public EntityReference resolveEntityReference() {
        return this.source.resolveEntityReference();
    }
}

