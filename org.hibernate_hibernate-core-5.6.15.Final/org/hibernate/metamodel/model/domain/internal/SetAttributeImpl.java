/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.metamodel.PluralAttribute$CollectionType
 */
package org.hibernate.metamodel.model.domain.internal;

import java.util.Set;
import javax.persistence.metamodel.PluralAttribute;
import org.hibernate.metamodel.model.domain.internal.AbstractPluralAttribute;
import org.hibernate.metamodel.model.domain.internal.PluralAttributeBuilder;
import org.hibernate.metamodel.model.domain.spi.SetPersistentAttribute;

public class SetAttributeImpl<X, E>
extends AbstractPluralAttribute<X, Set<E>, E>
implements SetPersistentAttribute<X, E> {
    public SetAttributeImpl(PluralAttributeBuilder<X, Set<E>, E, ?> xceBuilder) {
        super(xceBuilder);
    }

    public PluralAttribute.CollectionType getCollectionType() {
        return PluralAttribute.CollectionType.SET;
    }
}

