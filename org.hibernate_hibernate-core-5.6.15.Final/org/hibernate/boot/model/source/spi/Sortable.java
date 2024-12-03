/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.spi;

public interface Sortable {
    public boolean isSorted();

    public String getComparatorName();
}

