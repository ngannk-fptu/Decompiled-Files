/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.cache.CacheException
 */
package com.hazelcast.hibernate.instance;

import com.hazelcast.hibernate.instance.IHazelcastInstanceLoader;
import java.util.Properties;
import org.hibernate.cache.CacheException;

public interface IHazelcastInstanceFactory {
    public IHazelcastInstanceLoader createInstanceLoader(Properties var1) throws CacheException;
}

