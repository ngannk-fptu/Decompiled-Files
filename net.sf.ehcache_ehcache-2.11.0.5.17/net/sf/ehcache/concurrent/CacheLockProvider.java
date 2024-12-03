/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.concurrent;

import net.sf.ehcache.concurrent.Sync;

public interface CacheLockProvider {
    public Sync getSyncForKey(Object var1);
}

