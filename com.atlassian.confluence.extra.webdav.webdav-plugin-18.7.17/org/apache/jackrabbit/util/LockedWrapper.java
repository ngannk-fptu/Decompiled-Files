/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.util;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.lock.LockException;
import org.apache.jackrabbit.util.Locked;

public abstract class LockedWrapper<T>
extends Locked {
    public T with(Node lockable, boolean isDeep) throws RepositoryException, InterruptedException {
        return (T)super.with(lockable, isDeep);
    }

    public T with(Node lockable, boolean isDeep, boolean isSessionScoped) throws RepositoryException, InterruptedException {
        return (T)super.with(lockable, isDeep, isSessionScoped);
    }

    public T with(Node lockable, boolean isDeep, long timeout) throws UnsupportedRepositoryOperationException, RepositoryException, InterruptedException {
        Object r = super.with(lockable, isDeep, timeout);
        if (r == Locked.TIMED_OUT) {
            throw new LockException("Node locked.");
        }
        return (T)r;
    }

    @Override
    public Object with(Node lockable, boolean isDeep, long timeout, boolean isSessionScoped) throws UnsupportedRepositoryOperationException, RepositoryException, InterruptedException {
        Object r = super.with(lockable, isDeep, timeout, isSessionScoped);
        if (r == Locked.TIMED_OUT) {
            throw new LockException("Node locked.");
        }
        return r;
    }

    protected abstract T run(Node var1) throws RepositoryException;
}

