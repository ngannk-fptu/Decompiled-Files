/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.directory.SynchronisableDirectory
 *  com.atlassian.crowd.exception.DirectoryNotFoundException
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.atlassian.crowd.manager.directory.SynchronisationMode
 */
package com.atlassian.crowd.manager.directory;

import com.atlassian.crowd.directory.SynchronisableDirectory;
import com.atlassian.crowd.exception.DirectoryNotFoundException;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.manager.directory.SynchronisationMode;

public interface DirectorySynchroniser {
    public void synchronise(SynchronisableDirectory var1, SynchronisationMode var2) throws DirectoryNotFoundException, OperationFailedException;

    public boolean isSynchronising(long var1) throws DirectoryNotFoundException;
}

