/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.terracotta.toolkit.concurrent.locks.ToolkitReadWriteLock
 */
package com.terracotta.entity.internal;

import org.terracotta.toolkit.concurrent.locks.ToolkitReadWriteLock;

public interface InternalRootEntity {
    public ToolkitReadWriteLock getEntityLock();

    public void destroy();

    public void markDestroying();

    public void alive();
}

