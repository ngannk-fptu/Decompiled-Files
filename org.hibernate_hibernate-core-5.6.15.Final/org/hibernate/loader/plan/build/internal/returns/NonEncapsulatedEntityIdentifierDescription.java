/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.build.internal.returns;

import org.hibernate.loader.PropertyPath;
import org.hibernate.loader.plan.build.internal.returns.AbstractCompositeEntityIdentifierDescription;
import org.hibernate.loader.plan.build.spi.ExpandingCompositeQuerySpace;
import org.hibernate.loader.plan.spi.EntityReference;
import org.hibernate.type.CompositeType;

public class NonEncapsulatedEntityIdentifierDescription
extends AbstractCompositeEntityIdentifierDescription {
    public NonEncapsulatedEntityIdentifierDescription(EntityReference entityReference, ExpandingCompositeQuerySpace compositeQuerySpace, CompositeType compositeType, PropertyPath propertyPath) {
        super(entityReference, compositeQuerySpace, compositeType, propertyPath);
    }
}

