/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.build.internal.returns;

import org.hibernate.loader.PropertyPath;
import org.hibernate.loader.plan.build.internal.returns.AbstractExpandingFetchSource;
import org.hibernate.loader.plan.build.internal.returns.CompositeAttributeFetchImpl;
import org.hibernate.loader.plan.build.internal.returns.EncapsulatedEntityIdentifierDescription;
import org.hibernate.loader.plan.build.internal.returns.NonEncapsulatedEntityIdentifierDescription;
import org.hibernate.loader.plan.build.internal.returns.SimpleEntityIdentifierDescriptionImpl;
import org.hibernate.loader.plan.build.spi.ExpandingCompositeQuerySpace;
import org.hibernate.loader.plan.build.spi.ExpandingEntityQuerySpace;
import org.hibernate.loader.plan.spi.CompositeAttributeFetch;
import org.hibernate.loader.plan.spi.EntityIdentifierDescription;
import org.hibernate.loader.plan.spi.EntityReference;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.walking.spi.AttributeDefinition;
import org.hibernate.persister.walking.spi.EncapsulatedEntityIdentifierDefinition;
import org.hibernate.persister.walking.spi.EntityIdentifierDefinition;
import org.hibernate.type.CompositeType;
import org.hibernate.type.Type;

public abstract class AbstractEntityReference
extends AbstractExpandingFetchSource
implements EntityReference {
    private final EntityIdentifierDescription identifierDescription = this.buildIdentifierDescription();

    public AbstractEntityReference(ExpandingEntityQuerySpace entityQuerySpace, PropertyPath propertyPath) {
        super(entityQuerySpace, propertyPath);
    }

    private ExpandingEntityQuerySpace expandingEntityQuerySpace() {
        return (ExpandingEntityQuerySpace)this.expandingQuerySpace();
    }

    private EntityIdentifierDescription buildIdentifierDescription() {
        EncapsulatedEntityIdentifierDefinition encapsulatedIdentifierDefinition;
        Type idAttributeType;
        EntityIdentifierDefinition identifierDefinition = this.getEntityPersister().getEntityKeyDefinition();
        if (identifierDefinition.isEncapsulated() && !CompositeType.class.isInstance(idAttributeType = (encapsulatedIdentifierDefinition = (EncapsulatedEntityIdentifierDefinition)identifierDefinition).getAttributeDefinition().getType())) {
            return new SimpleEntityIdentifierDescriptionImpl();
        }
        ExpandingCompositeQuerySpace querySpace = this.expandingEntityQuerySpace().makeCompositeIdentifierQuerySpace();
        return identifierDefinition.isEncapsulated() ? this.buildEncapsulatedCompositeIdentifierDescription(querySpace) : this.buildNonEncapsulatedCompositeIdentifierDescription(querySpace);
    }

    private NonEncapsulatedEntityIdentifierDescription buildNonEncapsulatedCompositeIdentifierDescription(ExpandingCompositeQuerySpace compositeQuerySpace) {
        return new NonEncapsulatedEntityIdentifierDescription(this, compositeQuerySpace, (CompositeType)this.getEntityPersister().getIdentifierType(), this.getPropertyPath().append("id"));
    }

    private EncapsulatedEntityIdentifierDescription buildEncapsulatedCompositeIdentifierDescription(ExpandingCompositeQuerySpace compositeQuerySpace) {
        return new EncapsulatedEntityIdentifierDescription(this, compositeQuerySpace, (CompositeType)this.getEntityPersister().getIdentifierType(), this.getPropertyPath().append("id"));
    }

    @Override
    public EntityReference resolveEntityReference() {
        return this;
    }

    @Override
    public EntityPersister getEntityPersister() {
        return this.expandingEntityQuerySpace().getEntityPersister();
    }

    @Override
    public EntityIdentifierDescription getIdentifierDescription() {
        return this.identifierDescription;
    }

    @Override
    protected CompositeAttributeFetch createCompositeAttributeFetch(AttributeDefinition attributeDefinition, ExpandingCompositeQuerySpace compositeQuerySpace) {
        return new CompositeAttributeFetchImpl(this, attributeDefinition, compositeQuerySpace, true);
    }
}

