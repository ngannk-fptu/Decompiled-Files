/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.cache.CacheException
 */
package com.hazelcast.hibernate.instance;

import com.hazelcast.hibernate.CacheEnvironment;
import com.hazelcast.hibernate.instance.IHazelcastInstanceFactory;
import com.hazelcast.hibernate.instance.IHazelcastInstanceLoader;
import java.util.Properties;
import org.hibernate.cache.CacheException;

public final class DefaultHazelcastInstanceFactory
implements IHazelcastInstanceFactory {
    private static final String HZ_CLIENT_LOADER_CLASSNAME = "com.hazelcast.hibernate.instance.HazelcastClientLoader";
    private static final String HZ_INSTANCE_LOADER_CLASSNAME = "com.hazelcast.hibernate.instance.HazelcastInstanceLoader";

    @Override
    public IHazelcastInstanceLoader createInstanceLoader(Properties props) throws CacheException {
        try {
            Class loaderClass = DefaultHazelcastInstanceFactory.getInstanceLoaderClass(props);
            IHazelcastInstanceLoader instanceLoader = (IHazelcastInstanceLoader)loaderClass.newInstance();
            instanceLoader.configure(props);
            return instanceLoader;
        }
        catch (Exception e) {
            throw new CacheException((Throwable)e);
        }
    }

    private static Class getInstanceLoaderClass(Properties props) throws ClassNotFoundException {
        ClassLoader cl = DefaultHazelcastInstanceFactory.class.getClassLoader();
        if (props != null && CacheEnvironment.isNativeClient(props)) {
            return cl.loadClass(HZ_CLIENT_LOADER_CLASSNAME);
        }
        return cl.loadClass(HZ_INSTANCE_LOADER_CLASSNAME);
    }
}

