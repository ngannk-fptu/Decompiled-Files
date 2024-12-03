/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.metamodel.PluralAttribute$CollectionType
 */
package org.hibernate.metamodel.model.domain.internal;

import java.util.List;
import javax.persistence.metamodel.PluralAttribute;
import org.hibernate.metamodel.model.domain.internal.AbstractPluralAttribute;
import org.hibernate.metamodel.model.domain.internal.PluralAttributeBuilder;
import org.hibernate.metamodel.model.domain.spi.ListPersistentAttribute;

class ListAttributeImpl<X, E>
extends AbstractPluralAttribute<X, List<E>, E>
implements ListPersistentAttribute<X, E> {
    ListAttributeImpl(PluralAttributeBuilder<X, List<E>, E, ?> xceBuilder) {
        super(xceBuilder);
    }

    public PluralAttribute.CollectionType getCollectionType() {
        return PluralAttribute.CollectionType.LIST;
    }
}

