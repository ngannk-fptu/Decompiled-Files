/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.concurrent;

import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import net.sf.ehcache.concurrent.CacheLockProvider;
import net.sf.ehcache.concurrent.ReadWriteLockSync;

public interface StripedReadWriteLock
extends CacheLockProvider {
    public ReadWriteLock getLockForKey(Object var1);

    public List<ReadWriteLockSync> getAllSyncs();
}

