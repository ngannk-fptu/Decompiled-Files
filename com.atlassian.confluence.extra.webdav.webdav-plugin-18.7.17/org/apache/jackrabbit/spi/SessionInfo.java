/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi;

import javax.jcr.RepositoryException;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.lock.LockException;

public interface SessionInfo {
    public String getUserID();

    public String getWorkspaceName();

    public String[] getLockTokens() throws UnsupportedRepositoryOperationException, RepositoryException;

    public void addLockToken(String var1) throws UnsupportedRepositoryOperationException, LockException, RepositoryException;

    public void removeLockToken(String var1) throws UnsupportedRepositoryOperationException, LockException, RepositoryException;

    public void setUserData(String var1) throws RepositoryException;
}

