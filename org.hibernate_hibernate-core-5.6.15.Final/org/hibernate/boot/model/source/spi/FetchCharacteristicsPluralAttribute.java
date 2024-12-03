/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.spi;

import org.hibernate.boot.model.source.spi.FetchCharacteristics;

public interface FetchCharacteristicsPluralAttribute
extends FetchCharacteristics {
    public Integer getBatchSize();

    public boolean isExtraLazy();
}

