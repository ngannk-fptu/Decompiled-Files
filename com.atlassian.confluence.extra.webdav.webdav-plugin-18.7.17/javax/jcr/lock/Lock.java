/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr.lock;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.lock.LockException;

public interface Lock {
    public String getLockOwner();

    public boolean isDeep();

    public Node getNode();

    public String getLockToken();

    public long getSecondsRemaining() throws RepositoryException;

    public boolean isLive() throws RepositoryException;

    public boolean isSessionScoped();

    public boolean isLockOwningSession();

    public void refresh() throws LockException, RepositoryException;
}

