/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.directory.loader.AbstractDirectoryInstanceLoader
 *  com.atlassian.crowd.directory.loader.RemoteCrowdDirectoryInstanceLoader
 *  com.atlassian.crowd.directory.loader.RemoteDirectoryInstanceFactoryUtil
 *  com.atlassian.crowd.exception.DirectoryInstantiationException
 *  com.atlassian.crowd.util.InstanceFactory
 *  com.google.common.base.Preconditions
 */
package com.atlassian.crowd.directory.loader;

import com.atlassian.crowd.directory.RemoteCrowdDirectory;
import com.atlassian.crowd.directory.loader.AbstractDirectoryInstanceLoader;
import com.atlassian.crowd.directory.loader.RemoteCrowdDirectoryInstanceLoader;
import com.atlassian.crowd.directory.loader.RemoteDirectoryInstanceFactoryUtil;
import com.atlassian.crowd.exception.DirectoryInstantiationException;
import com.atlassian.crowd.util.InstanceFactory;
import com.google.common.base.Preconditions;
import java.util.Map;

public class RemoteCrowdDirectoryInstanceLoaderImpl
extends AbstractDirectoryInstanceLoader
implements RemoteCrowdDirectoryInstanceLoader {
    private final InstanceFactory instanceFactory;

    public RemoteCrowdDirectoryInstanceLoaderImpl(InstanceFactory instanceFactory) {
        this.instanceFactory = (InstanceFactory)Preconditions.checkNotNull((Object)instanceFactory);
    }

    public RemoteCrowdDirectory getRawDirectory(Long id, String className, Map<String, String> directoryAttributes) throws DirectoryInstantiationException {
        return (RemoteCrowdDirectory)RemoteDirectoryInstanceFactoryUtil.newRemoteDirectory(RemoteCrowdDirectory.class, (InstanceFactory)this.instanceFactory, (Long)id, (String)className, directoryAttributes);
    }

    public boolean canLoad(String className) {
        try {
            Class<?> clazz = Class.forName(className);
            return RemoteCrowdDirectory.class.isAssignableFrom(clazz);
        }
        catch (ClassNotFoundException e) {
            return false;
        }
    }
}

