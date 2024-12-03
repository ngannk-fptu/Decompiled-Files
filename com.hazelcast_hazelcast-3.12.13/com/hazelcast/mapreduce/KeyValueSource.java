/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.mapreduce;

import com.hazelcast.core.IList;
import com.hazelcast.core.IMap;
import com.hazelcast.core.ISet;
import com.hazelcast.core.MultiMap;
import com.hazelcast.mapreduce.impl.ListKeyValueSource;
import com.hazelcast.mapreduce.impl.MapKeyValueSource;
import com.hazelcast.mapreduce.impl.MultiMapKeyValueSource;
import com.hazelcast.mapreduce.impl.SetKeyValueSource;
import com.hazelcast.nio.serialization.BinaryInterface;
import com.hazelcast.spi.NodeEngine;
import java.io.Closeable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Deprecated
@BinaryInterface
public abstract class KeyValueSource<K, V>
implements Closeable {
    public abstract boolean open(NodeEngine var1);

    public abstract boolean hasNext();

    public abstract K key();

    public abstract Map.Entry<K, V> element();

    public abstract boolean reset();

    public final Collection<K> getAllKeys() {
        if (!this.isAllKeysSupported()) {
            throw new UnsupportedOperationException("getAllKeys is unsupported for this KeyValueSource");
        }
        return this.getAllKeys0();
    }

    public boolean isAllKeysSupported() {
        return false;
    }

    protected Collection<K> getAllKeys0() {
        return Collections.emptyList();
    }

    public static <K, V> KeyValueSource<K, V> fromMap(IMap<? super K, ? extends V> map) {
        return new MapKeyValueSource(map.getName());
    }

    public static <K, V> KeyValueSource<K, V> fromMultiMap(MultiMap<? super K, ? extends V> multiMap) {
        return new MultiMapKeyValueSource(multiMap.getName());
    }

    public static <V> KeyValueSource<String, V> fromList(IList<? extends V> list) {
        return new ListKeyValueSource(list.getName());
    }

    public static <V> KeyValueSource<String, V> fromSet(ISet<? extends V> set) {
        return new SetKeyValueSource(set.getName());
    }
}

