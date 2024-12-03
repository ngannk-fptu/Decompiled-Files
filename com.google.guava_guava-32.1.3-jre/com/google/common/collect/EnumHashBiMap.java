/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 *  javax.annotation.CheckForNull
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.J2ktIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.AbstractBiMap;
import com.google.common.collect.ElementTypesAreNonnullByDefault;
import com.google.common.collect.EnumBiMap;
import com.google.common.collect.ParametricNullness;
import com.google.common.collect.Serialization;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible(emulated=true)
@J2ktIncompatible
public final class EnumHashBiMap<K extends Enum<K>, V>
extends AbstractBiMap<K, V> {
    transient Class<K> keyTypeOrObjectUnderJ2cl;
    @GwtIncompatible
    private static final long serialVersionUID = 0L;

    public static <K extends Enum<K>, V> EnumHashBiMap<K, V> create(Class<K> keyType) {
        return new EnumHashBiMap<K, V>(keyType);
    }

    public static <K extends Enum<K>, V> EnumHashBiMap<K, V> create(Map<K, ? extends V> map) {
        EnumHashBiMap<K, V> bimap = EnumHashBiMap.create(EnumBiMap.inferKeyTypeOrObjectUnderJ2cl(map));
        bimap.putAll((Map)map);
        return bimap;
    }

    private EnumHashBiMap(Class<K> keyType) {
        super(new EnumMap(keyType), new HashMap());
        this.keyTypeOrObjectUnderJ2cl = keyType;
    }

    @Override
    K checkKey(K key) {
        return (K)((Enum)Preconditions.checkNotNull(key));
    }

    @Override
    @CheckForNull
    @CanIgnoreReturnValue
    public V put(K key, @ParametricNullness V value) {
        return super.put(key, value);
    }

    @Override
    @CheckForNull
    @CanIgnoreReturnValue
    public V forcePut(K key, @ParametricNullness V value) {
        return super.forcePut(key, value);
    }

    @GwtIncompatible
    public Class<K> keyType() {
        return this.keyTypeOrObjectUnderJ2cl;
    }

    @GwtIncompatible
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        stream.writeObject(this.keyTypeOrObjectUnderJ2cl);
        Serialization.writeMap(this, stream);
    }

    @GwtIncompatible
    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.keyTypeOrObjectUnderJ2cl = (Class)Objects.requireNonNull(stream.readObject());
        this.setDelegates(new EnumMap(this.keyTypeOrObjectUnderJ2cl), new HashMap());
        Serialization.populateMap(this, stream);
    }
}

