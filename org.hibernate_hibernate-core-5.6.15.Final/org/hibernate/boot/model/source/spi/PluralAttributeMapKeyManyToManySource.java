/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.spi;

import org.hibernate.boot.model.source.spi.PluralAttributeMapKeySource;
import org.hibernate.boot.model.source.spi.RelationalValueSourceContainer;

public interface PluralAttributeMapKeyManyToManySource
extends PluralAttributeMapKeySource,
RelationalValueSourceContainer {
    public String getReferencedEntityName();

    public String getExplicitForeignKeyName();
}

