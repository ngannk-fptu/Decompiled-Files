/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav.lock;

import java.io.Serializable;
import java.util.HashMap;
import org.apache.jackrabbit.util.Text;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceIterator;
import org.apache.jackrabbit.webdav.lock.ActiveLock;
import org.apache.jackrabbit.webdav.lock.LockInfo;
import org.apache.jackrabbit.webdav.lock.LockManager;
import org.apache.jackrabbit.webdav.lock.Scope;
import org.apache.jackrabbit.webdav.lock.SerializableActiveLock;
import org.apache.jackrabbit.webdav.lock.Type;

public class SerializableLockManager
implements LockManager,
Serializable {
    private HashMap locks = new HashMap();

    @Override
    public boolean hasLock(String lockToken, DavResource resource) {
        ActiveLock lock = (ActiveLock)this.locks.get(resource.getResourcePath());
        return lock != null && lock.getToken().equals(lockToken);
    }

    @Override
    public synchronized ActiveLock getLock(Type type, Scope scope, DavResource resource) {
        if (!Type.WRITE.equals(type) || !Scope.EXCLUSIVE.equals(scope)) {
            return null;
        }
        return this.getLock(resource.getResourcePath());
    }

    private ActiveLock getLock(String path) {
        ActiveLock parentLock;
        ActiveLock lock = (ActiveLock)this.locks.get(path);
        if (lock != null && lock.isExpired()) {
            lock = null;
        }
        if (lock == null && !path.equals("/") && (parentLock = this.getLock(Text.getRelativeParent(path, 1))) != null && parentLock.isDeep()) {
            lock = parentLock;
        }
        return lock;
    }

    @Override
    public synchronized ActiveLock createLock(LockInfo lockInfo, DavResource resource) throws DavException {
        if (lockInfo == null || resource == null) {
            throw new IllegalArgumentException("Neither lockInfo nor resource must be null.");
        }
        String resourcePath = resource.getResourcePath();
        ActiveLock lock = (ActiveLock)this.locks.get(resourcePath);
        if (lock != null && lock.isExpired()) {
            this.locks.remove(resourcePath);
            lock = null;
        }
        if (lock != null) {
            throw new DavException(423, "Resource '" + resource.getResourcePath() + "' already holds a lock.");
        }
        for (String key : this.locks.keySet()) {
            if (Text.isDescendant(key, resourcePath)) {
                ActiveLock l = (ActiveLock)this.locks.get(key);
                if (!l.isDeep() && (!key.equals(Text.getRelativeParent(resourcePath, 1)) || resource.isCollection())) continue;
                throw new DavException(423, "Resource '" + resource.getResourcePath() + "' already inherits a lock by its collection.");
            }
            if (!Text.isDescendant(resourcePath, key) || !lockInfo.isDeep() && !SerializableLockManager.isInternalMember(resource, key)) continue;
            throw new DavException(409, "Resource '" + resource.getResourcePath() + "' cannot be locked due to a lock present on the member resource '" + key + "'.");
        }
        lock = new SerializableActiveLock(lockInfo);
        this.locks.put(resource.getResourcePath(), lock);
        return lock;
    }

    @Override
    public ActiveLock refreshLock(LockInfo lockInfo, String lockToken, DavResource resource) throws DavException {
        ActiveLock lock = this.getLock(lockInfo.getType(), lockInfo.getScope(), resource);
        if (lock == null) {
            throw new DavException(412);
        }
        if (!lock.getToken().equals(lockToken)) {
            throw new DavException(423);
        }
        lock.setTimeout(lockInfo.getTimeout());
        return lock;
    }

    @Override
    public synchronized void releaseLock(String lockToken, DavResource resource) throws DavException {
        if (!this.locks.containsKey(resource.getResourcePath())) {
            throw new DavException(412);
        }
        ActiveLock lock = (ActiveLock)this.locks.get(resource.getResourcePath());
        if (!lock.getToken().equals(lockToken)) {
            throw new DavException(423);
        }
        this.locks.remove(resource.getResourcePath());
    }

    private static boolean isInternalMember(DavResource resource, String memberPath) {
        if (resource.getResourcePath().equals(Text.getRelativeParent(memberPath, 1))) {
            DavResourceIterator it = resource.getMembers();
            while (it.hasNext()) {
                DavResource member = it.nextResource();
                if (!member.getResourcePath().equals(memberPath)) continue;
                return !member.isCollection();
            }
        }
        return false;
    }
}

