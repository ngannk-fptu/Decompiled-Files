/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.CheckForNull
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.J2ktIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.CollectSpliterators;
import com.google.common.collect.ElementTypesAreNonnullByDefault;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import com.google.common.collect.UnmodifiableIterator;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.EnumMap;
import java.util.Map;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible(serializable=true, emulated=true)
final class ImmutableEnumMap<K extends Enum<K>, V>
extends ImmutableMap.IteratorBasedImmutableMap<K, V> {
    private final transient EnumMap<K, V> delegate;

    static <K extends Enum<K>, V> ImmutableMap<K, V> asImmutable(EnumMap<K, V> map) {
        switch (map.size()) {
            case 0: {
                return ImmutableMap.of();
            }
            case 1: {
                Map.Entry<K, V> entry = Iterables.getOnlyElement(map.entrySet());
                return ImmutableMap.of((Enum)entry.getKey(), entry.getValue());
            }
        }
        return new ImmutableEnumMap<K, V>(map);
    }

    private ImmutableEnumMap(EnumMap<K, V> delegate) {
        this.delegate = delegate;
        Preconditions.checkArgument(!delegate.isEmpty());
    }

    @Override
    UnmodifiableIterator<K> keyIterator() {
        return Iterators.unmodifiableIterator(this.delegate.keySet().iterator());
    }

    @Override
    Spliterator<K> keySpliterator() {
        return this.delegate.keySet().spliterator();
    }

    @Override
    public int size() {
        return this.delegate.size();
    }

    @Override
    public boolean containsKey(@CheckForNull Object key) {
        return this.delegate.containsKey(key);
    }

    @Override
    @CheckForNull
    public V get(@CheckForNull Object key) {
        return this.delegate.get(key);
    }

    @Override
    public boolean equals(@CheckForNull Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof ImmutableEnumMap) {
            object = ((ImmutableEnumMap)((Object)object)).delegate;
        }
        return this.delegate.equals((Object)object);
    }

    @Override
    UnmodifiableIterator<Map.Entry<K, V>> entryIterator() {
        return Maps.unmodifiableEntryIterator(this.delegate.entrySet().iterator());
    }

    @Override
    Spliterator<Map.Entry<K, V>> entrySpliterator() {
        return CollectSpliterators.map(this.delegate.entrySet().spliterator(), Maps::unmodifiableEntry);
    }

    @Override
    public void forEach(BiConsumer<? super K, ? super V> action) {
        this.delegate.forEach(action);
    }

    @Override
    boolean isPartialView() {
        return false;
    }

    @Override
    @J2ktIncompatible
    Object writeReplace() {
        return new EnumSerializedForm<K, V>(this.delegate);
    }

    @J2ktIncompatible
    private void readObject(ObjectInputStream stream) throws InvalidObjectException {
        throw new InvalidObjectException("Use EnumSerializedForm");
    }

    @J2ktIncompatible
    private static class EnumSerializedForm<K extends Enum<K>, V>
    implements Serializable {
        final EnumMap<K, V> delegate;
        private static final long serialVersionUID = 0L;

        EnumSerializedForm(EnumMap<K, V> delegate) {
            this.delegate = delegate;
        }

        Object readResolve() {
            return new ImmutableEnumMap(this.delegate);
        }
    }
}

