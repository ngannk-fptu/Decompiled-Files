/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr.lock;

import javax.jcr.AccessDeniedException;
import javax.jcr.InvalidItemStateException;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.lock.Lock;
import javax.jcr.lock.LockException;

public interface LockManager {
    public void addLockToken(String var1) throws LockException, RepositoryException;

    public Lock getLock(String var1) throws PathNotFoundException, LockException, AccessDeniedException, RepositoryException;

    public String[] getLockTokens() throws RepositoryException;

    public boolean holdsLock(String var1) throws PathNotFoundException, RepositoryException;

    public Lock lock(String var1, boolean var2, boolean var3, long var4, String var6) throws LockException, PathNotFoundException, AccessDeniedException, InvalidItemStateException, RepositoryException;

    public boolean isLocked(String var1) throws PathNotFoundException, RepositoryException;

    public void removeLockToken(String var1) throws LockException, RepositoryException;

    public void unlock(String var1) throws PathNotFoundException, LockException, AccessDeniedException, InvalidItemStateException, RepositoryException;
}

