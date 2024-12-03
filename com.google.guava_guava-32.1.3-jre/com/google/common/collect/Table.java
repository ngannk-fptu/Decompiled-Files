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
import com.google.common.collect.ElementTypesAreNonnullByDefault;
import com.google.common.collect.ParametricNullness;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.CompatibleWith;
import com.google.errorprone.annotations.DoNotMock;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import javax.annotation.CheckForNull;

@DoNotMock(value="Use ImmutableTable, HashBasedTable, or another implementation")
@ElementTypesAreNonnullByDefault
@GwtCompatible
public interface Table<R, C, V> {
    public boolean contains(@CheckForNull @CompatibleWith(value="R") Object var1, @CheckForNull @CompatibleWith(value="C") Object var2);

    public boolean containsRow(@CheckForNull @CompatibleWith(value="R") Object var1);

    public boolean containsColumn(@CheckForNull @CompatibleWith(value="C") Object var1);

    public boolean containsValue(@CheckForNull @CompatibleWith(value="V") Object var1);

    @CheckForNull
    public V get(@CheckForNull @CompatibleWith(value="R") Object var1, @CheckForNull @CompatibleWith(value="C") Object var2);

    public boolean isEmpty();

    public int size();

    public boolean equals(@CheckForNull Object var1);

    public int hashCode();

    public void clear();

    @CheckForNull
    @CanIgnoreReturnValue
    public V put(@ParametricNullness R var1, @ParametricNullness C var2, @ParametricNullness V var3);

    public void putAll(Table<? extends R, ? extends C, ? extends V> var1);

    @CheckForNull
    @CanIgnoreReturnValue
    public V remove(@CheckForNull @CompatibleWith(value="R") Object var1, @CheckForNull @CompatibleWith(value="C") Object var2);

    public Map<C, V> row(@ParametricNullness R var1);

    public Map<R, V> column(@ParametricNullness C var1);

    public Set<Cell<R, C, V>> cellSet();

    public Set<R> rowKeySet();

    public Set<C> columnKeySet();

    public Collection<V> values();

    public Map<R, Map<C, V>> rowMap();

    public Map<C, Map<R, V>> columnMap();

    public static interface Cell<R, C, V> {
        @ParametricNullness
        public R getRowKey();

        @ParametricNullness
        public C getColumnKey();

        @ParametricNullness
        public V getValue();

        public boolean equals(@CheckForNull Object var1);

        public int hashCode();
    }
}

