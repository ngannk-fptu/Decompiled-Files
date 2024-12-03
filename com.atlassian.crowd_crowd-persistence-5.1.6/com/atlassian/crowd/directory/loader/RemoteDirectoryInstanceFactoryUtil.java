/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.directory.RemoteDirectory
 *  com.atlassian.crowd.exception.DirectoryInstantiationException
 *  com.atlassian.crowd.util.InstanceFactory
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.directory.loader;

import com.atlassian.crowd.directory.RemoteDirectory;
import com.atlassian.crowd.exception.DirectoryInstantiationException;
import com.atlassian.crowd.util.InstanceFactory;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class RemoteDirectoryInstanceFactoryUtil {
    private static final Logger logger = LoggerFactory.getLogger(RemoteDirectoryInstanceFactoryUtil.class);

    private RemoteDirectoryInstanceFactoryUtil() {
    }

    public static <T extends RemoteDirectory> T newRemoteDirectory(Class<T> clazz, InstanceFactory instanceFactory, Long directoryId, String className, Map<String, String> attributes) throws DirectoryInstantiationException {
        try {
            RemoteDirectory remoteDirectory = (RemoteDirectory)clazz.cast(instanceFactory.getInstance(className));
            if (directoryId != null) {
                remoteDirectory.setDirectoryId(directoryId.longValue());
            }
            remoteDirectory.setAttributes(attributes);
            return (T)remoteDirectory;
        }
        catch (Exception e) {
            logger.error(e.getMessage(), (Throwable)e);
            throw new DirectoryInstantiationException((Throwable)e);
        }
    }

    public static RemoteDirectory newRemoteDirectory(InstanceFactory instanceFactory, Long directoryId, String className, Map<String, String> attributes) throws DirectoryInstantiationException {
        return RemoteDirectoryInstanceFactoryUtil.newRemoteDirectory(RemoteDirectory.class, instanceFactory, directoryId, className, attributes);
    }
}

