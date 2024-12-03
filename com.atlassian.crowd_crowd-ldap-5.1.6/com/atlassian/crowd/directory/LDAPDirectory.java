/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.directory.RemoteDirectory
 *  com.atlassian.crowd.exception.GroupNotFoundException
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.atlassian.crowd.exception.UserNotFoundException
 */
package com.atlassian.crowd.directory;

import com.atlassian.crowd.directory.RemoteDirectory;
import com.atlassian.crowd.exception.GroupNotFoundException;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.exception.UserNotFoundException;
import com.atlassian.crowd.model.LDAPDirectoryEntity;

public interface LDAPDirectory
extends RemoteDirectory {
    public <T extends LDAPDirectoryEntity> T findEntityByDN(String var1, Class<T> var2) throws UserNotFoundException, GroupNotFoundException, OperationFailedException;
}

