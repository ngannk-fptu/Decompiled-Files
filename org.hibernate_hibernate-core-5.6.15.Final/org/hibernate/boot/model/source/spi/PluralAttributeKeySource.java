/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.spi;

import org.hibernate.boot.model.source.spi.ForeignKeyContributingSource;
import org.hibernate.boot.model.source.spi.RelationalValueSourceContainer;

public interface PluralAttributeKeySource
extends ForeignKeyContributingSource,
RelationalValueSourceContainer {
    public String getReferencedPropertyName();

    @Override
    public boolean isCascadeDeleteEnabled();
}

