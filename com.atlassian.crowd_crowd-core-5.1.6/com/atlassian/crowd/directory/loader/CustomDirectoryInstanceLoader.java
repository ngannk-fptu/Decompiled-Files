/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.directory.RemoteDirectory
 *  com.atlassian.crowd.directory.loader.AbstractDirectoryInstanceLoader
 *  com.atlassian.crowd.directory.loader.DirectoryInstanceLoader
 *  com.atlassian.crowd.directory.loader.RemoteDirectoryInstanceFactoryUtil
 *  com.atlassian.crowd.exception.DirectoryInstantiationException
 *  com.atlassian.crowd.util.InstanceFactory
 *  com.google.common.base.Function
 *  com.google.common.base.Preconditions
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.directory.loader;

import com.atlassian.crowd.directory.RemoteDirectory;
import com.atlassian.crowd.directory.loader.AbstractDirectoryInstanceLoader;
import com.atlassian.crowd.directory.loader.DirectoryInstanceLoader;
import com.atlassian.crowd.directory.loader.RemoteDirectoryInstanceFactoryUtil;
import com.atlassian.crowd.exception.DirectoryInstantiationException;
import com.atlassian.crowd.util.InstanceFactory;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomDirectoryInstanceLoader
extends AbstractDirectoryInstanceLoader
implements DirectoryInstanceLoader {
    private static final Logger logger = LoggerFactory.getLogger(CustomDirectoryInstanceLoader.class);
    private final LoadingCache<String, Boolean> canLoadCache;
    private final InstanceFactory instanceFactory;

    public CustomDirectoryInstanceLoader(InstanceFactory instanceFactory) {
        this.instanceFactory = (InstanceFactory)Preconditions.checkNotNull((Object)instanceFactory);
        this.canLoadCache = CacheBuilder.newBuilder().weakKeys().build(CacheLoader.from((Function)new Function<String, Boolean>(){

            public Boolean apply(String className) {
                try {
                    Class<?> clazz = ((Object)((Object)CustomDirectoryInstanceLoader.this)).getClass().getClassLoader().loadClass(className);
                    return RemoteDirectory.class.isAssignableFrom(clazz);
                }
                catch (ClassNotFoundException e) {
                    logger.warn("Could not load class: {}", (Object)className);
                    return Boolean.FALSE;
                }
            }
        }));
    }

    public RemoteDirectory getRawDirectory(Long id, String className, Map<String, String> attributes) throws DirectoryInstantiationException {
        return RemoteDirectoryInstanceFactoryUtil.newRemoteDirectory((InstanceFactory)this.instanceFactory, (Long)id, (String)className, attributes);
    }

    public boolean canLoad(String className) {
        try {
            return (Boolean)this.canLoadCache.get((Object)className);
        }
        catch (ExecutionException e) {
            logger.warn("Failed to check class: {}", (Object)className, (Object)e);
            return Boolean.FALSE;
        }
    }
}

