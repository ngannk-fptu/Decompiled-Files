/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.metamodel.PluralAttribute$CollectionType
 */
package org.hibernate.metamodel.model.domain.internal;

import java.util.Map;
import javax.persistence.metamodel.PluralAttribute;
import org.hibernate.metamodel.model.domain.internal.AbstractPluralAttribute;
import org.hibernate.metamodel.model.domain.internal.PluralAttributeBuilder;
import org.hibernate.metamodel.model.domain.spi.MapPersistentAttribute;
import org.hibernate.metamodel.model.domain.spi.SimpleTypeDescriptor;

class MapAttributeImpl<X, K, V>
extends AbstractPluralAttribute<X, Map<K, V>, V>
implements MapPersistentAttribute<X, K, V> {
    private final SimpleTypeDescriptor<K> keyType;

    MapAttributeImpl(PluralAttributeBuilder<X, Map<K, V>, V, K> xceBuilder) {
        super(xceBuilder);
        this.keyType = xceBuilder.getKeyType();
    }

    public PluralAttribute.CollectionType getCollectionType() {
        return PluralAttribute.CollectionType.MAP;
    }

    public Class<K> getKeyJavaType() {
        return this.keyType.getJavaType();
    }

    @Override
    public SimpleTypeDescriptor<K> getKeyType() {
        return this.keyType;
    }

    @Override
    public SimpleTypeDescriptor<K> getKeyGraphType() {
        return this.getKeyType();
    }
}

