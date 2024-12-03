/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.persister.walking.spi;

import org.hibernate.persister.walking.spi.AnyMappingDefinition;
import org.hibernate.persister.walking.spi.CollectionDefinition;
import org.hibernate.persister.walking.spi.CompositeCollectionElementDefinition;
import org.hibernate.persister.walking.spi.EntityDefinition;
import org.hibernate.type.Type;

public interface CollectionElementDefinition {
    public CollectionDefinition getCollectionDefinition();

    public Type getType();

    public AnyMappingDefinition toAnyMappingDefinition();

    public EntityDefinition toEntityDefinition();

    public CompositeCollectionElementDefinition toCompositeElementDefinition();
}

