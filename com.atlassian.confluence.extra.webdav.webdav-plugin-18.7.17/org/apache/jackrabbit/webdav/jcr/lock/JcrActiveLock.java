/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.jcr.lock;

import javax.jcr.RepositoryException;
import javax.jcr.lock.Lock;
import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.jcr.ItemResourceConstants;
import org.apache.jackrabbit.webdav.jcr.lock.LockTokenMapper;
import org.apache.jackrabbit.webdav.lock.AbstractActiveLock;
import org.apache.jackrabbit.webdav.lock.ActiveLock;
import org.apache.jackrabbit.webdav.lock.Scope;
import org.apache.jackrabbit.webdav.lock.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JcrActiveLock
extends AbstractActiveLock
implements ActiveLock,
DavConstants {
    private static Logger log = LoggerFactory.getLogger(JcrActiveLock.class);
    private final Lock lock;

    public JcrActiveLock(Lock lock) {
        if (lock == null) {
            throw new IllegalArgumentException("Can not create a ActiveLock with a 'null' argument.");
        }
        this.lock = lock;
    }

    @Override
    public boolean isLockedByToken(String lockToken) {
        return lockToken != null && lockToken.equals(this.getToken());
    }

    @Override
    public boolean isExpired() {
        try {
            return !this.lock.isLive();
        }
        catch (RepositoryException e) {
            log.error("Unexpected error: " + e.getMessage());
            return false;
        }
    }

    @Override
    public String getToken() {
        try {
            return LockTokenMapper.getDavLocktoken(this.lock);
        }
        catch (RepositoryException e) {
            log.warn("Unexpected error while retrieving node identifier for building a DAV specific lock token. {}", (Object)e.getMessage());
            return null;
        }
    }

    @Override
    public String getOwner() {
        return this.lock.getLockOwner();
    }

    @Override
    public void setOwner(String owner) {
        throw new UnsupportedOperationException("setOwner is not implemented");
    }

    @Override
    public long getTimeout() {
        try {
            long to = this.lock.getSecondsRemaining();
            long reportAs = to == Long.MAX_VALUE ? Integer.MAX_VALUE : (to / 1000L <= 9223372036854775L ? to * 1000L : Integer.MAX_VALUE);
            return reportAs;
        }
        catch (RepositoryException e) {
            return Integer.MAX_VALUE;
        }
    }

    @Override
    public void setTimeout(long timeout) {
        throw new UnsupportedOperationException("setTimeout is not implemented");
    }

    @Override
    public boolean isDeep() {
        return this.lock.isDeep();
    }

    @Override
    public void setIsDeep(boolean isDeep) {
        throw new UnsupportedOperationException("setIsDeep is not implemented");
    }

    @Override
    public Type getType() {
        return Type.WRITE;
    }

    @Override
    public Scope getScope() {
        return this.lock.isSessionScoped() ? ItemResourceConstants.EXCLUSIVE_SESSION : Scope.EXCLUSIVE;
    }
}

