/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.build.internal.returns;

import org.hibernate.loader.PropertyPath;
import org.hibernate.loader.plan.build.internal.returns.AbstractCompositeFetch;
import org.hibernate.loader.plan.build.spi.ExpandingCompositeQuerySpace;
import org.hibernate.loader.plan.build.spi.ExpandingEntityIdentifierDescription;
import org.hibernate.loader.plan.spi.EntityReference;
import org.hibernate.loader.plan.spi.FetchSource;
import org.hibernate.type.CompositeType;
import org.hibernate.type.Type;

public abstract class AbstractCompositeEntityIdentifierDescription
extends AbstractCompositeFetch
implements ExpandingEntityIdentifierDescription {
    private final EntityReference entityReference;
    private final CompositeType identifierType;

    protected AbstractCompositeEntityIdentifierDescription(EntityReference entityReference, ExpandingCompositeQuerySpace compositeQuerySpace, CompositeType identifierType, PropertyPath propertyPath) {
        super(compositeQuerySpace, false, propertyPath);
        this.entityReference = entityReference;
        this.identifierType = identifierType;
    }

    @Override
    public boolean hasFetches() {
        return this.getFetches().length > 0;
    }

    @Override
    public boolean hasBidirectionalEntityReferences() {
        return this.getBidirectionalEntityReferences().length > 0;
    }

    @Override
    public FetchSource getSource() {
        return this.entityReference;
    }

    @Override
    public Type getFetchedType() {
        return this.identifierType;
    }

    @Override
    public boolean isNullable() {
        return false;
    }

    @Override
    public EntityReference resolveEntityReference() {
        return this.entityReference;
    }
}

