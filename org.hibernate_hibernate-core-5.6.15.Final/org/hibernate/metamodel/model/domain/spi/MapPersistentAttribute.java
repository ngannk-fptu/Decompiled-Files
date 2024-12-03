/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.metamodel.MapAttribute
 */
package org.hibernate.metamodel.model.domain.spi;

import java.util.Map;
import javax.persistence.metamodel.MapAttribute;
import org.hibernate.metamodel.model.domain.spi.PluralPersistentAttribute;
import org.hibernate.metamodel.model.domain.spi.SimpleTypeDescriptor;

public interface MapPersistentAttribute<D, K, V>
extends MapAttribute<D, K, V>,
PluralPersistentAttribute<D, Map<K, V>, V> {
    public SimpleTypeDescriptor<K> getKeyType();
}

