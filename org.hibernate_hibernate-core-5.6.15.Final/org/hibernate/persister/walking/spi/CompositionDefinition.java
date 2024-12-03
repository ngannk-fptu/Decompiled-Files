/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.persister.walking.spi;

import org.hibernate.persister.walking.spi.AttributeDefinition;
import org.hibernate.persister.walking.spi.AttributeSource;
import org.hibernate.type.CompositeType;

public interface CompositionDefinition
extends AttributeDefinition,
AttributeSource {
    @Override
    public CompositeType getType();
}

