/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav.lock;

import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.lock.ActiveLock;
import org.apache.jackrabbit.webdav.lock.LockInfo;
import org.apache.jackrabbit.webdav.lock.Scope;
import org.apache.jackrabbit.webdav.lock.Type;

public interface LockManager {
    public ActiveLock createLock(LockInfo var1, DavResource var2) throws DavException;

    public ActiveLock refreshLock(LockInfo var1, String var2, DavResource var3) throws DavException;

    public void releaseLock(String var1, DavResource var2) throws DavException;

    public ActiveLock getLock(Type var1, Scope var2, DavResource var3);

    public boolean hasLock(String var1, DavResource var2);
}

