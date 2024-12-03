/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.async;

import com.mchange.v1.util.ClosableResource;

public interface AsynchronousRunner
extends ClosableResource {
    public void postRunnable(Runnable var1);

    public void close(boolean var1);

    @Override
    public void close();
}

