/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.metamodel.PluralAttribute$CollectionType
 */
package org.hibernate.metamodel.model.domain.internal;

import java.util.Collection;
import javax.persistence.metamodel.PluralAttribute;
import org.hibernate.metamodel.model.domain.internal.AbstractPluralAttribute;
import org.hibernate.metamodel.model.domain.internal.PluralAttributeBuilder;
import org.hibernate.metamodel.model.domain.spi.BagPersistentAttribute;

class BagAttributeImpl<X, E>
extends AbstractPluralAttribute<X, Collection<E>, E>
implements BagPersistentAttribute<X, E> {
    BagAttributeImpl(PluralAttributeBuilder<X, Collection<E>, E, ?> xceBuilder) {
        super(xceBuilder);
    }

    public PluralAttribute.CollectionType getCollectionType() {
        return PluralAttribute.CollectionType.COLLECTION;
    }
}

