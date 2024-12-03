/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr.retention;

import javax.jcr.AccessDeniedException;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.lock.LockException;
import javax.jcr.retention.Hold;
import javax.jcr.retention.RetentionPolicy;
import javax.jcr.version.VersionException;

public interface RetentionManager {
    public Hold[] getHolds(String var1) throws PathNotFoundException, AccessDeniedException, RepositoryException;

    public Hold addHold(String var1, String var2, boolean var3) throws PathNotFoundException, AccessDeniedException, LockException, VersionException, RepositoryException;

    public void removeHold(String var1, Hold var2) throws PathNotFoundException, AccessDeniedException, LockException, VersionException, RepositoryException;

    public RetentionPolicy getRetentionPolicy(String var1) throws PathNotFoundException, AccessDeniedException, RepositoryException;

    public void setRetentionPolicy(String var1, RetentionPolicy var2) throws PathNotFoundException, AccessDeniedException, LockException, VersionException, RepositoryException;

    public void removeRetentionPolicy(String var1) throws PathNotFoundException, AccessDeniedException, LockException, VersionException, RepositoryException;
}

