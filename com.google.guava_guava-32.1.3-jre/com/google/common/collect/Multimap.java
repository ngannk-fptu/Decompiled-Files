/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 *  com.google.errorprone.annotations.CompatibleWith
 *  com.google.errorprone.annotations.DoNotMock
 *  javax.annotation.CheckForNull
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.ElementTypesAreNonnullByDefault;
import com.google.common.collect.Multiset;
import com.google.common.collect.ParametricNullness;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.CompatibleWith;
import com.google.errorprone.annotations.DoNotMock;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import javax.annotation.CheckForNull;

@DoNotMock(value="Use ImmutableMultimap, HashMultimap, or another implementation")
@ElementTypesAreNonnullByDefault
@GwtCompatible
public interface Multimap<K, V> {
    public int size();

    public boolean isEmpty();

    public boolean containsKey(@CheckForNull @CompatibleWith(value="K") Object var1);

    public boolean containsValue(@CheckForNull @CompatibleWith(value="V") Object var1);

    public boolean containsEntry(@CheckForNull @CompatibleWith(value="K") Object var1, @CheckForNull @CompatibleWith(value="V") Object var2);

    @CanIgnoreReturnValue
    public boolean put(@ParametricNullness K var1, @ParametricNullness V var2);

    @CanIgnoreReturnValue
    public boolean remove(@CheckForNull @CompatibleWith(value="K") Object var1, @CheckForNull @CompatibleWith(value="V") Object var2);

    @CanIgnoreReturnValue
    public boolean putAll(@ParametricNullness K var1, Iterable<? extends V> var2);

    @CanIgnoreReturnValue
    public boolean putAll(Multimap<? extends K, ? extends V> var1);

    @CanIgnoreReturnValue
    public Collection<V> replaceValues(@ParametricNullness K var1, Iterable<? extends V> var2);

    @CanIgnoreReturnValue
    public Collection<V> removeAll(@CheckForNull @CompatibleWith(value="K") Object var1);

    public void clear();

    public Collection<V> get(@ParametricNullness K var1);

    public Set<K> keySet();

    public Multiset<K> keys();

    public Collection<V> values();

    public Collection<Map.Entry<K, V>> entries();

    default public void forEach(BiConsumer<? super K, ? super V> action) {
        Preconditions.checkNotNull(action);
        this.entries().forEach((? super T entry) -> action.accept((Object)entry.getKey(), (Object)entry.getValue()));
    }

    public Map<K, Collection<V>> asMap();

    public boolean equals(@CheckForNull Object var1);

    public int hashCode();
}

