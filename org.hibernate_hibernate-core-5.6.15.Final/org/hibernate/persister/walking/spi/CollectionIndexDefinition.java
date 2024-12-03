/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.persister.walking.spi;

import org.hibernate.persister.walking.spi.AnyMappingDefinition;
import org.hibernate.persister.walking.spi.CollectionDefinition;
import org.hibernate.persister.walking.spi.CompositionDefinition;
import org.hibernate.persister.walking.spi.EntityDefinition;
import org.hibernate.type.Type;

public interface CollectionIndexDefinition {
    public CollectionDefinition getCollectionDefinition();

    public Type getType();

    public EntityDefinition toEntityDefinition();

    public CompositionDefinition toCompositeDefinition();

    public AnyMappingDefinition toAnyMappingDefinition();
}

