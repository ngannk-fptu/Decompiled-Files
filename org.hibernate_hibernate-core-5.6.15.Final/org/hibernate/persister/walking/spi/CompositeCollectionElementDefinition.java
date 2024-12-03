/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.persister.walking.spi;

import org.hibernate.persister.walking.spi.CollectionDefinition;
import org.hibernate.persister.walking.spi.CompositionDefinition;

public interface CompositeCollectionElementDefinition
extends CompositionDefinition {
    public CollectionDefinition getCollectionDefinition();
}

