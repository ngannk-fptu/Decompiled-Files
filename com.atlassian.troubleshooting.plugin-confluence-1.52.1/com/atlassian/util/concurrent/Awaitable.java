/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.util.concurrent;

import java.util.concurrent.TimeUnit;

public interface Awaitable {
    public void await() throws InterruptedException;

    public boolean await(long var1, TimeUnit var3) throws InterruptedException;
}

