/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.directory.RemoteDirectory
 *  com.atlassian.crowd.directory.loader.DirectoryInstanceLoader
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.exception.DirectoryInstantiationException
 */
package com.atlassian.crowd.manager.recovery;

import com.atlassian.crowd.directory.RemoteDirectory;
import com.atlassian.crowd.directory.loader.DirectoryInstanceLoader;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.exception.DirectoryInstantiationException;
import com.atlassian.crowd.manager.recovery.RecoveryModeDirectory;
import com.atlassian.crowd.manager.recovery.RecoveryModeRemoteDirectory;
import java.util.Map;

public class RecoveryModeDirectoryLoader
implements DirectoryInstanceLoader {
    private static final String RECOVERY_DIRECTORY_CLASS_NAME = RecoveryModeDirectory.class.getName();

    public RemoteDirectory getDirectory(Directory directory) throws DirectoryInstantiationException {
        if (!this.canLoad(directory.getImplementationClass())) {
            throw new DirectoryInstantiationException("Cannot load directory of class '" + directory.getImplementationClass() + "'");
        }
        RecoveryModeDirectory recoveryModeDirectory = (RecoveryModeDirectory)directory;
        return new RecoveryModeRemoteDirectory(recoveryModeDirectory);
    }

    public RemoteDirectory getRawDirectory(Long id, String className, Map<String, String> attributes) throws DirectoryInstantiationException {
        throw new DirectoryInstantiationException("RecoveryModeDirectoryLoader cannot be used to load raw directories");
    }

    public boolean canLoad(String className) {
        return RECOVERY_DIRECTORY_CLASS_NAME.equals(className);
    }
}

