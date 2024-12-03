/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.spi;

import org.hibernate.boot.model.source.internal.hbm.IndexedPluralAttributeSource;

public interface PluralAttributeSourceArray
extends IndexedPluralAttributeSource {
    public String getElementClass();
}

