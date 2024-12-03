/*
 * Decompiled with CFR 0.152.
 */
package org.postgresql.jdbc;

import java.util.concurrent.locks.ReentrantLock;

public final class ResourceLock
extends ReentrantLock
implements AutoCloseable {
    public ResourceLock obtain() {
        this.lock();
        return this;
    }

    @Override
    public void close() {
        this.unlock();
    }
}

