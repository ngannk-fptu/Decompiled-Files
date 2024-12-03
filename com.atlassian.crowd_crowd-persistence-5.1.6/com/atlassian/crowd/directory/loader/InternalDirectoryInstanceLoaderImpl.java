/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.directory.InternalRemoteDirectory
 *  com.atlassian.crowd.directory.loader.InternalDirectoryInstanceLoader
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.exception.DirectoryInstantiationException
 *  com.atlassian.crowd.util.InstanceFactory
 *  com.google.common.base.Preconditions
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.directory.loader;

import com.atlassian.crowd.directory.AbstractInternalDirectory;
import com.atlassian.crowd.directory.InternalRemoteDirectory;
import com.atlassian.crowd.directory.loader.AbstractDirectoryInstanceLoader;
import com.atlassian.crowd.directory.loader.InternalDirectoryInstanceLoader;
import com.atlassian.crowd.directory.loader.RemoteDirectoryInstanceFactoryUtil;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.exception.DirectoryInstantiationException;
import com.atlassian.crowd.util.InstanceFactory;
import com.google.common.base.Preconditions;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InternalDirectoryInstanceLoaderImpl
extends AbstractDirectoryInstanceLoader
implements InternalDirectoryInstanceLoader {
    private static final Logger logger = LoggerFactory.getLogger(InternalDirectoryInstanceLoaderImpl.class);
    private final InstanceFactory instanceFactory;

    public InternalDirectoryInstanceLoaderImpl(InstanceFactory instanceFactory) {
        this.instanceFactory = (InstanceFactory)Preconditions.checkNotNull((Object)instanceFactory);
    }

    public InternalRemoteDirectory getRawDirectory(Long id, String className, Map<String, String> directoryAttributes) throws DirectoryInstantiationException {
        return RemoteDirectoryInstanceFactoryUtil.newRemoteDirectory(InternalRemoteDirectory.class, this.instanceFactory, id, className, directoryAttributes);
    }

    public boolean canLoad(String className) {
        try {
            Class<?> clazz = Class.forName(className);
            return AbstractInternalDirectory.class.isAssignableFrom(clazz);
        }
        catch (ClassNotFoundException e) {
            return false;
        }
    }

    public InternalRemoteDirectory getDirectory(Directory directory) throws DirectoryInstantiationException {
        return (InternalRemoteDirectory)super.getDirectory(directory);
    }
}

