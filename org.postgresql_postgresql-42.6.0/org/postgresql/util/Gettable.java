/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package org.postgresql.util;

import org.checkerframework.checker.nullness.qual.Nullable;

public interface Gettable<K, V> {
    public @Nullable V get(K var1);
}

