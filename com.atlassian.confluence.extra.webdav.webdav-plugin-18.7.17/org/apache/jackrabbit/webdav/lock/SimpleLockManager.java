/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav.lock;

import java.util.HashMap;
import java.util.Map;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceIterator;
import org.apache.jackrabbit.webdav.lock.ActiveLock;
import org.apache.jackrabbit.webdav.lock.DefaultActiveLock;
import org.apache.jackrabbit.webdav.lock.LockInfo;
import org.apache.jackrabbit.webdav.lock.LockManager;
import org.apache.jackrabbit.webdav.lock.Scope;
import org.apache.jackrabbit.webdav.lock.Type;

public class SimpleLockManager
implements LockManager {
    private Map<String, ActiveLock> locks = new HashMap<String, ActiveLock>();

    @Override
    public boolean hasLock(String lockToken, DavResource resource) {
        ActiveLock lock = this.locks.get(resource.getResourcePath());
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
        ActiveLock lock = this.locks.get(path);
        if (lock != null && lock.isExpired()) {
            lock = null;
        }
        if (lock == null && !path.equals("/") && (parentLock = this.getLock(SimpleLockManager.getParentPath(path))) != null && parentLock.isDeep()) {
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
        ActiveLock lock = this.locks.get(resourcePath);
        if (lock != null && lock.isExpired()) {
            this.locks.remove(resourcePath);
            lock = null;
        }
        if (lock != null) {
            throw new DavException(423, "Resource '" + resource.getResourcePath() + "' already holds a lock.");
        }
        for (String key : this.locks.keySet()) {
            if (SimpleLockManager.isDescendant(key, resourcePath)) {
                ActiveLock l = this.locks.get(key);
                if (!l.isDeep() && (!key.equals(SimpleLockManager.getParentPath(resourcePath)) || resource.isCollection())) continue;
                throw new DavException(423, "Resource '" + resource.getResourcePath() + "' already inherits a lock by its collection.");
            }
            if (!SimpleLockManager.isDescendant(resourcePath, key) || !lockInfo.isDeep() && !SimpleLockManager.isInternalMember(resource, key)) continue;
            throw new DavException(409, "Resource '" + resource.getResourcePath() + "' cannot be locked due to a lock present on the member resource '" + key + "'.");
        }
        lock = new DefaultActiveLock(lockInfo);
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
        ActiveLock lock = this.locks.get(resource.getResourcePath());
        if (!lock.getToken().equals(lockToken)) {
            throw new DavException(423);
        }
        this.locks.remove(resource.getResourcePath());
    }

    private static boolean isInternalMember(DavResource resource, String memberPath) {
        if (resource.getResourcePath().equals(SimpleLockManager.getParentPath(memberPath))) {
            DavResourceIterator it = resource.getMembers();
            while (it.hasNext()) {
                DavResource member = it.nextResource();
                if (!member.getResourcePath().equals(memberPath)) continue;
                return !member.isCollection();
            }
        }
        return false;
    }

    private static String getParentPath(String path) {
        int idx = path.lastIndexOf(47);
        switch (idx) {
            case -1: {
                return "";
            }
            case 0: {
                return "/";
            }
        }
        return path.substring(0, idx);
    }

    private static boolean isDescendant(String path, String descendant) {
        String pattern = path.endsWith("/") ? path : path + "/";
        return !pattern.equals(descendant) && descendant.startsWith(pattern);
    }
}

