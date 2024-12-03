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
import java.util.Map;
import java.util.Set;
import javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public interface SetMultimap<K, V>
extends Multimap<K, V> {
    @Override
    public Set<V> get(@ParametricNullness K var1);

    @Override
    @CanIgnoreReturnValue
    public Set<V> removeAll(@CheckForNull Object var1);

    @Override
    @CanIgnoreReturnValue
    public Set<V> replaceValues(@ParametricNullness K var1, Iterable<? extends V> var2);

    @Override
    public Set<Map.Entry<K, V>> entries();

    @Override
    public Map<K, Collection<V>> asMap();

    @Override
    public boolean equals(@CheckForNull Object var1);
}

