/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.store;

import net.sf.ehcache.concurrent.StripedReadWriteLock;

public interface StripedReadWriteLockProvider {
    public StripedReadWriteLock createStripedReadWriteLock();
}

