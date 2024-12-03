/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.sax;

public interface WriteLimiter {
    public int getWriteLimit();

    public boolean isThrowOnWriteLimitReached();
}

