/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.directory.RemoteDirectory
 *  com.atlassian.crowd.directory.loader.DirectoryInstanceLoader
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.exception.DirectoryInstantiationException
 */
package com.atlassian.crowd.directory.loader;

import com.atlassian.crowd.directory.RemoteDirectory;
import com.atlassian.crowd.directory.loader.DirectoryInstanceLoader;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.exception.DirectoryInstantiationException;

public abstract class AbstractDirectoryInstanceLoader
implements DirectoryInstanceLoader {
    public RemoteDirectory getDirectory(Directory directory) throws DirectoryInstantiationException {
        return this.getRawDirectory(directory.getId(), directory.getImplementationClass(), directory.getAttributes());
    }
}

