/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.directory.RemoteDirectory
 *  com.atlassian.crowd.directory.loader.DelegatedAuthenticationDirectoryInstanceLoader
 *  com.atlassian.crowd.directory.loader.DelegatingDirectoryInstanceLoader
 *  com.atlassian.crowd.directory.loader.DirectoryInstanceLoader
 *  com.atlassian.crowd.directory.loader.InternalDirectoryInstanceLoader
 *  com.atlassian.crowd.directory.loader.InternalHybridDirectoryInstanceLoader
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.exception.DirectoryInstantiationException
 *  com.atlassian.util.concurrent.CopyOnWriteMap
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.directory.loader;

import com.atlassian.crowd.directory.RemoteDirectory;
import com.atlassian.crowd.directory.loader.DelegatedAuthenticationDirectoryInstanceLoader;
import com.atlassian.crowd.directory.loader.DelegatingDirectoryInstanceLoader;
import com.atlassian.crowd.directory.loader.DirectoryInstanceLoader;
import com.atlassian.crowd.directory.loader.InternalDirectoryInstanceLoader;
import com.atlassian.crowd.directory.loader.InternalHybridDirectoryInstanceLoader;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.exception.DirectoryInstantiationException;
import com.atlassian.util.concurrent.CopyOnWriteMap;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DelegatingDirectoryInstanceLoaderImpl
implements DelegatingDirectoryInstanceLoader {
    private static final Logger logger = LoggerFactory.getLogger(DelegatingDirectoryInstanceLoaderImpl.class);
    private final List<DirectoryInstanceLoader> directoryInstanceLoaders;
    private final ConcurrentMap<String, DirectoryInstanceLoader> classFactoryCache;

    public DelegatingDirectoryInstanceLoaderImpl(List<DirectoryInstanceLoader> loaders) {
        this.directoryInstanceLoaders = ImmutableList.copyOf((Collection)((Collection)Preconditions.checkNotNull(loaders)));
        this.classFactoryCache = CopyOnWriteMap.builder().newHashMap();
    }

    public DelegatingDirectoryInstanceLoaderImpl(InternalDirectoryInstanceLoader internalDirectoryInstanceLoader, InternalHybridDirectoryInstanceLoader ldapInternalHybridDirectoryInstanceLoader, DelegatedAuthenticationDirectoryInstanceLoader delegatedAuthenticationDirectoryInstanceLoader) {
        this(Arrays.asList(internalDirectoryInstanceLoader, ldapInternalHybridDirectoryInstanceLoader, delegatedAuthenticationDirectoryInstanceLoader));
    }

    public DelegatingDirectoryInstanceLoaderImpl(InternalDirectoryInstanceLoader internalDirectoryInstanceLoader, InternalHybridDirectoryInstanceLoader ldapInternalHybridDirectoryInstanceLoader) {
        this(Arrays.asList(internalDirectoryInstanceLoader, ldapInternalHybridDirectoryInstanceLoader));
    }

    public RemoteDirectory getDirectory(Directory directory) throws DirectoryInstantiationException {
        DirectoryInstanceLoader loader = this.getFactoryForClass(directory.getImplementationClass(), true);
        if (loader != null) {
            return loader.getDirectory(directory);
        }
        throw new DirectoryInstantiationException("Could not find a directory instance loader for directory <" + directory.getImplementationClass() + ">");
    }

    public RemoteDirectory getRawDirectory(Long id, String className, Map<String, String> attributes) throws DirectoryInstantiationException {
        DirectoryInstanceLoader loader = this.getFactoryForClass(className, true);
        if (loader != null) {
            return loader.getRawDirectory(id, className, attributes);
        }
        throw new DirectoryInstantiationException("Could not find a directory instance loader for directory <" + className + ">");
    }

    public boolean canLoad(String className) {
        return this.getFactoryForClass(className, false) != null;
    }

    private DirectoryInstanceLoader getFactoryForClass(String className, boolean logError) {
        if (className == null) {
            return null;
        }
        DirectoryInstanceLoader cachedLoader = (DirectoryInstanceLoader)this.classFactoryCache.get(className);
        if (cachedLoader != null) {
            return cachedLoader;
        }
        for (DirectoryInstanceLoader loader : this.directoryInstanceLoaders) {
            if (!loader.canLoad(className)) continue;
            DirectoryInstanceLoader existingLoader = this.classFactoryCache.putIfAbsent(className, loader);
            return existingLoader != null ? existingLoader : loader;
        }
        if (logError) {
            logger.error("Could not find DirectoryInstanceLoader for {}", (Object)className);
        }
        return null;
    }
}

