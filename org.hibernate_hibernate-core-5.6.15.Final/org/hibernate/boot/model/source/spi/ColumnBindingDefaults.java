/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.spi;

public interface ColumnBindingDefaults {
    public boolean areValuesIncludedInInsertByDefault();

    public boolean areValuesIncludedInUpdateByDefault();

    public boolean areValuesNullableByDefault();
}

