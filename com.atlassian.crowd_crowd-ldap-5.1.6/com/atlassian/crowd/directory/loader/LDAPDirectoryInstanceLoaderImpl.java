/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.directory.RemoteDirectory
 *  com.atlassian.crowd.directory.loader.AbstractDirectoryInstanceLoader
 *  com.atlassian.crowd.directory.loader.LDAPDirectoryInstanceLoader
 *  com.atlassian.crowd.directory.loader.RemoteDirectoryInstanceFactoryUtil
 *  com.atlassian.crowd.exception.DirectoryInstantiationException
 *  com.atlassian.crowd.util.InstanceFactory
 *  com.google.common.base.Preconditions
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.directory.loader;

import com.atlassian.crowd.directory.LDAPDirectory;
import com.atlassian.crowd.directory.RemoteDirectory;
import com.atlassian.crowd.directory.loader.AbstractDirectoryInstanceLoader;
import com.atlassian.crowd.directory.loader.LDAPDirectoryInstanceLoader;
import com.atlassian.crowd.directory.loader.RemoteDirectoryInstanceFactoryUtil;
import com.atlassian.crowd.exception.DirectoryInstantiationException;
import com.atlassian.crowd.util.InstanceFactory;
import com.google.common.base.Preconditions;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LDAPDirectoryInstanceLoaderImpl
extends AbstractDirectoryInstanceLoader
implements LDAPDirectoryInstanceLoader {
    private final Logger logger = LoggerFactory.getLogger(((Object)((Object)this)).getClass());
    private final InstanceFactory instanceFactory;

    public LDAPDirectoryInstanceLoaderImpl(InstanceFactory instanceFactory) {
        this.instanceFactory = (InstanceFactory)Preconditions.checkNotNull((Object)instanceFactory);
    }

    public RemoteDirectory getRawDirectory(Long id, String className, Map<String, String> attributes) throws DirectoryInstantiationException {
        return RemoteDirectoryInstanceFactoryUtil.newRemoteDirectory((InstanceFactory)this.instanceFactory, (Long)id, (String)className, attributes);
    }

    public boolean canLoad(String className) {
        try {
            return LDAPDirectory.class.isAssignableFrom(Class.forName(className));
        }
        catch (ClassNotFoundException e) {
            this.logger.error("Could not load class <" + className + ">", (Throwable)e);
            return false;
        }
    }
}

