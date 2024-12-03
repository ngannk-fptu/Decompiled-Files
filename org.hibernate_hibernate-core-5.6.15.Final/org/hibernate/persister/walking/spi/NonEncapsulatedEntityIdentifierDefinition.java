/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.persister.walking.spi;

import org.hibernate.persister.walking.spi.CompositionDefinition;
import org.hibernate.persister.walking.spi.EntityIdentifierDefinition;
import org.hibernate.type.Type;

public interface NonEncapsulatedEntityIdentifierDefinition
extends EntityIdentifierDefinition,
CompositionDefinition {
    public Type getCompositeType();

    public Class getSeparateIdentifierMappingClass();
}

