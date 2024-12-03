/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl;

import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.map.impl.MapDataSerializerHook;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.query.impl.CachedQueryEntry;
import com.hazelcast.query.impl.getters.Extractors;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

public class LazyMapEntry<K, V>
extends CachedQueryEntry<K, V>
implements Serializable,
IdentifiedDataSerializable {
    private static final long serialVersionUID = 0L;
    private transient boolean modified;

    public LazyMapEntry() {
    }

    public LazyMapEntry(Data key, Object value, InternalSerializationService serializationService) {
        this(key, value, serializationService, null);
    }

    public LazyMapEntry(Data key, Object value, InternalSerializationService serializationService, Extractors extractors) {
        this.init(serializationService, key, value, extractors);
    }

    @Override
    public V setValue(V value) {
        this.modified = true;
        Object oldValue = this.getValue();
        this.valueObject = value;
        this.valueData = null;
        return oldValue;
    }

    public void remove() {
        this.modified = true;
        this.valueObject = null;
        this.valueData = null;
    }

    public boolean hasNullValue() {
        return this.valueObject == null && this.valueData == null;
    }

    public boolean isModified() {
        return this.modified;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Map.Entry)) {
            return false;
        }
        Map.Entry e = (Map.Entry)o;
        return LazyMapEntry.eq(this.getKey(), e.getKey()) && LazyMapEntry.eq(this.getValue(), e.getValue());
    }

    private static boolean eq(Object o1, Object o2) {
        return o1 == null ? o2 == null : o1.equals(o2);
    }

    @Override
    public int hashCode() {
        return (this.getKey() == null ? 0 : this.getKey().hashCode()) ^ (this.getValue() == null ? 0 : this.getValue().hashCode());
    }

    public String toString() {
        return this.getKey() + "=" + this.getValue();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.keyObject = in.readObject();
        this.valueObject = in.readObject();
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(this.getKey());
        out.writeObject(this.getValue());
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.keyObject = in.readObject();
        this.valueObject = in.readObject();
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeObject(this.getKey());
        out.writeObject(this.getValue());
    }

    @Override
    public int getFactoryId() {
        return MapDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 131;
    }
}

