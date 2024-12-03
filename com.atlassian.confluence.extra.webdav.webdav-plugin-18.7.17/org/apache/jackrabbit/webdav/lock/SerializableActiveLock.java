/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav.lock;

import java.io.Serializable;
import java.util.UUID;
import org.apache.jackrabbit.webdav.lock.AbstractActiveLock;
import org.apache.jackrabbit.webdav.lock.LockInfo;
import org.apache.jackrabbit.webdav.lock.Scope;
import org.apache.jackrabbit.webdav.lock.Type;

public class SerializableActiveLock
extends AbstractActiveLock
implements Serializable {
    private String token = "opaquelocktoken:" + UUID.randomUUID().toString();
    private String owner;
    private boolean isDeep = true;
    private long expirationTime = Integer.MAX_VALUE;

    public SerializableActiveLock() {
    }

    public SerializableActiveLock(LockInfo lockInfo) {
        if (lockInfo != null) {
            if (!Type.WRITE.equals(lockInfo.getType()) || !Scope.EXCLUSIVE.equals(lockInfo.getScope())) {
                throw new IllegalArgumentException("Only 'exclusive write' lock is allowed scope/type pair.");
            }
            this.owner = lockInfo.getOwner();
            this.isDeep = lockInfo.isDeep();
            this.setTimeout(lockInfo.getTimeout());
        }
    }

    @Override
    public boolean isLockedByToken(String lockToken) {
        return this.token != null && this.token.equals(lockToken);
    }

    @Override
    public boolean isExpired() {
        return System.currentTimeMillis() > this.expirationTime;
    }

    @Override
    public String getToken() {
        return this.token;
    }

    @Override
    public String getOwner() {
        return this.owner;
    }

    @Override
    public void setOwner(String owner) {
        this.owner = owner;
    }

    @Override
    public long getTimeout() {
        return this.expirationTime - System.currentTimeMillis();
    }

    @Override
    public void setTimeout(long timeout) {
        if (timeout > 0L) {
            this.expirationTime = System.currentTimeMillis() + timeout;
        }
    }

    @Override
    public boolean isDeep() {
        return this.isDeep;
    }

    @Override
    public void setIsDeep(boolean isDeep) {
        this.isDeep = isDeep;
    }

    @Override
    public Type getType() {
        return Type.WRITE;
    }

    @Override
    public Scope getScope() {
        return Scope.EXCLUSIVE;
    }
}

