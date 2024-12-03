/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.persister.walking.spi;

import org.hibernate.persister.walking.spi.EntityDefinition;

public interface EntityIdentifierDefinition {
    public boolean isEncapsulated();

    public EntityDefinition getEntityDefinition();
}

