/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hazelcast.core.HazelcastInstance
 *  org.hibernate.cache.CacheException
 */
package com.hazelcast.hibernate.instance;

import com.hazelcast.core.HazelcastInstance;
import java.util.Properties;
import org.hibernate.cache.CacheException;

public interface IHazelcastInstanceLoader {
    public void configure(Properties var1);

    public HazelcastInstance loadInstance() throws CacheException;

    public void unloadInstance() throws CacheException;
}

