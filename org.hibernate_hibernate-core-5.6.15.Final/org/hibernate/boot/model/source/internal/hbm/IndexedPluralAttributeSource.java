/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.internal.hbm;

import org.hibernate.boot.model.source.spi.PluralAttributeIndexSource;
import org.hibernate.boot.model.source.spi.PluralAttributeSource;

public interface IndexedPluralAttributeSource
extends PluralAttributeSource {
    public PluralAttributeIndexSource getIndexSource();
}

