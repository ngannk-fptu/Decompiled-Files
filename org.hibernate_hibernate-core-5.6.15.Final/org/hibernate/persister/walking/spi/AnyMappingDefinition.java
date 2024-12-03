/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.persister.walking.spi;

import org.hibernate.type.AnyType;
import org.hibernate.type.Type;

public interface AnyMappingDefinition {
    public AnyType getType();

    public boolean isLazy();

    public Type getIdentifierType();

    public Type getDiscriminatorType();

    public Iterable<DiscriminatorMapping> getMappingDefinedDiscriminatorMappings();

    public static interface DiscriminatorMapping {
        public Object getDiscriminatorValue();

        public String getEntityName();
    }
}

