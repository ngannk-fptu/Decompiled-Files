/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 *  javax.annotation.CheckForNull
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.ElementTypesAreNonnullByDefault;
import com.google.common.collect.Multimap;
import com.google.common.collect.ParametricNullness;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public interface ListMultimap<K, V>
extends Multimap<K, V> {
    @Override
    public List<V> get(@ParametricNullness K var1);

    @Override
    @CanIgnoreReturnValue
    public List<V> removeAll(@CheckForNull Object var1);

    @Override
    @CanIgnoreReturnValue
    public List<V> replaceValues(@ParametricNullness K var1, Iterable<? extends V> var2);

    @Override
    public Map<K, Collection<V>> asMap();

    @Override
    public boolean equals(@CheckForNull Object var1);
}

