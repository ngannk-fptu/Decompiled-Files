/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.spi;

import org.hibernate.boot.model.source.spi.PluralAttributeIndexSource;
import org.hibernate.boot.model.source.spi.RelationalValueSourceContainer;

public interface PluralAttributeSequentialIndexSource
extends PluralAttributeIndexSource,
RelationalValueSourceContainer {
    public int getBase();
}

