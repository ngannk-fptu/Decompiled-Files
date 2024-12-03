/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.spi;

import org.hibernate.boot.model.source.spi.PluralAttributeIndexSource;

public interface PluralAttributeMapKeySource
extends PluralAttributeIndexSource {
    public Nature getMapKeyNature();

    public boolean isReferencedEntityAttribute();

    public static enum Nature {
        BASIC,
        EMBEDDED,
        MANY_TO_MANY,
        ANY;

    }
}

