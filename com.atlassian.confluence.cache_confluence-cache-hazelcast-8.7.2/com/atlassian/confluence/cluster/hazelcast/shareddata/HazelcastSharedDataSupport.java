/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.confluence.cluster.shareddata.SharedData
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Supplier
 *  com.hazelcast.core.HazelcastInstance
 *  com.hazelcast.core.IMap
 *  com.hazelcast.core.ISet
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.cluster.hazelcast.shareddata;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.cluster.shareddata.SharedData;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.ISet;
import java.io.Serializable;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.NonNull;

@Deprecated(since="8.2", forRemoval=true)
@Internal
public class HazelcastSharedDataSupport {
    public static final String PREFIX = "confluenceHazelcastSharedData.";
    private final String namePrefix;
    private final Supplier<HazelcastInstance> hazelcastRef;

    public HazelcastSharedDataSupport(String namePrefix, HazelcastInstance hazelcastInstance) {
        this(namePrefix, (Supplier<HazelcastInstance>)((Supplier)() -> hazelcastInstance));
    }

    public HazelcastSharedDataSupport(String namePrefix, Supplier<HazelcastInstance> hazelcastRef) {
        this.namePrefix = PREFIX + (String)Preconditions.checkNotNull((Object)namePrefix);
        this.hazelcastRef = (Supplier)Preconditions.checkNotNull(hazelcastRef);
    }

    <K extends Serializable, V extends Serializable> @NonNull IMap<K, V> getSharedMap(String name) {
        String qualifiedName = this.qualified(name);
        return ((HazelcastInstance)this.hazelcastRef.get()).getMap(qualifiedName);
    }

    <K extends Serializable> @NonNull ISet<K> getSharedSet(String name) {
        String qualifiedName = this.qualified(name);
        return ((HazelcastInstance)this.hazelcastRef.get()).getSet(qualifiedName);
    }

    private String qualified(String name) {
        return this.namePrefix + "." + name;
    }

    public @NonNull SharedData getSharedData(String name) {
        final IMap map = this.getSharedMap(name);
        return new SharedData(){

            public <K extends Serializable, V extends Serializable> @NonNull Map<K, V> getMap() {
                return map;
            }
        };
    }
}

