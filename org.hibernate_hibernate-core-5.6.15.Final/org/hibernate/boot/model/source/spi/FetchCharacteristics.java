/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.spi;

import org.hibernate.engine.FetchStyle;
import org.hibernate.engine.FetchTiming;

public interface FetchCharacteristics {
    public FetchTiming getFetchTiming();

    public FetchStyle getFetchStyle();
}

