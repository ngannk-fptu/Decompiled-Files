/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.persister.walking.spi;

import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.walking.spi.AttributeSource;
import org.hibernate.persister.walking.spi.EntityIdentifierDefinition;

public interface EntityDefinition
extends AttributeSource {
    public EntityPersister getEntityPersister();

    public EntityIdentifierDefinition getEntityKeyDefinition();
}

