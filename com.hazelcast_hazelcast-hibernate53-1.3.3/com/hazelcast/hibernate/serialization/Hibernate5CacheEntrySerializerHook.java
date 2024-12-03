/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hazelcast.nio.serialization.Serializer
 *  com.hazelcast.nio.serialization.SerializerHook
 */
package com.hazelcast.hibernate.serialization;

import com.hazelcast.hibernate.serialization.CacheEntryImpl;
import com.hazelcast.hibernate.serialization.Hibernate53CacheEntrySerializer;
import com.hazelcast.nio.serialization.Serializer;
import com.hazelcast.nio.serialization.SerializerHook;

public class Hibernate5CacheEntrySerializerHook
implements SerializerHook {
    private final Class<?> cacheEntryClass = CacheEntryImpl.class;

    public Serializer createSerializer() {
        return new Hibernate53CacheEntrySerializer();
    }

    public Class getSerializationType() {
        return this.cacheEntryClass;
    }

    public boolean isOverwritable() {
        return true;
    }
}

